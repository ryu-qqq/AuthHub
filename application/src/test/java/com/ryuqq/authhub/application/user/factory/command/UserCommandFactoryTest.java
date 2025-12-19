package com.ryuqq.authhub.application.user.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderPort;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.Clock;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserCommandFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandFactory 단위 테스트")
class UserCommandFactoryTest {

    @Mock private PasswordEncoderPort passwordEncoderPort;
    @Mock private UuidHolder uuidHolder;

    private UserCommandFactory factory;
    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = UserFixture.fixedClock();
        factory = new UserCommandFactory(clock, uuidHolder, passwordEncoderPort);
    }

    @Nested
    @DisplayName("create 메서드")
    class CreateTest {

        @Test
        @DisplayName("CreateUserCommand로 User를 생성한다")
        void shouldCreateUserFromCommand() {
            // given
            UUID generatedUuid = UUID.randomUUID();
            given(uuidHolder.random()).willReturn(generatedUuid);
            UUID tenantId = UserFixture.defaultTenantUUID();
            UUID orgId = UserFixture.defaultOrganizationUUID();
            String identifier = "newuser@example.com";
            String plainPassword = "password123";
            String hashedPassword = "hashed_password_123";

            CreateUserCommand command =
                    new CreateUserCommand(tenantId, orgId, identifier, plainPassword);
            given(passwordEncoderPort.hash(plainPassword)).willReturn(hashedPassword);

            // when
            User result = factory.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tenantIdValue()).isEqualTo(tenantId);
            assertThat(result.organizationIdValue()).isEqualTo(orgId);
            assertThat(result.getIdentifier()).isEqualTo(identifier);
            assertThat(result.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("비밀번호가 해싱되어 저장된다")
        void shouldHashPassword() {
            // given
            given(uuidHolder.random()).willReturn(UUID.randomUUID());
            String plainPassword = "myPassword!@#";
            String hashedPassword = "$2a$10$hashedPassword";
            CreateUserCommand command =
                    new CreateUserCommand(
                            UserFixture.defaultTenantUUID(),
                            UserFixture.defaultOrganizationUUID(),
                            "test@example.com",
                            plainPassword);
            given(passwordEncoderPort.hash(plainPassword)).willReturn(hashedPassword);

            // when
            User result = factory.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getHashedPassword()).isEqualTo(hashedPassword);
        }

        @Test
        @DisplayName("새 사용자는 ACTIVE 상태로 생성된다")
        void shouldCreateWithActiveStatus() {
            // given
            given(uuidHolder.random()).willReturn(UUID.randomUUID());
            CreateUserCommand command =
                    new CreateUserCommand(
                            UserFixture.defaultTenantUUID(),
                            UserFixture.defaultOrganizationUUID(),
                            "active@example.com",
                            "password");
            given(passwordEncoderPort.hash("password")).willReturn("hashed");

            // when
            User result = factory.create(command);

            // then
            assertThat(result.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("새 사용자에게 UUID가 할당된다")
        void shouldAssignUuidToNewUser() {
            // given
            UUID generatedUuid = UUID.randomUUID();
            given(uuidHolder.random()).willReturn(generatedUuid);
            CreateUserCommand command =
                    new CreateUserCommand(
                            UserFixture.defaultTenantUUID(),
                            UserFixture.defaultOrganizationUUID(),
                            "uuid@example.com",
                            "password");
            given(passwordEncoderPort.hash("password")).willReturn("hashed");

            // when
            User result = factory.create(command);

            // then
            assertThat(result.userIdValue()).isEqualTo(generatedUuid);
        }
    }
}
