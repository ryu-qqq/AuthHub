package com.ryuqq.authhub.application.auth.service.command;

import com.ryuqq.authhub.application.auth.assembler.TokenAssembler;
import com.ryuqq.authhub.application.auth.config.JwtProperties;
import com.ryuqq.authhub.application.auth.port.in.LoginUseCase;
import com.ryuqq.authhub.application.auth.port.out.GenerateTokenPort;
import com.ryuqq.authhub.application.auth.port.out.LoadCredentialByIdentifierPort;
import com.ryuqq.authhub.application.auth.port.out.LoadUserPort;
import com.ryuqq.authhub.application.auth.port.out.SaveRefreshTokenPort;
import com.ryuqq.authhub.domain.auth.credential.CredentialType;
import com.ryuqq.authhub.domain.auth.credential.Identifier;
import com.ryuqq.authhub.domain.auth.credential.PasswordHash;
import com.ryuqq.authhub.domain.auth.credential.UserCredential;
import com.ryuqq.authhub.domain.auth.credential.exception.CredentialNotFoundException;
import com.ryuqq.authhub.domain.auth.credential.exception.InvalidCredentialException;
import com.ryuqq.authhub.domain.auth.token.JwtToken;
import com.ryuqq.authhub.domain.auth.token.Token;
import com.ryuqq.authhub.domain.auth.token.TokenType;
import com.ryuqq.authhub.domain.auth.user.User;
import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.auth.user.UserStatus;
import com.ryuqq.authhub.domain.auth.user.exception.InvalidUserStatusException;
import com.ryuqq.authhub.domain.auth.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
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
 * LoginService 단위 테스트.
 *
 * <p>LoginService의 비즈니스 로직을 검증하는 단위 테스트입니다.
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
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 단위 테스트")
class LoginServiceTest {

    @Mock
    private LoadCredentialByIdentifierPort loadCredentialByIdentifierPort;

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private GenerateTokenPort generateTokenPort;

    @Mock
    private SaveRefreshTokenPort saveRefreshTokenPort;

    @Mock
    private TokenAssembler tokenAssembler;

    @Mock
    private PasswordHash.PasswordEncoder passwordEncoder;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private LoginService loginService;

    private LoginUseCase.Command validCommand;

    @BeforeEach
    void setUp() {
        // Given: JWT Properties Mock 설정
        lenient().when(jwtProperties.accessTokenValidity()).thenReturn(Duration.ofMinutes(15));
        lenient().when(jwtProperties.refreshTokenValidity()).thenReturn(Duration.ofDays(7));

        // Given: 유효한 로그인 Command
        validCommand = new LoginUseCase.Command(
                "EMAIL",
                "test@example.com",
                "password123",
                "WEB"
        );
    }

    @Test
    @DisplayName("로그인 성공 - 유효한 Credential과 ACTIVE User")
    void login_Success_WithValidCredentialAndActiveUser() {
        // Given
        UserCredential mockCredential = createMockCredential();
        User mockUser = createMockUser();
        Token mockAccessToken = createMockToken(TokenType.ACCESS, Duration.ofMinutes(15));
        Token mockRefreshToken = createMockToken(TokenType.REFRESH, Duration.ofDays(7));

        given(loadCredentialByIdentifierPort.loadByIdentifier(any(CredentialType.class), any(Identifier.class)))
                .willReturn(Optional.of(mockCredential));
        given(mockCredential.matchesPassword(eq("password123"), eq(passwordEncoder)))
                .willReturn(true);
        given(loadUserPort.load(any(UserId.class)))
                .willReturn(Optional.of(mockUser));
        given(mockUser.canUseSystem())
                .willReturn(true);
        given(generateTokenPort.generate(any(UserId.class), eq(TokenType.ACCESS), any(Duration.class)))
                .willReturn(mockAccessToken);
        given(generateTokenPort.generate(any(UserId.class), eq(TokenType.REFRESH), any(Duration.class)))
                .willReturn(mockRefreshToken);
        given(tokenAssembler.toLoginResponse(mockAccessToken, mockRefreshToken))
                .willReturn(new LoginUseCase.Response("access-token", "refresh-token", "Bearer", 900));

        // When
        LoginUseCase.Response response = loginService.login(validCommand);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900);

