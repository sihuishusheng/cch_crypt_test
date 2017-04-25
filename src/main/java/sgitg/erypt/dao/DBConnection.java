package sgitg.erypt.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by DELL on 2017/4/13.
 */
public interface DBConnection {

    public Connection getConnection() throws SQLException;

}