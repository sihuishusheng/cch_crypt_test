package sgitg.erypt;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by DELL on 2017/4/14.
 */
@RestController
@RequestMapping("/file")
public class fileController {
    @RequestMapping("/test")
    public String test() {
        try {
        BufferedReader br = new BufferedReader(new FileReader("C:\\encrypt\\test1.txt"));// 读取原始json文件
        String s = null;
        while ((s = br.readLine()) != null) {
             System.out.println(s);
            try {
                JSONObject dataJson = JSONObject.fromObject(s);// 创建一个包含原始json串的json对象
                String bid_id = dataJson.getString("bid_id");// 找到properties的json对象
                System.out.println("bid_id++++++++++++++++++++++++++++++++++"+bid_id);
                String price = dataJson.getString("price");// 找到properties的json对象
                System.out.println("price++++++++++++++++++++++++++++++++++"+price);

                JSONArray prices = dataJson.getJSONArray("prices");// 找到features的json数组
                for (int i = 0; i < prices.size(); i++) {
                    JSONObject g_p = prices.getJSONObject(i);// 获取features数组的第i个json对象
                    String good_id = g_p.getString("good_id");// 找到properties的json对象
                    System.out.println("good_id++++++++++++++++++++++++++++++++++"+good_id);//
                    String g_price = g_p.getString("price");// 找到properties的json对象
                    System.out.println("price++++++++++++++++++++++++++++++++++"+g_price);//
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // bw.newLine();

        br.close();

    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }



        return null;
    }
}
