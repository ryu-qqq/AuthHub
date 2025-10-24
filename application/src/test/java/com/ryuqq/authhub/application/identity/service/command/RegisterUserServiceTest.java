package com.ryuqq.authhub.application.identity.service.command;

import com.ryuqq.authhub.application.auth.port.out.SaveUserPort;
import com.ryuqq.authhub.application.identity.exception.DuplicateIdentifierException;
import com.ryuqq.authhub.application.identity.exception.DuplicateNicknameException;
import com.ryuqq.authhub.application.identity.port.in.RegisterUserUseCase;
import com.ryuqq.authhub.application.identity.port.out.CheckDuplicateIdentifierPort;
import com.ryuqq.authhub.application.identity.port.out.CheckDuplicateNicknamePort;
import com.ryuqq.authhub.application.identity.port.out.SaveUserCredentialPort;
import com.ryuqq.authhub.application.identity.port.out.SaveUserProfilePort;
import com.ryuqq.authhub.domain.auth.credential.CredentialId;
import com.ryuqq.authhub.domain.auth.credential.PasswordHash;
import com.ryuqq.authhub.domain.auth.credential.UserCredential;
import com.ryuqq.authhub.domain.auth.user.User;
import com.ryuqq.authhub.domain.identity.profile.UserProfile;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/**
 * RegisterUserService 단위 테스트.
 *
 * <p>RegisterUserService의 비즈니스 로직을 검증하는 단위 테스트입니다.
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
 *   <li>✅ 정상 케이스: 사용자 등록 성공</li>
 *   <li>✅ 예외 케이스: Identifier 중복</li>
 *   <li>✅ 예외 케이스: Nickname 중복</li>
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
@DisplayName("RegisterUserService 단위 테스트")
@Tag("unit")
class RegisterUserServiceTest {

    @Mock
    private CheckDuplicateIdentifierPort checkDuplicateIdentifierPort;

    @Mock
    private CheckDuplicateNicknamePort checkDuplicateNicknamePort;

    @Mock
    private SaveUserPort saveUserPort;

    @Mock
    private SaveUserCredentialPort saveUserCredentialPort;

    @Mock
    private SaveUserProfilePort saveUserProfilePort;

    @Mock
    private PasswordHash.PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserService registerUserService;

    private RegisterUserUseCase.Command validCommand;

    @BeforeEach
    void setUp() {
        // Given: 유효한 사용자 등록 Command
        validCommand = new RegisterUserUseCase.Command(
                "EMAIL",
                "newuser@example.com",
                "password123",
                "NewUser"
        );
    }

    @Test
    @DisplayName("사용자 등록 성공 - 중복 없는 유효한 요청")
    void register_Success_WithValidCommand() {
        // Given: 중복 없음
        given(checkDuplicateIdentifierPort.existsByIdentifier(
                eq("EMAIL"),
                eq("newuser@example.com")
        )).willReturn(false);

        given(checkDuplicateNicknamePort.existsByNickname(eq("NewUser")))
                .willReturn(false);

        // Mock PasswordEncoder: 평문 비밀번호를 BCrypt 해시로 변환
        String bcryptHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        given(passwordEncoder.encode(eq("password123"))).willReturn(bcryptHash);

        // Mock 저장 결과
        User mockUser = mock(User.class);
        UserCredential mockCredential = mock(UserCredential.class);
        UserProfile mockProfile = mock(UserProfile.class);

        CredentialId mockCredentialId = mock(CredentialId.class);
        lenient().when(mockCredential.getId()).thenReturn(mockCredentialId);
        lenient().when(mockCredentialId.value()).thenReturn(java.util.UUID.randomUUID());

        given(saveUserPort.save(any(User.class))).willReturn(mockUser);
        given(saveUserCredentialPort.save(any(UserCredential.class))).willReturn(mockCredential);
        given(saveUserProfilePort.save(any(UserProfile.class))).willReturn(mockProfile);

        // When
        RegisterUserUseCase.Response response = registerUserService.register(validCommand);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.userId()).isNotNull();
        assertThat(response.credentialId()).isNotNull();

        // Verify: 모든 Port가 올바르게 호출되었는지 확인
        then(checkDuplicateIdentifierPort).should(times(1))
                .existsByIdentifier(eq("EMAIL"), eq("newuser@example.com"));
        then(checkDuplicateNicknamePort).should(times(1))
                .existsByNickname(eq("NewUser"));
        then(passwordEncoder).should(times(1)).encode(eq("password123"));
        then(saveUserPort).should(times(1)).save(any(User.class));
        then(saveUserCredentialPort).should(times(1)).save(any(UserCredential.class));
        then(saveUserProfilePort).should(times(1)).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("사용자 등록 실패 - Identifier 중복")
    void register_Failure_DuplicateIdentifier() {
        // Given: Identifier 중복
        given(checkDuplicateIdentifierPort.existsByIdentifier(
                eq("EMAIL"),
                eq("newuser@example.com")
        )).willReturn(true);  // 중복 존재

        // When & Then
        assertThatThrownBy(() -> registerUserService.register(validCommand))
                .isInstanceOf(DuplicateIdentifierException.class)
                .hasMessageContaining("Identifier already exists")
                .hasMessageContaining("newuser@example.com");

        // Verify: 저장 Port가 호출되지 않아야 함
        then(saveUserPort).should(never()).save(any(User.class));
        then(saveUserCredentialPort).should(never()).save(any(UserCredential.class));
        then(saveUserProfilePort).should(never()).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("사용자 등록 실패 - Nickname 중복")
    void register_Failure_DuplicateNickname() {
        // Given: Identifier는 중복 없지만 Nickname 중복
        given(checkDuplicateIdentifierPort.existsByIdentifier(
                eq("EMAIL"),
                eq("newuser@example.com")
        )).willReturn(false);

        given(checkDuplicateNicknamePort.existsByNickname(eq("NewUser")))
                .willReturn(true);  // 중복 존재

        // When & Then
        assertThatThrownBy(() -> registerUserService.register(validCommand))
                .isInstanceOf(DuplicateNicknameException.class)
                .hasMessageContaining("Nickname already exists")
                .hasMessageContaining("NewUser");

        // Verify: 저장 Port가 호출되지 않아야 함
        then(saveUserPort).should(never()).save(any(User.class));
        then(saveUserCredentialPort).should(never()).save(any(UserCredential.class));
        then(saveUserProfilePort).should(never()).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("Command Validation 실패 - credentialType이 null")
    void register_Failure_NullCredentialType() {
        // When & Then
        assertThatThrownBy(() -> new RegisterUserUseCase.Command(
                null,
                "newuser@example.com",
                "password123",
                "NewUser"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("credentialType cannot be null");
    }

    @Test
    @DisplayName("Command Validation 실패 - identifier가 null")
    void register_Failure_NullIdentifier() {
        // When & Then
        assertThatThrownBy(() -> new RegisterUserUseCase.Command(
                "EMAIL",
                null,
                "password123",
                "NewUser"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("identifier cannot be null");
    }

    @Test
    @DisplayName("Command Validation 실패 - password가 8자 미만")
    void register_Failure_PasswordTooShort() {
        // When & Then
        assertThatThrownBy(() -> new RegisterUserUseCase.Command(
                "EMAIL",
                "newuser@example.com",
                "pass",  // 4자 (8자 미만)
                "NewUser"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("password must be at least 8 characters");
    }

    @Test
    @DisplayName("Command Validation 실패 - nickname이 null")
    void register_Failure_NullNickname() {
        // When & Then
        assertThatThrownBy(() -> new RegisterUserUseCase.Command(
                "EMAIL",
                "newuser@example.com",
                "password123",
                null
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nickname cannot be null");
    }

    @Test
    @DisplayName("Command Validation 실패 - nickname이 2자 미만")
    void register_Failure_NicknameTooShort() {
        // When & Then
        assertThatThrownBy(() -> new RegisterUserUseCase.Command(
                "EMAIL",
                "newuser@example.com",
                "password123",
                "A"  // 1자 (2자 미만)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nickname must be between 2 and 20 characters");
    }

    @Test
    @DisplayName("Command Validation 실패 - nickname이 20자 초과")
    void register_Failure_NicknameTooLong() {
        // When & Then
        assertThatThrownBy(() -> new RegisterUserUseCase.Command(
                "EMAIL",
                "newuser@example.com",
                "password123",
                "A".repeat(21)  // 21자 (20자 초과)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nickname must be between 2 and 20 characters");
    }

    @Test
    @DisplayName("Response Validation 실패 - userId가 null")
    void response_Failure_NullUserId() {
        // When & Then
        assertThatThrownBy(() -> new RegisterUserUseCase.Response(
                null,
                "credential-123"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId cannot be null");
    }

    @Test
    @DisplayName("Response Validation 실패 - credentialId가 null")
    void response_Failure_NullCredentialId() {
        // When & Then
        assertThatThrownBy(() -> new RegisterUserUseCase.Response(
                "user-123",
                null
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("credentialId cannot be null");
    }
}
