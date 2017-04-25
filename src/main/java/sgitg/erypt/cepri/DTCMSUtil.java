package sgitg.erypt.cepri;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Map;

import com.datech.jce.provider.ecc.EcPublicKey;
import com.datech.jce.provider.ecc.Field;
import com.datech.jce.util.encoder.Hex;
import com.dean.cms.KeyTransRecipientId;
import com.dean.util.encoders.Base64;

/**
 * Created by DELL on 2017/4/13.
 */
public class DTCMSUtil {

    public static int getKeyNumber(KeyTransRecipientId transKeyId, Map<String,String> keySet) throws BidCaException
    {
        String id = null;
        String tmpId = null;
        byte[] keyId = null;
        BigInteger sn = transKeyId.getSerialNumber();
        if (sn != null)
        {
            id= new String(Hex.encode(sn.toByteArray()));
            tmpId = keySet.get(id);
        }
        if (tmpId == null)
        {
            keyId = transKeyId.getSubjectKeyIdentifier();
            if (keyId != null)
                id = new String(Hex.encode(keyId));
            tmpId = keySet.get(id);
        }
        if (tmpId != null)
        {
            int keyNumber = Integer.valueOf(tmpId);
            return keyNumber;
        }
        throw new BidCaException(DTCMSConst.GetReciptientNumberError,"无法找到相应的密钥ID:"+id);
    }
    public static KeyPair genPublicKeyPair(int keyType, int keyNumber) throws BidCaException
    {
        SecureRandom secureRandom = null;
        KeyPairGenerator pg = null;
        KeyPair kr = null;

        try
        {
            if (keyType == 1)
            {
                secureRandom = SecureRandom.getInstance("RsaKey"+keyNumber,"DatechCrypto");
                pg = KeyPairGenerator.getInstance("RSA","DatechCrypto");
                pg.initialize(1024, secureRandom);
            }
            if (keyType == 2)
            {
                secureRandom = SecureRandom.getInstance("EccKey"+keyNumber,"DatechCrypto");
                pg = KeyPairGenerator.getInstance("SM2","DatechCrypto");
                pg.initialize(7, secureRandom);
            }

        }
        catch (NoSuchAlgorithmException e1)
        {
            throw new BidCaException(e1,DTCMSConst.RandomError,"不支持生随机数算法类型");
        }
        catch (NoSuchProviderException e1)
        {
            throw new BidCaException(e1,DTCMSConst.JceError,"密码服务提供者未提供");
        }
        try
        {
            kr = pg.genKeyPair();
            if (kr == null)
            {
                return null;
            }
            return kr;
        }
        catch (Exception e)
        {
            throw new BidCaException(e,DTCMSConst.GenKeyError,"生成加密机密钥错误");
        }
    }
    public static byte[] streamToByteArray(InputStream in) throws BidCaException
    {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        int ch;
        try
        {
            while ((ch = in.read()) >= 0)
            {
                bOut.write(ch);
            }
        }
        catch (IOException e)
        {
            throw new BidCaException(e,DTCMSConst.GetDataError,"获取消息数据错误");
        }
        return bOut.toByteArray();
    }
    public static byte[] getFileContent(String signFile) throws BidCaException
    {
        byte[] bOut = null;
        byte[] tmpOut = null;

        try
        {
            FileInputStream pf = new FileInputStream(signFile);
            int len;
            len = pf.available();
            bOut = new byte[len];
            pf.read(bOut);
            pf.close();
            if(bOut[0]!=0x30)
            {
                tmpOut = Base64.decode(bOut);
                return tmpOut;
            }
            else
            {
                return bOut;
            }
        }
        catch (FileNotFoundException e)
        {
            throw new BidCaException(e,DTCMSConst.FileNotFound,"待原始文件不存在");
        }
        catch (IOException e)
        {
            throw new BidCaException(e,DTCMSConst.ReadFile,"读取文件信息失败");
        }
    }
    public static PublicKey getSM2PublicKey(byte[] qx,byte[] qy) throws BidCaException
    {
        byte[] keypairBytes = new byte[292];
        try
        {
            for(int i = 0;i<292;i++)
            {
                keypairBytes[i] = 0;
            }
            keypairBytes[193] = 1;
            keypairBytes[194] = 7;

            // 密钥参数的byte类型
            byte[] P = new byte[32];
            byte[] a = new byte[32];
            byte[] b = new byte[32];
            byte[] Gx = new byte[32];
            byte[] Gy = new byte[32];
            byte[] N = new byte[32];
            byte[] paraLen = new byte[2];
            byte[] curveType = new byte[2];

            byte[] d = new byte[32];

            //将产生的密钥参数转换为大数，以标准输出
            int mLen = 32;
            //将密钥对分解
            System.arraycopy(keypairBytes,0,P,0,mLen);
            System.arraycopy(keypairBytes,mLen,a,0,mLen);
            System.arraycopy(keypairBytes,mLen*2,b,0,mLen);
            System.arraycopy(keypairBytes,mLen*3,Gx,0,mLen);
            System.arraycopy(keypairBytes,mLen*4,Gy,0,mLen);
            System.arraycopy(keypairBytes,mLen*5,N,0,mLen);
            System.arraycopy(keypairBytes,mLen*6,paraLen,0,2);
            System.arraycopy(keypairBytes,mLen*6+2,curveType,0,2);

            System.arraycopy(keypairBytes,mLen*8+4,d,0,mLen);
            int h= 256;//BytetointCom(paraLen);


            //将byte转化为大数
            BigInteger PI = new BigInteger(1,P);
            BigInteger aI = new BigInteger(1,a);
            BigInteger bI = new BigInteger(1,b);
            BigInteger GxI = new BigInteger(1,Gx);
            BigInteger GyI = new BigInteger(1,Gy);
            BigInteger NI = new BigInteger(1,N);
            // BigInteger paraLenI = new BigInteger(1,paraLen);
            BigInteger curveTypeI = new BigInteger(1,curveType);


            BigInteger qxI = new BigInteger(1,qx);
            BigInteger qyI = new BigInteger(1,qy);
            BigInteger dI = new BigInteger(1,d);

            Field field = new Field(PI,curveTypeI,7);
            EllipticCurve curve = new EllipticCurve(field,aI,bI,null);
            ECPoint point = new  ECPoint(GxI, GyI);
            NI = BigInteger.valueOf(1L);
            ECParameterSpec ecSpec = new ECParameterSpec(curve,point,NI,h);
            EcPublicKey     pub  = new EcPublicKey(ecSpec,qxI, qyI);
            return pub;
        }
        catch (Exception e)
        {
            throw new BidCaException(e,DTCMSConst.GetPublicKeyError,"获取SM2公钥失败");
        }

    }
}
