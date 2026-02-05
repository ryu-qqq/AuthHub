package com.ryuqq.authhub.application.user.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.port.out.IdGeneratorPort;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.user.dto.command.CreateUserWithRolesCommand;
import com.ryuqq.authhub.application.user.fixture.UserCommandFixtures;
import com.ryuqq.authhub.application.user.internal.CreateUserWithRolesBundle;
import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderClient;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateUserWithRolesCommandFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserWithRolesCommandFactory 단위 테스트")
class CreateUserWithRolesCommandFactoryTest {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final String GENERATED_USER_ID = "user-uuid-v7";
    private static final String HASHED_PASSWORD = "hashed-password";

    @Mock private TimeProvider timeProvider;
    @Mock private IdGeneratorPort idGeneratorPort;
    @Mock private PasswordEncoderClient passwordEncoderClient;

    private CreateUserWithRolesCommandFactory sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateUserWithRolesCommandFactory(
                        timeProvider, idGeneratorPort, passwordEncoderClient);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공: Command로부터 Bundle 생성 (User + 빈 UserRole + serviceCode + roleNames)")
        void shouldCreateBundle_FromCommand() {
            // given
            CreateUserWithRolesCommand command = UserCommandFixtures.createUserWithRolesCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);
            given(idGeneratorPort.generate()).willReturn(GENERATED_USER_ID);
            given(passwordEncoderClient.hash(anyString())).willReturn(HASHED_PASSWORD);

            // when
            CreateUserWithRolesBundle result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.user()).isNotNull();
            assertThat(result.user().userIdValue()).isEqualTo(GENERATED_USER_ID);
            assertThat(result.user().organizationIdValue()).isEqualTo(command.organizationId());
            assertThat(result.user().identifierValue()).isEqualTo(command.identifier());
            assertThat(result.userRoles()).isEmpty();
            assertThat(result.serviceCode()).isEqualTo(command.serviceCode());
            assertThat(result.roleNames()).isEqualTo(command.roleNames());
        }

        @Test
        @DisplayName("IdGeneratorPort를 통해 새 User ID 생성")
        void shouldGenerateNewUserId_ThroughIdGeneratorPort() {
            // given
            CreateUserWithRolesCommand command = UserCommandFixtures.createUserWithRolesCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);
            given(idGeneratorPort.generate()).willReturn(GENERATED_USER_ID);
            given(passwordEncoderClient.hash(anyString())).willReturn(HASHED_PASSWORD);

            // when
            CreateUserWithRolesBundle result = sut.create(command);

            // then
            assertThat(result.user().userIdValue()).isEqualTo(GENERATED_USER_ID);
        }

        @Test
        @DisplayName("roleNames가 null이면 빈 목록으로 Bundle 생성")
        void shouldCreateBundleWithEmptyRoleNames_WhenRoleNamesIsNull() {
            // given
            CreateUserWithRolesCommand command =
                    UserCommandFixtures.createUserWithRolesCommand(
                            "org-1", "id@test.com", null, "pass", "SVC", null);
            given(timeProvider.now()).willReturn(FIXED_TIME);
            given(idGeneratorPort.generate()).willReturn(GENERATED_USER_ID);
            given(passwordEncoderClient.hash(anyString())).willReturn(HASHED_PASSWORD);

            // when
            CreateUserWithRolesBundle result = sut.create(command);

            // then
            assertThat(result.roleNames()).isNull();
            assertThat(result.userRoles()).isEmpty();
        }

        @Test
        @DisplayName("serviceCode와 roleNames가 있으면 Bundle에 그대로 반영")
        void shouldReflectServiceCodeAndRoleNames_InBundle() {
            // given
            CreateUserWithRolesCommand command =
                    UserCommandFixtures.createUserWithRolesCommand(
                            "org-1",
                            "id@test.com",
                            null,
                            "pass",
                            "SVC_STORE",
                            List.of("ADMIN", "VIEWER"));
            given(timeProvider.now()).willReturn(FIXED_TIME);
            given(idGeneratorPort.generate()).willReturn(GENERATED_USER_ID);
            given(passwordEncoderClient.hash(anyString())).willReturn(HASHED_PASSWORD);

            // when
            CreateUserWithRolesBundle result = sut.create(command);

            // then
            assertThat(result.serviceCode()).isEqualTo("SVC_STORE");
            assertThat(result.roleNames()).containsExactly("ADMIN", "VIEWER");
        }
    }
}
