package com.ryuqq.authhub.bootstrap.config;

import com.ryuqq.authhub.domain.common.util.ClockHolder;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Core Configuration.
 *
 * <p>애플리케이션 핵심 빈들을 등록합니다.
 */
@Configuration
public class CoreConfiguration {

    /**
     * Clock 빈 등록.
     *
     * <p>시스템 기본 타임존 Clock을 제공합니다. 테스트 시 Mock Clock으로 교체 가능합니다.
     *
     * @return Clock 인스턴스
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    /**
     * ClockHolder Bean 등록
     *
     * <p>SystemClockHolder 구현체를 반환합니다.
     *
     * <p>Domain, Application, Persistence 등 모든 레이어에서 주입받아 사용할 수 있습니다.
     *
     * @param clock System Clock Bean
     * @return SystemClockHolder 구현체
     * @author ryu-qqq
     * @since 2025-11-21
     */
    @Bean
    public ClockHolder clockHolder(Clock clock) {
        return new SystemClockHolder(clock);
    }
}
