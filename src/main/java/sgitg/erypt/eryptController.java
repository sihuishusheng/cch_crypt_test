package sgitg.erypt;

/**
 * Created by DELL on 2017/4/13.
 */
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sgitg.erypt.keytest.TestRSAGenKeyFunc;
import sgitg.erypt.thread.PutThread;
import sgitg.erypt.thread.TakeThread;
import sgitg.erypt.thread.TestThread;
import sgitg.erypt.util.DecryptConstant;
import sgitg.erypt.util.LogUtil;
import sgitg.erypt.util.XmlUtil;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/erypt")
public class eryptController {

    static Logger logger = Logger.getLogger(eryptController.class);


    @RequestMapping(value = "/{type}", method = RequestMethod.GET)
    public String sayWorld(@PathVariable("type") String name) {

                try {
                    // 解密线程池
        ExecutorService executorService = Executors.newFixedThreadPool(26);

        // 设置一个300000为边界的列队，超过则block
        BlockingQueue queue = new ArrayBlockingQueue(DecryptConstant.QUEUE_NUM);

        logger.info(LogUtil.printMessage("生产线程启动"));

        Map<String,String> pkMap= XmlUtil.getPKPostionMap();

        PutThread putThread  = new PutThread(queue);

        putThread.setPriority(DecryptConstant.PUT_THREAD_PRIORITYputThreadPriority);
        executorService.execute(putThread);

            if(DecryptConstant.THREAD_TEST){
                TestThread testThread=new TestThread(pkMap);
                testThread.setPriority(DecryptConstant.TAKE_THREAD_PRIORITY);
                logger.info(LogUtil.printMessage("测试线程启动"));
                executorService.execute(testThread);
            }else{
                logger.info(LogUtil.printMessage("没有启动测试线程"));
            }

            TakeThread takeThread = null;
            for (int i = 0; i < DecryptConstant.DECRYPT_THREAD_NUM; i++) {
                takeThread = new TakeThread(queue,pkMap);
                takeThread.setPriority(DecryptConstant.TAKE_THREAD_PRIORITY);
                logger.info(LogUtil.printMessage(takeThread.getName() + "消费线程启动"));
                executorService.execute(takeThread);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            logger.info(LogUtil.printMessage("解密程序启动失败,请检查进程信息"));
            System.exit(0);
            return "";
        }
        return "Hello " + name;
    }
}
