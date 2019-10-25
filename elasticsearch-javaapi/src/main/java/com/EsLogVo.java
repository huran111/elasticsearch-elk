package com;

import lombok.Data;

@Data
public class EsLogVo {
    private String host;
    private String ip;
    private String env;
    private String message;
    private String timestamp;
    private String logger;
    private String level;
    private String thread;
    private String throwable;
    private Location location;
    //private String traceId;
    // private String rpcId;
}

@Data
class Location {
    private String className;
    private String method;
    private String file;
    private String line;
}
