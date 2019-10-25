package com;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.net.NetUtil;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import jest.JestClientMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class ESAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private static final Logger log = LoggerFactory.getLogger(ESAppender.class);
    private static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLS = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private static final FastDateFormat INDEX_NAME_DATE_FORMAT = FastDateFormat.getInstance("yyyy.MM.dd");
    private JestClient jestClient;
    //es索引名称
    String esIndex = "java-log-#date#";
    String esType = "";
    boolean isLocationInfo = true;
    String env = "";
    String esAddress = "";

    public void setEsIndex(String esIndex) {
        this.esIndex = esIndex;
    }

    public void setEsAddress(String esAddress) {
        this.esAddress = esAddress;
    }

    public void setEsType(String esType) {
        this.esType = esType;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    @Override
    protected void append(ILoggingEvent event) {
        log.info("=========================================");
        log.info("env:"+this.env);
        log.info("esAddress"+ esAddress);
        EsLogVo esLogVo = new EsLogVo();
        esLogVo.setHost(NetUtil.getLocalhostStr());
        esLogVo.setIp(NetUtil.getIpByHost(NetUtil.getLocalhostStr()));
        esLogVo.setEnv(this.env);
        esLogVo.setLevel(event.getLevel().toString());
        Location location = new Location();
        StackTraceElement[] callerDataArray = event.getCallerData();
        if (callerDataArray != null && callerDataArray.length > 0) {
            StackTraceElement stackTraceElement = callerDataArray[0];
            location.setClassName(stackTraceElement.getClassName());
            location.setMethod(stackTraceElement.getMethodName());
            location.setFile(stackTraceElement.getFileName());
            location.setLine(Integer.toString(stackTraceElement.getLineNumber()));
        }
        IThrowableProxy tp = event.getThrowableProxy();
        if (tp != null) {
            String throwable = ThrowableProxyUtil.asString(tp);
            esLogVo.setThrowable(throwable);
        }
        esLogVo.setLocation(location);
        esLogVo.setLogger(event.getLoggerName());
        esLogVo.setMessage(event.getFormattedMessage());
        esLogVo.setTimestamp(ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLS.format(new Date(event.getTimeStamp())));
        String esIndex_format = esIndex.replace("#date#", INDEX_NAME_DATE_FORMAT.format(new Date(event.getTimeStamp())));
        Index index = new Index.Builder(esLogVo).index(esIndex_format).type(esType).build();
        try {
            DocumentResult execute = jestClient.execute(index);
            log.info("AAAAAAAAAAAAAAAAAAAAAA  :"+execute.getJsonString());
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        log.info("================================================");
        log.info("================================================");
        log.info("================================================");

        super.start();
        Properties properties = new Properties();
        properties.put("es.hosts", esAddress);
        JestClientMgr jestClientMgr = new JestClientMgr(properties);
        jestClient = jestClientMgr.getJestClient();
    }

    @Override
    public void stop() {
        super.stop();
        try {
            jestClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
