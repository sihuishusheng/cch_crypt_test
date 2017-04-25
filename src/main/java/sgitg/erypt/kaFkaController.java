package sgitg.erypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sgitg.erypt.test.UserInfo;

/**
 * Created by DELL on 2017/4/17.
 */
@Controller
public class kaFkaController {
    @Autowired
    KafkaTemplate kafkaTemplate;

    @RequestMapping("/testKafka")
    @ResponseBody
    public void testkafka(String message) {
        UserInfo u=new UserInfo();
        u.setId(1);
        u.setName("name");
        System.out.println("+++++++++++++++++++++测试KafKa");
        kafkaTemplate.send("bootcwenaoTopic", "bootcwnao", u);
    }
}
