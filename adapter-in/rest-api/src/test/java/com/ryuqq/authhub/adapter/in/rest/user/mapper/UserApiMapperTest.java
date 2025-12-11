package com.ryuqq.authhub.adapter.in.rest.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.user.dto.command.AssignUserRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserPasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.query.SearchUsersApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.CreateUserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserRoleApiResponse;
import com.ryuqq.authhub.application.user.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.DeleteUserCommand;
import com.ryuqq.authhub.application.user.dto.command.RevokeUserRoleCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserPasswordCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserStatusCommand;
import com.ryuqq.authhub.application.user.dto.query.GetUserQuery;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.dto.response.UserRoleResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("mapper")
@DisplayName("UserApiMapper 테스트")
class UserApiMapperTest {

    private UserApiMapper mapper;

    private static final UUID USER_UUID = UUID.fromString("01941234-5678-7000-8000-000000000001");
    private static final UUID TENANT_UUID = UUID.fromString("01941234-5678-7000-8000-123456789abc");
    private static final UUID ORG_UUID = UUID.fromString("01941234-5678-7000-8000-123456789def");
    private static final UUID ROLE_UUID = UUID.fromString("01941234-5678-7000-8000-123456789111");
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        mapper = new UserApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateUserApiRequest) 테스트")
    class ToCreateCommandTest {

        @Test
        @DisplayName("CreateUserApiRequest를 CreateUserCommand로 변환 성공")
        void givenCreateRequest_whenToCommand_thenSuccess() {
            // given
            CreateUserApiRequest request =
                    new CreateUserApiRequest(
                            TENANT_UUID.toString(),
                            ORG_UUID.toString(),
                            "user@example.com",
                            "password123");

            // when
            CreateUserCommand command = mapper.toCommand(request);

            // then
            assertThat(command.tenantId()).isEqualTo(TENANT_UUID);
            assertThat(command.organizationId()).isEqualTo(ORG_UUID);
            assertThat(command.identifier()).isEqualTo("user@example.com");
            assertThat(command.password()).isEqualTo("password123");
        }
    }

    @Nested
    @DisplayName("toCommand(String, UpdateUserApiRequest) 테스트")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("UpdateUserApiRequest를 UpdateUserCommand로 변환 성공")
        void givenUpdateRequest_whenToCommand_thenSuccess() {
            // given
            UpdateUserApiRequest request = new UpdateUserApiRequest("updated@example.com");

            // when
            UpdateUserCommand command = mapper.toCommand(USER_UUID.toString(), request);

            // then
            assertThat(command.userId()).isEqualTo(USER_UUID);
            assertThat(command.identifier()).isEqualTo("updated@example.com");
        }
    }

    @Nested
    @DisplayName("toStatusCommand() 테스트")
    class ToStatusCommandTest {

        @Test
        @DisplayName("UpdateUserStatusApiRequest를 UpdateUserStatusCommand로 변환 성공")
        void givenStatusRequest_whenToStatusCommand_thenSuccess() {
            // given
            UpdateUserStatusApiRequest request = new UpdateUserStatusApiRequest("INACTIVE");

            // when
            UpdateUserStatusCommand command = mapper.toStatusCommand(USER_UUID.toString(), request);

            // then
            assertThat(command.userId()).isEqualTo(USER_UUID);
            assertThat(command.status()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("toPasswordCommand() 테스트")
    class ToPasswordCommandTest {

        @Test
        @DisplayName("UpdateUserPasswordApiRequest를 UpdateUserPasswordCommand로 변환 성공")
        void givenPasswordRequest_whenToPasswordCommand_thenSuccess() {
            // given
            UpdateUserPasswordApiRequest request =
                    new UpdateUserPasswordApiRequest("oldPassword", "newPassword123");

            // when
            UpdateUserPasswordCommand command =
                    mapper.toPasswordCommand(USER_UUID.toString(), request);

            // then
            assertThat(command.userId()).isEqualTo(USER_UUID);
            assertThat(command.currentPassword()).isEqualTo("oldPassword");
            assertThat(command.newPassword()).isEqualTo("newPassword123");
        }
    }

    @Nested
    @DisplayName("toDeleteCommand() 테스트")
    class ToDeleteCommandTest {

        @Test
        @DisplayName("userId를 DeleteUserCommand로 변환 성공")
        void givenUserId_whenToDeleteCommand_thenSuccess() {
            // when
            DeleteUserCommand command = mapper.toDeleteCommand(USER_UUID.toString());

            // then
            assertThat(command.userId()).isEqualTo(USER_UUID);
        }
    }

    @Nested
    @DisplayName("toGetQuery() 테스트")
    class ToGetQueryTest {

        @Test
        @DisplayName("userId를 GetUserQuery로 변환 성공")
        void givenUserId_whenToGetQuery_thenSuccess() {
            // when
            GetUserQuery query = mapper.toGetQuery(USER_UUID.toString());

            // then
            assertThat(query.userId()).isEqualTo(USER_UUID);
        }
    }

    @Nested
    @DisplayName("toQuery(SearchUsersApiRequest) 테스트")
    class ToSearchQueryTest {

        @Test
        @DisplayName("SearchUsersApiRequest를 SearchUsersQuery로 변환 성공")
        void givenSearchRequest_whenToQuery_thenSuccess() {
            // given
            SearchUsersApiRequest request =
                    new SearchUsersApiRequest(
                            TENANT_UUID.toString(), ORG_UUID.toString(), "user@", "ACTIVE", 0, 20);

            // when
            SearchUsersQuery query = mapper.toQuery(request);

            // then
            assertThat(query.tenantId()).isEqualTo(TENANT_UUID);
            assertThat(query.organizationId()).isEqualTo(ORG_UUID);
            assertThat(query.identifier()).isEqualTo("user@");
            assertThat(query.status()).isEqualTo("ACTIVE");
            assertThat(query.page()).isZero();
            assertThat(query.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("null 값들도 변환 성공")
        void givenNullValues_whenToQuery_thenSuccess() {
            // given
            SearchUsersApiRequest request =
                    new SearchUsersApiRequest(null, null, null, null, null, null);

            // when
            SearchUsersQuery query = mapper.toQuery(request);

            // then
            assertThat(query.tenantId()).isNull();
            assertThat(query.organizationId()).isNull();
            assertThat(query.page()).isZero();
            assertThat(query.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("toCreateResponse() 테스트")
    class ToCreateResponseTest {

        @Test
        @DisplayName("UserResponse를 CreateUserApiResponse로 변환 성공")
        void givenUserResponse_whenToCreateResponse_thenSuccess() {
            // given
            UserResponse response =
                    new UserResponse(
                            USER_UUID,
                            TENANT_UUID,
                            ORG_UUID,
                            "user@example.com",
                            "ACTIVE",
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            CreateUserApiResponse apiResponse = mapper.toCreateResponse(response);

            // then
            assertThat(apiResponse.userId()).isEqualTo(USER_UUID.toString());
        }
    }

    @Nested
    @DisplayName("toApiResponse() 테스트")
    class ToApiResponseTest {

        @Test
        @DisplayName("UserResponse를 UserApiResponse로 변환 성공")
        void givenUserResponse_whenToApiResponse_thenSuccess() {
            // given
            UserResponse response =
                    new UserResponse(
                            USER_UUID,
                            TENANT_UUID,
                            ORG_UUID,
                            "user@example.com",
                            "ACTIVE",
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            UserApiResponse apiResponse = mapper.toApiResponse(response);

            // then
            assertThat(apiResponse.userId()).isEqualTo(USER_UUID.toString());
            assertThat(apiResponse.tenantId()).isEqualTo(TENANT_UUID.toString());
            assertThat(apiResponse.organizationId()).isEqualTo(ORG_UUID.toString());
            assertThat(apiResponse.identifier()).isEqualTo("user@example.com");
            assertThat(apiResponse.status()).isEqualTo("ACTIVE");
            assertThat(apiResponse.createdAt()).isEqualTo(FIXED_INSTANT);
            assertThat(apiResponse.updatedAt()).isEqualTo(FIXED_INSTANT);
        }
    }

    @Nested
    @DisplayName("toApiResponseList() 테스트")
    class ToApiResponseListTest {

        @Test
        @DisplayName("UserResponse 목록을 UserApiResponse 목록으로 변환 성공")
        void givenResponseList_whenToApiResponseList_thenSuccess() {
            // given
            UserResponse response1 =
                    new UserResponse(
                            USER_UUID,
                            TENANT_UUID,
                            ORG_UUID,
                            "user1@example.com",
                            "ACTIVE",
                            FIXED_INSTANT,
                            FIXED_INSTANT);
            UserResponse response2 =
                    new UserResponse(
                            UUID.randomUUID(),
                            TENANT_UUID,
                            ORG_UUID,
                            "user2@example.com",
                            "INACTIVE",
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            List<UserApiResponse> apiResponses =
                    mapper.toApiResponseList(List.of(response1, response2));

            // then
            assertThat(apiResponses).hasSize(2);
            assertThat(apiResponses.get(0).identifier()).isEqualTo("user1@example.com");
            assertThat(apiResponses.get(1).identifier()).isEqualTo("user2@example.com");
        }
    }

    @Nested
    @DisplayName("UserRole 관련 변환 테스트")
    class UserRoleMappingTest {

        @Test
        @DisplayName("toAssignRoleCommand 변환 성공")
        void givenAssignRequest_whenToAssignRoleCommand_thenSuccess() {
            // given
            AssignUserRoleApiRequest request = new AssignUserRoleApiRequest(ROLE_UUID);

            // when
            AssignUserRoleCommand command =
                    mapper.toAssignRoleCommand(USER_UUID.toString(), request);

            // then
            assertThat(command.userId()).isEqualTo(USER_UUID);
            assertThat(command.roleId()).isEqualTo(ROLE_UUID);
        }

        @Test
        @DisplayName("toRevokeRoleCommand 변환 성공")
        void givenUserIdAndRoleId_whenToRevokeRoleCommand_thenSuccess() {
            // when
            RevokeUserRoleCommand command =
                    mapper.toRevokeRoleCommand(USER_UUID.toString(), ROLE_UUID.toString());

            // then
            assertThat(command.userId()).isEqualTo(USER_UUID);
            assertThat(command.roleId()).isEqualTo(ROLE_UUID);
        }

        @Test
        @DisplayName("toUserRoleApiResponse 변환 성공")
        void givenUserRoleResponse_whenToApiResponse_thenSuccess() {
            // given
            UserRoleResponse response = new UserRoleResponse(USER_UUID, ROLE_UUID, FIXED_INSTANT);

            // when
            UserRoleApiResponse apiResponse = mapper.toUserRoleApiResponse(response);

            // then
            assertThat(apiResponse.userId()).isEqualTo(USER_UUID);
            assertThat(apiResponse.roleId()).isEqualTo(ROLE_UUID);
            assertThat(apiResponse.assignedAt()).isEqualTo(FIXED_INSTANT);
        }

        @Test
        @DisplayName("toUserRoleApiResponseList 변환 성공")
        void givenResponseList_whenToUserRoleApiResponseList_thenSuccess() {
            // given
            UserRoleResponse response1 = new UserRoleResponse(USER_UUID, ROLE_UUID, FIXED_INSTANT);
            UserRoleResponse response2 =
                    new UserRoleResponse(USER_UUID, UUID.randomUUID(), FIXED_INSTANT);

            // when
            List<UserRoleApiResponse> apiResponses =
                    mapper.toUserRoleApiResponseList(List.of(response1, response2));

            // then
            assertThat(apiResponses).hasSize(2);
        }
    }
}
