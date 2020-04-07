package code.ponfee.console.service;

import java.io.IOException;

import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;

import code.ponfee.console.BaseTest;

public class RedisTemplateTest extends BaseTest<RedisTemplate<String, byte[]>> {

    public RedisTemplateTest() {
        super("redisTemplate");
    }
    @Test
    public void contextLoads() {
    }

    @Test
    public void deleteAll() throws IOException, InterruptedException {
        //getBean().delete(getBean().keys("*"));
        System.out.println(getBean().keys("*"));
    }

}
