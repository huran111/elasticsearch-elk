package rest;

import ch.qos.logback.core.helpers.ThrowableToStringArray;
import client.TraceClient;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.net.NetUtil;
import com.alibaba.fastjson.JSON;
import constants.RpcTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import thread.ThreadContext;
import thread.TraceInfo;
import vo.RpcTraceInfoVo;

import java.util.Date;

public class RestTraceClient {
    private static final Logger logger = LoggerFactory.getLogger(RestTraceClient.class);
    private static RestTemplate restTemplate = new RestTemplate();
    private static final FastDateFormat ISO_DATETIME_TIME = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

    public static <T> T request(String url, HttpMethod httpMethod, Object body, Class<String> stringClass) {
        T respinseEntityBody = null;
        TraceInfo traceInfo = ThreadContext.getTraceInfo();
        if (traceInfo == null) {
            traceInfo = new TraceInfo();
        } else {
            traceInfo.addSequenceNo();
        }
        MDC.put("traceId", traceInfo.getTraceId());
        MDC.put("rpcId", traceInfo.getRpcId());
        long startTime = System.currentTimeMillis();
        RpcTraceInfoVo rpcTraceInfoVo = new RpcTraceInfoVo();
        rpcTraceInfoVo.setRequestDateTime(ISO_DATETIME_TIME.format(new Date(startTime)));
        rpcTraceInfoVo.setTraceId(traceInfo.getTraceId());
        rpcTraceInfoVo.setRpcId(traceInfo.getRpcId());
        rpcTraceInfoVo.setRpcType(RpcTypeEnum.HTTP.getRpcName());
        rpcTraceInfoVo.setServiceCategory("rest");
        rpcTraceInfoVo.setServerName(url);
        rpcTraceInfoVo.setMethodName(httpMethod.name());
        rpcTraceInfoVo.setResponseJson(JSON.toJSONString(body));
        rpcTraceInfoVo.setServerHost(url);
        rpcTraceInfoVo.setClientHost(NetUtil.getLocalhostStr());
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("traceId", traceInfo.getTraceId());
            httpHeaders.add("rpcId", traceInfo.getRpcId());
            HttpEntity<Object> httpEntity = new HttpEntity<Object>(body, httpHeaders);
            ResponseEntity responseEntity = restTemplate.exchange(url, httpMethod, httpEntity, stringClass);
            respinseEntityBody = (T) responseEntity.getBody();
            long runTIme = System.currentTimeMillis() - startTime;
            rpcTraceInfoVo.setResponseJson(JSON.toJSONString(respinseEntityBody));
            rpcTraceInfoVo.setRunTime(runTIme);
            rpcTraceInfoVo.setResult("ok");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            long runTime = System.currentTimeMillis() - startTime;
            String traceStr[] = ThrowableToStringArray.convert(e);
            StringBuffer sb = new StringBuffer();
            for (String s : traceStr) {
                sb.append(s);
                sb.append("\n");
            }
            rpcTraceInfoVo.setResponseJson(sb.toString());
            rpcTraceInfoVo.setResult("ERROR");
            rpcTraceInfoVo.setRunTime(runTime);
        } finally {
            TraceClient.sendTraceInfo(rpcTraceInfoVo);
        }
        return respinseEntityBody;
    }
}
