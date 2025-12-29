package com.ryuqq.authhub.adapter.in.rest.role.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.role.dto.command.CreateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.GrantRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.UpdateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.query.SearchRolesApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.CreateRoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RolePermissionApiResponse;
import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.role.dto.command.RevokeRolePermissionCommand;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import com.ryuqq.authhub.application.role.dto.query.GetRoleQuery;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RolePermissionResponse;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("mapper")
@DisplayName("RoleApiMapper 테스트")
class RoleApiMapperTest {

    private RoleApiMapper mapper;

    private static final UUID ROLE_UUID = UUID.fromString("01941234-5678-7000-8000-123456789111");
    private static final UUID TENANT_UUID = UUID.fromString("01941234-5678-7000-8000-123456789abc");
    private static final UUID PERMISSION_UUID =
            UUID.fromString("01941234-5678-7000-8000-123456789222");
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant CREATED_FROM = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant CREATED_TO = Instant.parse("2025-12-31T23:59:59Z");

    @BeforeEach
    void setUp() {
        mapper = new RoleApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateRoleApiRequest) 테스트")
    class ToCreateCommandTest {

        @Test
        @DisplayName("CreateRoleApiRequest를 CreateRoleCommand로 변환 성공")
        void givenCreateRequest_whenToCommand_thenSuccess() {
            // given
            CreateRoleApiRequest request =
                    new CreateRoleApiRequest(
                            TENANT_UUID.toString(), "ADMIN", "관리자 역할", "ORGANIZATION", false);

            // when
            CreateRoleCommand command = mapper.toCommand(request);

            // then
            assertThat(command.tenantId()).isEqualTo(TENANT_UUID);
            assertThat(command.name()).isEqualTo("ADMIN");
            assertThat(command.description()).isEqualTo("관리자 역할");
            assertThat(command.scope()).isEqualTo("ORGANIZATION");
            assertThat(command.isSystem()).isFalse();
        }

        @Test
        @DisplayName("시스템 역할 생성 요청 변환 성공")
        void givenSystemRoleRequest_whenToCommand_thenSuccess() {
            // given
            CreateRoleApiRequest request =
                    new CreateRoleApiRequest(null, "SUPER_ADMIN", "슈퍼 관리자", "GLOBAL", true);

            // when
            CreateRoleCommand command = mapper.toCommand(request);

            // then
            assertThat(command.tenantId()).isNull();
            assertThat(command.isSystem()).isTrue();
        }

        @Test
        @DisplayName("null isSystem은 false로 변환")
        void givenNullIsSystem_whenToCommand_thenDefaultsFalse() {
            // given
            CreateRoleApiRequest request =
                    new CreateRoleApiRequest(TENANT_UUID.toString(), "USER", "사용자", "TENANT", null);

            // when
            CreateRoleCommand command = mapper.toCommand(request);

            // then
            assertThat(command.isSystem()).isFalse();
        }
    }

