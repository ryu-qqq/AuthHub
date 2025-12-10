package com.ryuqq.authhub.application.user.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.dto.command.RevokeUserRoleCommand;
import com.ryuqq.authhub.application.user.manager.command.UserRoleTransactionManager;
import com.ryuqq.authhub.application.user.manager.query.UserRoleReadManager;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.exception.UserRoleNotFoundException;
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
 * RevokeUserRoleService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RevokeUserRoleService 단위 테스트")
class RevokeUserRoleServiceTest {

    @Mock private UserRoleTransactionManager transactionManager;

    @Mock private UserRoleReadManager readManager;

    private RevokeUserRoleService service;

    @BeforeEach
    void setUp() {
        service = new RevokeUserRoleService(transactionManager, readManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("역할을 성공적으로 해제한다")
        void shouldRevokeRoleSuccessfully() {
            // given
            UUID userId = UserFixture.defaultUUID();
            UUID roleId = UUID.randomUUID();
            RevokeUserRoleCommand command = new RevokeUserRoleCommand(userId, roleId);

            given(readManager.existsByUserIdAndRoleId(UserId.of(userId), RoleId.of(roleId)))
                    .willReturn(true);

            // when
            service.execute(command);

            // then
            verify(readManager).existsByUserIdAndRoleId(UserId.of(userId), RoleId.of(roleId));
            verify(transactionManager).delete(UserId.of(userId), RoleId.of(roleId));
        }

        @Test
        @DisplayName("존재하지 않는 역할 해제 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenRoleNotFound() {
            // given
            UUID userId = UserFixture.defaultUUID();
            UUID roleId = UUID.randomUUID();
            RevokeUserRoleCommand command = new RevokeUserRoleCommand(userId, roleId);

            given(readManager.existsByUserIdAndRoleId(UserId.of(userId), RoleId.of(roleId)))
                    .willReturn(false);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(UserRoleNotFoundException.class);

            verify(transactionManager, never()).delete(UserId.of(userId), RoleId.of(roleId));
        }
    }
}
