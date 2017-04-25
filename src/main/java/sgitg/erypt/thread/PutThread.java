package sgitg.erypt.thread;

import org.apache.log4j.Logger;
import sgitg.erypt.dao.DecryptDao;
import sgitg.erypt.dao.DecryptFactory;
import sgitg.erypt.util.DecryptConstant;
import sgitg.erypt.util.LogUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * Created by DELL on 2017/4/13.
 */
public class PutThread extends Thread{

    @SuppressWarnings("unchecked")
    private final BlockingQueue<Map> q;

    Logger logger = Logger.getLogger(this.getClass());

    DecryptDao dao = DecryptFactory.getDecryptDao();

    @SuppressWarnings("unchecked")
    public PutThread(BlockingQueue queue) throws Exception {

        q = queue;
        Connection conn = null;
        Statement st = null;

        try {
			/* 部署外网时取消报价及明细的解密
			String sqlPrice = "update lb_t_package_price_encrypt e set e.decrypt_status =-1 where  e.allocate_address='"
					+ DecryptConstant.SERVER_NAME + "' and e.decrypt_status =0";
			String sqlDetail = "update lb_t_detail_price_encrypt e set e.decrypt_status =-1 where  e.allocate_address='"
					+ DecryptConstant.SERVER_NAME + "' and e.decrypt_status =0";
			*/
			//文件状态:-2待分配，-1已分配未解密,0正在解密，1解密成功,2失败
            String sqlFile = " UPDATE ecp_t_bid_file t set t.file_stuta = '-1' WHERE t.file_stuta = '-2' ";

            conn = DecryptFactory.getConnection();

            st = conn.createStatement();
			/*
			st.executeUpdate(sqlPrice);
			st.executeUpdate(sqlDetail);
			*/
            st.executeUpdate(sqlFile);
            logger.info(LogUtil.printMessage("初始化检查成功"));
        } catch (Exception e) {
            logger.error(LogUtil.getException(e));
            throw e;
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    logger.error(LogUtil.getException(e));
                }
                st = null;
            }
            if(conn != null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error(LogUtil.getException(e));
                }
            }
        }
    }

    public void run() {
        while (true) {
            try {
                this.produceTask();
                // 延迟10秒执行
                Thread.sleep(1000 * DecryptConstant.PUT_SLEEP_TIME);
            } catch (Exception e) {
                logger.error(LogUtil.getException(e));
            }
        }
    }

    // 生产解密任务
    @SuppressWarnings("unchecked")
    public void produceTask() {

        logger.debug(LogUtil.printMessage(Thread.currentThread().getName()
                + "put task装载前队列大小:" + q.size()));
        try {
//			报价  -- 部署外网时取消报价及明细的解密
			Set priceSet = dao.getDecryptSet(DecryptConstant.PRICE_TYPE);
			if (priceSet != null && priceSet.size() > 0) {
				listDivision(priceSet, q, DecryptConstant.PRICE_TYPE + "");
			}

			//明细
			if (q.size() == 0) {
				Set detailSet = dao.getDecryptSet(DecryptConstant.DETAIL_PRICE_TYPE);
				if (detailSet != null && detailSet.size() > 0) {
					listDivision(detailSet, q, DecryptConstant.DETAIL_PRICE_TYPE + "");
				}
			}


            // 大文件
            if (q.size() == 0) {
                Set fileSet = dao.getDecryptSet(DecryptConstant.BIG_FILE_TYPE);
                if (fileSet != null && fileSet.size() > 0) {
                    listDivision(fileSet, q, DecryptConstant.BIG_FILE_TYPE + "");
                }
            }



            Set fileSet = dao.getDecryptSet(DecryptConstant.BIG_FILE_TYPE);
            if (fileSet != null && fileSet.size() > 0) {
                listDivision(fileSet, q, DecryptConstant.BIG_FILE_TYPE + "");
            }

            logger.info(LogUtil.printMessage(Thread.currentThread().getName()
                    + "put task当前队列大小:" + q.size()));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(LogUtil.getException("装载队列异常",e));
        }
    }

    /**
     * set拆分
     */
    @SuppressWarnings("unchecked")
    public void listDivision(Set set, BlockingQueue<Map> q, String type)
            throws InterruptedException {

        Object[] objs = set.toArray();

        // 除数
        int out = set.size() / DecryptConstant.ONE_TASK_NUM;
        // 余数
        int remain = set.size() % DecryptConstant.ONE_TASK_NUM;

        if (out == 0) {
            Map map = new HashMap();
            map.put("type", type);
            map.put("value", set);
            q.put(map);
        } else {
            int i = 0;
            // 倍数
            for (int k = 0; k < out; k++) {
                Set set2 = new HashSet();
                for (int j = 0; j < DecryptConstant.ONE_TASK_NUM; j++) {
                    set2.add(objs[i]);
                    i++;
                }
                Map map = new HashMap();
                map.put("type", type);
                map.put("value", set2);
                q.put(map);
            }

            // 余数
            if (remain != 0) {
                Set set2 = new HashSet();
                for (; i < set.size(); i++) {
                    set2.add(objs[i]);
                }
                Map map = new HashMap();
                map.put("type", type);
                map.put("value", set2);
                q.put(map);
            }
        }
    }

}
