package com.ryuqq.authhub.application.role.manager.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.role.port.out.command.RolePersistencePort;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RoleTransactionManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleTransactionManager 단위 테스트")
class RoleTransactionManagerTest {

    @Mock private RolePersistencePort persistencePort;

    private RoleTransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        transactionManager = new RoleTransactionManager(persistencePort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("역할을 영속화하고 반환한다")
        void shouldPersistRole() {
            // given
            Role newRole = RoleFixture.createNew();
            Role savedRole = RoleFixture.create();
            given(persistencePort.persist(newRole)).willReturn(savedRole);

            // when
            Role result = transactionManager.persist(newRole);

            // then
            assertThat(result).isEqualTo(savedRole);
            verify(persistencePort).persist(newRole);
        }

        @Test
        @DisplayName("시스템 역할을 영속화한다")
        void shouldPersistSystemRole() {
            // given
            Role systemRole = RoleFixture.createSystemGlobal();
            given(persistencePort.persist(systemRole)).willReturn(systemRole);

            // when
            Role result = transactionManager.persist(systemRole);

            // then
            assertThat(result).isEqualTo(systemRole);
            verify(persistencePort).persist(systemRole);
        }
    }
}
