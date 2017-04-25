package sgitg.erypt.util;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by DELL on 2017/4/13.
 */
public class PathUtil {
    private static Logger logger = Logger.getLogger(PathUtil.class);

    public static String getPublicKeyPath() {
        String Path = getSystemPath("server.cer")  ;
        Path = isExist(Path);
        return Path;
    }

    public static String getPrivateKeyPath() {
        String Path = getSystemPath("server.pfx")  ;
        Path = isExist(Path);
        return Path;
    }
    public static String getJSKPath() {
        String Path = getSystemPath("") + File.separator +  XmlUtil.cfcaConfigElement("JKS_NAME");
        Path = isExist(Path);
        return Path;
    }

    public static String getCRlFile() {
        String Path = getSystemPath("SGCC.crl") ;
        Path = isExist(Path);
        return Path;
    }

    public static String getP7bPath() {
        String Path = getSystemPath("ca.p7b") ;
        Path = isExist(Path);
        return Path;
    }

    public static String getTsaderPath() {
        String Path = getSystemPath("tsa.der")  ;
        Path = isExist(Path);
        return Path;
    }

    public static String getCfcaConfigPath() {
        String Path = getSystemPath("cfcaConfig.xml") ;
        Path = isExist(Path);
        return Path;
    }
    public static String getGfaCaConfigPath() {
        String Path = getSystemPath("gfaCaConfig.xml")  ;
        Path = isExist(Path);
        return Path;
    }
    public static String getCaPath() {
        String Path = getSystemPath("ca.xml")  ;
        Path = isExist(Path);
        return Path;
    }

    public static String getDbPath() {
        String Path = getSystemPath("db.xml") ;
        Path = isExist(Path);
        return Path;
    }

    public static String getCpPath() {
        String Path = getSystemPath("c3p0Config.xml")  ;
        Path = isExist(Path);
        return Path;
    }

    public static String isExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException(file.getPath() + "文件不存在");
        }
        return file.getPath();
    }

    public static String getSystemPath(String name) {
        String path = PathUtil.class.getResource("/"+name ).getPath();
        return path;
    }

    public static String getTaskNum(String name) {
        String num = "500";
        InputStream in = PathUtil.class.getResourceAsStream("/task.properties");
        Properties props = new Properties();
        try {
            props.load(in);
            num = props.getProperty(name);
        } catch (IOException e) {
            logger.error(LogUtil.getException(e));
        }
        return num;
    }

    public static String getDBPool() {
        String str[] = XmlUtil.dbElements();
        return str[4];
    }
}
