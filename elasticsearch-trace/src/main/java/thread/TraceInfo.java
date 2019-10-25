package thread;


import cn.hutool.core.util.StrUtil;
import io.searchbox.strings.StringUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TraceInfo {
    //跟踪ID
    private String traceId;
    //rpc调用层级，从1.1开始 每新的一级结尾增加1
    private String hierarchy;
    //当前序列级别
    private AtomicInteger sequenceNo;

    public TraceInfo addSequenceNo() {
        this.sequenceNo.incrementAndGet();
        return this;
    }

    public TraceInfo addHierarchy() {
        this.hierarchy = getRpcId();
        this.sequenceNo = new AtomicInteger(0);
        return this;
    }

    public TraceInfo(String traceId, String rpcId) {
        this.traceId = traceId;
        setRpcId(rpcId);
    }

    public TraceInfo(String traceId, String rpcId, Map<String, String> userInfo) {
        this.traceId = traceId;
        setRpcId(rpcId);
    }

    public TraceInfo() {
        this.traceId = UUID.randomUUID().toString().replace("-", "");
        this.hierarchy = "";
        this.sequenceNo = new AtomicInteger(1);
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    /**
     * 设置rpc调用层级 从1.1开始 每级递增0.1
     *
     * @param rpcId
     */
    private void setRpcId(String rpcId) {
        int lastDotIndex = StrUtil.lastIndexOf(rpcId, ".", 0, true);
        if (lastDotIndex > -1) {
            this.hierarchy = rpcId.substring(0, lastDotIndex);
            this.sequenceNo = new AtomicInteger(Integer.parseInt(rpcId.substring(lastDotIndex + 1)));
        } else {
            this.hierarchy = "";
            this.sequenceNo = new AtomicInteger(Integer.parseInt(rpcId));
        }
    }

    public String getRpcId() {
        if (StringUtils.isBlank(hierarchy)) {
            return Integer.toString(sequenceNo.get()) + "";
        }
        return hierarchy + "." + sequenceNo.get();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TraceInfo{");
        sb.append("traceId='").append(traceId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
