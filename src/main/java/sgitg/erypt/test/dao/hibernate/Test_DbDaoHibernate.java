package sgitg.erypt.test.dao.hibernate;

import sgitg.erypt.ConnectionException;
import sgitg.erypt.test.dao.Test_dbDao;
import sgitg.erypt.test.entity.Bid;
import sgitg.erypt.test.entity.Crypt_file;
import sgitg.erypt.test.entity.Good_price;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by DELL on 2017/4/18.
 */
public class Test_DbDaoHibernate implements Test_dbDao {

    /**
     * 新增数据
     *
     * @param conn
     * @return
     * @throws ConnectionException
     * @throws SQLException
     */
    public void insertBid(Connection conn , Bid bid)
            throws ConnectionException, SQLException{
        String sql = "INSERT INTO ecp_t_bid (package_id, tol_price, ency_tol_price, signature, encry_stuta, encry_count, encry_remark) VALUES ( ?,?, ?, ?, ?, ?, ?)";
        PreparedStatement ps =conn.prepareStatement(sql);
        ps.setInt(1,bid.getPackage_id());
        ps.setDouble(2,bid.getTol_price());
        ps.setString(3,bid.getSignature());
        ps.setString(4,bid.getEncy_tol_price());
        ps.setString(5,bid.getEncry_stuta());
        ps.setInt(6,bid.getEncry_count());
        ps.setString(7,bid.getEncry_remark());
        ps.execute();

    }

    /**
     * 新增数据
     *
     * @param conn
     * @return
     * @throws ConnectionException
     * @throws SQLException
     */
    public void insertCrypt_file(Connection conn , Crypt_file crypt_file)
            throws ConnectionException, SQLException{
        String sql = "INSERT INTO `ecp_t_bid_file` (`encrypt_path`, `encrypt_file_name`, `express_path`, `express_file_name`, `file_type`, `file_stuta`, `bid_id`, `file_size`) VALUES (?, ?,?,?, ?,?, ?,?);";
        PreparedStatement ps =conn.prepareStatement(sql);
        ps.setString(1,crypt_file.getEncrypt_path());
        ps.setString(2,crypt_file.getEncrypt_file_name());
        ps.setString(3,crypt_file.getExpress_path());
        ps.setString(4,crypt_file.getExpress_file_name());
        ps.setString(5,crypt_file.getFile_type());
        ps.setString(6,crypt_file.getFile_stuta());
        ps.setInt(7,crypt_file.getBid_id());
        ps.setLong(8,crypt_file.getFile_size());

        ps.execute();
    }

    /**
     * 新增数据
     *
     * @param conn
     * @return
     * @throws ConnectionException
     * @throws SQLException
     */
    public void insertGood_price(Connection conn , Good_price good_price)
            throws ConnectionException, SQLException{
        String sql = "INSERT INTO `ecp_t_bid_good_price` (`bid_id`, `package_id`, `good_id`, `good_price`, `encry_price`, `signture`, `encry_statu`, `encry_count`, `encry_remark`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps =conn.prepareStatement(sql);
        ps.setInt(1,good_price.getBid_id());
        ps.setInt(2,good_price.getPackage_id());
        ps.setInt(3,good_price.getGood_id());
        ps.setDouble(4,good_price.getGood_price());
        ps.setString(5,good_price.getEncy_price());
        ps.setString(6,good_price.getSignature());
        ps.setString(7,good_price.getEncry_stuta());
        ps.setInt(8,good_price.getEncry_count());
        ps.setString(9,good_price.getEncry_remark());
        ps.execute();
    }
}
