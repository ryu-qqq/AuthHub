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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.reset;

/**
 * RegisterUserService 통합 테스트.
 *
 * <p>Spring Context를 로드하여 RegisterUserService의 실제 동작을 검증하는 통합 테스트입니다.
 * Application Layer의 트랜잭션 경계, Service 조립, UseCase 흐름을 검증합니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>✅ Spring Test Context 로딩 (경량 Bean 조립)</li>
 *   <li>✅ Mock Bean으로 Out Port만 Mocking (Adapter Layer 미구현)</li>
 *   <li>✅ UseCase Interface를 통한 호출 검증</li>
 *   <li>✅ Service 로직 흐름 검증</li>
 * </ul>
 *
 * <p><strong>테스트 범위:</strong></p>
 * <ul>
 *   <li>✅ Application Layer: Service, UseCase</li>
 *   <li>❌ Persistence Layer: Adapter 미구현으로 Mock 처리</li>
 *   <li>❌ Web Layer: REST API는 별도 테스트</li>
 * </ul>
 *
 * <p><strong>테스트 태그:</strong></p>
 * <ul>
 *   <li>@Tag("integration") - 통합 테스트 (Spring Context 로딩)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RegisterUserServiceIntegrationTest.TestConfig.class)
@DisplayName("RegisterUserService 통합 테스트")
@Tag("integration")
class RegisterUserServiceIntegrationTest {

    /**
     * Test Configuration - RegisterUserService와 Mock Port들을 Bean으로 등록.
     */
    @Configuration
    static class TestConfig {
        @Bean
        public RegisterUserService registerUserService(
                CheckDuplicateIdentifierPort checkDuplicateIdentifierPort,
                CheckDuplicateNicknamePort checkDuplicateNicknamePort,
                SaveUserPort saveUserPort,
                SaveUserCredentialPort saveUserCredentialPort,
                SaveUserProfilePort saveUserProfilePort,
                PasswordHash.PasswordEncoder passwordEncoder
        ) {
            return new RegisterUserService(
                    checkDuplicateIdentifierPort,
                    checkDuplicateNicknamePort,
                    saveUserPort,
                    saveUserCredentialPort,
                    saveUserProfilePort,
                    passwordEncoder
            );
        }

        @Bean
        public CheckDuplicateIdentifierPort checkDuplicateIdentifierPort() {
            return mock(CheckDuplicateIdentifierPort.class);
        }

        @Bean
        public CheckDuplicateNicknamePort checkDuplicateNicknamePort() {
            return mock(CheckDuplicateNicknamePort.class);
        }

        @Bean
        public SaveUserPort saveUserPort() {
            return mock(SaveUserPort.class);
        }

        @Bean
        public SaveUserCredentialPort saveUserCredentialPort() {
            return mock(SaveUserCredentialPort.class);
        }

        @Bean
        public SaveUserProfilePort saveUserProfilePort() {
            return mock(SaveUserProfilePort.class);
        }

        @Bean
        public PasswordHash.PasswordEncoder passwordEncoder() {
            return mock(PasswordHash.PasswordEncoder.class);
        }
    }

    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private CheckDuplicateIdentifierPort checkDuplicateIdentifierPort;

    @Autowired
    private CheckDuplicateNicknamePort checkDuplicateNicknamePort;

    @Autowired
    private SaveUserPort saveUserPort;

    @Autowired
    private SaveUserCredentialPort saveUserCredentialPort;

    @Autowired
    private SaveUserProfilePort saveUserProfilePort;

    @Autowired
    private PasswordHash.PasswordEncoder passwordEncoder;

    private RegisterUserUseCase.Command validCommand;

    @BeforeEach
    void setUp() {
        // Reset all mocks before each test
        reset(
                checkDuplicateIdentifierPort,
                checkDuplicateNicknamePort,
                saveUserPort,
                saveUserCredentialPort,
                saveUserProfilePort,
                passwordEncoder
        );

        // Given: 유효한 사용자 등록 Command
        validCommand = new RegisterUserUseCase.Command(
                "EMAIL",
                "integration@example.com",
                "password123",
                "IntegrationUser"
        );
    }

    @Test
    @DisplayName("[통합] 사용자 등록 성공 - Spring Context와 Service 동작 검증")
    void register_Success_WithSpringContext() {
        // Given: 중복 없음
        given(checkDuplicateIdentifierPort.existsByIdentifier(
                eq("EMAIL"),
                eq("integration@example.com")
        )).willReturn(false);

        given(checkDuplicateNicknamePort.existsByNickname(eq("IntegrationUser")))
                .willReturn(false);

        // Mock PasswordEncoder
        String bcryptHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        given(passwordEncoder.encode(eq("password123"))).willReturn(bcryptHash);

        // Mock 저장 결과
        User mockUser = mock(User.class);
        UserCredential mockCredential = mock(UserCredential.class);
        UserProfile mockProfile = mock(UserProfile.class);

        CredentialId mockCredentialId = mock(CredentialId.class);
        given(mockCredential.getId()).willReturn(mockCredentialId);
        given(mockCredentialId.value()).willReturn(UUID.randomUUID());

        given(saveUserPort.save(any(User.class))).willReturn(mockUser);
        given(saveUserCredentialPort.save(any(UserCredential.class))).willReturn(mockCredential);
        given(saveUserProfilePort.save(any(UserProfile.class))).willReturn(mockProfile);

        // When: UseCase Interface를 통한 호출
        RegisterUserUseCase.Response response = registerUserUseCase.register(validCommand);

        // Then: 정상 응답 확인
        assertThat(response).isNotNull();
        assertThat(response.userId()).isNotNull();
        assertThat(response.credentialId()).isNotNull();

        // Verify: 모든 Port 호출 확인
        then(checkDuplicateIdentifierPort).should(times(1))
                .existsByIdentifier(eq("EMAIL"), eq("integration@example.com"));
        then(checkDuplicateNicknamePort).should(times(1))
                .existsByNickname(eq("IntegrationUser"));
        then(passwordEncoder).should(times(1)).encode(eq("password123"));
        then(saveUserPort).should(times(1)).save(any(User.class));
        then(saveUserCredentialPort).should(times(1)).save(any(UserCredential.class));
        then(saveUserProfilePort).should(times(1)).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("[통합] 사용자 등록 실패 - Identifier 중복 시 예외 발생")
    void register_Failure_DuplicateIdentifier() {
        // Given: Identifier 중복
        given(checkDuplicateIdentifierPort.existsByIdentifier(
                eq("EMAIL"),
                eq("integration@example.com")
        )).willReturn(true);  // 중복 존재

        // When & Then: 예외 발생
        assertThatThrownBy(() -> registerUserUseCase.register(validCommand))
                .isInstanceOf(DuplicateIdentifierException.class)
                .hasMessageContaining("Identifier already exists")
                .hasMessageContaining("integration@example.com");

        // Verify: 저장 Port가 호출되지 않아야 함
        then(saveUserPort).should(never()).save(any(User.class));
        then(saveUserCredentialPort).should(never()).save(any(UserCredential.class));
        then(saveUserProfilePort).should(never()).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("[통합] 사용자 등록 실패 - Nickname 중복 시 예외 발생")
    void register_Failure_DuplicateNickname() {
        // Given: Identifier는 중복 없지만 Nickname 중복
        given(checkDuplicateIdentifierPort.existsByIdentifier(
                eq("EMAIL"),
                eq("integration@example.com")
        )).willReturn(false);

        given(checkDuplicateNicknamePort.existsByNickname(eq("IntegrationUser")))
                .willReturn(true);  // 중복 존재

        // When & Then: 예외 발생
        assertThatThrownBy(() -> registerUserUseCase.register(validCommand))
                .isInstanceOf(DuplicateNicknameException.class)
                .hasMessageContaining("Nickname already exists")
                .hasMessageContaining("IntegrationUser");

        // Verify: 저장 Port가 호출되지 않아야 함
        then(saveUserPort).should(never()).save(any(User.class));
        then(saveUserCredentialPort).should(never()).save(any(UserCredential.class));
        then(saveUserProfilePort).should(never()).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("[통합] UseCase Interface를 통한 접근 검증")
    void register_Success_ThroughUseCaseInterface() {
        // Given: 중복 없음
        given(checkDuplicateIdentifierPort.existsByIdentifier(any(), any())).willReturn(false);
        given(checkDuplicateNicknamePort.existsByNickname(any())).willReturn(false);

        String bcryptHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        given(passwordEncoder.encode(any())).willReturn(bcryptHash);

        User mockUser = mock(User.class);
        UserCredential mockCredential = mock(UserCredential.class);
        UserProfile mockProfile = mock(UserProfile.class);

        CredentialId mockCredentialId = mock(CredentialId.class);
        given(mockCredential.getId()).willReturn(mockCredentialId);
        given(mockCredentialId.value()).willReturn(UUID.randomUUID());

        given(saveUserPort.save(any(User.class))).willReturn(mockUser);
        given(saveUserCredentialPort.save(any(UserCredential.class))).willReturn(mockCredential);
        given(saveUserProfilePort.save(any(UserProfile.class))).willReturn(mockProfile);

        // When: UseCase Interface 타입으로 호출
        RegisterUserUseCase.Response response = registerUserUseCase.register(validCommand);

        // Then: UseCase Interface를 통한 정상 동작 확인
        assertThat(response).isNotNull();
        assertThat(registerUserUseCase).isInstanceOf(RegisterUserService.class);
    }

    @Test
    @DisplayName("[통합] Command Validation - Spring Context에서도 동작")
    void register_Failure_InvalidCommand_InSpringContext() {
        // When & Then: 잘못된 Command는 Spring Context에서도 검증됨
        assertThatThrownBy(() -> new RegisterUserUseCase.Command(
                null,
                "integration@example.com",
                "password123",
                "IntegrationUser"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("credentialType cannot be null");

        assertThatThrownBy(() -> new RegisterUserUseCase.Command(
                "EMAIL",
                "integration@example.com",
                "short",  // 8자 미만
                "IntegrationUser"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("password must be at least 8 characters");
    }
}
