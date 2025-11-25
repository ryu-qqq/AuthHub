package com.ryuqq.bootstrap.config;

import com.ryuqq.authhub.domain.common.util.ClockHolder;
import java.time.Clock;

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
 *   <li>생성자로 Clock 주입받아 사용
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public record SystemClockHolder(Clock clock) implements ClockHolder {}
