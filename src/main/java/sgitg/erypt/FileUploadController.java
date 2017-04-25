package sgitg.erypt;

import com.sansec.app.PKCS7;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sgitg.erypt.dao.DecryptFactory;
import sgitg.erypt.swxa.Util;
import sgitg.erypt.test.dao.Test_dbDao;
import sgitg.erypt.test.entity.Crypt_file;
import sgitg.erypt.util.DecryptConstant;

import java.io.*;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by DELL on 2017/4/19.
 */
@Controller
public class FileUploadController {

    @RequestMapping("/file")
    public String file(){
        return "/file";
    }

    /**
     * 文件上传具体实现方法;
     * @param file
     * @return
     */
    @RequestMapping("/upload")
    @ResponseBody
    public String handleFileUpload(@RequestParam("file")MultipartFile file,@RequestParam("number")String number)   {
        String fileName = null;
        Connection conn = null;
        if(number==null||number.equals("")||number.trim().equals("")){
            number="5";
        }
        if(!file.isEmpty()){
            try {
				//加密文件方法
                String certName = "cert.cer";
                String encAlg   = "SM4";

                String DIRECTORY = DecryptConstant.DIRECTORY;
                String ENCRY_FILE = DecryptConstant.ENCRY_FILE;
                String EXPRESS_FILE = DecryptConstant.EXPRESS_FILE;

                //读取SM2证书
                X509Certificate cerx509 = (X509Certificate) Util.readCertFromFile(DIRECTORY+certName);

                //读取文件内容
                byte[] fdate = file.getBytes();

                //制作SM2数字信封
                byte[] envelopData =  PKCS7.genEnvelopedData(cerx509, encAlg,fdate);





                int n =Integer.parseInt(number);
                fileName = file.getOriginalFilename();
                String[] f_r=fileName.split("\\.");
                 String newFileName = "";
                String expFileName = "";
                byte[] bytes = file.getBytes();
                for (int i=1;i<=n;i++) {
//                    newFileName =f_r[1]+"_"+i+"."+f_r[1]+"."+f_r[2];
                    newFileName =f_r[0]+"_"+i+"."+f_r[1]+".enc";
                    expFileName=f_r[0]+"_"+i+"."+f_r[1];
                    //写数字信封到文件
                    Util.writeToFile(ENCRY_FILE+newFileName, envelopData);
//插入数据库数据
                    Test_dbDao testDaoHibernate = DecryptFactory.getTESTdbDao();
                    Crypt_file cf =new Crypt_file();
                    cf.setBid_id(1);
                    cf.setEncrypt_file_name(newFileName);
                    cf.setEncrypt_path(ENCRY_FILE);
                    cf.setExpress_file_name(expFileName);
                    cf.setExpress_path(EXPRESS_FILE);
                    cf.setFile_size(file.getSize());
                    cf.setFile_type("3");
                    cf.setFile_stuta("-2");


                    conn = DecryptFactory.getConnection();
                    testDaoHibernate.insertCrypt_file(conn,cf);

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "上传失败,"+e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "上传失败,"+e.getMessage();
            } catch (CertificateException e){
                e.printStackTrace();
                return "上传失败,"+e.getMessage();
            } catch (NumberFormatException e){
                e.printStackTrace();
                return "上传失败,"+e.getMessage();
            } catch (NoSuchProviderException e){
                e.printStackTrace();
                return "上传失败,"+e.getMessage();
            }catch (SQLException e) {
                e.printStackTrace();
                return  "操作失败";

            } catch (ConnectionException ce){
                ce.printStackTrace();
                return  "操作失败";

            }catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if(conn != null){
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return "上传成功";
        }else{
            return "上传失败，因为文件是空的.";
        }
    }
}
