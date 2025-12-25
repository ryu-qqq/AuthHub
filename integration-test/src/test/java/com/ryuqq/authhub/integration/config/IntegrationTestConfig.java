package com.ryuqq.authhub.integration.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ryuqq.authhub.application.auth.port.out.cache.RefreshTokenCachePort;
import com.ryuqq.authhub.application.common.port.out.CachePort;
import com.ryuqq.authhub.application.common.port.out.DistributedLockPort;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 통합 테스트 환경 설정
 *
 * <p>외부 서비스(Redis 등)를 Mock으로 대체하여 통합 테스트 시 외부 의존성 없이 실행 가능하게 함
 *
 * <p><strong>Mock 대상:</strong>
 *
 * <ul>
 *   <li>CachePort - Redis 캐시 서비스
 *   <li>DistributedLockPort - Redis 분산 락
 *   <li>RefreshTokenCachePort - Refresh Token 캐시
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@TestConfiguration
public class IntegrationTestConfig {

    /**
     * Redis 캐시 서비스 Mock
     *
     * @return CachePort mock 인스턴스
     */
    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public CachePort<?> cachePort() {
        CachePort<?> mock = mock(CachePort.class);
        // 기본 동작 설정
        doNothing().when(mock).evict(anyString());
        doNothing().when(mock).evictByPattern(anyString());
        when(mock.exists(anyString())).thenReturn(false);
        when(mock.get(anyString())).thenReturn(Optional.empty());
        return mock;
    }

    /**
     * Redis 분산 락 Mock
     *
     * @return DistributedLockPort mock 인스턴스
     */
    @Bean
    @Primary
    public DistributedLockPort distributedLockPort() {
        DistributedLockPort mock = mock(DistributedLockPort.class);
        // 락 획득 성공으로 설정
        when(mock.tryLock(any(), anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        doNothing().when(mock).unlock(any());
        when(mock.isHeldByCurrentThread(any())).thenReturn(true);
        when(mock.isLocked(any())).thenReturn(false);
        return mock;
    }

    /**
     * Refresh Token 캐시 Mock
     *
     * @return RefreshTokenCachePort mock 인스턴스
     */
    @Bean
    @Primary
    public RefreshTokenCachePort refreshTokenCachePort() {
        RefreshTokenCachePort mock = mock(RefreshTokenCachePort.class);
        // 기본 동작 설정
        doNothing().when(mock).save(any(), anyString(), anyLong());
        doNothing().when(mock).deleteByUserId(any());
        doNothing().when(mock).deleteByToken(anyString());
        when(mock.findByUserId(any())).thenReturn(Optional.empty());
        when(mock.findUserIdByToken(anyString())).thenReturn(Optional.empty());
        return mock;
    }
}
