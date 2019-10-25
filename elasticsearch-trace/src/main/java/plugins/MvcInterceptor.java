package plugins;

import client.TraceClient;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.net.NetUtil;
import com.alibaba.fastjson.JSON;
import constants.RpcTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import thread.ThreadContext;
import thread.TraceInfo;
import vo.RpcTraceInfoVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


public class MvcInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MvcInterceptor.class);
    private static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT = FastDateFormat
            .getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("preHandle.....................");
        this.initTraceData(request, handler);
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        this.finishTraceData(request, ex);
    }

    private void finishTraceData(HttpServletRequest request, Exception ex) {
        try {
            RpcTraceInfoVo traceInfoVo = (RpcTraceInfoVo) request.getAttribute("rpcTraceInfoVo");
            if (traceInfoVo != null) {
                long beginTime = Long.parseLong((String) request.getAttribute("beginTime"));
                traceInfoVo.setRunTime(System.currentTimeMillis() - beginTime);
                if (ex == null) {
                    traceInfoVo.setResult("ok");
                } else {
                    traceInfoVo.setResult("ERROR");
                    traceInfoVo.setResponseJson(ex.getMessage());
                }

                TraceClient.sendTraceInfo(traceInfoVo);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            request.removeAttribute("rpcTraceInfoVo");
            ThreadContext.removeTraceInfo();
            MDC.remove("traceId");
            MDC.remove("rpcId");

        }

    }

    private void initTraceData(HttpServletRequest request, Object handler) {
        request.setAttribute("beginTime", System.currentTimeMillis() + "");
        ThreadContext.init();
        TraceInfo traceInfo = new TraceInfo();
        if (handler != null && handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RpcTraceInfoVo rpcTraceInfoVo = new RpcTraceInfoVo();
            rpcTraceInfoVo.setRequestDateTime(ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
            rpcTraceInfoVo.setTraceId(traceInfo.getTraceId());
            rpcTraceInfoVo.setRpcId(traceInfo.getRpcId());
            rpcTraceInfoVo.setRpcType(RpcTypeEnum.HTTP.getRpcName());
            rpcTraceInfoVo.setServiceCategory("mvc");
            rpcTraceInfoVo.setServerName(handlerMethod.getBean().getClass().getSimpleName());
            rpcTraceInfoVo.setMethodName(handlerMethod.getMethod().getName());
            rpcTraceInfoVo.setRequestJson(JSON.toJSONString(request.getParameterMap()));
            rpcTraceInfoVo.setServerHost(NetUtil.getLocalhostStr() + ":" + request.getLocalPort() + request.getServletPath());
            rpcTraceInfoVo.setClientHost(request.getRemoteAddr());
            rpcTraceInfoVo.setClientName(request.getServerName());
            request.setAttribute("rpcTraceInfoVo", rpcTraceInfoVo);
            Convert.toStr(12);
        }
        traceInfo.addHierarchy();
        ThreadContext.putTraceInfo(traceInfo);
        MDC.put("traceId", traceInfo.getTraceId());
        MDC.put("rpcId", traceInfo.getRpcId());

    }
}
