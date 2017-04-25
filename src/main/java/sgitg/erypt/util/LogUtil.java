package sgitg.erypt.util;

import java.util.Date;

/**
 * Created by DELL on 2017/4/13.
 */
public class LogUtil {
    public static String n = "\n";

    /**
     *
     * @title 获得详细日志
     * @param e
     *            异常对象
     * @return 堆栈串
     * @throws Exception
     */
    public static String getException(Exception e) {

        if (e == null) {
            return "异常为空";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(new Date());
        sb.append(" 异常描述:" + n);
        sb.append(e.toString() + " at " + n);
        StackTraceElement[] st = e.getStackTrace();
        if (st != null) {
            for (int i = 0; i < st.length; i++) {
                sb.append(st[i]);
                sb.append(n);
            }
        }
        return sb.toString();
    }

    public static String getException(String msg, Exception e) {

        if (e == null) {
            return "异常为空";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(new Date());
        sb.append(" 异常描述:" + n);
        sb.append(" 业务消息:" + msg + n);
        sb.append(e.toString() + " at " + n);
        StackTraceElement[] st = e.getStackTrace();
        if (st != null) {
            for (int i = 0; i < st.length; i++) {
                sb.append(st[i]);
                sb.append(n);
            }
        }
        return sb.toString();
    }

    public static String printMessage(String msg) {

        StringBuilder sb = new StringBuilder();
        sb.append("---");
        sb.append(msg);
        sb
                .append("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        int printlength = DecryptConstant.PRINT_LENGTH;
        if (printlength < 0 || printlength > 200) {
            printlength = 100;
        }
        return sb.toString().substring(0, printlength);
    }

    /**
     * @title
     * @Create on Sep 15, 2009 4:30:26 PM
     * @param args
     * @throws
     */
    public static void main(String[] args) {

    }

}
