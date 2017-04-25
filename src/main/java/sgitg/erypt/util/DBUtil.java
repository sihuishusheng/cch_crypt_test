package sgitg.erypt.util;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by DELL on 2017/4/13.
 */
public class DBUtil {
    private static Logger logger = Logger.getLogger(DBUtil.class);

    public static Connection getConn() throws SQLException {

        Connection conn = null;
        String str[] = XmlUtil.dbElements();
        try {
            Class.forName(str[2]);
        } catch (ClassNotFoundException e) {
            logger.fatal(LogUtil.getException(e));
        }
        conn = DriverManager.getConnection(str[3], str[0], str[1]);
        logger.debug("获取数据库连接成功！");
        return conn;
    }
}