        // Verify
        then(loadCredentialByIdentifierPort).should(times(1)).loadByIdentifier(any(CredentialType.class), any(Identifier.class));
        then(loadUserPort).should(times(1)).load(any(UserId.class));
        then(generateTokenPort).should(times(2)).generate(any(UserId.class), any(TokenType.class), any(Duration.class));
        then(saveRefreshTokenPort).should(times(1)).save(mockRefreshToken);
        then(tokenAssembler).should(times(1)).toLoginResponse(mockAccessToken, mockRefreshToken);
    }

    @Test
    @DisplayName("로그인 실패 - Credential이 존재하지 않음")
    void login_Failure_CredentialNotFound() {
        // Given (mock 객체 생성 불필요 - credential을 찾지 못하는 시나리오)
        given(loadCredentialByIdentifierPort.loadByIdentifier(any(CredentialType.class), any(Identifier.class)))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> loginService.login(validCommand))
                .isInstanceOf(CredentialNotFoundException.class)
                .hasMessageContaining("Credential not found");

        // Verify - Token 생성 및 저장이 호출되지 않아야 함
        then(generateTokenPort).should(never()).generate(any(UserId.class), any(TokenType.class), any(Duration.class));
        then(saveRefreshTokenPort).should(never()).save(any(Token.class));
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 CredentialType")
    void login_Failure_InvalidCredentialType() {
        // Given: 잘못된 credentialType (INVALID_TYPE)
        LoginUseCase.Command invalidCommand = new LoginUseCase.Command(
                "INVALID_TYPE",
                "test@example.com",
                "password123",
                "WEB"
        );

        // When & Then
        assertThatThrownBy(() -> loginService.login(invalidCommand))
                .isInstanceOf(InvalidCredentialException.class)
                .hasMessageContaining("Invalid credentialType")
                .hasMessageContaining("INVALID_TYPE");

        // Verify - Credential 조회조차 되지 않아야 함
        then(loadCredentialByIdentifierPort).should(never()).loadByIdentifier(any(CredentialType.class), any(Identifier.class));
        then(generateTokenPort).should(never()).generate(any(UserId.class), any(TokenType.class), any(Duration.class));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_Failure_InvalidPassword() {
        // Given
        UserCredential mockCredential = createMockCredential();

        given(loadCredentialByIdentifierPort.loadByIdentifier(any(CredentialType.class), any(Identifier.class)))
                .willReturn(Optional.of(mockCredential));
        given(mockCredential.matchesPassword(eq("password123"), eq(passwordEncoder)))
                .willReturn(false);  // 비밀번호 불일치

        // When & Then
        assertThatThrownBy(() -> loginService.login(validCommand))
                .isInstanceOf(InvalidCredentialException.class)
                .hasMessageContaining("Invalid password");

        // Verify - User 조회 및 Token 생성이 호출되지 않아야 함
        then(loadUserPort).should(never()).load(any(UserId.class));
        then(generateTokenPort).should(never()).generate(any(UserId.class), any(TokenType.class), any(Duration.class));
    }

    @Test
    @DisplayName("로그인 실패 - User가 존재하지 않음")
    void login_Failure_UserNotFound() {
        // Given
        UserCredential mockCredential = createMockCredential();

        given(loadCredentialByIdentifierPort.loadByIdentifier(any(CredentialType.class), any(Identifier.class)))
                .willReturn(Optional.of(mockCredential));
        given(mockCredential.matchesPassword(eq("password123"), eq(passwordEncoder)))
                .willReturn(true);
        given(loadUserPort.load(any(UserId.class)))
                .willReturn(Optional.empty());  // User 없음

        // When & Then
        assertThatThrownBy(() -> loginService.login(validCommand))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");

        // Verify - Token 생성이 호출되지 않아야 함
        then(generateTokenPort).should(never()).generate(any(UserId.class), any(TokenType.class), any(Duration.class));
    }

    @Test
    @DisplayName("로그인 실패 - User가 ACTIVE 상태가 아님 (SUSPENDED)")
    void login_Failure_UserNotActive() {
        // Given
        UserCredential mockCredential = createMockCredential();
        User mockUser = org.mockito.Mockito.mock(User.class);
        UserId userId = UserId.newId();
        lenient().when(mockUser.getId()).thenReturn(userId);
        lenient().when(mockUser.getStatus()).thenReturn(UserStatus.SUSPENDED);
        lenient().when(mockUser.canUseSystem()).thenReturn(false);

        given(loadCredentialByIdentifierPort.loadByIdentifier(any(CredentialType.class), any(Identifier.class)))
                .willReturn(Optional.of(mockCredential));
        given(mockCredential.matchesPassword(eq("password123"), eq(passwordEncoder)))
                .willReturn(true);
        given(loadUserPort.load(any(UserId.class)))
                .willReturn(Optional.of(mockUser));

        // When & Then
        assertThatThrownBy(() -> loginService.login(validCommand))
                .isInstanceOf(InvalidUserStatusException.class)
                .hasMessageContaining("User is not in ACTIVE status")
                .hasMessageContaining("SUSPENDED");

        // Verify - Token 생성이 호출되지 않아야 함
        then(generateTokenPort).should(never()).generate(any(UserId.class), any(TokenType.class), any(Duration.class));
    }

    /**
     * Mock UserCredential 생성 헬퍼 메서드.
     */
    private UserCredential createMockCredential() {
        UserCredential credential = org.mockito.Mockito.mock(UserCredential.class);
        UserId userId = UserId.newId();
        lenient().when(credential.getUserId()).thenReturn(userId);
        return credential;
    }

    /**
     * Mock User 생성 헬퍼 메서드.
     */
    private User createMockUser() {
        User user = org.mockito.Mockito.mock(User.class);
        UserId userId = UserId.newId();
        lenient().when(user.getId()).thenReturn(userId);
        lenient().when(user.getStatus()).thenReturn(UserStatus.ACTIVE);
        lenient().when(user.canUseSystem()).thenReturn(true);
        return user;
    }

    /**
     * Mock Token 생성 헬퍼 메서드.
     */
    private Token createMockToken(TokenType tokenType, Duration validity) {
        Token token = org.mockito.Mockito.mock(Token.class);
        JwtToken jwtToken = org.mockito.Mockito.mock(JwtToken.class);

        lenient().when(token.getJwtToken()).thenReturn(jwtToken);
        lenient().when(token.getType()).thenReturn(tokenType);
        lenient().when(token.remainingValidity()).thenReturn(validity);

        if (tokenType == TokenType.ACCESS) {
            lenient().when(token.isAccessToken()).thenReturn(true);
            lenient().when(token.isRefreshToken()).thenReturn(false);
        } else {
            lenient().when(token.isAccessToken()).thenReturn(false);
            lenient().when(token.isRefreshToken()).thenReturn(true);
        }

        return token;
    }
}
