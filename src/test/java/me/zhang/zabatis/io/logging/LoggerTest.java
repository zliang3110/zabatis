package me.zhang.zabatis.io.logging;

import me.zhang.zabatis.io.ResourceTest;
import me.zhangll.zabatis.logging.Log;
import me.zhangll.zabatis.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class LoggerTest {

    @Test
    public void getLoggerTest(){

        Log log = LogFactory.getLog(ResourceTest.class.getName());
        log.error("Hi...");
    }

    @Test
    public void OrginalLoggerTest(){
        Logger logger = LogManager.getLogger("aaa");

        logger.error("123");
    }
}
