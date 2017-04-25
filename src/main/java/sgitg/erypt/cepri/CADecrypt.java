package sgitg.erypt.cepri;

/**
 *
 */

import java.util.List;
import java.util.Map;

/**
 * 该方法主要用来在服务端验签、解密<p>
 *
 * <b>对实现类的要求：</b><br>
 * 1.实现类应多线程安全<br>
 * 2.实现类应该提供静态的方法 public static CADecrypt getInstance() throws BidCaException;隐藏构造函数<br>
 *
 * <p><b>基本的使用方式:</b><br>
 * 线程中调用实现类的getInstance() -> init() -> 调用验签、解密方法（可以多次调用）-> 线程结束
 *
 * <p><b>名词解释</b>：<br>
 * 非分离签名(Attached)- 签名信息附加在原始消息中<br>
 * 分离式签名(Detached)- 签名信息独立于原始消息<br>
 *
 * Created by DELL on 2017/4/13.
 *
 */
public interface CADecrypt {

    /**
     * 得到实例后首先调用该方法，且只调用一次
     * 该方法的实现可以为空
     * @throws BidCaException
     */
    public void init() throws BidCaException;

    /**
     * 验证字符串消息签名
     * @param sourceData 原始字符串
     * @param signData 签名信息
     * @param charSet sourceData的编码
     * @return
     * @throws BidCaException
     */
    public boolean verifyMessgeSignDetached(String sourceData, String signData, String charSet) throws BidCaException;
    /**
     * 验证字符串消息签名并返回签名的公钥证书
     * @param sourceData 原始字符串
     * @param signData 签名信息
     * @return 公钥证书 验证签名不通过返回null
     * @throws BidCaException
     */
    public byte[] verifyMessgeSignDetachedFetchCert(String sourceData, String signData) throws BidCaException;

    /**
     * 验证带签名字符串消息
     * @param messageSignAttached 带签名字符串消息
     * @return 原始符串消息
     * @throws BidCaException
     */
    public String verifyMessageSignAttached(String messageSignAttached) throws BidCaException;

    /**
     * 解密字符串消息信封
     * @param messageEnvelop 字符串消息信封
     * @param pkPostion 私钥位置
     * @return
     * @throws BidCaException
     */
    public String decryptMessageEnvelop(String messageEnvelop, String pkPostion) throws BidCaException;

    /**
     * 解密字符串消息信封，此方法要根据密文解析出证书关键字并以pkMap找到加密机的私钥位置
     * @param messageEnvelop 字符串消息信封
     *  @param pkMap (key值[如公钥证书的序列号],私钥位置),pkMap中<b>可能有</b>default_position,在以正常key值取不到私钥位置时可以使用default_position,
     * 否则最终抛出公钥证书不匹配的异常
     * @param charset 将解密后字符串的编码返回
     * @return
     * @throws BidCaException
     */
    public String decryptMessageEnvelop(String messageEnvelop, Map<String, String> pkMap, List<String> charset) throws BidCaException;

    public byte[] decryptMessageEnvelop(String messageEnvelop, Map<String, String> pkMap) throws BidCaException;

    /**
     * 验证带签名的文件
     * @param signAttachedFilePath 带签名的文件路径
     * @param sourceFilePath 原始文件路径
     * @return
     * @throws BidCaException
     */
    public boolean verifyFileSignAttached(String signAttachedFilePath, String sourceFilePath) throws BidCaException;

    /**
     * 验证带签名的文件并返回签名的公钥证书
     * @param signAttachedFilePath 带签名的文件路径
     * @param sourceFilePath 原始文件路径
     * @return 公钥证书 验证不通过返回null
     * @throws BidCaException
     */
    public byte[]  verifyFileSignAttachedFetchCert(String signAttachedFilePath, String sourceFilePath) throws BidCaException;


    /**
     * 验证文件签名
     * @param filePath 文件路径
     * @param signData 签名信息
     * @return
     * @throws BidCaException
     */
    public boolean verifyFileSignDetached(String filePath, String signData) throws BidCaException;

    /**
     * 验证文件签名并返回签名的公钥证书
     * @param filePath 文件路径
     * @param singnData 签名信息
     * @return 公钥证书 验证不通过返回null
     * @throws BidCaException
     */
    public byte[]  verifyFileSignDetachedFetchCert(String filePath, String singnData) throws BidCaException;

    /**
     * 解密文件
     * @param envelopeFilePath 加密过的文件路径
     * @param plainFilePath 解密后的文件路径
     * @param pkPostion 私钥位置
     * @throws BidCaException
     */
    public void decryptFileEnvelop(String envelopeFilePath, String plainFilePath, String pkPostion) throws BidCaException;

    /**
     * 解密文件，此方法要根据密文解析出证书关键字并以pkMap找到加密机的私钥位置
     * @param envelopeFilePath 加密过的文件路径
     * @param plainFilePath 解密后的文件路径
     * @param pkMap (key值[如公钥证书的序列号],私钥位置),pkMap中<b>可能有</b>default_position,在以正常key值取不到私钥位置时可以使用default_position,
     * 否则最终抛出公钥证书不匹配的异常
     * @throws BidCaException
     */
    public void decryptFileEnvelop(String envelopeFilePath, String plainFilePath, Map<String, String> pkMap) throws BidCaException;

    /**
     * 验证证书有效性
     * @param cert
     * @return 通过则返回true 不通过返回提示信息
     * @throws BidCaException
     */
    public String verifyCert(byte[] cert) throws BidCaException;

    /**
     * 获取公钥证书的序列号
     * @param str 密文
     * @return 十六进制不带空格的小写字符串
     * @throws BidCaException
     */
    public String getPubCertSerialNumber(String str) throws BidCaException;

    /**
     * 获取服务器公钥证书内容
     * @return
     * @throws BidCaException
     */
    public String getPubCertContent()  throws BidCaException;

    /**
     * 获取签名信息中的DN信息
     * @param signData 签名字符串
     * @return DN
     * @throws BidCaException
     */
    public String getSignDN(String signData)  throws BidCaException;

}

