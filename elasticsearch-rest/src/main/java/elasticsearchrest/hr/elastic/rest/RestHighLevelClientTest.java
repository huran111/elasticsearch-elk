//package elasticsearchrest.hr.elastic.rest;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestClientBuilder;
//import org.elasticsearch.client.RestHighLevelClient;
//
//import java.io.IOException;
//
//public class RestHighLevelClientTest {
//
//    public static void main(String[] args) throws IOException {
//        RestClient restClient = RestClient.builder(
//                new HttpHost("localhost", 9200, "http"),
//                new HttpHost("localhost", 5667, "http")).build();
//        restClient.close();
//    }
//}
