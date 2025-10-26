package com.ryuqq.authhub.application.security.blacklist.service.command;

import com.ryuqq.authhub.application.security.blacklist.assembler.BlacklistAssembler;
import com.ryuqq.authhub.application.security.blacklist.port.in.AddToBlacklistUseCase;
import com.ryuqq.authhub.application.security.blacklist.port.in.CheckBlacklistUseCase;
import com.ryuqq.authhub.application.security.blacklist.port.out.AddToBlacklistPort;
import com.ryuqq.authhub.application.security.blacklist.port.out.CheckBlacklistPort;
import com.ryuqq.authhub.domain.security.blacklist.BlacklistedToken;
import com.ryuqq.authhub.domain.security.blacklist.vo.BlacklistReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

/**
 * BlacklistService 단위 테스트.
 *
 * <p>BlacklistService의 비즈니스 로직을 검증하는 단위 테스트입니다.
 * 모든 외부 의존성(Port)은 Mockito로 Mocking하여 순수한 Service 로직만 테스트합니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>✅ Spring Context 로딩 없음 (빠른 실행)</li>
 *   <li>✅ 모든 Port는 Mock 객체 사용</li>
 *   <li>✅ Given-When-Then 패턴</li>
 *   <li>✅ BDD 스타일 (given, when, then)</li>
 * </ul>
 *
 * <p><strong>테스트 케이스:</strong></p>
 * <ul>
 *   <li>✅ 정상 케이스: 블랙리스트 추가 성공</li>
 *   <li>✅ 정상 케이스: 블랙리스트 확인 - 존재함</li>
 *   <li>✅ 정상 케이스: 블랙리스트 확인 - 존재하지 않음</li>
 *   <li>✅ 예외 케이스: 잘못된 Command (Validation)</li>
 * </ul>
 *
 * <p><strong>테스트 태그:</strong></p>
 * <ul>
 *   <li>@Tag("unit") - 단위 테스트 (빠른 실행, Mock 사용)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BlacklistService 단위 테스트")
@Tag("unit")
class BlacklistServiceTest {

    @Mock
    private AddToBlacklistPort addToBlacklistPort;

    @Mock
    private CheckBlacklistPort checkBlacklistPort;

    @Mock
    private BlacklistAssembler blacklistAssembler;

    @InjectMocks
    private BlacklistService blacklistService;

    private AddToBlacklistUseCase.Command validAddCommand;
    private CheckBlacklistUseCase.Query validCheckQuery;

    @BeforeEach
    void setUp() {
        // Given: 유효한 블랙리스트 추가 Command
        validAddCommand = new AddToBlacklistUseCase.Command(
                "unique-jwt-id-123",
                1735689600L,  // 2025-01-01 00:00:00 UTC
                BlacklistReason.LOGOUT
        );

        // Given: 유효한 블랙리스트 확인 Query
        validCheckQuery = new CheckBlacklistUseCase.Query("unique-jwt-id-123");
    }

    @Test
    @DisplayName("블랙리스트 추가 성공 - 유효한 Command")
    void addToBlacklist_Success_WithValidCommand() {
        // Given: Assembler가 BlacklistedToken 생성
        BlacklistedToken mockToken = mock(BlacklistedToken.class);
        given(blacklistAssembler.toDomain(eq(validAddCommand))).willReturn(mockToken);

        // When
        blacklistService.addToBlacklist(validAddCommand);

        // Then: Assembler와 Port가 올바르게 호출되었는지 확인
        then(blacklistAssembler).should(times(1)).toDomain(eq(validAddCommand));
        then(addToBlacklistPort).should(times(1)).add(eq(mockToken));
    }

    @Test
    @DisplayName("블랙리스트 추가 실패 - Command가 null")
    void addToBlacklist_Failure_NullCommand() {
        // When & Then
        assertThatThrownBy(() -> blacklistService.addToBlacklist(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Command cannot be null");

        // Verify: Port가 호출되지 않아야 함
        then(addToBlacklistPort).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("블랙리스트 확인 성공 - 블랙리스트에 존재함")
    void isBlacklisted_Success_TokenExists() {
        // Given: 블랙리스트에 존재
        given(checkBlacklistPort.exists(eq("unique-jwt-id-123"))).willReturn(true);

        // When
        boolean isBlacklisted = blacklistService.isBlacklisted(validCheckQuery);

        // Then
        assertThat(isBlacklisted).isTrue();
        then(checkBlacklistPort).should(times(1)).exists(eq("unique-jwt-id-123"));
    }

    @Test
    @DisplayName("블랙리스트 확인 성공 - 블랙리스트에 존재하지 않음")
    void isBlacklisted_Success_TokenNotExists() {
        // Given: 블랙리스트에 존재하지 않음
        given(checkBlacklistPort.exists(eq("unique-jwt-id-123"))).willReturn(false);

        // When
        boolean isBlacklisted = blacklistService.isBlacklisted(validCheckQuery);

        // Then
        assertThat(isBlacklisted).isFalse();
        then(checkBlacklistPort).should(times(1)).exists(eq("unique-jwt-id-123"));
    }

    @Test
    @DisplayName("블랙리스트 확인 실패 - Query가 null")
    void isBlacklisted_Failure_NullQuery() {
        // When & Then
        assertThatThrownBy(() -> blacklistService.isBlacklisted(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Query cannot be null");

        // Verify: Port가 호출되지 않아야 함
        then(checkBlacklistPort).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("AddCommand Validation 실패 - jti가 null")
    void addCommand_Validation_Failure_NullJti() {
        // When & Then
        assertThatThrownBy(() -> new AddToBlacklistUseCase.Command(
                null,
                1735689600L,
                BlacklistReason.LOGOUT
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JTI cannot be null or blank");
    }

    @Test
    @DisplayName("AddCommand Validation 실패 - jti가 빈 문자열")
    void addCommand_Validation_Failure_BlankJti() {
        // When & Then
        assertThatThrownBy(() -> new AddToBlacklistUseCase.Command(
                "   ",
                1735689600L,
                BlacklistReason.LOGOUT
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JTI cannot be null or blank");
    }

    @Test
    @DisplayName("AddCommand Validation 실패 - expiresAtEpochSeconds가 음수")
    void addCommand_Validation_Failure_NegativeExpiresAt() {
        // When & Then
        assertThatThrownBy(() -> new AddToBlacklistUseCase.Command(
                "unique-jwt-id-123",
                -1L,
                BlacklistReason.LOGOUT
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ExpiresAt cannot be negative");
    }

    @Test
    @DisplayName("AddCommand Validation 실패 - reason이 null")
    void addCommand_Validation_Failure_NullReason() {
        // When & Then
        assertThatThrownBy(() -> new AddToBlacklistUseCase.Command(
                "unique-jwt-id-123",
                1735689600L,
                null
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("BlacklistReason cannot be null");
    }

    @Test
    @DisplayName("CheckQuery Validation 실패 - jti가 null")
    void checkQuery_Validation_Failure_NullJti() {
        // When & Then
        assertThatThrownBy(() -> new CheckBlacklistUseCase.Query(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JTI cannot be null or blank");
    }

    @Test
    @DisplayName("CheckQuery Validation 실패 - jti가 빈 문자열")
    void checkQuery_Validation_Failure_BlankJti() {
        // When & Then
        assertThatThrownBy(() -> new CheckBlacklistUseCase.Query("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JTI cannot be null or blank");
    }

    @Test
    @DisplayName("멱등성 테스트 - 동일한 토큰을 여러 번 추가")
    void addToBlacklist_Idempotency_SameTokenMultipleTimes() {
        // Given
        BlacklistedToken mockToken = mock(BlacklistedToken.class);
        given(blacklistAssembler.toDomain(any(AddToBlacklistUseCase.Command.class)))
                .willReturn(mockToken);

        // When: 동일한 토큰을 3번 추가
        blacklistService.addToBlacklist(validAddCommand);
        blacklistService.addToBlacklist(validAddCommand);
        blacklistService.addToBlacklist(validAddCommand);

        // Then: Port가 3번 호출되어야 함 (멱등성은 Port에서 보장)
        then(addToBlacklistPort).should(times(3)).add(any(BlacklistedToken.class));
    }

    @Test
    @DisplayName("다양한 BlacklistReason 테스트 - LOGOUT")
    void addToBlacklist_Success_WithLogoutReason() {
        // Given
        AddToBlacklistUseCase.Command logoutCommand = new AddToBlacklistUseCase.Command(
                "logout-token-123",
                1735689600L,
                BlacklistReason.LOGOUT
        );
        BlacklistedToken mockToken = mock(BlacklistedToken.class);
        given(blacklistAssembler.toDomain(eq(logoutCommand))).willReturn(mockToken);

        // When
        blacklistService.addToBlacklist(logoutCommand);

        // Then
        then(addToBlacklistPort).should(times(1)).add(eq(mockToken));
    }

    @Test
    @DisplayName("다양한 BlacklistReason 테스트 - SECURITY_BREACH")
    void addToBlacklist_Success_WithSecurityBreachReason() {
        // Given
        AddToBlacklistUseCase.Command breachCommand = new AddToBlacklistUseCase.Command(
                "breach-token-456",
                1735689600L,
                BlacklistReason.SECURITY_BREACH
        );
        BlacklistedToken mockToken = mock(BlacklistedToken.class);
        given(blacklistAssembler.toDomain(eq(breachCommand))).willReturn(mockToken);

        // When
        blacklistService.addToBlacklist(breachCommand);

        // Then
        then(addToBlacklistPort).should(times(1)).add(eq(mockToken));
    }
}
