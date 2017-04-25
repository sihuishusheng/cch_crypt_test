package sgitg.erypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sgitg.erypt.dao.DecryptFactory;
import sgitg.erypt.test.dao.Test_dbDao;
import sgitg.erypt.test.entity.Bid;
import sgitg.erypt.test.entity.Crypt_file;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by DELL on 2017/4/18.
 */
@RestController
@RequestMapping("/db")
public class dbTestController {

    @RequestMapping("/bid")
    public String insertBid() {
        Test_dbDao testDaoHibernate =DecryptFactory.getTESTdbDao();
        Crypt_file cf =new Crypt_file();
        cf.setBid_id(1);
        cf.setEncrypt_file_name("");
        cf.setEncrypt_path("");
        cf.setExpress_file_name("");
        cf.setExpress_path("");
//        cf.setFile_size(null);
        cf.setFile_type("");
        cf.setFile_stuta("");

        Connection conn = null;

        try {
            conn = DecryptFactory.getConnection();
            testDaoHibernate.insertCrypt_file(conn,cf);
        } catch (SQLException e) {
            e.printStackTrace();
            return  "操作失败";

        } catch (ConnectionException ce){
            ce.printStackTrace();
            return  "操作失败";

        }finally {
            if(conn != null){
                try {
                    conn.close();
                } catch (SQLException e) {
                 e.printStackTrace();
                }
            }
        }
        return  "操作成功";
    }
}
