package sgitg.erypt.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;


import com.mchange.v2.c3p0.DataSources;
import sgitg.erypt.util.LogUtil;
import sgitg.erypt.util.XmlUtil;

/**
 * Created by DELL on 2017/4/13.
 */
public class ConnectionPool {

    private static Logger logger = Logger.getLogger(ConnectionPool.class);

    private static ConnectionPool cp;
    private static DataSource dataSource = null;

    private ConnectionPool() {
    }

    public static ConnectionPool getInstance() {
        if (cp == null) {
            cp = new ConnectionPool();
        }
        return cp;
    }

    static {

        String str[] = XmlUtil.dbElements();
        try {
            Class.forName(str[2]);
        } catch (ClassNotFoundException e) {
            logger.error(LogUtil.printMessage("加载数据库连接驱动失败"));
        }

        Map overrides = XmlUtil.cpElements();

        DataSource ds_unpooled = null;
        try {
            ds_unpooled = DataSources
                    .unpooledDataSource(str[3], str[0], str[1]);

            dataSource = DataSources.pooledDataSource(ds_unpooled, overrides);

        } catch (Exception e) {
            logger.error(LogUtil.printMessage("创建连接池失败"));
        }

    }

    /**
     * 获取连接池链接
     *
     * @return
     */
    public static Connection getConnection() throws SQLException {

        Connection conn = dataSource.getConnection();

        return conn;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Connection conn = ConnectionPool.getConnection();

            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("select sysdate from dual");
            while (rs.next()) {
                Date date = rs.getDate(1);
                System.out.println(date);
            }
            stmt.close();
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
