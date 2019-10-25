package jest;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.core.*;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EsJestClientTest {
    private JestClient jestClient;
    private JestClientMgr jestClientMgr;
    private StudentVo studentVo;

    @Before
    public void init() {
        jestClientMgr = new JestClientMgr();
        jestClient = jestClientMgr.getJestClient();
        studentVo = new StudentVo();
        studentVo.setAge(2323);
        studentVo.setCourse("sdfsd");
        studentVo.setDocId(1);
        studentVo.setName("sdfsf");
        studentVo.setStudyDate(System.currentTimeMillis());
        studentVo.setMark("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @Test
    public void deleteIndex() throws IOException {
        JestResult result = jestClient.execute(new DeleteIndex.Builder("huran").setParameter("pretty", true).build());
        System.out.println(result.getJsonString());
    }

    @Test
    public void createIndex() throws IOException {
        Map<String, Object> settingMap = new HashMap<String, Object>();
        settingMap.put("number_of_shards", 10);
        settingMap.put("number_of_replicas", 1);
        JestResult result = jestClient.execute(new CreateIndex.Builder("huran")
                .setParameter("pretty", true).settings(settingMap).build());
        System.out.println(result.toString());
    }

    @Test
    public void createDoumentById() throws IOException {
        Index index = new Index.Builder(studentVo).index("school").type("_doc").id("1").build();
        DocumentResult execute = jestClient.execute(index);
        System.out.println(execute.getJsonString());
    }

    @Test
    public void getDocumentById() throws IOException {
        Get get = new Get.Builder("school", "1").type("_doc").build();
        DocumentResult execute = jestClient.execute(get);
        StudentVo sourceAsObject = execute.getSourceAsObject(StudentVo.class);
        System.out.println(sourceAsObject.getName());
    }

    @Test
    public void deleteById() throws IOException {
        Delete delete = new Delete.Builder("1").index("school").type("_doc").build();
        DocumentResult execute = jestClient.execute(delete);
        System.out.println(execute.getJsonString());
    }

    @Test
    public void bulk() throws IOException {
        Bulk bulk = new Bulk.Builder().defaultIndex("school")
                .defaultType("_doc")
                .addAction(new Index.Builder(studentVo).build())
                .addAction(new Index.Builder(studentVo).build())
                .addAction(new Delete.Builder("2").index("school").build())
                .setParameter("pretty", true).build();
        BulkResult execute = jestClient.execute(bulk);
        System.out.println(execute.getJsonString());
    }

    @Test
    public void bulk2() throws IOException {
        StudentVo studentVo = new StudentVo();
        StudentVo studentVo1 = new StudentVo();
        List<Index> indexList = new ArrayList<Index>();
        indexList.add(new Index.Builder(studentVo).build());
        indexList.add(new Index.Builder(studentVo).build());
        Bulk build = new Bulk.Builder().defaultIndex("school").defaultType("_doc")
                .addAction(indexList).setParameter("pretty", true).build();
        BulkResult execute = jestClient.execute(build);
        System.out.println(execute.getJsonString());
    }

    /**
     * 异步
     */
    @Test
    public void testAsync() {
        Index index = new Index.Builder(studentVo).index("school").build();
        jestClient.executeAsync(index, new JestResultHandler<DocumentResult>() {
            public void completed(DocumentResult documentResult) {
                System.out.println(documentResult.getJsonString());
            }

            public void failed(Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    @Test
    public void searchByJson() throws IOException {
        String query = " \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"must\": [\n" +
                "        {\n" +
                "          \"exists\": {\n" +
                "            \"field\": \"name\"\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }";
        Search search = new Search.Builder(query)
                .addIndex("school")
                .setParameter("pretty", true)
                .setParameter("from", 0)
                .setParameter("size", 5).build();
        SearchResult result = jestClient.execute(search);
        SearchResult.Hit<StudentVo, Void> firstHit = result.getFirstHit(StudentVo.class);
        System.out.println(firstHit.source.getName());
        List<SearchResult.Hit<StudentVo, Void>> hits = result.getHits(StudentVo.class);
        for (SearchResult.Hit<StudentVo, Void> hit : hits) {
            System.out.println(hit.source.getName());
        }
    }

}
