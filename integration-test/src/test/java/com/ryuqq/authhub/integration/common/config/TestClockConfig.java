package com.ryuqq.authhub.integration.common.config;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
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
}
