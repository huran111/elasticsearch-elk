package client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import jest.JestClientMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vo.RpcTraceInfoVo;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Component
public class TraceClient {
    private static final Logger logger = LoggerFactory.getLogger(TraceClient.class);
    private static JestClient jestClient;
    private static ExecutorService pool = Executors.newFixedThreadPool(10);

    static {
        logger.info("staticstaticstaticstaticstaticstatic");
        Properties properties = new Properties();
        properties.put("es.hosts", "localhost:9200");
        JestClientMgr jestClientMgr = new JestClientMgr(properties);
        jestClient = jestClientMgr.getJestClient();
    }

    public TraceClient() {
    }

    public  static void sendTraceInfo(final RpcTraceInfoVo rpcTraceInfoVo) {
        System.out.println("============");
        pool.submit(new Runnable() {

            public void run() {
                logger.info("追踪Id:"+Thread.currentThread().getName()+":"+rpcTraceInfoVo.getTraceId());
                logger.info("rpcId:"+Thread.currentThread().getName()+":"+rpcTraceInfoVo.getRpcId());
                logger.info("==================================");
                Index index = new Index.Builder(JSON.toJSONString(rpcTraceInfoVo, SerializerFeature.WriteNullStringAsEmpty)).index("trace_info").type("_doc")
                        .build();
                try {
                    DocumentResult result = jestClient.execute(index);
                    logger.info(result.getJsonString());
                    System.out.println(result.getErrorMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
