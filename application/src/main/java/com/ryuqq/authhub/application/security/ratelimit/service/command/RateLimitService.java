package com.ryuqq.authhub.application.security.ratelimit.service.command;

import com.ryuqq.authhub.application.security.ratelimit.assembler.RateLimitAssembler;
import com.ryuqq.authhub.application.security.ratelimit.port.in.CheckRateLimitUseCase;
import com.ryuqq.authhub.application.security.ratelimit.port.in.IncrementRateLimitUseCase;
import com.ryuqq.authhub.application.security.ratelimit.port.in.ResetRateLimitUseCase;
import com.ryuqq.authhub.application.security.ratelimit.port.out.IncrementRateLimitPort;
import com.ryuqq.authhub.application.security.ratelimit.port.out.LoadRateLimitPort;
import com.ryuqq.authhub.application.security.ratelimit.port.out.ResetRateLimitPort;
import com.ryuqq.authhub.domain.security.ratelimit.RateLimitRule;
import com.ryuqq.authhub.domain.security.ratelimit.vo.LimitCount;
import com.ryuqq.authhub.domain.security.ratelimit.vo.TimeWindow;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Rate Limit Service - Rate Limit UseCase 구현체.
 *
 * <p>Redis 기반 분산 Rate Limiting 시스템의 핵심 비즈니스 로직을 담당하는 Command Service입니다.
 * RateLimitRule Aggregate를 활용하여 제한 정책을 적용하고, Redis Port를 통해 카운터를 관리합니다.</p>
 *
 * <p><strong>비즈니스 로직 흐름:</strong></p>
 * <ol>
 *   <li><strong>Check Rate Limit</strong>:
 *     <ul>
 *       <li>Redis에서 현재 카운트 조회 (LoadRateLimitPort)</li>
 *       <li>RateLimitRule 생성 (타입별 정책 적용)</li>
 *       <li>제한 초과 여부 검증 (rule.isExceeded())</li>
 *       <li>결과 반환 (초과 여부, 남은 횟수 등)</li>
 *     </ul>
 *   </li>
 *   <li><strong>Increment Rate Limit</strong>:
 *     <ul>
 *       <li>Redis 카운터 원자적 증가 (IncrementRateLimitPort)</li>
 *       <li>TTL 자동 설정 (시간 윈도우 기반)</li>
 *     </ul>
 *   </li>
 *   <li><strong>Reset Rate Limit</strong>:
 *     <ul>
 *       <li>Redis 카운터 삭제 (ResetRateLimitPort)</li>
 *       <li>관리자 수동 리셋 또는 테스트 초기화</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Transaction 경계 - Redis 연산은 트랜잭션 밖에서 처리 (성능 최적화)</li>
 *   <li>✅ @Transactional 미사용 - Redis는 자체적으로 원자성 보장</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Law of Demeter 준수 - Getter chaining 금지</li>
 *   <li>✅ Port/Adapter 패턴 - 의존성 역전 원칙 준수</li>
 *   <li>✅ Command/Query 분리 - Check(Query), Increment/Reset(Command)</li>
 * </ul>
 *
 * <p><strong>트랜잭션 설계:</strong></p>
 * <ul>
 *   <li>@Transactional 없음 - Redis 연산은 자체적으로 원자성 보장</li>
 *   <li>Redis INCR 명령 - 원자적(Atomic) 연산</li>
 *   <li>DB 트랜잭션 불필요 - 읽기/쓰기가 모두 Redis</li>
 *   <li>성능 최적화 - 트랜잭션 오버헤드 제거</li>
 *   <li>ArchUnit 예외 - Redis 기반 서비스는 @Transactional 불필요</li>
 * </ul>
 *
 * <p><strong>Rate Limit 정책 (타입별):</strong></p>
 * <ul>
 *   <li><strong>IP_BASED</strong>: IP당 100회/분</li>
 *   <li><strong>USER_BASED</strong>: 사용자당 1000회/분</li>
 *   <li><strong>ENDPOINT_BASED</strong>: 엔드포인트당 5000회/분</li>
 * </ul>
 *
 * <p><strong>의존성:</strong></p>
 * <ul>
 *   <li>LoadRateLimitPort - Redis 카운트 조회</li>
 *   <li>IncrementRateLimitPort - Redis 카운트 증가</li>
 *   <li>ResetRateLimitPort - Redis 카운트 삭제</li>
 *   <li>RateLimitAssembler - Domain ↔ Application 변환</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Service