    @Nested
    @DisplayName("toCommand(String, UpdateRoleApiRequest) 테스트")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("UpdateRoleApiRequest를 UpdateRoleCommand로 변환 성공")
        void givenUpdateRequest_whenToCommand_thenSuccess() {
            // given
            UpdateRoleApiRequest request = new UpdateRoleApiRequest("UPDATED_ROLE", "수정된 설명");

            // when
            UpdateRoleCommand command = mapper.toCommand(ROLE_UUID.toString(), request);

            // then
            assertThat(command.roleId()).isEqualTo(ROLE_UUID);
            assertThat(command.name()).isEqualTo("UPDATED_ROLE");
            assertThat(command.description()).isEqualTo("수정된 설명");
        }
    }

    @Nested
    @DisplayName("toDeleteCommand() 테스트")
    class ToDeleteCommandTest {

        @Test
        @DisplayName("roleId를 DeleteRoleCommand로 변환 성공")
        void givenRoleId_whenToDeleteCommand_thenSuccess() {
            // when
            DeleteRoleCommand command = mapper.toDeleteCommand(ROLE_UUID.toString());

            // then
            assertThat(command.roleId()).isEqualTo(ROLE_UUID);
        }
    }

    @Nested
    @DisplayName("toGetQuery() 테스트")
    class ToGetQueryTest {

        @Test
        @DisplayName("roleId를 GetRoleQuery로 변환 성공")
        void givenRoleId_whenToGetQuery_thenSuccess() {
            // when
            GetRoleQuery query = mapper.toGetQuery(ROLE_UUID.toString());

            // then
            assertThat(query.roleId()).isEqualTo(ROLE_UUID);
        }
    }

    @Nested
    @DisplayName("toQuery(SearchRolesApiRequest) 테스트")
    class ToSearchQueryTest {

        @Test
        @DisplayName("SearchRolesApiRequest를 SearchRolesQuery로 변환 성공")
        void givenSearchRequest_whenToQuery_thenSuccess() {
            // given
            SearchRolesApiRequest request =
                    new SearchRolesApiRequest(
                            TENANT_UUID.toString(),
                            "ADMIN",
                            List.of("ORGANIZATION"),
                            List.of("CUSTOM"),
                            CREATED_FROM,
                            CREATED_TO,
                            0,
                            20);

            // when
            SearchRolesQuery query = mapper.toQuery(request);

            // then
            assertThat(query.tenantId()).isEqualTo(TENANT_UUID);
            assertThat(query.name()).isEqualTo("ADMIN");
            assertThat(query.scopes()).containsExactly("ORGANIZATION");
            assertThat(query.types()).containsExactly("CUSTOM");
            assertThat(query.createdFrom()).isEqualTo(CREATED_FROM);
            assertThat(query.createdTo()).isEqualTo(CREATED_TO);
            assertThat(query.page()).isZero();
            assertThat(query.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("null tenantId도 변환 성공")
        void givenNullTenantId_whenToQuery_thenSuccess() {
            // given
            SearchRolesApiRequest request =
                    new SearchRolesApiRequest(
                            null,
                            "SUPER_ADMIN",
                            List.of("GLOBAL"),
                            List.of("SYSTEM"),
                            CREATED_FROM,
                            CREATED_TO,
                            null,
                            null);

            // when
            SearchRolesQuery query = mapper.toQuery(request);

            // then
            assertThat(query.tenantId()).isNull();
            assertThat(query.page()).isZero();
            assertThat(query.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("다중 scope/type 필터 변환 성공")
        void givenMultipleScopesAndTypes_whenToQuery_thenSuccess() {
            // given
            SearchRolesApiRequest request =
                    new SearchRolesApiRequest(
                            TENANT_UUID.toString(),
                            "ADMIN",
                            List.of("TENANT", "ORGANIZATION"),
                            List.of("SYSTEM", "CUSTOM"),
                            CREATED_FROM,
                            CREATED_TO,
                            0,
                            20);

            // when
            SearchRolesQuery query = mapper.toQuery(request);

            // then
            assertThat(query.scopes()).containsExactly("TENANT", "ORGANIZATION");
            assertThat(query.types()).containsExactly("SYSTEM", "CUSTOM");
        }

        @Test
        @DisplayName("null scope/type은 null로 변환")
        void givenNullScopeAndType_whenToQuery_thenNull() {
            // given
            SearchRolesApiRequest request =
                    new SearchRolesApiRequest(
                            TENANT_UUID.toString(),
                            "ADMIN",
                            null,
                            null,
                            CREATED_FROM,
                            CREATED_TO,
                            0,
                            20);

            // when
            SearchRolesQuery query = mapper.toQuery(request);

            // then
            assertThat(query.scopes()).isNull();
            assertThat(query.types()).isNull();
        }
    }

    @Nested
    @DisplayName("toCreateResponse() 테스트")
    class ToCreateResponseTest {

        @Test
        @DisplayName("RoleResponse를 CreateRoleApiResponse로 변환 성공")
        void givenRoleResponse_whenToCreateResponse_thenSuccess() {
            // given
            RoleResponse response =
                    new RoleResponse(
                            ROLE_UUID,
                            TENANT_UUID,
                            "ADMIN",
                            "관리자",
                            "ORGANIZATION",
                            "CUSTOM",
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            CreateRoleApiResponse apiResponse = mapper.toCreateResponse(response);

            // then
            assertThat(apiResponse.roleId()).isEqualTo(ROLE_UUID.toString());
        }
    }

    @Nested
    @DisplayName("toApiResponse() 테스트")
    class ToApiResponseTest {

        @Test
        @DisplayName("RoleResponse를 RoleApiResponse로 변환 성공")
        void givenRoleResponse_whenToApiResponse_thenSuccess() {
            // given
            RoleResponse response =
                    new RoleResponse(
                            ROLE_UUID,
                            TENANT_UUID,
                            "ADMIN",
                            "관리자 역할",
                            "ORGANIZATION",
                            "CUSTOM",
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            RoleApiResponse apiResponse = mapper.toApiResponse(response);

            // then
            assertThat(apiResponse.roleId()).isEqualTo(ROLE_UUID.toString());
            assertThat(apiResponse.tenantId()).isEqualTo(TENANT_UUID.toString());
            assertThat(apiResponse.name()).isEqualTo("ADMIN");
            assertThat(apiResponse.description()).isEqualTo("관리자 역할");
            assertThat(apiResponse.scope()).isEqualTo("ORGANIZATION");
            assertThat(apiResponse.type()).isEqualTo("CUSTOM");
        }

        @Test
        @DisplayName("null tenantId도 변환 성공")
        void givenNullTenantId_whenToApiResponse_thenSuccess() {
            // given
            RoleResponse response =
                    new RoleResponse(
                            ROLE_UUID,
                            null,
                            "SUPER_ADMIN",
                            "슈퍼 관리자",
                            "GLOBAL",
                            "SYSTEM",
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            RoleApiResponse apiResponse = mapper.toApiResponse(response);

            // then
            assertThat(apiResponse.tenantId()).isNull();
        }
    }

    @Nested
    @DisplayName("toApiResponseList() 테스트")
    class ToApiResponseListTest {

        @Test
        @DisplayName("RoleResponse 목록을 RoleApiResponse 목록으로 변환 성공")
        void givenResponseList_whenToApiResponseList_thenSuccess() {
            // given
            RoleResponse response1 =
                    new RoleResponse(
                            ROLE_UUID,
                            TENANT_UUID,
                            "ADMIN",
                            "관리자",
                            "ORGANIZATION",
                            "CUSTOM",
                            FIXED_INSTANT,
                            FIXED_INSTANT);
            RoleResponse response2 =
                    new RoleResponse(
                            UUID.randomUUID(),
                            TENANT_UUID,
                            "USER",
                            "사용자",
                            "TENANT",
                            "CUSTOM",
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            List<RoleApiResponse> apiResponses =
                    mapper.toApiResponseList(List.of(response1, response2));

            // then
            assertThat(apiResponses).hasSize(2);
            assertThat(apiResponses.get(0).name()).isEqualTo("ADMIN");
            assertThat(apiResponses.get(1).name()).isEqualTo("USER");
        }
    }

    @Nested
    @DisplayName("RolePermission 관련 변환 테스트")
    class RolePermissionMappingTest {

        @Test
        @DisplayName("toGrantPermissionCommand 변환 성공")
        void givenGrantRequest_whenToGrantPermissionCommand_thenSuccess() {
            // given
            GrantRolePermissionApiRequest request =
                    new GrantRolePermissionApiRequest(PERMISSION_UUID);

            // when
            GrantRolePermissionCommand command =
                    mapper.toGrantPermissionCommand(ROLE_UUID.toString(), request);

            // then
            assertThat(command.roleId()).isEqualTo(ROLE_UUID);
            assertThat(command.permissionId()).isEqualTo(PERMISSION_UUID);
        }

        @Test
        @DisplayName("toRevokePermissionCommand 변환 성공")
        void givenRoleIdAndPermissionId_whenToRevokePermissionCommand_thenSuccess() {
            // when
            RevokeRolePermissionCommand command =
                    mapper.toRevokePermissionCommand(
                            ROLE_UUID.toString(), PERMISSION_UUID.toString());

            // then
            assertThat(command.roleId()).isEqualTo(ROLE_UUID);
            assertThat(command.permissionId()).isEqualTo(PERMISSION_UUID);
        }

        @Test
        @DisplayName("toRolePermissionApiResponse 변환 성공")
        void givenRolePermissionResponse_whenToApiResponse_thenSuccess() {
            // given
            RolePermissionResponse response =
                    new RolePermissionResponse(ROLE_UUID, PERMISSION_UUID, FIXED_INSTANT);

            // when
            RolePermissionApiResponse apiResponse = mapper.toRolePermissionApiResponse(response);

            // then
            assertThat(apiResponse.roleId()).isEqualTo(ROLE_UUID);
            assertThat(apiResponse.permissionId()).isEqualTo(PERMISSION_UUID);
            assertThat(apiResponse.grantedAt()).isEqualTo(FIXED_INSTANT);
        }

        @Test
        @DisplayName("toRolePermissionApiResponseList 변환 성공")
        void givenResponseList_whenToRolePermissionApiResponseList_thenSuccess() {
            // given
            RolePermissionResponse response1 =
                    new RolePermissionResponse(ROLE_UUID, PERMISSION_UUID, FIXED_INSTANT);
            RolePermissionResponse response2 =
                    new RolePermissionResponse(ROLE_UUID, UUID.randomUUID(), FIXED_INSTANT);

            // when
            List<RolePermissionApiResponse> apiResponses =
                    mapper.toRolePermissionApiResponseList(List.of(response1, response2));

            // then
            assertThat(apiResponses).hasSize(2);
        }
    }
}
