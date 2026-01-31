package com.ryuqq.authhub.application.user.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.port.out.IdGeneratorPort;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.fixture.UserCommandFixtures;
import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderClient;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import java.time.Instant;
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

    @Mock private TimeProvider timeProvider;

    @Mock private IdGeneratorPort idGeneratorPort;

    @Mock private PasswordEncoderClient passwordEncoderClient;

    private UserCommandFactory sut;

    private static final Instant FIXED_TIME = UserFixture.fixedTime();
    private static final String GENERATED_ID = "01941234-5678-7000-8000-123456789001";
    private static final String HASHED_PASSWORD = "$2a$10$hashedpasswordvalue";

    @BeforeEach
    void setUp() {
        sut = new UserCommandFactory(timeProvider, idGeneratorPort, passwordEncoderClient);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공: Command로부터 User 도메인 객체 생성")
        void shouldCreateUser_FromCommand() {
            // given
            CreateUserCommand command = UserCommandFixtures.createCommand();

            given(idGeneratorPort.generate()).willReturn(GENERATED_ID);
            given(timeProvider.now()).willReturn(FIXED_TIME);
            given(passwordEncoderClient.hash(command.rawPassword())).willReturn(HASHED_PASSWORD);

            // when
            User result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.userIdValue()).isEqualTo(GENERATED_ID);
            assertThat(result.organizationIdValue()).isEqualTo(command.organizationId());
            assertThat(result.identifierValue()).isEqualTo(command.identifier());
            assertThat(result.phoneNumberValue()).isEqualTo(command.phoneNumber());
            assertThat(result.createdAt()).isEqualTo(FIXED_TIME);
        }
    }
}
