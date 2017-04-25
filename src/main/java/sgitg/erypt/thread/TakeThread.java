package sgitg.erypt.thread;

import com.sansec.app.PKCS7;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import sgitg.erypt.ConnectionException;
import sgitg.erypt.cepri.BidCaException;
import sgitg.erypt.cepri.CADecrypt;
import sgitg.erypt.cepri.CAFactory;
import sgitg.erypt.dao.DecryptDao;
import sgitg.erypt.dao.DecryptFactory;
import sgitg.erypt.swxa.Util;
import sgitg.erypt.util.DecryptConstant;
import sgitg.erypt.util.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * Created by DELL on 2017/4/14.
 */
public class TakeThread  extends Thread {
    @SuppressWarnings("unchecked")
    BlockingQueue<Map> q;

    Logger logger = Logger.getLogger(this.getClass());
    DecryptDao dao = DecryptFactory.getDecryptDao();
    CADecrypt cad = null;

    String commUploadPath = "";

    Map<String,String> pkMap=null;


    // 构造方法one次
    @SuppressWarnings("unchecked")
    public TakeThread(BlockingQueue queue, Map<String,String> pkMap)  throws Exception{
        logger.debug(LogUtil.printMessage("take当前服务:"
                + DecryptConstant.SERVER_NAME + "下队列的总大小：" + queue.size()));

        q = queue;
        Connection conn = null;
        try {
            try{
                this.pkMap=pkMap;
                logger.info("初始化CA 实例...");
                cad = CAFactory.getCADecryptInstance();
                logger.info("初始化CA 实例...通过");
            }catch (Exception e) {
                logger.error("初始化CA 实例 异常,解密程序终止!",e);
                System.exit(0);
            }
            //获取数据库链接
            conn = DecryptFactory.getConnection();
            //获取文件根路径
        //    commUploadPath = DecryptFactory.getDecryptDao().getCommUploadPath(conn);
        } catch (Exception e) {
            logger.error(LogUtil.getException(e));
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    logger.error(LogUtil.getException(e));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void run() {

        while (true) {
            Connection conn = null;
            Map map = null;
            try {
                logger.info(LogUtil.printMessage(Thread.currentThread().getName()
                        + " take task当前队列大小:" + q.size()));

                map = q.take();
                if (((Set) map.get("value")).size() > 0) {
                    conn = DecryptFactory.getConnection();
                    this.decryptTask(map, conn);
                }
            } catch (ConnectionException ce) {
                returnQueue(q, map);
            } catch (SQLException e) {
                returnQueue(q, map);
            } catch (Exception e) {
                logger.error(LogUtil.getException(e));
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (Exception e) {
                        logger.error(LogUtil.getException(e));
                    }
                }
            }
        }
    }

    /**
     * 解密任务
     *
     * @return void
     * @throws SQLException
     * @throws ConnectionException
     */
    @SuppressWarnings("unchecked")
    public void decryptTask(Map map, Connection conn)
            throws ConnectionException {


        if (map.get("type").equals(DecryptConstant.PRICE_TYPE + "")) {

            logger.info(LogUtil.printMessage(Thread.currentThread().getName()
                    + ":take task当前正在解密报价表"));
            this.decryptPrice((Set) map.get("value"), conn);

        }
        else if (map.get("type").equals(DecryptConstant.DETAIL_PRICE_TYPE + "")) {
            logger.info(LogUtil.printMessage(Thread.currentThread().getName()
                    + ":take task当前正在解密明细报价"));
            this.decryptGoodPrice((Set) map.get("value"), conn);
        }
        else
            if (map.get("type").equals(DecryptConstant.BIG_FILE_TYPE + "")) {
            logger.info(LogUtil.printMessage(Thread.currentThread().getName()
                    + ":take task当前正在解密大文件"));
            this.decryptFile((Set) map.get("value"), conn);
        }
    }

    /**
     * 返回队列
     */
    @SuppressWarnings("unchecked")
    public void returnQueue(BlockingQueue q, Map map) {
        try {
            logger.warn(LogUtil.printMessage(Thread.currentThread().getName()
                    + "当前队列长度=" + q.size()));
            q.put(map);
            logger.warn(LogUtil.printMessage(Thread.currentThread().getName()
                    + "数据库连接异常，返回列队"));
            logger.warn(LogUtil.printMessage(Thread.currentThread().getName()
                    + "当前队列长度=" + q.size()));
        } catch (InterruptedException e) {
            logger.error(LogUtil.getException(e));
        }
    }

    /**
     * 解密大文件（非分离式加解密）
     *
     * @return void
     * @throws SQLException
     * @throws ConnectionException
     */
    public void decryptFile(Set<String[]> set, Connection conn) throws ConnectionException {

        PreparedStatement updateFileps = null;
        try {
            //解密大文件过程中更新大文件状态
            updateFileps = dao.updateFileStatement(conn);

            File temp = null;
            File de = null;

            for (String[] ss : set) {
                // 0主键, 1加密文件名, 2加密文件路径, 3明文件名, 4明文路径, 5投标ID
                // 3000 =未知错误 ，3100=密文验签失败 ，3101=明文验签失败，3102=数据库操作失败，3103
                String repariCount = ss[6];
                if(repariCount==null||"".equals(repariCount)){
                    repariCount="0";
                }
                try {

                    // 解密后明文路径及文件
                    String decryptDir = ss[4];
                    String decrytFile = ss[3];

                    // 密文路径及文件（非分离式）检查
//                    String encrytFile = this.commUploadPath + File.separator + ss[8].replace("decrypt", "encrypt") + ".enc";
                    String encrytFile = ss[2]+File.separator +ss[1];

                    // logger.info(LogUtil.printMessage(Thread.currentThread().getName()+"解密文件路径："+encrytFile));
                    File isFile = new File(encrytFile);
                    if (!isFile.exists()) {
                        logger.info(LogUtil.printMessage(Thread.currentThread().getName()
                                + "不存在文件路径：" + encrytFile));
                        dao.updateFile(updateFileps, DecryptConstant.DECRYPT_ERROR, "3103",
                                Integer.parseInt(repariCount) + 1, ss[0]);
                        continue;
                    }

                    // 创建解密目录
                    File filedir = new File(decryptDir);
                    if (!filedir.exists()) {
                        filedir.mkdirs();
                    }

                    // 临时文件路径
                    long ct = System.currentTimeMillis();
                    String tempFile = decryptDir + File.separator
                            + DecryptConstant.SERVER_NAME + "-" + ss[0] + "-" + ct + ".temp";
                    String deFile = decryptDir + File.separator
                            + DecryptConstant.SERVER_NAME + "-" + ss[0] + "-" + ct + ".ver";

                    // 创建临时文件
                    temp = new File(tempFile);
                    de = new File(deFile);
                    temp.createNewFile();
                    de.createNewFile();


                    // 验签解密
//                    cad.verifyFileSignAttached(encrytFile,tempFile);

//                   三未信安公司提供加密机硬解密
                    //读取数字信封
                    byte[] envelopData = Util.readFromFile(encrytFile);

                    //从文件读取私钥和公钥合成SM2私钥
//		PrivateKey key = Util.parseSM2PrivateKey(DIRECTORY+pubKeyName,DIRECTORY+priKeyName);
                    //从设备内区出私钥，这个私钥存放的是索引号，不含有真正的私钥数据
                    PrivateKey key = (PrivateKey)Util.readKeyFromDevice(11, "SM2", false);
                    //读取证书文件
//        X509Certificate cerx509 = (X509Certificate)Util.readCertFromFile(DIRECTORY+certName);

                    //解密数字信封
                    byte[] plain = PKCS7.decEnvelopedData(envelopData, key);

                    System.out.println("Data length="+plain.length);
                    //PrintUtil.printWithHex(plain);
                    Util.writeToFile(decryptDir+File.separator+decrytFile, plain);

//                    cad.verifyFileSignAttached(deFile,tempFile);

                    // 删除临时文件
                    File decryptFile = new File(decrytFile);
                    if (decryptFile.exists()) {
                        decryptFile.delete();
                    } else {
                    }
                    temp.renameTo(decryptFile);

                    de.delete();

                    dao.updateFile(updateFileps, DecryptConstant.DECRYPT_SUC,
                            "", Integer.parseInt(repariCount) + 1, ss[0]);

                }
//                catch (BidCaException e) {
//                    logger.error(LogUtil.getException("文件部分：验签解密文件异常", e));
//                    try {
//                        dao.updateFile(updateFileps, DecryptConstant.DECRYPT_ERROR, e.getErrCode(),
//                                Integer.parseInt(repariCount) + 1, ss[0]);
//                    } catch (Exception e1) {
//                        logger.error(LogUtil.getException("文件部分：更新记录状态异常", e1));
//                    }
//
//                }
                catch (SQLException se) {
                    logger.error(LogUtil.getException("文件部分：执行数据库操作异常", se));
                    try {
                        dao.updateFile(updateFileps, DecryptConstant.DECRYPT_ERROR, DecryptConstant.DB_ERROR,
                                Integer.parseInt(repariCount) + 1, ss[0]);
                    } catch (NumberFormatException e1) {
                        logger.error(LogUtil.getException(e1));
                    } catch (SQLException e1) {
                        logger.error(LogUtil.getException("文件部分：更新记录状态异常", e1));
                    }

                } catch (Exception e) {
                    logger.error(LogUtil.getException("文件部分：未知异常", e));
                    try {
                        dao.updateFile(updateFileps, DecryptConstant.DECRYPT_ERROR, DecryptConstant.UN_KNOWN_EXCEPTION,
                                Integer.parseInt(repariCount) + 1, ss[0]);
                    } catch (NumberFormatException e1) {
                        logger.error(LogUtil.getException(e1));
                    } catch (SQLException e1) {
                        logger.error(LogUtil.getException("文件部分：更新记录状态异常", e1));
                        throw new ConnectionException();
                    }
                } finally{
                    deleteTempFile(temp);
                    deleteTempFile(de);
                }
            }
        } catch (SQLException e1) {
            logger.error(LogUtil.getException(e1));
            throw new ConnectionException();
        } finally {
            try {
                if (updateFileps != null) {
                    updateFileps.close();
                }
            } catch (SQLException e) {
                logger.error(LogUtil.getException(e));
            }
        }
    }

    /**
     * 删除缓存文件
     * @param temp
     */
    private void deleteTempFile(File temp) {

        try {
            if (temp != null) {
                temp.delete();
            }
        } catch (Exception e) {
            logger.error(LogUtil.printMessage("删除缓存文件出错"));
        }
    }

    /**
     * 解密开标报价
     *
     * @return void
     * @throws SQLException
     * @throws ConnectionException
     */
    public void decryptPrice(Set<String[]> set, Connection conn)
            throws ConnectionException {

        PreparedStatement updateFileps = null;
        PreparedStatement updatePrice = null;

        try {
            //解密大文件过程中更新大文件状态
            updateFileps = dao.updateFileStatement(conn);

            //解密报价
            updatePrice = dao.updatePriceStatement(conn);

            File temp = null;
            File de = null;

            for (String[] ss : set) {
                // 0主键, 1加密文件名, 2加密文件路径, 3明文件名, 4明文路径, 5投标ID
                // 3000 =未知错误 ，3100=密文验签失败 ，3101=明文验签失败，3102=数据库操作失败，3103
                String repariCount = ss[6];
                String bid_id=ss[5];
                try {

                    // 解密后明文路径及文件
                    String decryptDir = ss[4];
                    String decrytFile = ss[4]+File.separator+ss[3];

                    // 密文路径及文件（非分离式）检查
//                    String encrytFile = this.commUploadPath + File.separator + ss[8].replace("decrypt", "encrypt") + ".enc";
                    String encrytFile = ss[2]+File.separator +ss[1];

                    // logger.info(LogUtil.printMessage(Thread.currentThread().getName()+"解密文件路径："+encrytFile));
                    File isFile = new File(encrytFile);
                    if (!isFile.exists()) {
                        logger.info(LogUtil.printMessage(Thread.currentThread().getName()
                                + "不存在文件路径：" + encrytFile));
                        dao.updateFile(updateFileps, DecryptConstant.DECRYPT_ERROR, "3103",
                                Integer.parseInt(repariCount) + 1, ss[0]);
                        continue;
                    }

                    // 创建解密目录
                    File filedir = new File(decryptDir);
                    if (!filedir.exists()) {
                        filedir.mkdirs();
                    }

                    // 临时文件路径
                    long ct = System.currentTimeMillis();
                    String tempFile = decryptDir + File.separator
                            + DecryptConstant.SERVER_NAME + "-" + ss[0] + "-" + ct + ".temp";
                    String deFile = decryptDir + File.separator
                            + DecryptConstant.SERVER_NAME + "-" + ss[0] + "-" + ct + ".ver";

                    // 创建临时文件
                    temp = new File(tempFile);
                    de = new File(deFile);
                    temp.createNewFile();
                    de.createNewFile();

                    // 验签解密
                    cad.verifyFileSignAttached(encrytFile,tempFile);
                    cad.decryptFileEnvelop(tempFile, deFile, this.pkMap);
                    cad.verifyFileSignAttached(deFile,tempFile);

                    // 删除临时文件
                    File decryptFile = new File(decrytFile);
                    if (decryptFile.exists()) {
                        decryptFile.delete();
                    } else {
                    }
                    temp.renameTo(decryptFile);

                    de.delete();
                    //读取文件更新数据库价格  decrytFile
                    BufferedReader br = new BufferedReader(new FileReader(decrytFile));// 读取原始json文件
                    String s = null;
                    while ((s = br.readLine()) != null) {
                        System.out.println("读取的字符串是+++++++++++++++++"+s);
                        JSONObject dataJson = JSONObject.fromObject(s);// 创建一个包含原始json串的json对象
                        String js_bid_id = dataJson.getString("bid_id");// 找到properties的json对象
                        System.out.println("bid_id++++++++++++++++++++++++++++++++++"+js_bid_id);
                        String price = dataJson.getString("price");// 找到properties的json对象
                        System.out.println("price++++++++++++++++++++++++++++++++++"+price);
                        dao.updatePrice(updatePrice,price,js_bid_id);
                    }

                    dao.updateFile(updateFileps, DecryptConstant.DECRYPT_SUC,
                            "", Integer.parseInt(repariCount) + 1, ss[0]);

                } catch (BidCaException e) {
                    logger.error(LogUtil.getException("文件部分：验签解密文件异常", e));
                    try {
                        dao.updateFile(updateFileps, DecryptConstant.DECRYPT_ERROR, e.getErrCode(),
                                Integer.parseInt(repariCount) + 1, ss[0]);
                    } catch (Exception e1) {
                        logger.error(LogUtil.getException("文件部分：更新记录状态异常", e1));
                    }

                } catch (SQLException se) {
                    logger.error(LogUtil.getException("文件部分：执行数据库操作异常", se));
                    try {
                        dao.updateFile(updateFileps, DecryptConstant.DECRYPT_ERROR, DecryptConstant.DB_ERROR,
                                Integer.parseInt(repariCount) + 1, ss[0]);
                    } catch (NumberFormatException e1) {
                        logger.error(LogUtil.getException(e1));
                    } catch (SQLException e1) {
                        logger.error(LogUtil.getException("文件部分：更新记录状态异常", e1));
                    }

                } catch (Exception e) {
                    logger.error(LogUtil.getException("文件部分：未知异常", e));
                    try {
                        dao.updateFile(updateFileps, DecryptConstant.DECRYPT_ERROR, DecryptConstant.UN_KNOWN_EXCEPTION,
                                Integer.parseInt(repariCount) + 1, ss[0]);
                    } catch (NumberFormatException e1) {
                        logger.error(LogUtil.getException(e1));
                    } catch (SQLException e1) {
                        logger.error(LogUtil.getException("文件部分：更新记录状态异常", e1));
                        throw new ConnectionException();
                    }
                } finally{
                    deleteTempFile(temp);
                    deleteTempFile(de);
                }
            }
        } catch (SQLException e1) {
            logger.error(LogUtil.getException(e1));
            throw new ConnectionException();
        } finally {
            try {
                if (updateFileps != null) {
                    updateFileps.close();
                }
            } catch (SQLException e) {
                logger.error(LogUtil.getException(e));
            }
        }
    }

    /**
     * 解密明细报价
     *
     * @return void
     * @throws SQLException
     * @throws ConnectionException
     */
    public void decryptGoodPrice(Set<String[]> set, Connection conn)
            throws ConnectionException {

        PreparedStatement updateFileps = null;
        PreparedStatement updateGoodPrice = null;

        try {
            //解密大文件过程中更新大文件状态
            updateFileps = dao.updateFileStatement(conn);

            //解密报价
            updateGoodPrice = dao.updateGoodPriceStatement(conn);

            File temp = null;
            File de = null;

            for (String[] ss : set) {
                // 0主键, 1加密文件名, 2加密文件路径, 3明文件名, 4明文路径, 5投标ID
                // 3000 =未知错误 ，3100=密文验签失败 ，3101=明文验签失败，3102=数据库操作失败，3103
                String repariCount = ss[6];
                String bid_id=ss[5];
                try {

                    // 解密后明文路径及文件
                    String decryptDir = ss[4];
                    String decrytFile = ss[4]+File.separator+ss[3];

                    // 密文路径及文件（非分离式）检查
//                    String encrytFile = this.commUploadPath + File.separator + ss[8].replace("decrypt", "encrypt") + ".enc";
                    String encrytFile = ss[2]+File.separator +ss[1];

                    // logger.info(LogUtil.printMessage(Thread.currentThread().getName()+"解密文件路径："+encrytFile));
                    File isFile = new File(encrytFile);
                    if (!isFile.exists()) {
                        logger.info(LogUtil.printMessage(Thread.currentThread().getName()
                                + "不存在文件路径：" + encrytFile));
                        dao.updateFile(updateFileps, DecryptConstant.DECRYPT_ERROR, "3103",
                                Integer.parseInt(repariCount) + 1, ss[0]);
                        continue;
                    }

                    // 创建解密目录
                    File filedir = new File(decryptDir);
                    if (!filedir.exists()) {
                        filedir.mkdirs();
                    }

                    // 临时文件路径
                    long ct = System.currentTimeMillis();
                    String tempFile = decryptDir + File.separator
                            + DecryptConstant.SERVER_NAME + "-" + ss[0] + "-" + ct + ".temp";
                    String deFile = decryptDir + File.separator
                            + DecryptConstant.SERVER_NAME + "-" + ss[0] + "-" + ct + ".ver";

                    // 创建临时文件
                    temp = new File(tempFile);
                    de = new File(deFile);
                    temp.createNewFile();
                    de.createNewFile();

                    // 验签解密
                    cad.verifyFileSignAttached(encrytFile,tempFile);
                    cad.decryptFileEnvelop(tempFile, deFile, this.pkMap);
                    cad.verifyFileSignAttached(deFile,tempFile);

                    // 删除临时文件
                    File decryptFile = new File(decrytFile);
                    if (decryptFile.exists()) {
                        decryptFile.delete();
                    } else {
                    }
                    temp.renameTo(decryptFile);

                    de.delete();
                    //读取文件更新数据库价格  decrytFile
                    BufferedReader br = new BufferedReader(new FileReader(decrytFile));// 读取原始json文件
                    String s = null;
                    while ((s = br.readLine()) != null) {
                        System.out.println("读取的字符串是+++++++++++++++++"+s);
                        JSONObject dataJson = JSONObject.fromObject(s);// 创建一个包含原始json串的json对象
                        JSONArray prices = dataJson.getJSONArray("prices");// 找到features的json数组
                        for (int i = 0; i < prices.size(); i++) {
                            JSONObject g_p = prices.getJSONObject(i);// 获取features数组的第i个json对象
                            String good_id = g_p.getString("good_id");// 找到properties的json对象
                            System.out.println("good_id++++++++++++++++++++++++++++++++++"+good_id);//
                            String g_price = g_p.getString("price");// 找到properties的json对象
                            System.out.println("price++++++++++++++++++++++++++++++++++"+g_price);//
                            String package_id = g_p.getString("package_id");// 找到properties的json对象
                            System.out.println("price++++++++++++++++++++++++++++++++++"+package_id);//
                            dao.updateGoodPrice(updateGoodPrice,g_price,bid_id,package_id,good_id);
                        }
                    }

                    dao.updateFile(updateFileps, DecryptConstant.DECRYPT_SUC,
                            "", Integer.parseInt(repariCount) + 1, ss[0]);

                } catch (BidCaException e) {
                    logger.error(LogUtil.getException("文件部分：验签解密文件异常", e));
                    try {
                        dao.updateFile(updateFileps, DecryptConstant.DECRYPT_ERROR, e.getErrCode(),
                                Integer.parseInt(repariCount) + 1, ss[0]);
                    } catch (Exception e1) {
                        logger.error(LogUtil.getException("文件部分：更新记录状态异常", e1));
                    }

                } catch (SQLException se) {
                    logger.error(LogUtil.getException("文件部分：执行数据库操作异常", se));
                    try {
                        dao.updateFile(updateFileps, DecryptConstant.DECRYPT_ERROR, DecryptConstant.DB_ERROR,
                                Integer.parseInt(repariCount) + 1, ss[0]);
                    } catch (NumberFormatException e1) {
                        logger.error(LogUtil.getException(e1));
                    } catch (SQLException e1) {
                        logger.error(LogUtil.getException("文件部分：更新记录状态异常", e1));
                    }

                } catch (Exception e) {
                    logger.error(LogUtil.getException("文件部分：未知异常", e));
                    try {
                        dao.updateFile(updateFileps, DecryptConstant.DECRYPT_ERROR, DecryptConstant.UN_KNOWN_EXCEPTION,
                                Integer.parseInt(repariCount) + 1, ss[0]);
                    } catch (NumberFormatException e1) {
                        logger.error(LogUtil.getException(e1));
                    } catch (SQLException e1) {
                        logger.error(LogUtil.getException("文件部分：更新记录状态异常", e1));
                        throw new ConnectionException();
                    }
                } finally{
                    deleteTempFile(temp);
                    deleteTempFile(de);
                }
            }
        } catch (SQLException e1) {
            logger.error(LogUtil.getException(e1));
            throw new ConnectionException();
        } finally {
            try {
                if (updateFileps != null) {
                    updateFileps.close();
                }
            } catch (SQLException e) {
                logger.error(LogUtil.getException(e));
            }
        }
    }
}
