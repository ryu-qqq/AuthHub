package com.ryuqq.authhub.application.user.manager.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.port.out.persistence.UserRolePersistencePort;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
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
 * UserRoleTransactionManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleTransactionManager 단위 테스트")
class UserRoleTransactionManagerTest {

    @Mock private UserRolePersistencePort persistencePort;

    private UserRoleTransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        transactionManager = new UserRoleTransactionManager(persistencePort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("사용자 역할을 저장한다")
        void shouldPersistUserRole() {
            // given
            UserId userId = UserFixture.defaultId();
            RoleId roleId = RoleFixture.defaultId();
            UserRole userRole = UserRole.of(userId, roleId, Instant.now());
            given(persistencePort.save(userRole)).willReturn(userRole);

            // when
            UserRole result = transactionManager.persist(userRole);

            // then
            assertThat(result).isEqualTo(userRole);
            verify(persistencePort).save(userRole);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class DeleteTest {

        @Test
        @DisplayName("사용자 역할을 삭제한다")
        void shouldDeleteUserRole() {
            // given
            UserId userId = UserFixture.defaultId();
            RoleId roleId = RoleFixture.defaultId();

            // when
            transactionManager.delete(userId, roleId);

            // then
            verify(persistencePort).delete(userId, roleId);
        }
    }
}
