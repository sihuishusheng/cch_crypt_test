package sgitg.erypt.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Created by DELL on 2017/4/13.
 */
public class XmlUtil {
    private static Logger logger = Logger.getLogger(XmlUtil.class);

    /**
     *
     * @title 获得cfcaConfig文件中的元素值
     * @param name
     * @return
     * @throws DocumentException
     * @throws FileNotFoundException
     * @throws Exception
     */
    public static String cfcaConfigElement(String name) {

        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(new File(PathUtil.getCfcaConfigPath()));

            Element rootElement = doc.getRootElement();
            Element element = rootElement.element(name);
            return element.getText();

        } catch (DocumentException e) {
            logger.error(LogUtil.getException(e));
            throw new RuntimeException("读取cfcaConfig.xml失败");
        }

    }
    /**
     * 国富安
     * @param name
     * @return
     */
    public static String gfaCaConfigElement(String name) {

        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(new File(PathUtil.getGfaCaConfigPath()));

            Element rootElement = doc.getRootElement();
            Element element = rootElement.element(name);
            return element.getText();

        } catch (DocumentException e) {
            logger.error(LogUtil.getException(e));
            throw new RuntimeException("gfaCaConfig.xml失败");
        }

    }
    public static Map<String,String> getPKPostionMap(){
        Map<String,String> pkMap=new HashMap<String,String>();
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(new File(PathUtil.getGfaCaConfigPath()));

            Element rootElement = doc.getRootElement();
            Element certs=rootElement.element("certs");
            StringBuilder sb=new StringBuilder();
            for (Iterator cert = certs.elementIterator("cert"); cert.hasNext();) {
                Element foo = (Element) cert.next();
                sb.append("cert[");
                for(Iterator attribute= foo.attributeIterator();attribute.hasNext();){
                    Attribute attr=(Attribute)attribute.next();
                    String a=attr.getName();
                    String n=attr.getValue();
                    sb.append(a).append("=").append(n).append(",");
                }
                sb.setLength(sb.length()-1);
                sb.append("]=");
                String key=foo.attributeValue("key");
                String value=foo.getText();
                pkMap.put(key, value);
                sb.append(value).append("\n");
            }
            logger.info("certList:--------------------\n"+sb.toString()+"\n-------------------------------------------------------");
        } catch (DocumentException e) {
            logger.error(LogUtil.getException(e));
            throw new RuntimeException("解析gfaCaConfig.xml失败");
        }
        return pkMap;
    }

    /**
     *
     * @title 获得CA厂家实现类
     * @return 厂家名称，实现类
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static String[] caElements() {

        SAXReader reader = new SAXReader();
        Document doc = null;
        String[] str = new String[2];

        try {
            doc = reader.read(new File(PathUtil.getCaPath()));

            Element rootElement = doc.getRootElement();
            Iterator nodes = rootElement.elementIterator("CA");

            while (nodes.hasNext()) {
                Element element = (Element) nodes.next();
                Element provider = element.element("provider");
                Element status = element.element("status");
                Element className = element.element("className");
                if (status.getTextTrim().equals("true")) {
                    str[0] = provider.getTextTrim();
                    str[1] = className.getTextTrim();
                }
            }
        } catch (DocumentException e) {
            logger.error(LogUtil.getException(e));
            throw new RuntimeException("读取ca.xml失败");
        }

        return str;
    }

    /**
     *
     * @title 获得数据库连接参数
     * @return 用户名，密码，驱动名，地址
     * @throws Exception
     * @throws
     */
    public static String[] dbElements() {

        SAXReader reader = new SAXReader();
        Document doc = null;
        String[] str = new String[5];
        try {
            doc = reader.read(new File(PathUtil.getDbPath()));

            Element rootElement = doc.getRootElement();

            str[0] = rootElement.elementText("USER_NAME");
            str[1] = rootElement.elementText("USER_PASS");
            str[2] = rootElement.elementText("DB_DRIVER");
            str[3] = rootElement.elementText("DB_URL");
            str[4] = rootElement.elementText("USE_POOL");

            for (int i = 0; i < 4; i++) {
                if (str[i] == null || "".equals(str[i]))
                    throw new RuntimeException("请确保配置文件的正确性！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(LogUtil.getException(e));
            throw new RuntimeException("读取db.xml失败");
        }

        return str;
    }

    /**
     * @title 连接池配置
     */
    @SuppressWarnings("unchecked")
    public static Map cpElements() {

        SAXReader reader = new SAXReader();
        Document doc = null;

        Map map = new HashMap();

        try {
            doc = reader.read(new File(PathUtil.getCpPath()));

            Element root = doc.getRootElement();

            List list = root.elements();

            for (int i = 0; i < list.size(); i++) {

                Element item = (Element) list.get(i);

                if(item.getStringValue() != null && !"".equals(item.getStringValue()))
                    map.put(item.attributeValue("name"), item.getStringValue());

            }

        } catch (Exception e) {
            logger.error(LogUtil.getException(e));
            throw new RuntimeException("读取c3p0Config.xml失败");
        }

        return map;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(cpElements());
        getPKPostionMap();
    }
}
