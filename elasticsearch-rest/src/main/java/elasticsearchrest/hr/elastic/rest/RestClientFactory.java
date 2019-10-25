//package elasticsearchrest.hr.elastic.rest;
//
//import org.apache.http.HttpHost;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestClientBuilder;
//import org.elasticsearch.client.sniff.SniffOnFailureListener;
//import org.elasticsearch.client.sniff.Sniffer;
//
//public class RestClientFactory {
//    private volatile static RestClient restClient;
//
//    private RestClientFactory() {
//    }
//
//    public static RestClient getRestClient() {
//        if (restClient == null) {
//            synchronized (RestClientFactory.class) {
//                if (restClient == null) {
//                    // final CredentialsProvider credentialsProvider=new BasicCredentialsProvider();
//                    //  credentialsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("",""));
//                    SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();
//                    restClient = RestClient.builder(new HttpHost("127.0.0.1", 9200),
//                            new HttpHost("127.0.0.1", 5667))
//                            .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//                                @Override
//                                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
//                                    return httpAsyncClientBuilder.setDefaultCredentialsProvider(null);
//                                }
//                            }).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
//                                @Override
//                                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
//                                    return builder.setConnectTimeout(5000).setSocketTimeout(60000);
//                                }
//                            }).setMaxRetryTimeoutMillis(60000).setFailureListener(sniffOnFailureListener).build();
//                    Sniffer sniffer = Sniffer.builder(restClient).setSniffIntervalMillis(60000).setSniffAfterFailureDelayMillis(30000).build();
//                    sniffOnFailureListener.setSniffer(sniffer);
//                }
//            }
//        }
//        return restClient;
//    }
//}