public class RateLimitService implements
        CheckRateLimitUseCase,
        IncrementRateLimitUseCase,
        ResetRateLimitUseCase {

    private final LoadRateLimitPort loadRateLimitPort;
    private final IncrementRateLimitPort incrementRateLimitPort;
    private final ResetRateLimitPort resetRateLimitPort;
    private final RateLimitAssembler rateLimitAssembler;

    /**
     * RateLimitService 생성자.
     *
     * @param loadRateLimitPort Redis 조회 Port
     * @param incrementRateLimitPort Redis 증가 Port
     * @param resetRateLimitPort Redis 리셋 Port
     * @param rateLimitAssembler Domain ↔ Application 변환 Assembler
     * @throws NullPointerException 파라미터가 null인 경우
     */
    public RateLimitService(
            final LoadRateLimitPort loadRateLimitPort,
            final IncrementRateLimitPort incrementRateLimitPort,
            final ResetRateLimitPort resetRateLimitPort,
            final RateLimitAssembler rateLimitAssembler
    ) {
        this.loadRateLimitPort = Objects.requireNonNull(loadRateLimitPort, "LoadRateLimitPort cannot be null");
        this.incrementRateLimitPort = Objects.requireNonNull(incrementRateLimitPort, "IncrementRateLimitPort cannot be null");
        this.resetRateLimitPort = Objects.requireNonNull(resetRateLimitPort, "ResetRateLimitPort cannot be null");
        this.rateLimitAssembler = Objects.requireNonNull(rateLimitAssembler, "RateLimitAssembler cannot be null");
    }

    /**
     * Rate Limit 확인을 수행합니다 (Query Operation).
     *
     * <p><strong>실행 흐름:</strong></p>
     * <ol>
     *   <li>Redis에서 현재 카운트 조회</li>
     *   <li>타입별 RateLimitRule 생성</li>
     *   <li>Assembler를 통해 Result 변환 및 반환</li>
     * </ol>
     *
     * @param command 확인 요청 Command
     * @return 확인 결과 Result
     * @throws IllegalArgumentException command가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public CheckRateLimitUseCase.Result checkRateLimit(final CheckRateLimitUseCase.Command command) {
        Objects.requireNonNull(command, "Command cannot be null");

        // 1. Redis에서 현재 카운트 조회
        final int currentCount = this.loadRateLimitPort.loadCurrentCount(
                command.getIdentifier(),
                command.getEndpoint(),
                command.getType()
        );

        // 2. 타입별 RateLimitRule 생성
        final RateLimitRule rule = this.createRuleByType(command.getType());

        // 3. Assembler를 통해 Result 변환 및 반환
        return this.rateLimitAssembler.toCheckResult(rule, currentCount);
    }

    /**
     * Rate Limit 카운터를 증가시킵니다 (Command Operation).
     *
     * <p><strong>실행 흐름:</strong></p>
     * <ol>
     *   <li>Redis INCR 명령으로 카운터 원자적 증가</li>
     *   <li>첫 증가 시 TTL 자동 설정</li>
     * </ol>
     *
     * <p><strong>주의사항:</strong></p>
     * <ul>
     *   <li>@Transactional 없음 - Redis는 자체적으로 원자성 보장</li>
     *   <li>네트워크 지연 가능 - 비동기 처리 고려 가능</li>
     * </ul>
     *
     * @param command 증가 요청 Command
     * @throws IllegalArgumentException command가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public void incrementRateLimit(final IncrementRateLimitUseCase.Command command) {
        Objects.requireNonNull(command, "Command cannot be null");

        // Redis 카운터 원자적 증가 (TTL 포함)
        this.incrementRateLimitPort.incrementAndGet(
                command.getIdentifier(),
                command.getEndpoint(),
                command.getType(),
                command.getTtlSeconds()
        );
    }

    /**
     * Rate Limit 카운터를 리셋(삭제)합니다 (Command Operation).
     *
     * <p><strong>실행 흐름:</strong></p>
     * <ol>
     *   <li>Redis DEL 명령으로 카운터 삭제</li>
     *   <li>삭제 결과 무시 (멱등성 보장)</li>
     * </ol>
     *
     * @param command 리셋 요청 Command
     * @throws IllegalArgumentException command가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public void resetRateLimit(final ResetRateLimitUseCase.Command command) {
        Objects.requireNonNull(command, "Command cannot be null");

        // Redis 카운터 삭제 (멱등성 보장)
        this.resetRateLimitPort.reset(
                command.getIdentifier(),
                command.getEndpoint(),
                command.getType()
        );
    }

    /**
     * 타입별 RateLimitRule을 생성합니다 (내부 헬퍼 메서드).
     *
     * <p><strong>정책 (현재 구현):</strong></p>
     * <ul>
     *   <li>IP_BASED: 100회/60초</li>
     *   <li>USER_BASED: 1000회/60초</li>
     *   <li>ENDPOINT_BASED: 5000회/60초</li>
     * </ul>
     *
     * <p><strong>향후 개선:</strong></p>
     * <ul>
     *   <li>RateLimitRuleRepository에서 정책 조회 (DB 기반)</li>
     *   <li>설정 파일 기반 정책 관리 (application.yml)</li>
     *   <li>동적 정책 변경 지원</li>
     * </ul>
     *
     * @param type Rate Limit 타입
     * @return 생성된 RateLimitRule
     */
    private RateLimitRule createRuleByType(final com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitType type) {
        // TODO: 향후 DB 또는 설정 파일에서 정책 조회
        // 현재는 하드코딩된 정책 사용

        return switch (type) {
            case IP_BASED -> RateLimitRule.create(
                    type,
                    LimitCount.of(100),  // IP당 100회
                    TimeWindow.ofSeconds(60)  // 60초
            );
            case USER_BASED -> RateLimitRule.create(
                    type,
                    LimitCount.of(1000),  // 사용자당 1000회
                    TimeWindow.ofSeconds(60)
            );
            case ENDPOINT_BASED -> RateLimitRule.create(
                    type,
                    LimitCount.of(5000),  // 엔드포인트당 5000회
                    TimeWindow.ofSeconds(60)
            );
        };
    }
}
