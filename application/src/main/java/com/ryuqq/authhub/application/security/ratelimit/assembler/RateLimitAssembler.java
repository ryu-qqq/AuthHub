package com.ryuqq.authhub.application.security.ratelimit.assembler;

import com.ryuqq.authhub.application.security.ratelimit.port.in.CheckRateLimitUseCase;
import com.ryuqq.authhub.domain.security.ratelimit.RateLimitRule;
import org.springframework.stereotype.Component;

/**
 * RateLimit Assembler - RateLimitRule Domain ↔ UseCase Response 변환.
 *
 * <p>Application Layer의 Assembler 패턴을 따르며, Domain Aggregate(RateLimitRule)를
 * UseCase Response로 변환하는 책임을 가집니다.</p>
 *
 * <p><strong>책임 범위:</strong></p>
 * <ul>
 *   <li>Domain → Response 변환 (RateLimitRule → CheckRateLimitUseCase.Result)</li>
 *   <li>Value Object 추출 및 Primitive 타입 변환</li>
 *   <li>Law of Demeter 준수 - RateLimitRule의 행위 메서드 활용</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Law of Demeter 준수 - Getter chaining 금지</li>
 *   <li>✅ 비즈니스 로직 금지 - 순수 변환 로직만</li>
 *   <li>✅ Port 호출 금지 - 외부 의존성 없음</li>
 * </ul>
 *
 * <p><strong>참고:</strong></p>
 * <p>현재 RateLimitService에서는 이 Assembler를 사용하지 않지만,
 * 향후 복잡한 변환 로직이 필요할 경우를 대비하여 작성되었습니다.
 * 현재는 Result를 직접 생성하는 방식을 사용합니다.</p>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class RateLimitAssembler {

    /**
     * RateLimitRule과 currentCount의 유효성을 검증합니다.
     *
     * @param rule RateLimitRule Domain Aggregate
     * @param currentCount 현재 요청 횟수
     * @throws IllegalArgumentException rule이 null이거나 currentCount가 음수인 경우
     */
    private void validateInput(final RateLimitRule rule, final int currentCount) {
        if (rule == null) {
            throw new IllegalArgumentException("RateLimitRule cannot be null");
        }
        if (currentCount < 0) {
            throw new IllegalArgumentException("Current count cannot be negative");
        }
    }

    /**
     * RateLimitRule과 현재 카운트를 기반으로 CheckRateLimitUseCase.Result를 생성합니다.
     *
     * <p><strong>Law of Demeter 준수:</strong></p>
     * <ul>
     *   <li>❌ {@code rule.getLimitCount().value()} (Getter chaining)</li>
     *   <li>✅ {@code rule.getLimitCountValue()} (Law of Demeter 준수)</li>
     * </ul>
     *
     * @param rule RateLimitRule Domain Aggregate (null 불가)
     * @param currentCount 현재 요청 횟수 (0 이상)
     * @return CheckRateLimitUseCase.Result
     * @throws IllegalArgumentException rule이 null이거나 currentCount가 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public CheckRateLimitUseCase.Result toCheckResult(
            final RateLimitRule rule,
            final int currentCount
    ) {
        validateInput(rule, currentCount);

        // ✅ Law of Demeter 준수 - RateLimitRule의 행위 메서드 활용
        final boolean exceeded = rule.isExceeded(currentCount);
        final int limitCount = rule.getLimitCountValue();
        final long remainingCount = rule.getRemainingCount(currentCount);
        final long timeWindowSeconds = rule.getTimeWindowSeconds();

        return new CheckRateLimitUseCase.Result(
                exceeded,
                currentCount,
                limitCount,
                remainingCount,
                timeWindowSeconds
        );
    }

    /**
     * 제한 초과 시 Result를 생성하는 헬퍼 메서드.
     *
     * <p>제한이 초과된 경우 remainingCount는 0으로 설정됩니다.</p>
     *
     * @param rule RateLimitRule Domain Aggregate
     * @param currentCount 현재 요청 횟수
     * @return 제한 초과 Result
     * @author AuthHub Team
     * @since 1.0.0
     */
    public CheckRateLimitUseCase.Result toExceededResult(
            final RateLimitRule rule,
            final int currentCount
    ) {
        validateInput(rule, currentCount);

        return new CheckRateLimitUseCase.Result(
                true,  // exceeded
                currentCount,
                rule.getLimitCountValue(),
                0L,  // remainingCount (초과 시 0)
                rule.getTimeWindowSeconds()
        );
    }

    /**
     * 제한 이내 시 Result를 생성하는 헬퍼 메서드.
     *
     * @param rule RateLimitRule Domain Aggregate
     * @param currentCount 현재 요청 횟수
     * @return 제한 이내 Result
     * @author AuthHub Team
     * @since 1.0.0
     */
    public CheckRateLimitUseCase.Result toWithinLimitResult(
            final RateLimitRule rule,
            final int currentCount
    ) {
        validateInput(rule, currentCount);

        return new CheckRateLimitUseCase.Result(
                false,  // exceeded
                currentCount,
                rule.getLimitCountValue(),
                rule.getRemainingCount(currentCount),
                rule.getTimeWindowSeconds()
        );
    }
}
