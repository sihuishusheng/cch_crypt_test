package sgitg.erypt.keytest;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Created by DELL on 2017/4/14.
 */
public class TestSM2GenKeyFunc {
    public static KeyPair testGenExternalKey()
    {
        int keysize = 256;
        KeyPair kp = null;

        System.out.print("产生RSA密钥对: 密钥模长(" + keysize + ") ... ");
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("SM2", "SwxaJCE");
            kpg.initialize(keysize);
            kp = kpg.genKeyPair();

            if (kp == null) {
                System.out.println("fail！");
            }
            else {
                System.out.println("ok！");
                System.out.println(kp.getPublic());
                System.out.println(kp.getPrivate());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return kp;
    }
}
