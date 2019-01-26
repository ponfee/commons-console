package code.ponfee.console.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import code.ponfee.commons.jedis.spring.BytesRedisSerializer;
import code.ponfee.commons.jedis.spring.KryoRedisSerializer;

/**
 * RedisTemplate初始化
 *
 * @author Ponfee
 */
@Configuration
@EnableCaching
public class RedisTemplateConfig {

    @Bean("strRedisTemplate")
    public RedisTemplate<String, String> strRedisTemplate(
        RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory);
        template.setEnableTransactionSupport(false);
        template.setExposeConnection(false);
        return template;
    }

    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(
        RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new KryoRedisSerializer<>());

        template.setEnableTransactionSupport(false);
        template.setExposeConnection(false);
        template.afterPropertiesSet();
        return template;
    }

    @Bean("bytRedisTemplate")
    public RedisTemplate<String, byte[]> bytRedisTemplate(
        RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new BytesRedisSerializer());

        template.setEnableTransactionSupport(false);
        template.setExposeConnection(false);
        template.afterPropertiesSet();
        return template;
    }

}
