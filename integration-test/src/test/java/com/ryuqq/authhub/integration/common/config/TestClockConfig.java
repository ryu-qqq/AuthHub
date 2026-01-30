package com.ryuqq.authhub.integration.common.config;

import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 테스트용 Clock 설정.
 *
 * <p>시간 의존 테스트에서 일관성을 보장하기 위해 고정된 시간을 사용. 모든 시간 관련 테스트는 이 설정을 통해 예측 가능한 결과를 얻음.
 *
 * <p>고정 시간: 2026-01-30T10:00:00Z (Asia/Seoul)
 */
@TestConfiguration
public class TestClockConfig {

    /** 테스트용 고정 시간. 2026-01-30 10:00:00 UTC */
    public static final Instant FIXED_INSTANT = Instant.parse("2026-01-30T10:00:00Z");

    /** 테스트 타임존. */
    public static final ZoneId TEST_ZONE = ZoneId.of("Asia/Seoul");

    /**
     * 고정된 Clock Bean. 프로덕션 Clock을 오버라이드하여 테스트에서 일관된 시간 제공.
     *
     * @return 고정된 Clock
     */
    @Bean
    @Primary
    public Clock clock() {
        return Clock.fixed(FIXED_INSTANT, TEST_ZONE);
    }

    /**
     * 테스트용 ClockHolder Bean. Domain Layer의 ClockHolder 인터페이스를 구현하여 고정된 시간 제공.
     *
     * @return ClockHolder (고정 시간 사용)
     */
    @Bean
    @Primary
    public ClockHolder clockHolder() {
        Clock fixedClock = Clock.fixed(FIXED_INSTANT, TEST_ZONE);
        return () -> fixedClock;
    }

    /**
     * 테스트용 UuidHolder Bean. Domain Layer의 UuidHolder 인터페이스를 구현하여 UUID 제공.
     *
     * @return UuidHolder (랜덤 UUID 생성)
     */
    @Bean
    @Primary
    public UuidHolder uuidHolder() {
        return UUID::randomUUID;
    }
}
