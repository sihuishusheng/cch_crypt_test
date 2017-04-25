package sgitg.erypt.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by DELL on 2017/4/13.
 */
public class DecryptConstant {


    /**
     * 获取本地IP
     */
    public static final String IP = getIp();

    /**
     * 获取解密服务名称
     */
    public static final String SERVER_NAME = PathUtil.getTaskNum("serverName");

    /**
     * 解密jre
     */
    public static final String PROVIDER = PathUtil.getTaskNum("provider");

    /**
     * 证书路径
     */
    public static final String DIRECTORY = PathUtil.getTaskNum("directory");

    /**
     * 加密文件路径
     */
    public static final String ENCRY_FILE = PathUtil.getTaskNum("encry_file");

    /**
     * 解密文件路径
     */
    public static final String EXPRESS_FILE = PathUtil.getTaskNum("express_file");
    /**
     * 队列上限
     */
    public static final int QUEUE_NUM = Integer.parseInt(PathUtil
            .getTaskNum("queueNum"));


    /**
     * 数据库连接池回收线程
     */
    public static final int POOL_THREAD_PRIORITY = 1;
    /**
     * 生产线程优先级
     */
    public static final int PUT_THREAD_PRIORITYputThreadPriority = 2;
    /**
     * 消费线程优先级
     */
    public static final int TAKE_THREAD_PRIORITY = 8;

    /**
     * 解密线程数
     */
    public static final int DECRYPT_THREAD_NUM = Integer.parseInt(PathUtil
            .getTaskNum("decryptThreadNum"));
    /**
     * 生产线程休眠间隔
     */
    public static final int PUT_SLEEP_TIME = Integer.parseInt(PathUtil
            .getTaskNum("putSleepTime"));
    /**
     * 解密单元
     */
    public static final int ONE_TASK_NUM = Integer.parseInt(PathUtil
            .getTaskNum("oneTaskNum"));

    /**
     * 是否开启测试线程
     */
    public static final boolean THREAD_TEST = Boolean.parseBoolean(PathUtil
            .getTaskNum("threadTest"));
    /**
     * 测试线程频率
     */
    public static final int TEST_SLEEP_TIME = Integer.parseInt(PathUtil
            .getTaskNum("testSleepTime"));

    /**
     * 解密对象类型-开标报价
     */
    public static final int PRICE_TYPE = 1;

    /**
     * 解密对象类型-明细报价
     */
    public static final int DETAIL_PRICE_TYPE = 2;
    /**
     * 解密对象类型-大文件
     */
    public static final int BIG_FILE_TYPE = 3;

    /**
     * 未分配
     */
    public static final String UN_DISTRIBTION = "-2";

    /**
     * 已分配未解密
     */
    public static final String UN_DECRYPT = "-1";

    /**
     * 正在解密
     */
    public static final String DECRYPTING = "0";

    /**
     * 解密成功
     */
    public static final String DECRYPT_SUC = "1";

    /**
     * 解密失败
     */
    public static final String DECRYPT_ERROR = "2";

    /**
     * 未知异常
     */
    public static final String UN_KNOWN_EXCEPTION = "3000";
    /**
     * 密文验签失败
     */
    public static final String ENCRYPT_VERIFY_FALSE = "3100";

    /**
     * 密文验明文验签失败失败
     */
    public static final String SOURCE_VERIFY_FALSE = "3101";

    /**
     * 数据库操作失败
     */
    public static final String DB_ERROR = "3102";

    /**
     * 打印输出长度
     */
    public static final int PRINT_LENGTH = Integer.parseInt(PathUtil
            .getTaskNum("printLength"));

    /////////////////////////////预编译脚本--开始///////////////////////////////////
    /**
     * 解密明细成功后插入明细报价
     */
    public static final String INSERT_DETAIL_PRICE = " INSERT INTO LB_T_DETAIL_PRICE (DETAIL_PRICE__ID,PROJECT_ID,OBJECT_ID,PACKAGE_ID,PROVIDER_ID,ENGINEERING_ID,TYPES,PACKAGE_DETAIL_ID,PRICE_NOTES,PRICE_ENCRYPT_ID,CONTENT) VALUES (?,?,?,?,?,?,?,?,?,?,EMPTY_CLOB()) ";
    public static final String DETAIL_FOR_UPDATE = "SELECT CONTENT FROM LB_T_DETAIL_PRICE WHERE DETAIL_PRICE__ID = ? FOR UPDATE";
    public static final String DETAIL_DO_UPDATE = "UPDATE LB_T_DETAIL_PRICE SET CONTENT = ? WHERE DETAIL_PRICE__ID = ?";

    /**
     * 解密大文件过程中更新大文件状态
     */
    public static final String UPDATE_FILE_STATUS = " UPDATE ecp_t_bid_file t SET t.file_stuta =?,t.crypt_remark = ? ,t.crypt_count=? WHERE t.id=? ";

    /**
     * 解密总价
     */
    public static final String UPDATE_PRICE_STATUS = " UPDATE ecp_t_bid  etb SET etb.tol_price = ? WHERE etb.id = ? ";

    /**
     * 解密总价
     */
    public static final String UPDATE_GOOD_PRICE_STATUS = " UPDATE ecp_t_bid_good_price etbgp SET etbgp.good_price = ? WHERE etbgp.bid_id = ? AND etbgp.package_id = ? AND etbgp.good_id = ? ";

    /**
     * 生产线程在装载数据时对数据状态的更新操作脚本
     */
    public static final String UPDATE_PRICE_STATEMENT = "UPDATE  LB_T_PACKAGE_PRICE_ENCRYPT  SET DECRYPT_STATUS=? , DECRYPT_ERROR=? ,REPAIR_COUNT=?  WHERE PRICE_ENCRYPT_ID =? ";
    public static final String UPDATE_DETAIL_STATEMENT = " UPDATE LB_T_DETAIL_PRICE_ENCRYPT SET DECRYPT_STATUS =?,DECRYPT_ERROR=?,REPAIR_COUNT=?  WHERE DETAIL_ENCRYPT_ID =? ";
    public static final String UPDATE_FILE_STATEMENT = " UPDATE LB_T_BID_FILE SET DECRYPT_STATUS =?, DECRYPT_ERROR=?, REPAIR_COUNT=? WHERE BID_FILE_ID =? ";

    /**
     * 获取文件根路径
     */
    public static final String GET_ROOT_PATH = "SELECT P.CODE_NAME FROM SYS_CODE_TYPE P WHERE P.PARENT_ID =(SELECT P.UU_ID FROM SYS_CODE_TYPE P WHERE P.CODE_ID ='filepath')";
    /////////////////////////////预编译脚本--结束///////////////////////////////////
    /**
     * 是否使用连接池
     */
    public static final boolean POOL_USED = Boolean.parseBoolean(PathUtil
            .getDBPool());

    @SuppressWarnings("unchecked")
    private static String getIp() {
        String localip = "127.0.0.1";// 本地IP

        Enumeration netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces
                        .nextElement();
                Enumeration address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = (InetAddress) address.nextElement();

                    if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                        localip = ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {

            // logger.error(LogUtil.getException(e));
        }

        return localip;
    }
}
