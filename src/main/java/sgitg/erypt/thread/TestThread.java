package sgitg.erypt.thread;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import sgitg.erypt.cepri.CADecrypt;
import sgitg.erypt.cepri.CAFactory;
import sgitg.erypt.util.DecryptConstant;
import sgitg.erypt.util.PathUtil;


/**
 * Created by DELL on 2017/4/13.
 */
public class TestThread extends Thread{


    Logger logger = Logger.getLogger(this.getClass());
    CADecrypt cad = null;

    private String plainMsg="";
    private String signMsg="";
    private String envlopMsg="";

    //加密数据
    private String encrpytMsg="MIIBUwYJKoZIhvcNAQcDoIIBRDCCAUACAQAxgdgwgdUCAQAwPjAqMQswCQYDVQQGEwJDTjEbMBkGA1UEChMSQ0ZDQSBPcGVyYXRpb24gQ0EyAhAaQQqKK94TQEYkX2F6mXgJMA0GCSqGSIb3DQEBAQUABIGArR7RCeTyvtSJYse23XrCoFkIUfXi4mzs4IX5iBWU/XaI68mWN5XFIPmLH/1lb2AlcWYStCv3pSKLZ3dvvtL3E6zNHFleAmvEZGaWJ04yVSdvpMc9UnTzBgHKzIAwU73b45eea9BIDrht1bKChBma75JvUgG488fG336NrYlJZ1AwYAYJKoZIhvcNAQcBMBEGBSsOAwIHBAifVlJ524pFdIBAY8DxXWQijv82HYTd0NVbp374DjiyCJBqLDY3cTBmV6UTeQWnrluyr7i+LkZoTAjwI0fGh8fPGVNn2hDA9DPYcQ==";
    //加密数据 的签名
    private String encrpytMsgSign="MIIFCQYJKoZIhvcNAQcCoIIE+jCCBPYCAQExCzAJBgUrDgMCGgUAMAsGCSqGSIb3DQEHAaCCA+8wggPrMIIDVKADAgECAhBmRThYpJR/RzrG3GHUN+SbMA0GCSqGSIb3DQEBBQUAMCoxCzAJBgNVBAYTAkNOMRswGQYDVQQKExJDRkNBIE9wZXJhdGlvbiBDQTIwHhcNMDkxMjEwMDIyMjA4WhcNMTIxMjEwMDIyMjA4WjCBnjELMAkGA1UEBhMCQ04xGzAZBgNVBAoTEkNGQ0EgT3BlcmF0aW9uIENBMjENMAsGA1UECxMEU0dDQzEUMBIGA1UECxMLZW50ZXJwcmlzZXMxTTBLBgNVBAMeRAAwADQAMQBAADgAMQAyADMANAA1ADYANwA4ADkAQFb9f1FOjGcfbUuL1U8BThqLwU5mAEAAMAAwADAAMAAwADAAMQAwMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6eHM70m9FzFzWlNXQzO+Z1+jFvySRCvbOlGg4KPO4O2wjyiDX/QDqCQZpflmUFl4VP2ZvMGFC4LV+8lihU+HJzf/bla/gdmPFzptHWlBPKaPq9p8SxeVccsKkQMxaMgZLN4BGInC+5fMZ4LWIBQApWUp/M6HovlTYJTSDgQ1YDwIDAQABo4IBmzCCAZcwHwYDVR0jBBgwFoAU8I3ts0G7++8IHlUCwzE37zwUTs0wHQYDVR0OBBYEFLJUEhhlcbeYAA7XJSSR2MTli8qAMAsGA1UdDwQEAwIF4DAbBgNVHREEFDASgRB4am1hQGNmY2EuY29tLmNuMAwGA1UdEwQFMAMBAQAwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMIH9BgNVHR8EgfUwgfIwVqBUoFKkUDBOMQswCQYDVQQGEwJDTjEbMBkGA1UEChMSQ0ZDQSBPcGVyYXRpb24gQ0EyMQwwCgYDVQQLEwNDUkwxFDASBgNVBAMTC2NybDEwNF8xMjk4MIGXoIGUoIGRhoGObGRhcDovL2NlcnQ4NjMuY2ZjYS5jb20uY246Mzg5L0NOPWNybDEwNF8xMjk4LE9VPUNSTCxPPUNGQ0EgT3BlcmF0aW9uIENBMixDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDANBgkqhkiG9w0BAQUFAAOBgQALep1m0LWEKezsG3bkcWvPGm4nzWYIGivjUKo6NwD0SvMENVw7UhpdMo31aoU5n12UL2+W0XGxtVIBh6qyZYw7r/PLv0OFDB5sfUmFZOSEycFAj4acB7sXZo+/QJghGLaJVrNVcxaoiCtXWf/A0xjw6SrvG10Eh2j0sd95qTJyzDGB4zCB4AIBATA+MCoxCzAJBgNVBAYTAkNOMRswGQYDVQQKExJDRkNBIE9wZXJhdGlvbiBDQTICEGZFOFiklH9HOsbcYdQ35JswCQYFKw4DAhoFADANBgkqhkiG9w0BAQEFAASBgEs6PRKMbigmaSzzn7yPMJDdBJ3Xh4hARZjsD8g9kYHQ+//Iy63NvurQHSZgQh9mgaCZhOAvJ63T2dii7UxEqFqSDRfQ27SNUAOzewnLAMZLNGEHsak7R7enWR7ZeegMjQwEKqYmSIikIGG1btCh3NrGQwMBLodhCUIF9UEhyoUW";

    public TestThread(Map<String,String> pkMap) {
        try {
            cad= CAFactory.getCADecryptInstance();
            InputStream in = PathUtil.class.getResourceAsStream("/testdata.properties");
            Properties props = new Properties();
            props.load(in);
            String eM=props.getProperty("encrpytMsg");
            String eMS=props.getProperty("encrpytMsgSign");
            if(eM.length()>0 && eMS.length()>0){
                this.encrpytMsg=eM;
                this.encrpytMsgSign=eMS;
            }
        } catch (IOException e) {
            logger.error(e);
            logger.info("TestThread 加载测试数据失败，解密服务将停止");
            System.exit(0);
        }catch (Exception e) {
            logger.error(e);
            logger.info("TestThread 加载测试数据失败，解密服务将停止");
            System.exit(0);
        }
        logger.info("TestThread 测试数据为,加密数据encrpytMsg= "+encrpytMsg+"    ,签名encrpytMsgSign="+encrpytMsgSign);

    }

    public void run() {
        while (true) {
            this.produceTask();

            try {
                Thread.sleep(1000 * DecryptConstant.TEST_SLEEP_TIME);
            } catch (InterruptedException e) {
                logger.error("测试线程",e);
            }
        }
    }

    private void produceTask() {
        try {
            boolean b=cad.verifyMessgeSignDetached(this.encrpytMsg, this.encrpytMsgSign,"UTF8");
            logger.info("测试线程 测试验签结果="+b);
        } catch (Exception e) {
            logger.error("测试线程验签异常",e);
        }
    }


}
