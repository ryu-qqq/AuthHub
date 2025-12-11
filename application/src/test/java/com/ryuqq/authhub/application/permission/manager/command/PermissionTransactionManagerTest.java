package com.ryuqq.authhub.application.permission.manager.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.permission.port.out.command.PermissionPersistencePort;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PermissionTransactionManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionTransactionManager 단위 테스트")
class PermissionTransactionManagerTest {

    @Mock private PermissionPersistencePort persistencePort;

    private PermissionTransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        transactionManager = new PermissionTransactionManager(persistencePort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("권한을 영속화하고 반환한다")
        void shouldPersistPermission() {
            // given
            Permission newPermission = PermissionFixture.create();
            Permission savedPermission = PermissionFixture.createReconstituted();
            given(persistencePort.persist(newPermission)).willReturn(savedPermission);

            // when
            Permission result = transactionManager.persist(newPermission);

            // then
            assertThat(result).isEqualTo(savedPermission);
            verify(persistencePort).persist(newPermission);
        }

        @Test
        @DisplayName("시스템 권한을 영속화한다")
        void shouldPersistSystemPermission() {
            // given
            Permission systemPermission = PermissionFixture.createSystem();
            given(persistencePort.persist(systemPermission)).willReturn(systemPermission);

            // when
            Permission result = transactionManager.persist(systemPermission);

            // then
            assertThat(result).isEqualTo(systemPermission);
            verify(persistencePort).persist(systemPermission);
        }
    }
}
