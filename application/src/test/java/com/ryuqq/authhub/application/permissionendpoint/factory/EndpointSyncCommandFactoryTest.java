package com.ryuqq.authhub.application.permissionendpoint.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand.EndpointSyncItem;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.service.id.ServiceId;
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
 * EndpointSyncCommandFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("EndpointSyncCommandFactory 단위 테스트")
class EndpointSyncCommandFactoryTest {

    private static final Instant FIXED_NOW = Instant.parse("2025-01-15T10:00:00Z");

    @Mock private TimeProvider timeProvider;

    private EndpointSyncCommandFactory sut;

    @BeforeEach
    void setUp() {
        sut = new EndpointSyncCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("createPermission 메서드")
    class CreatePermission {

        @Test
        @DisplayName("성공: permissionKey 파싱 후 Permission 도메인 생성")
        void shouldCreatePermission_FromValidKey() {
            Permission result =
                    sut.createPermission(
                            "product:create", "Product create", ServiceId.of(1L), FIXED_NOW);

            assertThat(result).isNotNull();
            assertThat(result.permissionKeyValue()).isEqualTo("product:create");
            assertThat(result.resourceValue()).isEqualTo("product");
            assertThat(result.actionValue()).isEqualTo("create");
        }

        @Test
        @DisplayName("실패: permissionKey 형식이 resource:action이 아니면 IllegalArgumentException")
        void shouldThrow_WhenInvalidPermissionKeyFormat() {
            assertThatThrownBy(
                            () ->
                                    sut.createPermission(
                                            "invalid", "desc", ServiceId.of(1L), FIXED_NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid permissionKey format");
        }

        @Test
        @DisplayName("성공: serviceId null 허용")
        void shouldCreatePermission_WhenServiceIdNull() {
            Permission result = sut.createPermission("user:read", "User read", null, FIXED_NOW);

            assertThat(result).isNotNull();
            assertThat(result.permissionKeyValue()).isEqualTo("user:read");
        }
    }

    @Nested
    @DisplayName("createMissingPermissions 메서드")
    class CreateMissingPermissions {

        @Test
        @DisplayName("성공: 누락된 permissionKey 목록으로 Permission 목록 생성")
        void shouldCreateMissingPermissions() {
            given(timeProvider.now()).willReturn(FIXED_NOW);
            List<String> missingKeys = List.of("product:create", "product:read");
            EndpointSyncItem item1 =
                    new EndpointSyncItem(
                            "POST", "/api/products", "product:create", "Create product", false);
            EndpointSyncItem item2 =
                    new EndpointSyncItem(
                            "GET", "/api/products", "product:read", "Read product", false);
            Map<String, EndpointSyncItem> itemsByKey =
                    Map.of("product:create", item1, "product:read", item2);

            List<Permission> result =
                    sut.createMissingPermissions(missingKeys, itemsByKey, ServiceId.of(1L));

            assertThat(result).hasSize(2);
            assertThat(result.get(0).permissionKeyValue()).isEqualTo("product:create");
            assertThat(result.get(1).permissionKeyValue()).isEqualTo("product:read");
        }
    }

    @Nested
    @DisplayName("createPermissionEndpoint 메서드")
    class CreatePermissionEndpoint {

        @Test
        @DisplayName("성공: EndpointSyncItem과 permissionId로 PermissionEndpoint 생성")
        void shouldCreatePermissionEndpoint() {
            EndpointSyncItem item =
                    new EndpointSyncItem("GET", "/api/v1/users", "user:read", "User list", false);

            PermissionEndpoint result =
                    sut.createPermissionEndpoint("authhub", item, 100L, FIXED_NOW);

            assertThat(result).isNotNull();
            assertThat(result.permissionIdValue()).isEqualTo(100L);
            assertThat(result.urlPatternValue()).isEqualTo("/api/v1/users");
            assertThat(result.getHttpMethod()).isEqualTo(HttpMethod.GET);
            assertThat(result.serviceNameValue()).isEqualTo("authhub");
        }
    }

    @Nested
    @DisplayName("createMissingEndpoints 메서드")
    class CreateMissingEndpoints {

        @Test
        @DisplayName("성공: permissionKeyToIdMap 기반으로 PermissionEndpoint 목록 생성")
        void shouldCreateMissingEndpoints() {
            given(timeProvider.now()).willReturn(FIXED_NOW);
            EndpointSyncItem item =
                    new EndpointSyncItem("GET", "/api/v1/users", "user:read", "User list", false);
            List<EndpointSyncItem> items = List.of(item);
            Map<String, Long> permissionKeyToIdMap = Map.of("user:read", 100L);

            List<PermissionEndpoint> result =
                    sut.createMissingEndpoints("authhub", items, permissionKeyToIdMap);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).permissionIdValue()).isEqualTo(100L);
            assertThat(result.get(0).urlPatternValue()).isEqualTo("/api/v1/users");
        }

        @Test
        @DisplayName("실패: permissionKey에 해당하는 permissionId가 없으면 IllegalStateException")
        void shouldThrow_WhenPermissionIdNotFound() {
            given(timeProvider.now()).willReturn(FIXED_NOW);
            EndpointSyncItem item =
                    new EndpointSyncItem("GET", "/api/v1/users", "user:read", "User list", false);
            Map<String, Long> permissionKeyToIdMap = Map.of();

            assertThatThrownBy(
                            () ->
                                    sut.createMissingEndpoints(
                                            "authhub", List.of(item), permissionKeyToIdMap))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Permission not found for key");
        }
    }
}
