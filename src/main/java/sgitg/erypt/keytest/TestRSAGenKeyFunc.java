package sgitg.erypt.keytest;

import java.io.PrintStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Created by DELL on 2017/4/14.
 */
public class TestRSAGenKeyFunc {

    public static KeyPair testGenInternalKey()
    {
        int keynum = 6;
        KeyPair kp = null;

        System.out.print("产生RSA密钥对: 密钥序号(" + keynum + ") ... ");
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "SwxaJCE");
            kpg.initialize(keynum << 16);
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
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return kp;
    }
}
