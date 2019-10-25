package com;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


public class JavaApiTest {
    private static Logger log = LoggerFactory.getLogger(JavaApiTest.class);
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
        IndexResponse indexResponse = client.prepareIndex("huran", "_doc", "1")
                .setSource(JSON.toJSONString(studentVo), XContentType.JSON).get();
        String index = indexResponse.getIndex();
        String id = indexResponse.getId();
        long version = indexResponse.getVersion();
        RestStatus status = indexResponse.status();
        System.out.println(id);
        System.out.println(index);
        System.out.println(version);
        System.out.println(status);
    }

    @Test
    public void createDocument02() throws ExecutionException, InterruptedException {
        IndexRequest indexRequest = new IndexRequest()
                .index("huran01")
                .id("1").source(JSON.toJSONString(studentVo), XContentType.JSON);
        IndexResponse indexResponse = client.index(indexRequest).get();
        System.out.println(indexResponse.toString());
    }

    @Test
    public void getDocument() {
        GetResponse response = client.prepareGet("huran01", "_doc", "1").get();
        System.out.println(response.getSourceAsString());
    }

    @Test
    public void deleteByQuery() {
        BulkByScrollResponse response =
                new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE)
                        .filter(QueryBuilders.matchQuery("_id", "W78aBG0BBts06bfxu64d"))
                        .source("ips")
                        .get();
        System.out.println(response.getDeleted());
    }

    @Test
    public void Update() throws ExecutionException, InterruptedException {
        studentVo = new StudentVo();
        studentVo.setAge(1822);
        studentVo.setCourse("elastic");
        studentVo.setName("huran");
        studentVo.setStudyDate(System.currentTimeMillis());
        studentVo.setMark("aaaaaaaaaaaaaaaaaa");
        IndexRequest indexRequest = new IndexRequest("huran01", "_doc", "2").source(JSON.toJSONString(studentVo), XContentType.JSON);
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("huran01");
        updateRequest.id("2");
        updateRequest.doc(JSON.toJSONString(studentVo), XContentType.JSON).upsert(indexRequest);
        UpdateResponse updateResponse = client.update(updateRequest).get();
        System.out.println(updateResponse.toString());
    }

    @Test
    public void mulitGet() {
        MultiGetResponse multiGetItemResponses =
                client.prepareMultiGet()
                        .add("huran01", "_doc", "1")
                        .add("huran", "_doc", "1").get();
        System.out.println(multiGetItemResponses.getResponses().length);
        for (MultiGetItemResponse multiGetItemRespons : multiGetItemResponses) {
            GetResponse response = multiGetItemRespons.getResponse();
            if (response.isExists()) {
                String sourceAsString = response.getSourceAsString();
                System.out.println(sourceAsString);
            }
        }
    }

    @Test
    public void bulkTest() {
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        bulkRequestBuilder.add(client.prepareIndex("huran01", "_doc", "1").setSource(JSON.toJSONString(studentVo), XContentType.JSON));
        bulkRequestBuilder.add(client.prepareIndex("huran", "_doc", "1").setSource(JSON.toJSONString(studentVo), XContentType.JSON));
        BulkResponse bulkItemResponses = bulkRequestBuilder.get();
        for (BulkItemResponse bulkItemRespons : bulkItemResponses) {
            System.out.println(bulkItemRespons.getFailureMessage());
        }
    }

    @Test

    public void BulkP() throws InterruptedException {

        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
            public void beforeBulk(long l, BulkRequest bulkRequest) {
                System.out.println(l);
                System.out.println(bulkRequest.getDescription());
            }

            public void afterBulk(long l, BulkRequest request, BulkResponse response) {
                if (response.hasFailures()) {
                    System.out.println(response.buildFailureMessage());
                }
            }

            public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {

            }
        }).setBulkActions(1000)
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(1)
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)).build();//1 标识异步 0 同步
        IntStream.range(0, 10000).forEach(x -> {
            bulkProcessor.add(new IndexRequest().index("huran").id(x + "").source(JSON.toJSONString(studentVo), XContentType.JSON));
        });
        bulkProcessor.flush();
        bulkProcessor.close();
        RefreshResponse movies = client.admin().indices().prepareRefresh("huran").get();
        System.out.println("movies.getStatus():" + movies.getStatus());
        TimeUnit.SECONDS.sleep(10);

    }

    @Test
    public void aaa() {
        IntStream.range(0, 100).forEach(x -> {
            System.out.println(x);
        });
    }

    @Test
    public void scroll() {
        QueryBuilder qb = QueryBuilders.termQuery("name", "huran");
        SearchResponse searchResponse = client.prepareSearch("huran")
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000))
                .setQuery(qb)
                .setSize(1000).get();
        do {
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                //
            }
            System.out.println(searchResponse.getHits().getTotalHits());
            searchResponse = client.prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(60000)).get();
        } while (searchResponse.getHits().getHits().length != 0);
    }

    @Test
    public void multiSearch() {
        SearchRequestBuilder srb1 = client.prepareSearch().setQuery(QueryBuilders.queryStringQuery("1111212fd")).setSize(1);
        SearchRequestBuilder srb2 = client.prepareSearch().setQuery(QueryBuilders.queryStringQuery("huran")).setSize(1);
        MultiSearchResponse sr = client.prepareMultiSearch().add(srb1)
                .add(srb2).get();
        long totalHits = 0;
        for (MultiSearchResponse.Item item : sr.getResponses()) {
            SearchResponse response = item.getResponse();
            totalHits += response.getHits().getTotalHits().value;
        }

        System.out.println(totalHits);

    }

    @Test
    public void agg() {
        SearchResponse sr = client.prepareSearch("school").setQuery(QueryBuilders.matchAllQuery())
                .setSize(0).addAggregation(AggregationBuilders.terms("agg1").field("course"))
                .get();
        System.out.println(sr.toString());
        Terms agg1 = sr.getAggregations().get("agg1");
        Terms.Bucket bucket = agg1.getBuckets().get(0);
        System.out.println(bucket.getKey());
        System.out.println(bucket.getKeyAsString());
    }

    public static void main(String[] args) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().aggregation(AggregationBuilders.terms("colors ").field("color"));
        System.out.println(searchSourceBuilder.toString());
        log.info("人才");
    }

    @Test
    public void delete() {
    }
}
