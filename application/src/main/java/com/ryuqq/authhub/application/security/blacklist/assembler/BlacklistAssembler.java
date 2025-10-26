package com.ryuqq.authhub.application.security.blacklist.assembler;

import com.ryuqq.authhub.application.security.blacklist.port.in.AddToBlacklistUseCase;
import com.ryuqq.authhub.domain.security.blacklist.BlacklistedToken;
import com.ryuqq.authhub.domain.security.blacklist.vo.ExpiresAt;
import com.ryuqq.authhub.domain.security.blacklist.vo.Jti;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Blacklist Assembler - BlacklistedToken Domain ↔ UseCase Command 변환.
 *
 * <p>Application Layer의 Assembler 패턴을 따르며, UseCase Command를
 * Domain Aggregate(BlacklistedToken)로 변환하는 책임을 가집니다.</p>
 *
 * <p><strong>책임 범위:</strong></p>
 * <ul>
 *   <li>Command → Domain 변환 (AddToBlacklistUseCase.Command → BlacklistedToken)</li>
 *   <li>Primitive 타입 → Value Object 변환</li>
 *   <li>Law of Demeter 준수 - Domain의 행위 메서드 활용</li>
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
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class BlacklistAssembler {

    /**
     * AddToBlacklistUseCase.Command를 BlacklistedToken으로 변환합니다.
     *
     * <p><strong>변환 흐름:</strong></p>
     * <ol>
     *   <li>Primitive String → Jti Value Object</li>
     *   <li>Primitive long → ExpiresAt Value Object</li>
     *   <li>BlacklistedToken.create() Factory Method 호출</li>
     * </ol>
     *
     * @param command AddToBlacklistUseCase.Command (null 불가)
     * @return BlacklistedToken Aggregate
     * @throws NullPointerException command가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public BlacklistedToken toDomain(final AddToBlacklistUseCase.Command command) {
        Objects.requireNonNull(command, "Command cannot be null");

        // Primitive → Value Object 변환
        final Jti jti = Jti.of(command.getJti());
        final ExpiresAt expiresAt = ExpiresAt.fromEpochSeconds(command.getExpiresAtEpochSeconds());

        // BlacklistedToken Aggregate 생성
        return BlacklistedToken.create(
                jti,
                expiresAt,
                command.getReason()
        );
    }
}
