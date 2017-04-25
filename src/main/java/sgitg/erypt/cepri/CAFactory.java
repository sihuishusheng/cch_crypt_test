package sgitg.erypt.cepri;


/**
 * 本工厂用来取得CA有关的实例，可根据需要修改
 * Created by DELL on 2017/4/13.
 *
 */
public class CAFactory {
    private static CADecrypt cADecrypt = null;

    private static CAEncrypt cAEecrypt = null;

	/*private static CAUtil caUtil = null;

	@SuppressWarnings("unchecked")
	public static CAUtil getCAUtil() throws Exception {

		if (caUtil == null) {
			String ca[] = XmlUtil.caElements();
			Class caClass = Class.forName(ca[1]);
			caUtil = (CAUtil) caClass.newInstance();
		}

		return caUtil;
	}
*/

    public static CADecrypt getCADecryptInstance() throws BidCaException{
        if(cADecrypt==null){
            cADecrypt=GfaCADecrypt.getInstance();
            cADecrypt.init();
        }
        return cADecrypt;

    }

    public static CAEncrypt getCAEncryptInstance() throws BidCaException{
        if(cAEecrypt==null){
            cAEecrypt=GfaCAEecrypt.getInstance();
            cAEecrypt.init();
        }
        return cAEecrypt;
    }



}