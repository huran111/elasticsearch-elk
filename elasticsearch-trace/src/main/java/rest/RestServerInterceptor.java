package rest;

import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import thread.ThreadContext;
import thread.TraceInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RestServerInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RestServerInterceptor.class);
    private static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT = FastDateFormat
            .getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ThreadContext.init();
        TraceInfo traceInfo = new TraceInfo();
        String traceId = request.getHeader("traceId");
        String rpcId = request.getHeader("rpcId");
        if (StrUtil.isAllBlank(traceId, rpcId)) {
            traceInfo = new TraceInfo(traceId, rpcId);
            traceInfo.addHierarchy();
        }
        ThreadContext.putTraceInfo(traceInfo);
        MDC.put("traceId", traceInfo.getTraceId());
        MDC.put("rpcId", traceInfo.getRpcId());
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        ThreadContext.removeTraceInfo();
        MDC.remove("traceId");
        MDC.remove("rpcId");
    }
}
