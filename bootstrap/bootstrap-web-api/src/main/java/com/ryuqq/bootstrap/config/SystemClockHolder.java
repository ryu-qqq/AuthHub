package com.ryuqq.bootstrap.config;

import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.common.util.ClockHolder;
import java.time.Instant;

/**
 * SystemClockHolder - ClockHolder 구현체
 *
 * <p>System Clock을 사용하는 ClockHolder 구현체입니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>Bootstrap Layer에서 Bean으로 등록
 *   <li>Domain Layer의 ClockHolder 인터페이스 구현
 *   <li>System 시간 기반 Clock 제공
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public final class SystemClockHolder implements ClockHolder {

    private static final Clock SYSTEM_CLOCK = Instant::now;

    @Override
    public Clock clock() {
        return SYSTEM_CLOCK;
    }
}
