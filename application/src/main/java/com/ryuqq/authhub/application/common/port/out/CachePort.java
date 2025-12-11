package com.ryuqq.authhub.application.common.port.out;

import java.time.Duration;
import java.util.Optional;

/**
 * Cache Port (출력 포트)
 *
 * <p>캐시 저장/조회/무효화를 위한 포트입니다.
 *
 * <p><strong>Cache-Aside 패턴:</strong>
 *
 * <ol>
 *   <li>Cache 조회 (CachePort.get)
 *   <li>Cache Miss → DB 조회 (QueryPort)
 *   <li>Cache 저장 (CachePort.set)
 * </ol>
 *
 * <p><strong>Key Naming Convention:</strong>
 *
 * <pre>
 * {namespace}::{entity}::{id}
 * 예: cache::orders::123
 * </pre>
 *
 * @param <T> 캐시 대상 타입
 * @author Development Team
 * @since 1.0.0
 */
public interface CachePort<T> {

    /**
     * 캐시 저장 (기본 TTL)
     *
     * @param key 캐시 키
     * @param value 저장할 값
     */
    void set(String key, T value);

    /**
     * 캐시 저장 (TTL 지정)
     *
     * @param key 캐시 키
     * @param value 저장할 값
     * @param ttl Time-To-Live
     */
    void set(String key, T value, Duration ttl);

    /**
     * 캐시 조회
     *
     * @param key 캐시 키
     * @return Optional<T> (Cache Hit 시 값, Miss 시 Empty)
     */
    Optional<T> get(String key);

    /**
     * 캐시 조회 (타입 지정)
     *
     * @param key 캐시 키
     * @param clazz 타입 클래스
     * @return Optional<T> (Cache Hit 시 값, Miss 시 Empty)
     */
    Optional<T> get(String key, Class<T> clazz);

    /**
     * 캐시 무효화
     *
     * @param key 캐시 키
     */
    void evict(String key);

    /**
     * 패턴 기반 캐시 무효화
     *
     * <p><strong>주의:</strong> KEYS 명령어 사용 금지, SCAN 사용
     *
     * @param pattern 키 패턴 (예: "cache::orders::*")
     */
    void evictByPattern(String pattern);

    /**
     * 캐시 존재 여부 확인
     *
     * @param key 캐시 키
     * @return 존재 여부
     */
    boolean exists(String key);

    /**
     * TTL 조회
     *
     * @param key 캐시 키
     * @return TTL (Duration), 키가 없거나 TTL 없으면 null
     */
    Duration getTtl(String key);
}
