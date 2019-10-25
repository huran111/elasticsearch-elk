package jest;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.indices.CreateIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class JestClientMgr {
    private static final Logger logger = LoggerFactory.getLogger(JestClientMgr.class);
    private String CONFIG_PATH = "property/es-config.properties";
    private Properties properties;
    private volatile JestClient jestClient;

    public JestClientMgr() {
    }

    public JestClientMgr(String configFilePath) {
        CONFIG_PATH = configFilePath;
    }

    public JestClientMgr(Properties properties) {
        this.properties = properties;
    }

    private void init() {
        if (jestClient == null) {
            synchronized (JestClientMgr.class) {
                if (this.properties == null) {
                    properties = new Properties();
                    InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_PATH);
                    if (null == inputStream) {
                        throw new RuntimeException(String.format("config file is not founds:%s", CONFIG_PATH));
                    }
                    try {
                        properties.load(inputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    String esHosts = properties.getProperty("es.hosts");
                    List<String> serverList = new ArrayList<String>(10);
                    for (String address : esHosts.split(",")) {
                        serverList.add("http://" + address);
                    }
                    JestClientFactory factory = new JestClientFactory();
                    factory.setHttpClientConfig(new HttpClientConfig.Builder(serverList).multiThreaded(true)
                            .maxTotalConnection(20).defaultMaxTotalConnectionPerRoute(5)
                            .discoveryEnabled(true).discoveryFrequency(1, TimeUnit.SECONDS)
                            .build()
                    );
                    jestClient = factory.getObject();
                }
            }
        }
    }

    public JestClient getJestClient() {
        if (jestClient == null) {
            init();
        }
        return jestClient;
    }

    public JestResult createIndex(String indexName, int number_of_shard, int number_of_replice) throws IOException {
        Map<String, Object> settingsMap = new HashMap<String, Object>();
        settingsMap.put("number_of_shards", number_of_shard);
        settingsMap.put("number_of_replicas", number_of_replice);
        return getJestClient().execute(new CreateIndex.Builder(indexName).setParameter("pretty", true).settings(settingsMap).build());
    }
}
