package sgitg.erypt.dao;

import sgitg.erypt.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by DELL on 2017/4/13.
 */
public class JdbcConnection implements DBConnection {

    public Connection getConnection() throws SQLException {
        return DBUtil.getConn();
    }

}
