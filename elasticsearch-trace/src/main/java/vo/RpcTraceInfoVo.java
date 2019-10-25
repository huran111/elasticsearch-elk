package vo;

import lombok.Data;

@Data
public class RpcTraceInfoVo {
    private String traceId;
    private String rpcId;
    private String rpcType;
    private String clientName;
    private String serviceCategory;
    private String methodName;
    private String serverName;
    private String serverHost;
    private String clientHost;
    private String requestDateTime;
    private String result;
    private String responseJson;
    private long runTime;
    private String env;
    private String requestJson;
}
