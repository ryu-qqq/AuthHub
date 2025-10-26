package com.ryuqq.authhub.application.security.blacklist.assembler;

import com.ryuqq.authhub.application.security.blacklist.port.in.AddToBlacklistUseCase;
import com.ryuqq.authhub.domain.security.blacklist.BlacklistedToken;
import com.ryuqq.authhub.domain.security.blacklist.vo.BlacklistReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * BlacklistAssembler 단위 테스트.
 *
 * <p>BlacklistAssembler의 Command → Domain 변환 로직을 검증하는 단위 테스트입니다.
 * 외부 의존성이 없는 순수 변환 로직만 테스트합니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>✅ Spring Context 로딩 없음 (빠른 실행)</li>
 *   <li>✅ 외부 의존성 없음 (Pure Java)</li>
 *   <li>✅ Given-When-Then 패턴</li>
 *   <li>✅ BDD 스타일 (given, when, then)</li>
 * </ul>
 *
 * <p><strong>테스트 케이스:</strong></p>
 * <ul>
 *   <li>✅ 정상 케이스: 유효한 Command → BlacklistedToken 변환</li>
 *   <li>✅ 예외 케이스: null Command</li>
 *   <li>✅ 다양한 BlacklistReason 테스트</li>
 *   <li>✅ 경계값 테스트</li>
 * </ul>
 *
 * <p><strong>테스트 태그:</strong></p>
 * <ul>
 *   <li>@Tag("unit") - 단위 테스트 (빠른 실행, 외부 의존성 없음)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("BlacklistAssembler 단위 테스트")
@Tag("unit")
class BlacklistAssemblerTest {

    private BlacklistAssembler blacklistAssembler;

    @BeforeEach
    void setUp() {
        this.blacklistAssembler = new BlacklistAssembler();
    }

    @Test
    @DisplayName("Domain 변환 성공 - 유효한 Command with LOGOUT reason")
    void toDomain_Success_WithLogoutReason() {
        // Given: LOGOUT 사유로 블랙리스트 추가 Command
        AddToBlacklistUseCase.Command command = new AddToBlacklistUseCase.Command(
                "unique-jwt-id-123",
                1735689600L,  // 2025-01-01 00:00:00 UTC
                BlacklistReason.LOGOUT
        );

        // When: Domain으로 변환
        BlacklistedToken token = blacklistAssembler.toDomain(command);

        // Then: BlacklistedToken이 올바르게 생성됨
        assertThat(token).isNotNull();
        assertThat(token.getJti()).isNotNull();
        assertThat(token.getJti().asString()).isEqualTo("unique-jwt-id-123");
        assertThat(token.getExpiresAt()).isNotNull();
        assertThat(token.getExpiresAt().toEpochSeconds()).isEqualTo(1735689600L);
        assertThat(token.getReason()).isEqualTo(BlacklistReason.LOGOUT);
    }

    @Test
    @DisplayName("Domain 변환 성공 - 유효한 Command with SECURITY_BREACH reason")
    void toDomain_Success_WithSecurityBreachReason() {
        // Given: SECURITY_BREACH 사유로 블랙리스트 추가 Command
        AddToBlacklistUseCase.Command command = new AddToBlacklistUseCase.Command(
                "security-breach-token-456",
                1735776000L,  // 2025-01-02 00:00:00 UTC
                BlacklistReason.SECURITY_BREACH
        );

        // When: Domain으로 변환
        BlacklistedToken token = blacklistAssembler.toDomain(command);

        // Then: BlacklistedToken이 올바르게 생성됨
        assertThat(token).isNotNull();
        assertThat(token.getJti()).isNotNull();
        assertThat(token.getJti().asString()).isEqualTo("security-breach-token-456");
        assertThat(token.getExpiresAt()).isNotNull();
        assertThat(token.getExpiresAt().toEpochSeconds()).isEqualTo(1735776000L);
        assertThat(token.getReason()).isEqualTo(BlacklistReason.SECURITY_BREACH);
    }

    @Test
    @DisplayName("Domain 변환 성공 - 유효한 Command with PASSWORD_CHANGE reason")
    void toDomain_Success_WithPasswordChangeReason() {
        // Given: PASSWORD_CHANGE 사유로 블랙리스트 추가 Command
        AddToBlacklistUseCase.Command command = new AddToBlacklistUseCase.Command(
                "password-change-token-789",
                1735862400L,  // 2025-01-03 00:00:00 UTC
                BlacklistReason.PASSWORD_CHANGE
        );

        // When: Domain으로 변환
        BlacklistedToken token = blacklistAssembler.toDomain(command);

        // Then: BlacklistedToken이 올바르게 생성됨
        assertThat(token).isNotNull();
        assertThat(token.getJti()).isNotNull();
        assertThat(token.getJti().asString()).isEqualTo("password-change-token-789");
        assertThat(token.getExpiresAt()).isNotNull();
        assertThat(token.getExpiresAt().toEpochSeconds()).isEqualTo(1735862400L);
        assertThat(token.getReason()).isEqualTo(BlacklistReason.PASSWORD_CHANGE);
    }

