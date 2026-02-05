package com.ryuqq.authhub.application.permissionendpoint.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.permission.manager.PermissionCommandManager;
import com.ryuqq.authhub.application.permission.manager.PermissionReadManager;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand.EndpointSyncItem;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.SyncEndpointsResult;
import com.ryuqq.authhub.application.permissionendpoint.factory.EndpointSyncCommandFactory;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointCommandManager;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointReadManager;
import com.ryuqq.authhub.application.role.manager.RoleReadManager;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionCommandManager;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionReadManager;
import com.ryuqq.authhub.application.service.manager.ServiceReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * EndpointSyncCoordinator 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("EndpointSyncCoordinator 단위 테스트")
class EndpointSyncCoordinatorTest {

    private static final Instant FIXED_NOW = Instant.parse("2025-01-15T10:00:00Z");

    @Mock private PermissionReadManager permissionReadManager;
    @Mock private PermissionCommandManager permissionCommandManager;
    @Mock private PermissionEndpointReadManager permissionEndpointReadManager;
    @Mock private PermissionEndpointCommandManager permissionEndpointCommandManager;
    @Mock private EndpointSyncCommandFactory factory;
    @Mock private ServiceReadManager serviceReadManager;
    @Mock private RoleReadManager roleReadManager;
    @Mock private RolePermissionReadManager rolePermissionReadManager;
    @Mock private RolePermissionCommandManager rolePermissionCommandManager;
    @Mock private TimeProvider timeProvider;

    private EndpointSyncCoordinator sut;

    @BeforeEach
    void setUp() {
        sut =
                new EndpointSyncCoordinator(
                        permissionReadManager,
                        permissionCommandManager,
                        permissionEndpointReadManager,
                        permissionEndpointCommandManager,
                        factory,
                        serviceReadManager,
                        roleReadManager,
                        rolePermissionReadManager,
                        rolePermissionCommandManager,
                        timeProvider);
    }

    @Nested
    @DisplayName("coordinate 메서드")
    class Coordinate {

        @Test
        @DisplayName("빈 endpoints 요청 시 생성 없이 모든 카운트 0 반환")
        void shouldReturnZeros_WhenEndpointsEmpty() {
            SyncEndpointsCommand command = new SyncEndpointsCommand("authhub", null, List.of());

            SyncEndpointsResult result = sut.coordinate(command);

            assertThat(result.serviceName()).isEqualTo("authhub");
            assertThat(result.totalEndpoints()).isZero();
            assertThat(result.createdPermissions()).isZero();
            assertThat(result.createdEndpoints()).isZero();
            assertThat(result.skippedEndpoints()).isZero();
            assertThat(result.mappedRolePermissions()).isZero();
            then(permissionReadManager).shouldHaveNoInteractions();
            then(factory).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("null endpoints 요청 시 모든 카운트 0 반환")
        void shouldReturnZeros_WhenEndpointsNull() {
            SyncEndpointsCommand command = new SyncEndpointsCommand("authhub", null, null);

            SyncEndpointsResult result = sut.coordinate(command);

            assertThat(result.totalEndpoints()).isZero();
            assertThat(result.createdPermissions()).isZero();
            assertThat(result.createdEndpoints()).isZero();
        }

        @Test
        @DisplayName("성공: 한 건 동기화 시 Permission·Endpoint 생성 및 결과 반환")
        void shouldSyncOneEndpoint_WhenMissing() {
            EndpointSyncItem item =
                    new EndpointSyncItem("GET", "/api/v1/users", "user:read", "User list", false);
            SyncEndpointsCommand command = new SyncEndpointsCommand("authhub", null, List.of(item));
            Permission newPermission =
                    Permission.createCustom(null, "user", "read", "User list", FIXED_NOW);
            PermissionEndpoint newEndpoint =
                    PermissionEndpoint.create(
                            1L,
                            "authhub",
                            "/api/v1/users",
                            com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod.GET,
                            "User list",
                            false,
                            FIXED_NOW);

            given(permissionReadManager.findAllByPermissionKeys(List.of("user:read")))
                    .willReturn(List.of());
            given(factory.createMissingPermissions(anyList(), anyMap(), isNull()))
                    .willReturn(List.of(newPermission));
            given(permissionCommandManager.persistAllAndReturnKeyToIdMap(List.of(newPermission)))
                    .willReturn(Map.of("user:read", 1L));
            given(permissionEndpointReadManager.findAllByUrlPatterns(List.of("/api/v1/users")))
                    .willReturn(List.of());
            given(
                            factory.createMissingEndpoints(
                                    eq("authhub"), anyList(), eq(Map.of("user:read", 1L))))
                    .willReturn(List.of(newEndpoint));

            SyncEndpointsResult result = sut.coordinate(command);

            assertThat(result.serviceName()).isEqualTo("authhub");
            assertThat(result.totalEndpoints()).isEqualTo(1);
            assertThat(result.createdPermissions()).isEqualTo(1);
            assertThat(result.createdEndpoints()).isEqualTo(1);
            assertThat(result.skippedEndpoints()).isZero();
        }
    }
}
