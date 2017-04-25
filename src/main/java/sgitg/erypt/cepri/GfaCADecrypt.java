package sgitg.erypt.cepri;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import org.apache.log4j.Logger;
import sgitg.erypt.util.LogUtil;

import java.util.List;
import java.util.Map;


/**
 * Created by DELL on 2017/4/13.
 */
public class GfaCADecrypt implements CADecrypt {
    private Logger logger = Logger.getLogger(GfaCADecrypt.class);

    private static CADecrypt caDecrypt = null;

    public static CADecrypt getInstance() throws BidCaException {
        if (caDecrypt == null) {
            caDecrypt = new GfaCADecrypt();
        }
        return caDecrypt;
    }

    public void init() throws BidCaException {

    }

    public String verifyMessageSignAttached(String messageSignAttached)
            throws BidCaException {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean verifyMessgeSignDetached(String sourceData, String signData,String charset)
            throws BidCaException {
        boolean b = false;
        try {
            if(charset!=null){
                b = DTCMSData.VerifySignData(sourceData.getBytes(charset), signData.getBytes());
            }else{
                b = DTCMSData.VerifySignData(sourceData.getBytes(), signData.getBytes());
            }
        } catch (BidCaException e) {
            logger.error("",e);
            logger.debug(LogUtil.getException(e));
            throw e;
        }catch (Exception e) {
            logger.error("",e);
            logger.debug(LogUtil.getException(e));
            throw new BidCaException(e);
        }
        if(!b){
            logger.info("verifyMessgeSignDetached false:\nsourceData:"+sourceData+",signData:"+signData);
        }
        return b;

    }

    public byte[] verifyMessgeSignDetachedFetchCert(String sourceData,
                                                    String signData) throws BidCaException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 验证带签名的文件
     * @param signAttachedFilePath 带签名的文件路径
     * @param sourceFilePath 原始文件路径
     * @return
     * @throws BidCaException
     */
    public boolean verifyFileSignAttached(String signAttachedFilePath,
                                          String sourceFilePath) throws BidCaException {
        boolean b = false;
        try {
            //signAttachedFilePath 带签名的文件路径   sourceFilePath 原始文件路径
            b = DTCMSData.VerifySignFile(signAttachedFilePath, sourceFilePath);
        } catch (BidCaException e) {
            logger.error("验证文件签名失败："+signAttachedFilePath,e);
            logger.debug(LogUtil.getException(e));
            throw e;
        }catch (Exception e) {
            logger.error("验证文件签名失败："+signAttachedFilePath,e);
            logger.debug(LogUtil.getException(e));
            throw new BidCaException(e);
        }
        return b;
    }

    public byte[] verifyFileSignAttachedFetchCert(String signAttachedFilePath,
                                                  String sourceFilePath) throws BidCaException {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean verifyFileSignDetached(String filePath, String signData)
            throws BidCaException {
        return false;
    }

    public byte[] verifyFileSignDetachedFetchCert(String filePath,
                                                  String singnData) throws BidCaException {
        // TODO Auto-generated method stub
        return null;
    }

    public String decryptMessageEnvelop(String messageEnvelop, String pkPostion)
            throws BidCaException {
        // TODO Auto-generated method stub
        return null;
    }

    public String decryptMessageEnvelop(String messageEnvelop,
                                        Map<String, String> pkMap,List<String> charset) throws BidCaException {
        String sourceData=null;
        String encoding ="UTF-8";
        try {
            byte[] b=DTCMSData.OpenEnvlopData(messageEnvelop.getBytes(), pkMap);

            CharsetDetector detector  =  new CharsetDetector();
            detector.setText(b);
            CharsetMatch match = detector.detect();
            encoding=match.getName();

            logger.debug("encoding="+encoding);
            if(encoding!=null && encoding.startsWith("UTF-8")){
                encoding="UTF-8";
            }else{
                encoding="GBK";
            }
            if(charset!=null){
                charset.add(encoding);
            }

            sourceData=new String(b,encoding);

        } catch (BidCaException e) {
            logger.error("",e);
            logger.debug(LogUtil.getException(e));
            throw e;
        }catch (Exception e) {
            logger.error("",e);
            logger.debug(LogUtil.getException(e));
            throw new BidCaException(e);
        }

        return sourceData;
    }
    public byte[] decryptMessageEnvelop(String messageEnvelop,
                                        Map<String, String> pkMap) throws BidCaException {

        byte[] b=null;
        try {
            b=DTCMSData.OpenEnvlopData(messageEnvelop.getBytes(), pkMap);
        } catch (BidCaException e) {
            logger.error("",e);
            logger.debug(LogUtil.getException(e));
            throw e;
        }catch (Exception e) {
            logger.error("",e);
            logger.debug(LogUtil.getException(e));
            throw new BidCaException(e);
        }

        return b;
    }
    public void decryptFileEnvelop(String envelopeFilePath,
                                   String plainFilePath, String pkPostion) throws BidCaException {
        // TODO Auto-generated method stub
    }

    /* 解密
     * @see com.cepri.bid.lb.cmm.ca.CADecrypt#decryptFileEnvelop(java.lang.String, java.lang.String, java.util.Map)
     */
    public void decryptFileEnvelop(String envelopeFilePath,
                                   String plainFilePath, Map<String, String> pkMap)
            throws BidCaException {
        try {
            DTCMSData.OpenEnvlopFile(envelopeFilePath, plainFilePath, 0, pkMap);
        } catch (BidCaException e) {
            logger.error("解密失败："+envelopeFilePath,e);
            logger.debug(LogUtil.getException(e));
            throw e;
        }catch (Exception e) {
            logger.error("解密失败："+envelopeFilePath,e);
            logger.debug(LogUtil.getException(e));
            throw new BidCaException(e);
        }

    }

    public String verifyCert(byte[] cert) throws BidCaException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPubCertContent() throws BidCaException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPubCertSerialNumber(String str) throws BidCaException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSignDN(String sourceData, String signData, boolean flag)
            throws BidCaException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSignDN(String signData) throws BidCaException {
        // TODO Auto-generated method stub
        return null;
    }


}
