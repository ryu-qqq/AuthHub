package com.ryuqq.authhub.adapter.in.rest.auth.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.LogoutApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.RefreshTokenApiRequest;
import com.ryuqq.authhub.application.auth.dto.command.LoginCommand;
import com.ryuqq.authhub.application.auth.dto.command.LogoutCommand;
import com.ryuqq.authhub.application.auth.dto.command.RefreshTokenCommand;

/**
 * AuthApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("mapper")
@DisplayName("AuthApiMapper 테스트")
class AuthApiMapperTest {

    private AuthApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AuthApiMapper();
    }

    @Nested
    @DisplayName("toLoginCommand() 테스트")
    class ToLoginCommandTest {

        @Test
        @DisplayName("LoginApiRequest를 LoginCommand로 변환 성공")
        void givenLoginApiRequest_whenToLoginCommand_thenSuccess() {
            // given
            Long tenantId = 1L;
            String identifier = "user@example.com";
            String password = "password123";
            LoginApiRequest request = new LoginApiRequest(tenantId, identifier, password);

            // when
            LoginCommand command = mapper.toLoginCommand(request);

            // then
            assertThat(command.tenantId()).isEqualTo(tenantId);
            assertThat(command.identifier()).isEqualTo(identifier);
            assertThat(command.password()).isEqualTo(password);
        }
    }

    @Nested
    @DisplayName("toRefreshTokenCommand() 테스트")
    class ToRefreshTokenCommandTest {

        @Test
        @DisplayName("RefreshTokenApiRequest를 RefreshTokenCommand로 변환 성공")
        void givenRefreshTokenApiRequest_whenToRefreshTokenCommand_thenSuccess() {
            // given
            String refreshToken = "refreshToken123";
            RefreshTokenApiRequest request = new RefreshTokenApiRequest(refreshToken);

            // when
            RefreshTokenCommand command = mapper.toRefreshTokenCommand(request);

            // then
            assertThat(command.refreshToken()).isEqualTo(refreshToken);
        }
    }

    @Nested
    @DisplayName("toLogoutCommand() 테스트")
    class ToLogoutCommandTest {

        @Test
        @DisplayName("LogoutApiRequest를 LogoutCommand로 변환 성공")
        void givenLogoutApiRequest_whenToLogoutCommand_thenSuccess() {
            // given
            UUID userId = UUID.randomUUID();
            LogoutApiRequest request = new LogoutApiRequest(userId);

            // when
            LogoutCommand command = mapper.toLogoutCommand(request);

            // then
            assertThat(command.userId()).isEqualTo(userId);
        }
    }
}
