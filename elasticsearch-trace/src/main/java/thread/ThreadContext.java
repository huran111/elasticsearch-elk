package thread;

import vo.RpcTraceInfoVo;

import javax.jws.Oneway;
import java.util.HashMap;
import java.util.Map;

public class ThreadContext {

    private final static ThreadLocal<Map<String, Object>> CTX_HOLDER = new ThreadLocal<Map<String, Object>>();

    static {
        CTX_HOLDER.set(new HashMap<String, Object>());
    }

    /**
     * 添加内容到线程上下文中
     */
    public final static void putContext(String key, Object value) {
        Map<String, Object> ctx = CTX_HOLDER.get();
        if (ctx == null) {
            return;
        }
        ctx.put(key, value);
    }

    /**
     * 从线程上下文中获取内容
     */
    public final static <T extends Object> T getContext(String key) {
        Map<String, Object> ctx = CTX_HOLDER.get();
        if (ctx == null) {
            return null;
        }
        return (T) ctx.get(key);
    }

    /**
     * 删除线程上下文
     */
    public final static void remove(String key) {
        Map<String, Object> ctx = CTX_HOLDER.get();
        if (ctx != null) {
            ctx.remove(key);
        }
    }

    /**
     * 上下文中是否包含key
     */
    public final static boolean contains(String key) {
        Map<String, Object> ctx = CTX_HOLDER.get();
        if (ctx != null) {
            return ctx.containsKey(key);
        }
        return false;
    }

    /**
     * 清空线程上下文
     */
    public final static void clear() {
        CTX_HOLDER.set(null);
    }

    public final static void init() {
        CTX_HOLDER.set(new HashMap<String, Object>());
    }

    /**
     * 设置调用栈信息
     */
    public final static void putTraceInfo(TraceInfo traceInfo) {
        putContext(TRACE_INFO_KEY, traceInfo);
    }

    public final static TraceInfo getTraceInfo() {
        return getContext(TRACE_INFO_KEY);
    }

    public final static void removeTraceInfo() {
        remove(TRACE_INFO_KEY);
    }

    private final static String TRACE_INFO_KEY = "traceinfo";
}
