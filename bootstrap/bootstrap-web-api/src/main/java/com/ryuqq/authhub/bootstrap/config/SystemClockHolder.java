package com.ryuqq.authhub.bootstrap.config;

import com.ryuqq.authhub.domain.common.util.ClockHolder;
import java.time.Clock;

/**
 * System Clock 기반 ClockHolder 구현체
 *
 * <p>System Default Zone Clock을 제공하는 실제 운영 환경용 구현체입니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>✅ ClockHolder 인터페이스 구현
 *   <li>✅ Bootstrap Layer에서 Bean으로 등록 (Infrastructure 구현체)
 *   <li>✅ 테스트 환경에서는 FixedClockHolder로 교체 가능
 * </ul>
 *
 * <p><strong>위치:</strong> Bootstrap Layer (Infrastructure 구현체)
 *
 * <p>Application Layer는 Domain을 호출하는 Orchestrator 역할만 수행하므로, Infrastructure 구현체는 Bootstrap Layer에
 * 위치해야 합니다.
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
    public Clock clock() {
        return clock;
    }
}
