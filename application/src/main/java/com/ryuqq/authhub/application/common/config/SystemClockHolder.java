package com.ryuqq.authhub.application.common.config;

import com.ryuqq.authhub.domain.common.util.ClockHolder;
import java.time.Clock;

/**
 * System Clock 구현체 (Production 환경)
 *
 * <p>Spring Bean으로 등록되어 전역 Singleton으로 사용됩니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>✅ ClockHolder 인터페이스 구현 (DIP)
 *   <li>✅ System Default Zone 사용
 *   <li>✅ Production 환경에서 사용
 *   <li>✅ Test 환경에서는 FixedClockHolder로 교체
 * </ul>
 *
 * <p><strong>Bean 등록:</strong>
 *
 * <pre>{@code
 * @Configuration
 * public class ClockConfig {
 *     @Bean
 *     public Clock clock() {
 *         return Clock.systemDefaultZone();
 *     }
 *
 *     @Bean
 *     public ClockHolder clockHolder(Clock clock) {
 *         return new SystemClockHolder(clock);
 *     }
 * }
 * }</pre>
 *
 * @author ryu-qqq
 * @since 2025-11-21
 */
public class SystemClockHolder implements ClockHolder {

    private final Clock clock;

    public SystemClockHolder(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Clock getClock() {
        return clock;
    }
}
