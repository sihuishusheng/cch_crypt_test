package sgitg.erypt.cepri;


import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.datech.jce.provider.ecc.EcPublicKey;
import com.datech.jce.provider.ecc.Field;
import com.datech.jce.util.encoder.Hex;
import com.dean.asn1.chinese.ChineseObjectIdentifiers;
import com.dean.asn1.pkcs.PKCSObjectIdentifiers;
import com.dean.cert.X509CertificateHolder;
import com.dean.cms.*;
import com.dean.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import com.dean.cms.jcajce.JceKeyTransEnvelopedRecipient;
import com.dean.operator.OperatorCreationException;
import com.dean.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import com.dean.util.Store;
import com.dean.util.encoders.Base64;

/**
 * Created by DELL on 2017/4/13.
 */
public class DTCMSData {
    private static boolean DatechCryptoVerify_SM2(byte[] data,byte[] sign,byte[] pubKey) throws BidCaException
    {
        boolean result = false;
        byte[] x = new byte[32];
        byte[] y = new byte[32];
        PublicKey publicKey = null;
        if (pubKey[0] == 0x04)
        {
            System.arraycopy(pubKey,1,x,0,32);
            System.arraycopy(pubKey,33,y,0,32);
            publicKey = DTCMSUtil.getSM2PublicKey(x,y);

            try
            {
                Signature sSig = Signature.getInstance( "SM3/SM2", "DatechCrypto");
                sSig.initVerify(publicKey);
                sSig.update(data);
                result=sSig.verify(sign);
                return result;
            }
            catch (InvalidKeyException e)
            {
                throw new BidCaException(e,DTCMSConst.InvalidKeyError,"签名密钥无效");
            }
            catch (SignatureException e)
            {
                throw new BidCaException(e,DTCMSConst.SignedData,"签名数据错误");
            }
            catch (NoSuchAlgorithmException e)
            {
                throw new BidCaException(e,DTCMSConst.SignatureAlgorithm,"无效签名算法");
            }
            catch (NoSuchProviderException e)
            {
                throw new BidCaException(e,DTCMSConst.NoSuchProvider,"无效加密服务提供者");
            }
        }
        return result;
    }
    private static boolean verifySignatures(CMSSignedDataParser sp, byte[] contentDigest) throws BidCaException
    {
        boolean result = false;
        Store certStore = null;
        Store   crlStore = null;
        SignerInformationStore signers = null;
        FileOutputStream pf = null;

        try
        {
            certStore = sp.getCertificates();
            crlStore = sp.getCRLs();
            signers = sp.getSignerInfos();

            Collection c = signers.getSigners();
            Iterator it = c.iterator();

            while (it.hasNext())
            {
                SignerInformation signer = (SignerInformation)it.next();
                Collection          certCollection = certStore.getMatches(signer.getSID());

                Iterator        certIt = certCollection.iterator();
                X509CertificateHolder cert = (X509CertificateHolder)certIt.next();

                //-----------------------------------------------------------------------------------------------------
                //signerInfoVerifierBuilder
                if ((signer.getDigestAlgorithmID().getAlgorithm().equals(ChineseObjectIdentifiers.DigestSm3))
                        &&(signer.getEncryptionAlgOID().compareTo("1.2.156.10197.1.501") == 0))
                {
                    //----------------------------------------------------------------------
                    //sm3运算
                    byte[] pubKey = cert.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
                    byte[] sign = signer.getSignature();
                    result = DatechCryptoVerify_SM2(contentDigest,sign,pubKey);
                }
                else if (signer.getEncryptionAlgOID().compareTo(PKCSObjectIdentifiers.rsaEncryption.getId())==0)
                {
                    try
                    {
                        result = signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("DatechCrypto").build(cert));
                    }
                    catch (OperatorCreationException e)
                    {
                        throw new BidCaException(e,DTCMSConst.VerifySign,"验证过程错误");
                    }
                    catch (CertificateException e)
                    {
                        throw new BidCaException(e,DTCMSConst.CertificateError,"解析证书错误");
                    }
                }
            }
        }
        catch (CMSException e)
        {
            throw new BidCaException(e,DTCMSConst.GetSignerCertificateError,"获得签名者证书的X509Certificate编码失败");
        }
        return result;
    }

    /**解密文件
     * @param srcFile 临时文件夹
     * @param desFile  临时文件夹
     * @param sizeByte 0
     * @param keySet
     * @return
     * @throws BidCaException
     */
    public static byte[] OpenEnvlopFile(String srcFile,String desFile,int sizeByte,Map<String,String> keySet) throws BidCaException
    {
        boolean result = false;
        CMSTypedStream recData = null;
        KeyTransRecipientId reciptId = null;
        KeyPair kr = null;
        int keyNumber = 0;

        CMSEnvelopedDataParser ep = null;
        try
        {
            byte[] bOut = DTCMSUtil.getFileContent(srcFile);
            //数据信封解析
            ep = new CMSEnvelopedDataParser(bOut);
        }
        catch (CMSException e1)
        {
            throw new BidCaException(e1,DTCMSConst.AbstractDigitalEnvelope,"解析数字信封数据错误");
        }
        catch (IOException e1)
        {
            throw new BidCaException(e1,DTCMSConst.Getdigitalenvelope,"从文件中获取数字信封数据错误");
        }

        RecipientInformationStore  recipients = ep.getRecipientInfos();

        Collection  c = recipients.getRecipients();
        Iterator    it = c.iterator();

        while (it.hasNext())
        {
            RecipientInformation   recipient = (RecipientInformation)it.next();
            reciptId = (KeyTransRecipientId) ((KeyTransRecipientInformation)recipient).getRID();
            if (recipient.getKeyEncryptionAlgOID().equals(PKCSObjectIdentifiers.rsaEncryption.getId()))
            {
                keyNumber = DTCMSUtil.getKeyNumber(reciptId, keySet);
                kr = DTCMSUtil.genPublicKeyPair(1, keyNumber);
            }
            else if (recipient.getKeyEncryptionAlgOID().equals(ChineseObjectIdentifiers.ASymmSm2_3.getId()))
            {
                keyNumber = DTCMSUtil.getKeyNumber(reciptId, keySet);
                kr = DTCMSUtil.genPublicKeyPair(2, keyNumber);
            }
            try
            {
                recData = recipient.getContentStream(new JceKeyTransEnvelopedRecipient(kr.getPrivate()).setProvider("DatechCrypto"));
                if (recData != null)
                {
                    try
                    {
                        ep.close();
                        FileOutputStream pf = new FileOutputStream(desFile);
                        InputStream in= recData.getContentStream();
                        int len = -1;
                        byte[] chStr = new byte[8912];
                        while ((len = in.read(chStr,0,8912)) >= 0)
                        {
                            pf.write(chStr,0,len);
                        }
                        pf.close();
                    }
                    catch (IOException e)
                    {
                        throw new BidCaException(e,DTCMSConst.WriteFlie,"写入文件信息失败");
                    }
                    return null;
                }
            }
            catch (CMSException e)
            {
                throw new BidCaException(e,DTCMSConst.GetDataError,"获取明文数据错误");
            }
            catch (IOException e)
            {
                throw new BidCaException(e,DTCMSConst.ReadFile,"读取文件信息失败");
            }
        }
        return null;
    }

    /**
     * @param signFile 带签名的文件路径
     * @param dataFile  原始文件路径
     * @return
     * @throws BidCaException
     */
    public static boolean VerifySignFile(String signFile,String dataFile) throws BidCaException
    {
        CMSSignedDataParser sp = null;
        try
        {
            boolean result = false;
            byte[] bOut = DTCMSUtil.getFileContent(signFile);

            sp = new CMSSignedDataParser(new JcaDigestCalculatorProviderBuilder().setProvider("DatechCrypto").build(),bOut);
            CMSTypedStream cmsStm = sp.getSignedContent();
            InputStream cntStm = cmsStm.getContentStream();

            ByteArrayOutputStream cntByteStm = new ByteArrayOutputStream();
            FileOutputStream pf = new FileOutputStream(dataFile);

            if (cntStm != null)
            {
                int len = -1;
                byte[] chStr = new byte[8912];
                while ((len = cntStm.read(chStr,0,8912)) >= 0)
                {
                    cntByteStm.write(chStr,0,len);
                    pf.write(chStr,0,len);
                }
                cntByteStm.close();
                pf.close();
            }
            cmsStm.drain();
            result = verifySignatures(sp,cntByteStm.toByteArray());
            return result;
        }
        catch (OperatorCreationException e1)
        {
            throw new BidCaException(e1,DTCMSConst.HashAlgorithm,"Hash算法不正确");
        }
        catch (CMSException e1)
        {
            throw new BidCaException(e1,DTCMSConst.AnalysisError,"解析签名文件失败");
        }
        catch (Exception e)
        {
            throw new BidCaException(e,DTCMSConst.WriteFlie,"写入文件信息失败");
        }
    }
    public static byte[] OpenEnvlopData(byte[] base64EnvlopData,Map<String,String> keySet) throws BidCaException
    {
        byte[] envlopData = Base64.decode(new String(base64EnvlopData));
        CMSTypedStream recData = null;
        KeyPair kr = null;
        KeyTransRecipientId reciptId = null;

        int keyNumber = 0;

        CMSEnvelopedData data = null;
        try
        {
            data = new CMSEnvelopedData(envlopData);
        }
        catch (CMSException e)
        {
            throw new BidCaException(e,DTCMSConst.AbstractDigitalEnvelope,"解析数字信封数据错误");
        }

        RecipientInformationStore recipientInformationStore = data.getRecipientInfos();
        Collection collection = recipientInformationStore.getRecipients();
        for (Iterator iterator = collection.iterator(); iterator.hasNext(); )
        {
            RecipientInformation next = (RecipientInformation) iterator.next();
            reciptId = (KeyTransRecipientId) ((KeyTransRecipientInformation)next).getRID();

            try
            {
                if (next != null)
                {
                    if (next.getKeyEncryptionAlgOID().equals(PKCSObjectIdentifiers.rsaEncryption.getId()))
                    {
                        keyNumber = DTCMSUtil.getKeyNumber(reciptId, keySet);

                        kr = DTCMSUtil.genPublicKeyPair(1, keyNumber);
                    }
                    else if (next.getKeyEncryptionAlgOID().equals(ChineseObjectIdentifiers.ASymmSm2_3.getId()))
                    {
                        keyNumber = DTCMSUtil.getKeyNumber(reciptId, keySet);

                        kr = DTCMSUtil.genPublicKeyPair(2, keyNumber);
                    }

                    recData = next.getContentStream(new JceKeyTransEnvelopedRecipient(kr.getPrivate()).setProvider("DatechCrypto"));
                    return DTCMSUtil.streamToByteArray(recData.getContentStream());
                }
            }
            catch (CMSException e)
            {
                throw new BidCaException(e,DTCMSConst.recipientError,"没有接受者信息");
            }
            catch (IOException e)
            {
                throw new BidCaException(e,DTCMSConst.GetDataError,"获取用户数据错误");
            }
        }
        return null;
    }
    public static boolean VerifySignData(byte[] data,byte[] base64SignData) throws BidCaException
    {
        boolean result = false;
        byte[] sign = Base64.decode(new String(base64SignData));

        CMSSignedData signature = null;
        CMSSignedDataParser sp = null;
        try
        {
            signature = new CMSSignedData(sign);
            sp = new CMSSignedDataParser(
                    new JcaDigestCalculatorProviderBuilder().setProvider("DatechCrypto").build(),
                    new CMSTypedStream(new ByteArrayInputStream(data)),
                    signature.getEncoded());

            sp.getSignedContent().drain();
            return verifySignatures(sp,data);
        }
        catch (OperatorCreationException e)
        {
            throw new BidCaException(e,DTCMSConst.HashAlgorithm,"Hash算法不正确");
        }
        catch (CMSException e)
        {
            throw new BidCaException(e,DTCMSConst.AnalysisError,"解析签名文件失败");
        }
        catch (IOException e)
        {
            throw new BidCaException(e,DTCMSConst.ReadFile,"读取信息失败");
        }
        catch (Exception e)
        {
            throw new BidCaException(e,DTCMSConst.VerifySign,"验证签名错误");
        }
    }
    public static boolean VerifySignDataAttached(byte[]signData) throws BidCaException
    {
        boolean result = false;
        byte[] sign = Base64.decode(new String(signData));

        CMSSignedDataParser sp = null;
        try
        {
            sp = new CMSSignedDataParser(new JcaDigestCalculatorProviderBuilder().setProvider("DatechCrypto").build(),sign);
            CMSTypedStream cmsStm = sp.getSignedContent();
            InputStream cntStm = cmsStm.getContentStream();

            ByteArrayOutputStream cntByteStm = new ByteArrayOutputStream();
            if (cntStm != null)
            {
                int len = -1;
                byte[] chStr = new byte[8912];
                while ((len = cntStm.read(chStr,0,8912)) >= 0)
                {
                    cntByteStm.write(chStr,0,len);
                }
                cntByteStm.close();
            }
            sp.getSignedContent().drain();
            return verifySignatures(sp,cntByteStm.toByteArray());
        }
        catch (OperatorCreationException e)
        {
            throw new BidCaException(e,DTCMSConst.HashAlgorithm,"Hash算法不正确");
        }
        catch (CMSException e)
        {
            throw new BidCaException(e,DTCMSConst.AnalysisError,"解析签名文件失败");
        }
        catch (IOException e)
        {
            throw new BidCaException(e,DTCMSConst.AbstractData,"获取摘要数据失败");
        }
        catch (Exception e)
        {
            throw new BidCaException(e,DTCMSConst.VerifySign,"验证签名错误");
        }
    }
}
