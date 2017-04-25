package sgitg.erypt.test.dao;

import sgitg.erypt.ConnectionException;
import sgitg.erypt.test.entity.Bid;
import sgitg.erypt.test.entity.Crypt_file;
import sgitg.erypt.test.entity.Good_price;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by DELL on 2017/4/18.
 */
public interface Test_dbDao {

    /**
     * 新增数据
     *
     * @param conn
     * @return
     * @throws ConnectionException
     * @throws SQLException
     */
    public void insertBid(Connection conn , Bid bid)
            throws ConnectionException, SQLException;

    /**
     * 新增数据
     *
     * @param conn
     * @return
     * @throws ConnectionException
     * @throws SQLException
     */
    public void insertCrypt_file(Connection conn , Crypt_file crypt_file)
            throws ConnectionException, SQLException;

    /**
     * 新增数据
     *
     * @param conn
     * @return
     * @throws ConnectionException
     * @throws SQLException
     */
    public void insertGood_price(Connection conn , Good_price good_price)
            throws ConnectionException, SQLException;
}
