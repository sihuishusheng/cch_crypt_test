package sgitg.erypt.dao;


import sgitg.erypt.ConnectionException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

public interface DecryptDao {

    /**
     * 更新大文件 预编译
     *
     * @param conn
     * @return
     * @throws ConnectionException
     * @throws SQLException
     */
    public PreparedStatement updateFileStatement(Connection conn)
            throws ConnectionException, SQLException;

    /**
     * 更新 大文件
     *
     * @param updateFileps
     * @param decryptStatus
     * @param errorCode
     * @param repariCount
     * @param fileId
     * @throws SQLException
     */
    public void updateFile(PreparedStatement updateFileps,
                           String decryptStatus, String errorCode, int repariCount,
                           String fileId) throws SQLException;

    /**
     * 更新总价 预编译
     *
     * @param conn
     * @return
     * @throws ConnectionException
     * @throws SQLException
     */
    public PreparedStatement updatePriceStatement(Connection conn)
            throws ConnectionException, SQLException;

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
                           String bid_id) throws SQLException;

    /**
     * 更新明细总价 预编译
     *
     * @param conn
     * @return
     * @throws ConnectionException
     * @throws SQLException
     */
    public PreparedStatement updateGoodPriceStatement(Connection conn)
            throws ConnectionException, SQLException;

    /**
     * 更新明细
     *
     * @param updateGoodPrice
     * @param price
     * @param bid_id
     * @param pakage_id
     * @param good_id
     * @throws SQLException
     */
    public void updateGoodPrice(PreparedStatement updateGoodPrice,
                            String price,
                            String bid_id,String pakage_id,String good_id) throws SQLException;

    /**
     * 生产解密任务
     */
    @SuppressWarnings("unchecked")
    public Set getDecryptSet(int type) throws Exception;

}