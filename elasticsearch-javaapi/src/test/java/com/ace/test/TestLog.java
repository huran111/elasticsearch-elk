package com.ace.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TestLog {
    private static final Logger logger= LoggerFactory.getLogger(TestLog.class);
    @Test
    public void testLog() throws InterruptedException {
        logger.info("test info message");
        logger.error("这是一条异常信息",new Exception("err"));
        logger.debug("debug hello 世界");
        logger.warn("注意...");
        TimeUnit.SECONDS.sleep(10);
    }
}