    @Test
    @DisplayName("Domain 변환 실패 - Command가 null")
    void toDomain_Failure_NullCommand() {
        // When & Then: null Command는 IllegalArgumentException 발생
        assertThatThrownBy(() -> blacklistAssembler.toDomain(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Command cannot be null");
    }

    @Test
    @DisplayName("Domain 변환 성공 - expiresAt이 0 (즉시 만료)")
    void toDomain_Success_WithZeroExpiresAt() {
        // Given: expiresAt이 0인 Command (즉시 만료)
        AddToBlacklistUseCase.Command command = new AddToBlacklistUseCase.Command(
                "immediate-expiry-token",
                0L,
                BlacklistReason.LOGOUT
        );

        // When: Domain으로 변환
        BlacklistedToken token = blacklistAssembler.toDomain(command);

        // Then: BlacklistedToken이 올바르게 생성됨
        assertThat(token).isNotNull();
        assertThat(token.getJti()).isNotNull();
        assertThat(token.getJti().asString()).isEqualTo("immediate-expiry-token");
        assertThat(token.getExpiresAt()).isNotNull();
        assertThat(token.getExpiresAt().toEpochSeconds()).isEqualTo(0L);
        assertThat(token.getReason()).isEqualTo(BlacklistReason.LOGOUT);
    }

    @Test
    @DisplayName("Domain 변환 성공 - expiresAt이 매우 큰 값 (먼 미래)")
    void toDomain_Success_WithLargeExpiresAt() {
        // Given: expiresAt이 매우 큰 값 (2099-12-31)
        AddToBlacklistUseCase.Command command = new AddToBlacklistUseCase.Command(
                "far-future-token",
                4102444800L,  // 2099-12-31 00:00:00 UTC
                BlacklistReason.LOGOUT
        );

        // When: Domain으로 변환
        BlacklistedToken token = blacklistAssembler.toDomain(command);

        // Then: BlacklistedToken이 올바르게 생성됨
        assertThat(token).isNotNull();
        assertThat(token.getJti()).isNotNull();
        assertThat(token.getJti().asString()).isEqualTo("far-future-token");
        assertThat(token.getExpiresAt()).isNotNull();
        assertThat(token.getExpiresAt().toEpochSeconds()).isEqualTo(4102444800L);
        assertThat(token.getReason()).isEqualTo(BlacklistReason.LOGOUT);
    }

    @Test
    @DisplayName("Domain 변환 성공 - 특수 문자 포함 JTI")
    void toDomain_Success_WithSpecialCharactersInJti() {
        // Given: 특수 문자가 포함된 JTI (Base64 URL-safe)
        AddToBlacklistUseCase.Command command = new AddToBlacklistUseCase.Command(
                "abc123-DEF_456.xyz",
                1735689600L,
                BlacklistReason.LOGOUT
        );

        // When: Domain으로 변환
        BlacklistedToken token = blacklistAssembler.toDomain(command);

        // Then: BlacklistedToken이 올바르게 생성됨
        assertThat(token).isNotNull();
        assertThat(token.getJti()).isNotNull();
        assertThat(token.getJti().asString()).isEqualTo("abc123-DEF_456.xyz");
        assertThat(token.getExpiresAt()).isNotNull();
        assertThat(token.getReason()).isEqualTo(BlacklistReason.LOGOUT);
    }

    @Test
    @DisplayName("Value Object 변환 검증 - Jti와 ExpiresAt이 올바르게 생성됨")
    void toDomain_ValueObjectConversion_CorrectlyCreated() {
        // Given: 유효한 Command
        AddToBlacklistUseCase.Command command = new AddToBlacklistUseCase.Command(
                "test-jti-value",
                1735689600L,
                BlacklistReason.LOGOUT
        );

        // When: Domain으로 변환
        BlacklistedToken token = blacklistAssembler.toDomain(command);

        // Then: Value Object들이 올바르게 생성됨
        assertThat(token.getJti()).isNotNull();
        assertThat(token.getJti().asString()).isEqualTo("test-jti-value");

        assertThat(token.getExpiresAt()).isNotNull();
        assertThat(token.getExpiresAt().toEpochSeconds()).isEqualTo(1735689600L);

        assertThat(token.getReason()).isNotNull();
        assertThat(token.getReason()).isEqualTo(BlacklistReason.LOGOUT);
    }

    @Test
    @DisplayName("동일한 Command로 여러 번 변환 - 멱등성 보장")
    void toDomain_Idempotency_MultipleCallsWithSameCommand() {
        // Given: 동일한 Command
        AddToBlacklistUseCase.Command command = new AddToBlacklistUseCase.Command(
                "idempotent-jti",
                1735689600L,
                BlacklistReason.LOGOUT
        );

        // When: 동일한 Command로 여러 번 변환
        BlacklistedToken token1 = blacklistAssembler.toDomain(command);
        BlacklistedToken token2 = blacklistAssembler.toDomain(command);
        BlacklistedToken token3 = blacklistAssembler.toDomain(command);

        // Then: 각 호출은 독립적인 인스턴스를 반환하지만, 값은 동일
        assertThat(token1).isNotSameAs(token2);
        assertThat(token2).isNotSameAs(token3);

        assertThat(token1.getJti().asString()).isEqualTo(token2.getJti().asString());
        assertThat(token2.getJti().asString()).isEqualTo(token3.getJti().asString());

        assertThat(token1.getExpiresAt().toEpochSeconds()).isEqualTo(token2.getExpiresAt().toEpochSeconds());
        assertThat(token2.getExpiresAt().toEpochSeconds()).isEqualTo(token3.getExpiresAt().toEpochSeconds());

        assertThat(token1.getReason()).isEqualTo(token2.getReason());
        assertThat(token2.getReason()).isEqualTo(token3.getReason());
    }
}
