package mybatis;

import client.TraceClient;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.sun.rowset.internal.Row;
import constants.RpcTypeEnum;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.javassist.tools.reflect.Metaobject;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plugins.MvcInterceptor;
import thread.ThreadContext;
import thread.TraceInfo;
import vo.RpcTraceInfoVo;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Properties;


@Intercepts({@org.apache.ibatis.plugin.Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
        , @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class})})
public class MybatisInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(MybatisInterceptor.class);
    private static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT = FastDateFormat
            .getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
    private Properties properties;

    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parmeter = null;
        if (invocation.getArgs().length > 1) {
            parmeter = invocation.getArgs()[1];
        }
        String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parmeter);
        Configuration configuration = mappedStatement.getConfiguration();
        Object retuenValue = null;
        long startTIme = System.currentTimeMillis();
        retuenValue = invocation.proceed();
        long runTime = System.currentTimeMillis() - startTIme;
        String jdbcUrl = "";
        DataSource dataSource = configuration.getEnvironment().getDataSource();
        if (dataSource instanceof DruidDataSource) {
            jdbcUrl = ((DruidDataSource) dataSource).getUrl();
        }
        send2Trace(jdbcUrl, configuration.getDatabaseId(), sqlId, showSql(configuration, boundSql), retuenValue,runTime);
        return retuenValue;
    }

    private void send2Trace(String jdbcUrl, String databaseId, String sqlId, String showSql, Object retuenValue, long runTime) {
        TraceInfo traceInfo = ThreadContext.getTraceInfo();
        if (traceInfo != null) {
            traceInfo.addSequenceNo();
            long beginTIme = System.currentTimeMillis();
            RpcTraceInfoVo rpcTraceInfoVo = new RpcTraceInfoVo();
            try {
                rpcTraceInfoVo.setRequestDateTime(ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date(beginTIme)));
                rpcTraceInfoVo.setTraceId(traceInfo.getTraceId());
                rpcTraceInfoVo.setRpcId(traceInfo.getRpcId());
                rpcTraceInfoVo.setRpcType(RpcTypeEnum.DB.getRpcName());
                rpcTraceInfoVo.setServiceCategory("mybatis");
                String sqlIdStr = StrUtil.subAfter(sqlId, "mapper.", false);
                rpcTraceInfoVo.setServerName(StrUtil.subAfter(sqlIdStr, ".", true));
                rpcTraceInfoVo.setMethodName(StrUtil.subAfter(sqlId, ".", true));
                rpcTraceInfoVo.setRequestJson(showSql);
                rpcTraceInfoVo.setServerHost(StrUtil.subBetween(jdbcUrl, "jdbc:", "?"));
                rpcTraceInfoVo.setRunTime(runTime);
                rpcTraceInfoVo.setResult("ok");
                rpcTraceInfoVo.setResponseJson(JSON.toJSONString(retuenValue));
            } catch (Exception e) {
                rpcTraceInfoVo.setResult("ERROR");
                e.printStackTrace();
            } finally {
                TraceClient.sendTraceInfo(rpcTraceInfoVo);
            }
        }
    }

    public static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        try {
            if (parameterMappings.size() > 0 && parameterObject != null) {
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
                } else {
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    for (ParameterMapping parameterMapping : parameterMappings) {
                        String propertyName = parameterMapping.getProperty();
                        if (metaObject.hasGetter(propertyName)) {
                            Object o = metaObject.getValue(propertyName);
                            sql = sql.replaceFirst("\\?", getParameterValue(o));
                        } else if (boundSql.hasAdditionalParameter(propertyName)) {
                            Object o = boundSql.getAdditionalParameter(propertyName);
                            sql = sql.replaceFirst("\\?", getParameterValue(o));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql;
    }

    private static String getParameterValue(Object parameterObject) {

        return null;
    }
}
