package sgitg.erypt.swxa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import com.sansec.asn1.DERBitString;
import com.sansec.asn1.DERNull;
import com.sansec.asn1.pkcs.GBObjectIdentifiers;
import com.sansec.asn1.pkcs.PKCSObjectIdentifiers;
import com.sansec.asn1.pkcs.PrivateKeyInfo;
import com.sansec.asn1.pkcs.RSAPrivateKeyStructure;
import com.sansec.asn1.pkcs.SM2PrivateKeyStructure;
import com.sansec.asn1.x509.AlgorithmIdentifier;


public class Util {
    public static final String provider = "SwxaJCE";
    /**
     * 使用绝对路径解析RSA私钥
     * @param fileName
     * @return
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey parseRSAPrivateKey(String fileName) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException{
        File file = new File(fileName);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] tmppriKey = new byte[new Long(file.length()).intValue()];
        inputStream.read(tmppriKey);
        inputStream.close();

        byte[] modulus = new byte[128];
        byte[] pE = new byte[3];
        byte[] d = new byte[128];
        byte[] p = new byte[64];
        byte[] q = new byte[64];
        byte[] p1 = new byte[64];
        byte[] q1 = new byte[64];
        byte[] coef = new byte[64];
        int offset = 4;
        offset += 8*16;
        System.arraycopy(tmppriKey, offset, modulus, 0, 128);
        offset += 128;

        BigInteger bN = new BigInteger(1, modulus);

        offset += 256 -3;
        System.arraycopy(tmppriKey, offset, pE, 0, 3);
        offset += 3;

        BigInteger bE = new BigInteger(1, pE);

        offset += 128;
        System.arraycopy(tmppriKey, offset, d, 0, 128);
        offset += 128;

        BigInteger bD = new BigInteger(1, d);

        offset += 64;
        System.arraycopy(tmppriKey, offset, p, 0, 64);
        offset += 64;

        BigInteger bP = new BigInteger(1, p);

        offset += 64;
        System.arraycopy(tmppriKey, offset, q, 0, 64);
        offset += 64;
        BigInteger bQ = new BigInteger(1, q);

        offset += 64;
        System.arraycopy(tmppriKey, offset, p1, 0, 64);
        offset += 64;
        BigInteger bP1 = new BigInteger(1, p1);

        offset += 64;
        System.arraycopy(tmppriKey, offset, q1, 0, 64);
        offset += 64;
        BigInteger bQ1 = new BigInteger(1, q1);

        offset += 64;
        System.arraycopy(tmppriKey, offset, coef, 0, 64);
        offset += 64;
        BigInteger bCoef = new BigInteger(1, coef);

        RSAPrivateKeyStructure structure = new RSAPrivateKeyStructure(bN,bE,bD,bP,bQ,bP1,bQ1,bCoef);
        PrivateKeyInfo          info = new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, new DERNull()), structure.getDERObject());


        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(info.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", provider);
        PrivateKey key = keyFactory.generatePrivate(keySpec);

        return key;
    }

    public static PrivateKey parseSM2PrivateKey(String pubKeyFile,String priKeyFile) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException{
        File file = new File(priKeyFile);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] tmppriKey = new byte[new Long(file.length()).intValue()];
        inputStream.read(tmppriKey);
        inputStream.close();

        byte[] tmpkey  = new byte[32];
        System.arraycopy(tmppriKey, 4, tmpkey, 0, 32);
        BigInteger s = new BigInteger(tmpkey);

        //公钥
        file = new File(pubKeyFile);
        inputStream = new FileInputStream(file);
        byte[] exPubKey = new byte[new Long(file.length()).intValue()];
        inputStream.read(exPubKey);
        inputStream.close();
        byte[] pubKey = new byte[65];
        pubKey[0] = 0x04;
        System.arraycopy(exPubKey, 4, pubKey, 1, 64);

        DERBitString pKey = new DERBitString(pubKey);

        SM2PrivateKeyStructure sm2Structure = new SM2PrivateKeyStructure(s, pKey, null);

        PrivateKeyInfo info = new PrivateKeyInfo(new AlgorithmIdentifier(/*PKCSObjectIdentifiers.ecPublicKey,*/GBObjectIdentifiers.sm2), sm2Structure.getDERObject());

//		PrintUtil.printWithHex(info.getEncoded());


        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(info.getEncoded());
        KeyFactory factory = KeyFactory.getInstance("SM2", provider);
        PrivateKey key = factory.generatePrivate(keySpec);

        return key;
    }


    public static Certificate readCertFromFile(String fileName) throws FileNotFoundException, CertificateException, NoSuchProviderException{
        FileInputStream inputStream = new FileInputStream(fileName);
        CertificateFactory certFactory = CertificateFactory.getInstance("X509", provider);
        Certificate cerx509 = (Certificate)certFactory.generateCertificate(inputStream);

        return cerx509;
    }


    public static byte[] readFromFile(String fileName) throws IOException{
        byte[] data = null;
        File file = new File(fileName);
        data = new byte[new Long(file.length()).intValue()];
        FileInputStream in = new FileInputStream(file);

        in.read(data);

        in.close();

        return data;
    }
    public static void writeToFile(String fileName,byte[] data) throws IOException{
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(data);
        out.close();
    }

    /**
     * 导出设备内部指定算法的指定索引的非对称密钥
     * @param index     要导出的密钥索引号
     * @param alg       算法：RSA或者SM2
     * @param isPublic   导出公钥：true。导出私钥：false
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static Key readKeyFromDevice(int index,String alg,boolean isPublic) throws NoSuchAlgorithmException, NoSuchProviderException{
        KeyPairGenerator generator = KeyPairGenerator.getInstance(alg, provider);
        generator.initialize(index<<16);
        KeyPair pair = generator.generateKeyPair();
        if(pair != null){
            if(isPublic)
                return pair.getPublic();
            else
                return pair.getPrivate();
        }
        return null;
    }


}
