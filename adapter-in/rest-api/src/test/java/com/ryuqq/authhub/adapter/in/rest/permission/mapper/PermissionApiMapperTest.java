package com.ryuqq.authhub.adapter.in.rest.permission.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.permission.dto.command.CreatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.command.UpdatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.query.SearchPermissionsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.CreatePermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.PermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.UserPermissionsApiResponse;
import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.query.GetPermissionQuery;
import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("mapper")
@DisplayName("PermissionApiMapper 테스트")
class PermissionApiMapperTest {

    private PermissionApiMapper mapper;

    private static final UUID PERMISSION_UUID =
            UUID.fromString("01941234-5678-7000-8000-123456789001");
    private static final UUID USER_UUID = UUID.fromString("01941234-5678-7000-8000-000000000001");
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        mapper = new PermissionApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreatePermissionApiRequest) 테스트")
    class ToCreateCommandTest {

        @Test
        @DisplayName("CreatePermissionApiRequest를 CreatePermissionCommand로 변환 성공")
        void givenCreateRequest_whenToCommand_thenSuccess() {
            // given
            CreatePermissionApiRequest request =
                    new CreatePermissionApiRequest("user", "read", "사용자 조회 권한", false);

            // when
            CreatePermissionCommand command = mapper.toCommand(request);

            // then
            assertThat(command.resource()).isEqualTo("user");
            assertThat(command.action()).isEqualTo("read");
            assertThat(command.description()).isEqualTo("사용자 조회 권한");
            assertThat(command.isSystem()).isFalse();
        }

        @Test
        @DisplayName("시스템 권한 생성 요청 변환 성공")
        void givenSystemPermissionRequest_whenToCommand_thenSuccess() {
            // given
            CreatePermissionApiRequest request =
                    new CreatePermissionApiRequest("system", "admin", "시스템 관리 권한", true);

            // when
            CreatePermissionCommand command = mapper.toCommand(request);

            // then
            assertThat(command.isSystem()).isTrue();
        }

        @Test
        @DisplayName("null isSystem은 false로 변환")
        void givenNullIsSystem_whenToCommand_thenDefaultsFalse() {
            // given
            CreatePermissionApiRequest request =
                    new CreatePermissionApiRequest("user", "read", "설명", null);

            // when
            CreatePermissionCommand command = mapper.toCommand(request);

            // then
            assertThat(command.isSystem()).isFalse();
        }
    }

    @Nested
    @DisplayName("toCommand(String, UpdatePermissionApiRequest) 테스트")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("UpdatePermissionApiRequest를 UpdatePermissionCommand로 변환 성공")
        void givenUpdateRequest_whenToCommand_thenSuccess() {
            // given
            UpdatePermissionApiRequest request = new UpdatePermissionApiRequest("수정된 설명");

            // when
            UpdatePermissionCommand command = mapper.toCommand(PERMISSION_UUID.toString(), request);

            // then
            assertThat(command.permissionId()).isEqualTo(PERMISSION_UUID);
            assertThat(command.description()).isEqualTo("수정된 설명");
        }
    }

    @Nested
    @DisplayName("toDeleteCommand() 테스트")
    class ToDeleteCommandTest {

        @Test
        @DisplayName("permissionId를 DeletePermissionCommand로 변환 성공")
        void givenPermissionId_whenToDeleteCommand_thenSuccess() {
            // when
            DeletePermissionCommand command = mapper.toDeleteCommand(PERMISSION_UUID.toString());

            // then
            assertThat(command.permissionId()).isEqualTo(PERMISSION_UUID);
        }
    }

    @Nested
    @DisplayName("toGetQuery() 테스트")
    class ToGetQueryTest {

        @Test
        @DisplayName("permissionId를 GetPermissionQuery로 변환 성공")
        void givenPermissionId_whenToGetQuery_thenSuccess() {
            // when
            GetPermissionQuery query = mapper.toGetQuery(PERMISSION_UUID.toString());

            // then
            assertThat(query.permissionId()).isEqualTo(PERMISSION_UUID);
        }
    }

    @Nested
    @DisplayName("toQuery(SearchPermissionsApiRequest) 테스트")
    class ToSearchQueryTest {

        @Test
        @DisplayName("SearchPermissionsApiRequest를 SearchPermissionsQuery로 변환 성공")
        void givenSearchRequest_whenToQuery_thenSuccess() {
            // given
            SearchPermissionsApiRequest request =
                    new SearchPermissionsApiRequest("user", "read", "CUSTOM", 0, 20);

            // when
            SearchPermissionsQuery query = mapper.toQuery(request);

            // then
            assertThat(query.resource()).isEqualTo("user");
            assertThat(query.action()).isEqualTo("read");
            assertThat(query.type()).isEqualTo(PermissionType.CUSTOM);
            assertThat(query.page()).isZero();
            assertThat(query.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("유효하지 않은 type은 null로 변환")
        void givenInvalidType_whenToQuery_thenTypeIsNull() {
            // given
            SearchPermissionsApiRequest request =
                    new SearchPermissionsApiRequest("user", "read", "INVALID", 0, 20);

            // when
            SearchPermissionsQuery query = mapper.toQuery(request);

            // then
            assertThat(query.type()).isNull();
        }

        @Test
        @DisplayName("빈 type은 null로 변환")
        void givenBlankType_whenToQuery_thenTypeIsNull() {
            // given
            SearchPermissionsApiRequest request =
                    new SearchPermissionsApiRequest("user", "read", "  ", 0, 20);

            // when
            SearchPermissionsQuery query = mapper.toQuery(request);

            // then
            assertThat(query.type()).isNull();
        }

        @Test
        @DisplayName("소문자 type도 변환 성공")
        void givenLowercaseType_whenToQuery_thenConverts() {
            // given
            SearchPermissionsApiRequest request =
                    new SearchPermissionsApiRequest("user", "read", "system", 0, 20);

            // when
            SearchPermissionsQuery query = mapper.toQuery(request);

            // then
            assertThat(query.type()).isEqualTo(PermissionType.SYSTEM);
        }
    }

    @Nested
    @DisplayName("toCreateResponse() 테스트")
    class ToCreateResponseTest {

        @Test
        @DisplayName("PermissionResponse를 CreatePermissionApiResponse로 변환 성공")
        void givenPermissionResponse_whenToCreateResponse_thenSuccess() {
            // given
            PermissionResponse response =
                    new PermissionResponse(
                            PERMISSION_UUID,
                            "user:read",
                            "user",
                            "read",
                            "설명",
                            "CUSTOM",
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            CreatePermissionApiResponse apiResponse = mapper.toCreateResponse(response);

            // then
            assertThat(apiResponse.permissionId()).isEqualTo(PERMISSION_UUID.toString());
        }
    }

    @Nested
    @DisplayName("toApiResponse() 테스트")
    class ToApiResponseTest {

        @Test
        @DisplayName("PermissionResponse를 PermissionApiResponse로 변환 성공")
        void givenPermissionResponse_whenToApiResponse_thenSuccess() {
            // given
            PermissionResponse response =
                    new PermissionResponse(
                            PERMISSION_UUID,
                            "user:read",
                            "user",
                            "read",
                            "사용자 조회 권한",
                            "CUSTOM",
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            PermissionApiResponse apiResponse = mapper.toApiResponse(response);

            // then
            assertThat(apiResponse.permissionId()).isEqualTo(PERMISSION_UUID.toString());
            assertThat(apiResponse.key()).isEqualTo("user:read");
            assertThat(apiResponse.resource()).isEqualTo("user");
            assertThat(apiResponse.action()).isEqualTo("read");
            assertThat(apiResponse.description()).isEqualTo("사용자 조회 권한");
            assertThat(apiResponse.type()).isEqualTo("CUSTOM");
            assertThat(apiResponse.createdAt()).isEqualTo(FIXED_INSTANT);
            assertThat(apiResponse.updatedAt()).isEqualTo(FIXED_INSTANT);
        }
    }

    @Nested
    @DisplayName("toApiResponseList() 테스트")
    class ToApiResponseListTest {

        @Test
        @DisplayName("PermissionResponse 목록을 PermissionApiResponse 목록으로 변환 성공")
        void givenResponseList_whenToApiResponseList_thenSuccess() {
            // given
            PermissionResponse response1 =
                    new PermissionResponse(
                            PERMISSION_UUID,
                            "user:read",
                            "user",
                            "read",
                            "설명1",
                            "CUSTOM",
                            FIXED_INSTANT,
                            FIXED_INSTANT);
            PermissionResponse response2 =
                    new PermissionResponse(
                            UUID.randomUUID(),
                            "user:write",
                            "user",
                            "write",
                            "설명2",
                            "SYSTEM",
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            List<PermissionApiResponse> apiResponses =
                    mapper.toApiResponseList(List.of(response1, response2));

            // then
            assertThat(apiResponses).hasSize(2);
            assertThat(apiResponses.get(0).key()).isEqualTo("user:read");
            assertThat(apiResponses.get(1).key()).isEqualTo("user:write");
        }
    }

    @Nested
    @DisplayName("toUserPermissionsApiResponse() 테스트")
    class ToUserPermissionsApiResponseTest {

        @Test
        @DisplayName("UserRolesResponse를 UserPermissionsApiResponse로 변환 성공")
        void givenUserRolesResponse_whenToUserPermissionsApiResponse_thenSuccess() {
            // given
            Set<String> roles = Set.of("ADMIN", "USER");
            Set<String> permissions = Set.of("user:read", "user:write");
            UserRolesResponse response = new UserRolesResponse(USER_UUID, roles, permissions);

            // when
            UserPermissionsApiResponse apiResponse = mapper.toUserPermissionsApiResponse(response);

            // then
            assertThat(apiResponse.userId()).isEqualTo(USER_UUID.toString());
            assertThat(apiResponse.roles()).containsExactlyInAnyOrder("ADMIN", "USER");
            assertThat(apiResponse.permissions())
                    .containsExactlyInAnyOrder("user:read", "user:write");
        }
    }
}
