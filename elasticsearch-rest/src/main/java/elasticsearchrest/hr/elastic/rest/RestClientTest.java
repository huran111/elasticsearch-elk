//package elasticsearchrest.hr.elastic.rest;
//
//import com.alibaba.fastjson.JSON;
//import elasticsearchrest.hr.vo.StudentVo;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.http.HttpEntity;
//import org.apache.http.client.methods.HttpPut;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.nio.entity.NStringEntity;
//import org.apache.http.util.EntityUtils;
//import org.elasticsearch.client.Response;
//import org.elasticsearch.client.ResponseListener;
//import org.elasticsearch.client.RestClient;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.Collections;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//public class RestClientTest {
//    private RestClient restClient;
//    private StudentVo studentVo;
//
//    @Before
//    public void init() {
//        restClient = RestClientFactory.getRestClient();
//        studentVo = new StudentVo();
//        studentVo.setAge(18);
//        studentVo.setCourse("elastic");
//        studentVo.setName("huran");
//        studentVo.setStudyDate(System.currentTimeMillis());
//        studentVo.setMark("忽然説道1111");
//    }
//
//    @Test
//    public void tesetQ() throws IOException {
//        Response response = restClient.performRequest("GET", "/school/_doc/1", Collections.singletonMap("pretty", "true"));
//        log.info(String.format("host:%s,statusLine:%s,requestLine:%s", response.getHost(), response.getStatusLine(), response.getRequestLine()));
//        log.info(EntityUtils.toString(response.getEntity()));
//    }
//
//    @Test
//    public void testq2() throws IOException {
//        HttpEntity entity = new StringEntity(JSON.toJSONString(studentVo), ContentType.APPLICATION_JSON);
//        Response indexResponse = restClient.performRequest("PUT", "/school/_doc/1", Collections.singletonMap("pretty", "true"), entity);
//        log.info(EntityUtils.toString(indexResponse.getEntity()));
//    }
//
//    @Test
//    public void testQ3() throws UnsupportedEncodingException, InterruptedException {
//        restClient.performRequestAsync(HttpPut.METHOD_NAME, "/school/_doc/1", Collections.singletonMap("pretty", "true")
//                , new NStringEntity(JSON.toJSONString(studentVo), ContentType.APPLICATION_JSON)
//                , new ResponseListener() {
//                    @Override
//                    public void onSuccess(Response response) {
//                        try {
//                            log.info(EntityUtils.toString(response.getEntity()));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Exception e) {
//                        log.info(e.getMessage());
//                    }
//                });
//        TimeUnit.SECONDS.sleep(3);
//    }
//}
