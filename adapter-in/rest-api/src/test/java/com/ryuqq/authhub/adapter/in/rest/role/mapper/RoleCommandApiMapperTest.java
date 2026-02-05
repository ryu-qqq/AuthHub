package com.ryuqq.authhub.adapter.in.rest.role.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.role.dto.request.CreateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.request.UpdateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.fixture.RoleApiFixture;
import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RoleCommandApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("RoleCommandApiMapper 단위 테스트")
class RoleCommandApiMapperTest {

    private RoleCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RoleCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateRoleApiRequest) 메서드는")
    class ToCommandCreateRole {

        @Test
        @DisplayName("CreateRoleApiRequest를 CreateRoleCommand로 변환한다")
        void shouldConvertToCreateRoleCommand() {
            // Given
            var request = RoleApiFixture.createRoleRequest();

            // When
            CreateRoleCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isEqualTo(RoleApiFixture.defaultTenantId());
            assertThat(result.serviceId()).isEqualTo(RoleApiFixture.defaultServiceId());
            assertThat(result.name()).isEqualTo(RoleApiFixture.defaultName());
            assertThat(result.displayName()).isEqualTo(RoleApiFixture.defaultDisplayName());
            assertThat(result.description()).isEqualTo(RoleApiFixture.defaultDescription());
            assertThat(result.isSystem()).isFalse(); // REST API를 통한 생성은 항상 CUSTOM
        }

        @Test
        @DisplayName("Global 역할 생성 요청을 CreateRoleCommand로 변환한다")
        void shouldConvertGlobalRoleRequest() {
            // Given
            var request = RoleApiFixture.createGlobalRoleRequest();

            // When
            CreateRoleCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isNull();
            assertThat(result.serviceId()).isNull();
            assertThat(result.name()).isEqualTo(RoleApiFixture.defaultName());
            assertThat(result.isSystem()).isFalse();
        }

        @Test
        @DisplayName("null 필드를 포함한 요청도 정상 변환한다")
        void shouldConvertWithNullFields() {
            // Given
            var request =
                    new CreateRoleApiRequest(null, null, RoleApiFixture.defaultName(), null, null);

            // When
            CreateRoleCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isNull();
            assertThat(result.serviceId()).isNull();
            assertThat(result.name()).isEqualTo(RoleApiFixture.defaultName());
            assertThat(result.displayName()).isNull();
            assertThat(result.description()).isNull();
            assertThat(result.isSystem()).isFalse();
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateRoleApiRequest) 메서드는")
    class ToCommandUpdateRole {

        @Test
        @DisplayName("UpdateRoleApiRequest를 UpdateRoleCommand로 변환한다")
        void shouldConvertToUpdateRoleCommand() {
            // Given
            Long roleId = RoleApiFixture.defaultRoleId();
            var request = RoleApiFixture.updateRoleRequest();

            // When
            UpdateRoleCommand result = mapper.toCommand(roleId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(roleId);
            assertThat(result.displayName()).isEqualTo("수정된 표시 이름");
            assertThat(result.description()).isEqualTo("수정된 설명");
        }

        @Test
        @DisplayName("displayName만 포함한 요청을 변환한다")
        void shouldConvertWithDisplayNameOnly() {
            // Given
            Long roleId = RoleApiFixture.defaultRoleId();
            var request = RoleApiFixture.updateDisplayNameRequest("새로운 표시 이름");

            // When
            UpdateRoleCommand result = mapper.toCommand(roleId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(roleId);
            assertThat(result.displayName()).isEqualTo("새로운 표시 이름");
            assertThat(result.description()).isNull();
        }

        @Test
        @DisplayName("description만 포함한 요청을 변환한다")
        void shouldConvertWithDescriptionOnly() {
            // Given
            Long roleId = RoleApiFixture.defaultRoleId();
            var request = RoleApiFixture.updateDescriptionRequest("새로운 설명");

            // When
            UpdateRoleCommand result = mapper.toCommand(roleId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(roleId);
            assertThat(result.displayName()).isNull();
            assertThat(result.description()).isEqualTo("새로운 설명");
        }

        @Test
        @DisplayName("모든 필드가 null인 요청도 정상 변환한다")
        void shouldConvertWithAllNullFields() {
            // Given
            Long roleId = RoleApiFixture.defaultRoleId();
            var request = new UpdateRoleApiRequest(null, null);

            // When
            UpdateRoleCommand result = mapper.toCommand(roleId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(roleId);
            assertThat(result.displayName()).isNull();
            assertThat(result.description()).isNull();
        }
    }

    @Nested
    @DisplayName("toDeleteCommand(Long) 메서드는")
    class ToDeleteCommand {

        @Test
        @DisplayName("roleId를 DeleteRoleCommand로 변환한다")
        void shouldConvertToDeleteRoleCommand() {
            // Given
            Long roleId = RoleApiFixture.defaultRoleId();

            // When
            DeleteRoleCommand result = mapper.toDeleteCommand(roleId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(roleId);
        }

        @Test
        @DisplayName("다른 roleId도 정상 변환한다")
        void shouldConvertDifferentRoleId() {
            // Given
            Long roleId = 999L;

            // When
            DeleteRoleCommand result = mapper.toDeleteCommand(roleId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isEqualTo(999L);
        }
    }
}
