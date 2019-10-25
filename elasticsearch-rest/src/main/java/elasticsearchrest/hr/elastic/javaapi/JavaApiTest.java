package elasticsearchrest.hr.elastic.javaapi;

import com.alibaba.fastjson.JSON;
import elasticsearchrest.hr.vo.StudentVo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.junit.Before;
import org.junit.Test;
public class JavaApiTest {
    private TransportClient client;
    private StudentVo studentVo;

    @Before
    public void init() {
        client = TransportClientFactory.getTransportClient();
        studentVo = new StudentVo();
        studentVo.setAge(18);
        studentVo.setCourse("elastic");
        studentVo.setName("huran");
        studentVo.setStudyDate(System.currentTimeMillis());
        studentVo.setMark("忽然説道1111");
    }

    @Test
    public void createDocumet() {
        IndexResponse indexResponse = client.prepareIndex("huran", "student", "1")
                .setSource(JSON.toJSONString(studentVo), XContentType.JSON).get();
        String index = indexResponse.getIndex();
        String id = indexResponse.getId();
        long version = indexResponse.getVersion();
        RestStatus status = indexResponse.status();
     ;

    }
}
