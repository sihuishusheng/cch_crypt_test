package sgitg.erypt.swxa;

import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import com.sansec.app.PKCS7;
import com.sansec.jce.provider.SwxaProvider;
import com.sansec.util.PrintUtil;

public class PKCS7EnvelopedData {
    static final String provider  = "SwxaJCE";
    static final String DIRECTORY = "C:\\express\\";



//    public static void main(String[] args) throws Exception{
//        Security.addProvider( new SwxaProvider());
//        //genRSAEnvelopedData();
//        //decRSAEnvelopedData();
//
////        genSM2EnvelopedData();
////        decSM2EnvelopedData();
//    }



    public static void genSM2EnvelopedData()throws Exception{

        String message  = "Hello world!";
        String efname  = "111.docx";

        String certName = "cert.cer";
        String encAlg   = "SM4";
//        String envName  = "EnvelopedData_SM2_"+encAlg;
        String envName  = efname+".enc";

        //读取SM2证书
        X509Certificate cerx509 = (X509Certificate)Util.readCertFromFile(DIRECTORY+certName);

        //读取文件内容
        byte[] fdate = Util.readFromFile(DIRECTORY+efname);

        //制作SM2数字信封
        byte[] envelopData = PKCS7.genEnvelopedData(cerx509, encAlg,fdate);

        //写数字信封到文件
        Util.writeToFile(DIRECTORY+envName, envelopData);
    }

    public static void decSM2EnvelopedData()throws Exception{
        String envName    =   "111.docx.enc";  //"1111.docx.enc";
//        String envName    =   "EnvelopedData_SM2";
        //String pubKeyName = "pubkey.sm2001";
        //String priKeyName = "prikey.sm2001";
        String certName   = "cert.cer";

        //读取数字信封
        byte[] envelopData = Util.readFromFile(DIRECTORY+envName);

        //从文件读取私钥和公钥合成SM2私钥
//		PrivateKey key = Util.parseSM2PrivateKey(DIRECTORY+pubKeyName,DIRECTORY+priKeyName);
        //从设备内区出私钥，这个私钥存放的是索引号，不含有真正的私钥数据
        PrivateKey key = (PrivateKey)Util.readKeyFromDevice(11, "SM2", false);
        //读取证书文件
//        X509Certificate cerx509 = (X509Certificate)Util.readCertFromFile(DIRECTORY+certName);

        //解密数字信封
        byte[] plain = PKCS7.decEnvelopedData(envelopData, key);

        System.out.println("Data length="+plain.length);
        //PrintUtil.printWithHex(plain);
        Util.writeToFile(DIRECTORY+"jimi_11.docx", plain);
        //System.out.println(new String(plain));
    }


}
