package com.ryuqq.authhub.adapter.out.persistence.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisConfig - Redis 설정
 *
 * <p>Redis 연결 및 Template 설정을 담당합니다.
 *
 * <p><strong>설정 항목:</strong>
 *
 * <ul>
 *   <li>RedisTemplate - String Key/Value 직렬화
 *   <li>연결 풀 설정 - redis.yml에서 관리
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Configuration
public class RedisConfig {

    /**
     * String 전용 RedisTemplate
     *
     * <p>Key, Value 모두 String 직렬화를 사용합니다.
     *
     * @param connectionFactory Redis 연결 팩토리
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
