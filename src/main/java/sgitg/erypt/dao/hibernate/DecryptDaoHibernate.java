package sgitg.erypt.dao.hibernate;

import org.apache.log4j.Logger;
import sgitg.erypt.ConnectionException;
import sgitg.erypt.dao.DecryptDao;
import sgitg.erypt.dao.DecryptFactory;
import sgitg.erypt.util.DecryptConstant;
import sgitg.erypt.util.LogUtil;
import sgitg.erypt.util.PathUtil;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by DELL on 2017/4/13.
 */
public class DecryptDaoHibernate  implements DecryptDao {

    public static Logger logger = Logger.getLogger(DecryptDaoHibernate.class);
    int rowNum = 4000;
    String appendWhere=" ";

    public DecryptDaoHibernate() {
        rowNum = Integer.parseInt(PathUtil.getTaskNum("putRowNum"));
//        String st=PathUtil.getTaskNum("appendWhere");
//        if(st!=null && !"".equals(st.trim())){
//            appendWhere=" and "+ st+" ";
//        }
    }

    /**
     * 解密大文件过程中更新大文件状态
     */
    public PreparedStatement updateFileStatement(Connection conn) throws SQLException {
        return conn.prepareStatement(DecryptConstant.UPDATE_FILE_STATUS);
    }

    /**
     * 更新 大文件
     */
    public void updateFile(PreparedStatement updateFileps,
                           String decryptStatus, String errorCode, int repariCount,
                           String fileId) throws SQLException {

        if (repariCount > 100) {
            repariCount = 100;
        }
        updateFileps.setString(1, decryptStatus);
        updateFileps.setString(2, errorCode);
        updateFileps.setInt(3, repariCount);
        updateFileps.setString(4, fileId);
        updateFileps.execute();
    }

    /**
     * 更新总价 预编译
     *
     * @param conn
     * @return
     * @throws ConnectionException
     * @throws SQLException
     */
    public PreparedStatement updatePriceStatement(Connection conn)
            throws ConnectionException, SQLException{
        return conn.prepareStatement(DecryptConstant.UPDATE_PRICE_STATUS);

    }

    /**
     * 更新总价
     *
     * @param updatePrice
     * @param price
     * @param bid_id
     * @throws SQLException
     */
    public void updatePrice(PreparedStatement updatePrice,
                            String price,
                            String bid_id) throws SQLException{

        updatePrice.setString(1, price);
        updatePrice.setString(2, bid_id);
        updatePrice.execute();
    }

    /**
     * 更新明细总价 预编译
     *
     * @param conn
     * @return
     * @throws ConnectionException
     * @throws SQLException
     */
    public PreparedStatement updateGoodPriceStatement(Connection conn)
            throws ConnectionException, SQLException{
        return conn.prepareStatement(DecryptConstant.UPDATE_GOOD_PRICE_STATUS);

    }
    /**
     * 更新明细
     *
     * @param updateGoodPrice
     * @param price
     * @param bid_id
     * @throws SQLException
     */
    public void updateGoodPrice(PreparedStatement updateGoodPrice,
                                String price,
                                String bid_id,String pakage_id,String good_id) throws SQLException{
        updateGoodPrice.setString(1, price);
        updateGoodPrice.setString(2, bid_id);
        updateGoodPrice.setString(3, pakage_id);
        updateGoodPrice.setString(4, good_id);
        updateGoodPrice.execute();

    }


    /**
     * 生产解密任务
     * @param type 获取变量值
     */
    @SuppressWarnings("unchecked")
    public Set getDecryptSet(int type) throws Exception {

        Set<String[]> set = new HashSet();
        StringBuffer sql = new StringBuffer();

        // 共10个  =  实用10个
        sql.append(" SELECT etbf.id,etbf.encrypt_file_name,etbf.encrypt_path,etbf.express_file_name,etbf.express_path,etbf.bid_id,etbf.crypt_count  ");
        sql.append(" from ecp_t_bid_file etbf   ");
        sql.append(" where etbf.file_stuta = '-1' ");
        sql.append(" and etbf.file_type = '").append(type).append("'");
        sql.append(this.appendWhere);
        sql.append(" LIMIT ").append(rowNum);
        System.out.print("当前检查大文件sql:" + sql.toString() + "---->");
        logger.debug("当前检查大文件sql:" + sql.toString() + "---->");

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        PreparedStatement updateStatement = null;

        try {
            conn = DecryptFactory.getConnection();

            st = conn.createStatement();
            st.setQueryTimeout(120); // 超时120秒
            rs = st.executeQuery(sql.toString());

            // 生产线程在装载数据时对数据状态的更新 ==> 正在解密
            updateStatement = updateFileStatement(conn);

            logger.debug(LogUtil.printMessage("正在组装大文件数据"));
            logger.debug(LogUtil.printMessage("更新解密状态->正在解密"));
            while (rs.next()) {
                String dataArray[] = new String[7];
                dataArray[0] = rs.getString(1); // 主键
                dataArray[1] = rs.getString(2); // 加密文件名
                dataArray[2] = rs.getString(3); // 加密文件路径
                dataArray[3] = rs.getString(4); // 明文件名
                dataArray[4] = rs.getString(5); // 明文路径
                dataArray[5] = rs.getString(6); // 投标ID
                dataArray[6] = rs.getString(7); // 解密次数

                // update
                updateStatement.setString(1, "0");
                updateStatement.setString(2, "");
                updateStatement.setString(3, "0");
                updateStatement.setString(4, dataArray[0]);
                updateStatement.execute();

                set.add(dataArray);
            }
        } catch (Exception e) {
            logger.error(LogUtil.printMessage("生产线程装载数据时异常"));
            throw e;
        } finally {
            try {
                this.close(st, rs);
                if (updateStatement != null) {
                    updateStatement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                logger.error(LogUtil.getException(e));
            }
        }
        return set;
    }

    private void close(Statement st, ResultSet rs) {

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error(LogUtil.getException(e));
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                logger.error(LogUtil.getException(e));
            }
        }
    }
}
