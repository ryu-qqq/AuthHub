package com.ryuqq.authhub.application.auth.service.command;

import com.ryuqq.authhub.application.auth.config.JwtProperties;
import com.ryuqq.authhub.application.auth.port.in.RefreshTokenUseCase;
import com.ryuqq.authhub.application.auth.port.out.CheckBlacklistPort;
import com.ryuqq.authhub.application.auth.port.out.GenerateTokenPort;
import com.ryuqq.authhub.application.auth.port.out.LoadRefreshTokenPort;
import com.ryuqq.authhub.application.auth.port.out.ValidateTokenPort;
import com.ryuqq.authhub.domain.auth.token.ExpiresAt;
import com.ryuqq.authhub.domain.auth.token.JwtToken;
import com.ryuqq.authhub.domain.auth.token.Token;
import com.ryuqq.authhub.domain.auth.token.TokenId;
import com.ryuqq.authhub.domain.auth.token.TokenType;
import com.ryuqq.authhub.domain.auth.token.exception.BlacklistedTokenException;
import com.ryuqq.authhub.domain.auth.token.exception.ExpiredTokenException;
import com.ryuqq.authhub.domain.auth.token.exception.InvalidTokenException;
import com.ryuqq.authhub.domain.auth.token.exception.TokenNotFoundException;
import com.ryuqq.authhub.domain.auth.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/**
 * RefreshTokenService 단위 테스트.
 *
 * <p>RefreshTokenService의 비즈니스 로직을 검증하는 단위 테스트입니다.
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
 * <p><strong>테스트 시나리오:</strong></p>
 * <ul>
 *   <li>정상 시나리오: 유효한 Refresh Token으로 Access Token 재발급</li>
 *   <li>실패 시나리오: JWT 검증 실패 (InvalidTokenException)</li>
 *   <li>실패 시나리오: Refresh Token 타입이 아님 (InvalidTokenException)</li>
 *   <li>실패 시나리오: Token 만료 (ExpiredTokenException)</li>
 *   <li>실패 시나리오: Redis에 Token 없음 (TokenNotFoundException)</li>
 *   <li>실패 시나리오: Blacklist에 등록됨 (BlacklistedTokenException)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService 단위 테스트")
class RefreshTokenServiceTest {

    @Mock
    private ValidateTokenPort validateTokenPort;

    @Mock
    private LoadRefreshTokenPort loadRefreshTokenPort;

    @Mock
    private CheckBlacklistPort checkBlacklistPort;

    @Mock
    private GenerateTokenPort generateTokenPort;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private RefreshTokenUseCase.Command validCommand;
    private String validRefreshTokenString;

    @BeforeEach
    void setUp() {
        // Given: JWT Properties Mock 설정
        lenient().when(jwtProperties.accessTokenValidity()).thenReturn(Duration.ofMinutes(15));
        lenient().when(jwtProperties.refreshTokenValidity()).thenReturn(Duration.ofDays(7));

        // Given: 유효한 Refresh Token Command
        validRefreshTokenString = "valid.refresh.token";
        validCommand = new RefreshTokenUseCase.Command(validRefreshTokenString);
    }

    @Test
    @DisplayName("Refresh Token 재발급 성공 - 유효한 Refresh Token")
    void refresh_Success_WithValidRefreshToken() {
        // Given
        Token mockRefreshToken = createMockRefreshToken();
        Token mockNewAccessToken = createMockAccessToken();
        TokenId tokenId = TokenId.newId();

        given(validateTokenPort.validate(validRefreshTokenString))
                .willReturn(mockRefreshToken);
        given(mockRefreshToken.getId())
                .willReturn(tokenId);
        given(loadRefreshTokenPort.load(tokenId))
                .willReturn(Optional.of(mockRefreshToken));
        given(checkBlacklistPort.isBlacklisted(tokenId))
                .willReturn(false);
        given(generateTokenPort.generate(any(UserId.class), eq(TokenType.ACCESS), any(Duration.class)))
                .willReturn(mockNewAccessToken);

        // When
        RefreshTokenUseCase.Response response = refreshTokenService.refresh(validCommand);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900);  // 15분 = 900초

        // Verify
        then(validateTokenPort).should(times(1)).validate(validRefreshTokenString);
        then(loadRefreshTokenPort).should(times(1)).load(tokenId);
        then(checkBlacklistPort).should(times(1)).isBlacklisted(tokenId);
        then(generateTokenPort).should(times(1)).generate(any(UserId.class), eq(TokenType.ACCESS), any(Duration.class));
    }

    @Test
    @DisplayName("Refresh Token 재발급 실패 - JWT 검증 실패 (잘못된 서명)")
    void refresh_Failure_InvalidTokenSignature() {
        // Given
        given(validateTokenPort.validate(validRefreshTokenString))
                .willThrow(new InvalidTokenException("Invalid JWT signature"));

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.refresh(validCommand))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid JWT signature");

        // Verify - Redis 조회 및 Token 생성이 호출되지 않아야 함
        then(loadRefreshTokenPort).should(never()).load(any(TokenId.class));
        then(checkBlacklistPort).should(never()).isBlacklisted(any(TokenId.class));
        then(generateTokenPort).should(never()).generate(any(UserId.class), any(TokenType.class), any(Duration.class));
    }

    @Test
    @DisplayName("Refresh Token 재발급 실패 - Token 타입이 REFRESH가 아님 (ACCESS 타입)")
    void refresh_Failure_NotRefreshTokenType() {
        // Given
        Token mockAccessToken = createMockAccessToken();  // ACCESS 타입 Token

        given(validateTokenPort.validate(validRefreshTokenString))
                .willReturn(mockAccessToken);

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.refresh(validCommand))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Token type must be REFRESH");

        // Verify - Redis 조회 및 Token 생성이 호출되지 않아야 함
        then(loadRefreshTokenPort).should(never()).load(any(TokenId.class));
        then(generateTokenPort).should(never()).generate(any(UserId.class), any(TokenType.class), any(Duration.class));
    }

    @Test
    @DisplayName("Refresh Token 재발급 실패 - Token 만료")
    void refresh_Failure_ExpiredToken() {
        // Given
        Token mockExpiredToken = createMockExpiredRefreshToken();

        given(validateTokenPort.validate(validRefreshTokenString))
                .willReturn(mockExpiredToken);

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.refresh(validCommand))
                .isInstanceOf(ExpiredTokenException.class)
                .hasMessageContaining("Refresh token has expired");

        // Verify - Redis 조회는 되지 않아야 함 (만료 확인에서 실패)
        then(loadRefreshTokenPort).should(never()).load(any(TokenId.class));
        then(generateTokenPort).should(never()).generate(any(UserId.class), any(TokenType.class), any(Duration.class));
    }

    @Test
    @DisplayName("Refresh Token 재발급 실패 - Redis에 Token 없음 (이미 사용되었거나 삭제됨)")
    void refresh_Failure_TokenNotFoundInRedis() {
        // Given
        Token mockRefreshToken = createMockRefreshToken();
        TokenId tokenId = TokenId.newId();

        given(validateTokenPort.validate(validRefreshTokenString))
                .willReturn(mockRefreshToken);
        given(mockRefreshToken.getId())
                .willReturn(tokenId);
        given(loadRefreshTokenPort.load(tokenId))
                .willReturn(Optional.empty());  // Redis에 Token 없음

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.refresh(validCommand))
                .isInstanceOf(TokenNotFoundException.class)
                .hasMessageContaining("Refresh token not found in Redis");

        // Verify - Blacklist 확인 및 Token 생성이 호출되지 않아야 함
        then(checkBlacklistPort).should(never()).isBlacklisted(any(TokenId.class));
        then(generateTokenPort).should(never()).generate(any(UserId.class), any(TokenType.class), any(Duration.class));
    }

    @Test
    @DisplayName("Refresh Token 재발급 실패 - Blacklist에 등록됨 (로그아웃)")
    void refresh_Failure_TokenBlacklisted() {
        // Given
        Token mockRefreshToken = createMockRefreshToken();
        TokenId tokenId = TokenId.newId();

        given(validateTokenPort.validate(validRefreshTokenString))
                .willReturn(mockRefreshToken);
        given(mockRefreshToken.getId())
                .willReturn(tokenId);
        given(loadRefreshTokenPort.load(tokenId))
                .willReturn(Optional.of(mockRefreshToken));
        given(checkBlacklistPort.isBlacklisted(tokenId))
                .willReturn(true);  // Blacklist에 등록됨

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.refresh(validCommand))
                .isInstanceOf(BlacklistedTokenException.class)
                .hasMessageContaining("Refresh token is blacklisted");

        // Verify - Token 생성이 호출되지 않아야 함
        then(generateTokenPort).should(never()).generate(any(UserId.class), any(TokenType.class), any(Duration.class));
    }

    /**
     * Mock Refresh Token 생성 헬퍼 메서드.
     * 유효한 (만료되지 않은) REFRESH 타입 Token을 생성합니다.
     */
    private Token createMockRefreshToken() {
        Token token = org.mockito.Mockito.mock(Token.class);
        JwtToken jwtToken = org.mockito.Mockito.mock(JwtToken.class);
        ExpiresAt expiresAt = org.mockito.Mockito.mock(ExpiresAt.class);
        UserId userId = UserId.newId();

        lenient().when(token.getJwtToken()).thenReturn(jwtToken);
        lenient().when(jwtToken.value()).thenReturn("refresh.jwt.token");
        lenient().when(token.getType()).thenReturn(TokenType.REFRESH);
        lenient().when(token.isRefreshToken()).thenReturn(true);
        lenient().when(token.isAccessToken()).thenReturn(false);
        lenient().when(token.isExpired()).thenReturn(false);  // 유효한 Token
        lenient().when(token.getUserId()).thenReturn(userId);
        lenient().when(token.getExpiresAt()).thenReturn(expiresAt);
        lenient().when(expiresAt.value()).thenReturn(Instant.now(Clock.systemUTC()).plus(Duration.ofDays(7)));

        return token;
    }

    /**
     * Mock 만료된 Refresh Token 생성 헬퍼 메서드.
     */
    private Token createMockExpiredRefreshToken() {
        Token token = org.mockito.Mockito.mock(Token.class);
        JwtToken jwtToken = org.mockito.Mockito.mock(JwtToken.class);
        ExpiresAt expiresAt = org.mockito.Mockito.mock(ExpiresAt.class);

        lenient().when(token.getJwtToken()).thenReturn(jwtToken);
        lenient().when(jwtToken.value()).thenReturn("expired.refresh.token");
        lenient().when(token.getType()).thenReturn(TokenType.REFRESH);
        lenient().when(token.isRefreshToken()).thenReturn(true);
        lenient().when(token.isAccessToken()).thenReturn(false);
        lenient().when(token.isExpired()).thenReturn(true);  // 만료된 Token
        lenient().when(token.getExpiresAt()).thenReturn(expiresAt);
        lenient().when(expiresAt.value()).thenReturn(Instant.now(Clock.systemUTC()).minus(Duration.ofDays(1)));

        return token;
    }

    /**
     * Mock Access Token 생성 헬퍼 메서드.
     */
    private Token createMockAccessToken() {
        Token token = org.mockito.Mockito.mock(Token.class);
        JwtToken jwtToken = org.mockito.Mockito.mock(JwtToken.class);
        UserId userId = UserId.newId();

        lenient().when(token.getJwtToken()).thenReturn(jwtToken);
        lenient().when(jwtToken.value()).thenReturn("new-access-token");
        lenient().when(token.getType()).thenReturn(TokenType.ACCESS);
        lenient().when(token.isAccessToken()).thenReturn(true);
        lenient().when(token.isRefreshToken()).thenReturn(false);
        lenient().when(token.remainingValidity()).thenReturn(Duration.ofMinutes(15));
        lenient().when(token.getUserId()).thenReturn(userId);

        return token;
    }
}
