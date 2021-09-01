package code.ponfee.console.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import code.ponfee.commons.jedis.spring.ByteArrayRedisSerializer;
import code.ponfee.commons.jedis.spring.KryoRedisSerializer;

/**
 * RedisTemplate初始化
 *
 * @author Ponfee
 */
@Configuration
@EnableCaching // To support @Cacheable/@CachePut/@CacheEvict annotation
public class RedisTemplateConfig {

    @Bean("stringRedis")
    public RedisTemplate<String, String> stringRedis(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory);
        /*template.setEnableTransactionSupport(false);
        template.setExposeConnection(false);
        template.afterPropertiesSet();*/
        return template;
    }

    @Bean("normalRedis")
    public RedisTemplate<String, Object> normalRedis(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new KryoRedisSerializer<>());
        return template;
    }

    @Bean("bytesRedis")
    public RedisTemplate<String, byte[]> bytesRedis(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new ByteArrayRedisSerializer());
        return template;
    }

}
