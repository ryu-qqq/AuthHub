package com.ryuqq.authhub.application.endpointpermission.manager.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.endpointpermission.port.out.command.EndpointPermissionPersistencePort;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.fixture.EndpointPermissionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * EndpointPermissionTransactionManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("EndpointPermissionTransactionManager 단위 테스트")
class EndpointPermissionTransactionManagerTest {

    @Mock private EndpointPermissionPersistencePort persistencePort;

    private EndpointPermissionTransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        transactionManager = new EndpointPermissionTransactionManager(persistencePort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("엔드포인트 권한을 영속화하고 반환한다")
        void shouldPersistEndpointPermission() {
            // given
            EndpointPermission newEndpointPermission = EndpointPermissionFixture.createProtected();
            EndpointPermission savedEndpointPermission =
                    EndpointPermissionFixture.createReconstituted();
            given(persistencePort.persist(newEndpointPermission))
                    .willReturn(savedEndpointPermission);

            // when
            EndpointPermission result = transactionManager.persist(newEndpointPermission);

            // then
            assertThat(result).isEqualTo(savedEndpointPermission);
            verify(persistencePort).persist(newEndpointPermission);
        }

        @Test
        @DisplayName("Public 엔드포인트 권한을 영속화한다")
        void shouldPersistPublicEndpointPermission() {
            // given
            EndpointPermission publicEndpoint = EndpointPermissionFixture.createPublic();
            given(persistencePort.persist(publicEndpoint)).willReturn(publicEndpoint);

            // when
            EndpointPermission result = transactionManager.persist(publicEndpoint);

            // then
            assertThat(result).isEqualTo(publicEndpoint);
            verify(persistencePort).persist(publicEndpoint);
        }

        @Test
        @DisplayName("기존 엔드포인트 권한을 업데이트한다")
        void shouldUpdateExistingEndpointPermission() {
            // given
            EndpointPermission existingEndpoint = EndpointPermissionFixture.createReconstituted();
            given(persistencePort.persist(existingEndpoint)).willReturn(existingEndpoint);

            // when
            EndpointPermission result = transactionManager.persist(existingEndpoint);

            // then
            assertThat(result).isEqualTo(existingEndpoint);
            verify(persistencePort).persist(existingEndpoint);
        }
    }
}
