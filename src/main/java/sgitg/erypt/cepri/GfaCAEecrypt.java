package sgitg.erypt.cepri;

import org.apache.log4j.Logger;

/**
 * Created by DELL on 2017/4/19.
 */
public class GfaCAEecrypt  implements CAEncrypt{

    private Logger logger = Logger.getLogger(GfaCAEecrypt.class);

    private static CAEncrypt caEncrypt = null;

    public static CAEncrypt getInstance() throws BidCaException {
        if (caEncrypt == null) {
            caEncrypt = new GfaCAEecrypt();
        }
        return caEncrypt;
    }

    /**
     * 得到实例后首先调用该方法，且只调用一次
     * 该方法的实现可以为空
     * @throws BidCaException
     */
    public void init() throws BidCaException{}

    /**
     *  创建会话<br>
     *  在调用签名方法前调用该方法<br>
     *  @throws BidCaException
     */
    public void createSession() throws BidCaException{}

    /**
     *  释放会话<br>
     *  实例的生命周期结束前调用该方法
     * @throws BidCaException
     */
    public void releaseSession()  throws BidCaException{}

    /**
     * 根据输入的主题DN过滤条件，过滤出带私钥的签名证书。
     重复调用此函数，会覆盖上次选择的签名证书。在使用签名功能是必须调用该函数设置签名函数。
     <br>
     * 在调用签名方法前调用该方法
     * @param subject
     * @return
     * @throws BidCaException
     */
    public boolean selectSignCert(String subject) throws BidCaException{    return true;}

    /**
     * 加载用来加密的公钥证书<br>
     *
     * @param pubCerStr 公钥证书字符串
     * @throws BidCaException
     */
    public void addPubCertByContent(String pubCerStr) throws BidCaException{}

    /**
     * 获取字符串的签名结果
     * @param message 待签名的字符串
     * @return 成功返回字符串的签名结果
     * @throws BidCaException
     */
    public String signMessageDetached(String message) throws BidCaException{    return null;}

    /**
     * 对字符串签名且将签名信息附加到原始字符串中
     * @param message 待签名的字符串
     * @return 带签名的消息
     * @throws BidCaException
     */
    public String signMessageAttached(String message) throws BidCaException{    return null;}

    /**
     * 字符串加密，返回加密后的字符串
     * @param plaintext 待加密字符串
     * @return 成功返回加密字符串
     * @throws BidCaException
     */
    public String encryptMessage(String plaintext) throws BidCaException{    return null;}

    /**
     *  文件签名（非分离式）
     * @param srcFilePath 待签名的文件
     * @param signedFilePath 签名后的完整文件路径
     * @throws BidCaException
     */
    public void signFileAttached(String srcFilePath, String signedFilePath) throws BidCaException{}

    /**
     * 获取文件的签名结果
     * @param srcFilePath 待签名的文件路径
     * @return 成功返回签名信息
     * @throws BidCaException
     */
    public String signFileDetached(String srcFilePath) throws BidCaException{    return null;}

    /**
     *  对文件加密
     * @param srcFilePath 待加密的完整文件路径
     * @param encFilePath 加密后的完整文件路径
     * @throws BidCaException
     */
    public void encryptFile(String srcFilePath, String encFilePath) throws BidCaException {

    }



}
