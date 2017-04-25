package sgitg.erypt.dao;

import sgitg.erypt.dao.hibernate.DecryptDaoHibernate;
import sgitg.erypt.test.dao.Test_dbDao;
import sgitg.erypt.test.dao.hibernate.Test_DbDaoHibernate;
import sgitg.erypt.util.DecryptConstant;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by DELL on 2017/4/13.
 */
public class DecryptFactory {

    public static DecryptDao getDecryptDao() {
        return new DecryptDaoHibernate();
    }

    public static Test_dbDao getTESTdbDao() {return new Test_DbDaoHibernate();}

    public static Connection getConnection() throws SQLException {

        if (DecryptConstant.POOL_USED) {
            return ConnectionPool.getConnection();
        } else {
            DBConnection db = new JdbcConnection();
            return db.getConnection();
        }

    }

}