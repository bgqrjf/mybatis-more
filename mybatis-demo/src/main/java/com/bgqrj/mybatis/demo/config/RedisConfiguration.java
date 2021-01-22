package com.bgqrj.mybatis.demo.config;

import com.bgqrj.mybatis.demo.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.*;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 描述: redis配置类
 *
 * @author yangxin
 * 日期: 2020/7/8
 */
@Configuration
public class RedisConfiguration extends CachingConfigurerSupport {

    @Resource
    private LettuceConnectionFactory lettuceConnectionFactory;

    private static final long DEFAULT_CACHE_REMOVE_SECOND = 86400L;


    /**
     * redis 工具模型
     *
     * @param <K> key
     * @param <V> value
     * @return redisTemplate
     */
    @Bean
    public <K, V> RedisTemplate<K, V> redisTemplate() {
        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 功能描述: string类型的template
     * 键和值都是String,主要功能就是方便String类型的存储
     */

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return new StringRedisTemplate(lettuceConnectionFactory);
    }

    /**
     * 功能描述: 缓存管理器
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        //缓存过期时间（全局）
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                //key序列化方式
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                //value序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(DEFAULT_CACHE_REMOVE_SECOND));

        //缓存过期时间(定制指定的key过期时间)
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(lettuceConnectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .withInitialCacheConfigurations(getRedisCacheConfigurationMap());

        return builder.build();
    }

    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    private RedisSerializer<Object> valueSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //将数据库里的数据按一定类型存储到redis缓存中，enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)过时
        om.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }


    /**
     * 定制key的缓存相关配置
     *
     * @return Map<String, RedisCacheConfiguration>
     */
    private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>(2);
        return redisCacheConfigurationMap;
    }

    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(long seconds) {

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.entryTtl(Duration.ofSeconds(seconds))
                //key序列化方式
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                //value序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()));
        return redisCacheConfiguration;
    }

    /**
     * 功能描述: 缓存键的生成器
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            // 目标类的类名
            sb.append(target.getClass().getName());
            // 目标方法名
            sb.append('#').append(method.getName());
            if (params.length == 0) {
                sb.append("()");
            } else {
                sb.append("(");
                for (Object param : params) {
                    if (Objects.nonNull(param)) {
                        sb.append(param).append(StringUtils.REG_EN_COMMA);
                    } else {
                        // 参数为空的时候拼接上null
                        sb.append("null").append(StringUtils.REG_EN_COMMA);
                    }
                }
                //移除最后一个逗号
                sb.deleteCharAt(sb.length() - 1);
                sb.append(")");
            }
            return sb.toString();
        };
    }

    /**
     * 功能描述: 缓存解析器，可以通过定时的方式，对缓存中的内容进行处理
     */
    @Override
    public CacheResolver cacheResolver() {
        return new SimpleCacheResolver(Objects.requireNonNull(cacheManager()));
    }

    /**
     * 功能描述: 缓存异常处理器
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler();
    }

}
