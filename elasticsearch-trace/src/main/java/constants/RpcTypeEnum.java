package constants;

public enum RpcTypeEnum {
    ICE("ice"), WEBSERVICE("ws"), HTTP("http"), DB("db"), REDIS("redis");
    private String rpcName;

    RpcTypeEnum(String rpcName) {
        this.rpcName = rpcName;
    }

    public String getRpcName() {
        return rpcName;
    }
}
