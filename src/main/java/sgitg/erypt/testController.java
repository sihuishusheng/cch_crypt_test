package sgitg.erypt;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sgitg.erypt.cepri.CAEncrypt;
import sgitg.erypt.cepri.CAFactory;
import sgitg.erypt.keytest.TestSM2GenKeyFunc;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;

/**
 * 密码机验证
 * Created by DELL on 2017/4/14.
 */
@RestController
@RequestMapping("/jiami")
public class testController {
    static Logger logger = Logger.getLogger(testController.class);

    public static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static String toHexString(byte[] data)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            if ((i % 8 == 0) && (i != 0)) {
                sb.append(" ");
            }
            if ((i % 16 == 0) && (i != 0)) {
                sb.append("\n");
            }
            if (i % 16 == 0) {
                sb.append(to8HexString(i));
                sb.append("  ");
            }
            sb.append(byte2hex(data[i]));
            sb.append(" ");
        }

        return sb.toString();
    }

    private static String to8HexString(int n) {
        StringBuffer sb = new StringBuffer(Integer.toHexString(n));
        while (sb.length() < 8) {
            sb.insert(0, "0");
        }
        sb.append("h");

        return sb.toString();
    }

    private static String byte2hex(byte n)
    {
        String str = "";

        str = str + HEX[((n & 0xF0) >> 4)];
        str = str + HEX[(n & 0xF)];

        return str;
    }
    @RequestMapping("/test")
    public String test() {
//        TestSM2GenKeyFunc testSM2GenKeyFunc= new TestSM2GenKeyFunc();
//        KeyPair kp = testSM2GenKeyFunc.testGenExternalKey();
//        PrivateKey privateKey = kp.getPrivate();
//        PublicKey publicKey = kp.getPublic();
//        Signature signature = null;
//
//        byte[] dataInput = "ALICE123@YAHOO.COM".getBytes();
//        dataInput = "hello world".getBytes();
//        List alg = new ArrayList();
//        alg.add("SHA1WithSM2");
//        alg.add("SHA224WithSM2");
//        alg.add("SHA256WithSM2");
//        alg.add("SM3WithSM2");
//        System.out.println("原文数据: ");
//        toHexString(dataInput);
//        try {
//            for (int i = 0; i < alg.size(); i++) {
//                System.out.println("签名算法[ " + (String)alg.get(i) + " ]");
//                signature = Signature.getInstance((String)alg.get(i), "SwxaJCE");
//
//                signature.initSign(privateKey);
//                signature.update(dataInput);
//                byte[] out = signature.sign();
//
//                System.out.println("签名结果: ");
//                toHexString(out);
//
//                signature.initVerify(publicKey);
//                signature.update(dataInput);
//                boolean flag = signature.verify(out);
//
//                System.out.println("验证结果: " + flag);
//                System.out.println();
//            }
            return "连接成功";
//        } catch (Exception e) {
//
//            e.printStackTrace();
//            return  "连接失败";
//        }
    }
}
