package com.ryuqq.authhub.application.auth.service;

import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.auth.dto.command.LogoutCommand;
import com.ryuqq.authhub.application.auth.manager.TokenManager;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
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
 * LogoutService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LogoutService 단위 테스트")
class LogoutServiceTest {

    @Mock private TokenManager tokenManager;

    private LogoutService service;

    @BeforeEach
    void setUp() {
        service = new LogoutService(tokenManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("로그아웃을 성공적으로 수행한다")
        void shouldLogoutSuccessfully() {
            // given
            UUID userId = UserFixture.defaultUUID();
            LogoutCommand command = new LogoutCommand(userId);

            // when
            service.execute(command);

            // then
            verify(tokenManager).revokeTokensByUserId(UserId.of(userId));
        }

        @Test
        @DisplayName("다른 사용자의 로그아웃도 처리한다")
        void shouldLogoutDifferentUser() {
            // given
            UUID userId = UUID.randomUUID();
            LogoutCommand command = new LogoutCommand(userId);

            // when
            service.execute(command);

            // then
            verify(tokenManager).revokeTokensByUserId(UserId.of(userId));
        }
    }
}
