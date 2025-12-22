package com.ryuqq.authhub.adapter.in.rest.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContextHolder;
import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.AssignUserRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserPasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.CreateUserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserRoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserApiMapper;
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
import com.ryuqq.authhub.application.user.port.in.command.AssignUserRoleUseCase;
import com.ryuqq.authhub.application.user.port.in.command.CreateUserUseCase;
import com.ryuqq.authhub.application.user.port.in.command.DeleteUserUseCase;
import com.ryuqq.authhub.application.user.port.in.command.RevokeUserRoleUseCase;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserPasswordUseCase;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserStatusUseCase;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserUseCase;
import com.ryuqq.authhub.application.user.port.in.query.GetUserUseCase;
import com.ryuqq.authhub.application.user.port.in.query.SearchUsersUseCase;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

/**
 * UserController REST API 문서화 테스트
 *
 * <p>사용자 API의 REST Docs 문서를 생성합니다.
 *
 * <p>문서화 대상 엔드포인트:
 *
 * <ul>
 *   <li>POST /api/v1/auth/users - 사용자 생성
 *   <li>PUT /api/v1/auth/users/{userId} - 사용자 정보 수정
 *   <li>PATCH /api/v1/auth/users/{userId}/status - 사용자 상태 변경
 *   <li>PATCH /api/v1/auth/users/{userId}/password - 사용자 비밀번호 변경
 *   <li>PATCH /api/v1/auth/users/{userId}/delete - 사용자 삭제
 *   <li>GET /api/v1/auth/users/me - 내 정보 조회
 *   <li>GET /api/v1/auth/users/{userId} - 사용자 단건 조회
 *   <li>GET /api/v1/auth/users - 사용자 목록 검색
 *   <li>POST /api/v1/auth/users/{userId}/roles - 사용자 역할 할당
 *   <li>PATCH /api/v1/auth/users/{userId}/roles/{roleId}/revoke - 사용자 역할 해제
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest({UserCommandController.class, UserQueryController.class, UserRoleController.class})
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("docs")
@DisplayName("User API 문서화 테스트")
class UserControllerDocsTest extends RestDocsTestSupport {

    @MockBean private CreateUserUseCase createUserUseCase;
    @MockBean private UpdateUserUseCase updateUserUseCase;
    @MockBean private UpdateUserStatusUseCase updateUserStatusUseCase;
    @MockBean private UpdateUserPasswordUseCase updateUserPasswordUseCase;
    @MockBean private DeleteUserUseCase deleteUserUseCase;
    @MockBean private GetUserUseCase getUserUseCase;
    @MockBean private SearchUsersUseCase searchUsersUseCase;
    @MockBean private AssignUserRoleUseCase assignUserRoleUseCase;
    @MockBean private RevokeUserRoleUseCase revokeUserRoleUseCase;
    @MockBean private UserApiMapper mapper;
    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Test
    @DisplayName("POST /api/v1/users - 사용자 생성")
    void createUser() throws Exception {
        // given
        String tenantId = UUID.randomUUID().toString();
        String organizationId = UUID.randomUUID().toString();
        String identifier = "user@example.com";
        String password = "password123";
        CreateUserApiRequest request =
                new CreateUserApiRequest(tenantId, organizationId, identifier, password);

        UUID userId = UUID.randomUUID();
        CreateUserCommand command =
                new CreateUserCommand(
                        UUID.fromString(tenantId),
                        UUID.fromString(organizationId),
                        identifier,
                        password);
        Instant now = Instant.now();
        UserResponse useCaseResponse =
                new UserResponse(
                        userId,
                        UUID.fromString(tenantId),
                        UUID.fromString(organizationId),
                        identifier,
                        "ACTIVE",
                        now,
                        now);

        given(mapper.toCommand(any(CreateUserApiRequest.class))).willReturn(command);
        given(createUserUseCase.execute(any(CreateUserCommand.class))).willReturn(useCaseResponse);
        given(mapper.toCreateResponse(any(UserResponse.class)))
                .willReturn(new CreateUserApiResponse(userId.toString()));

        // when & then
        mockMvc.perform(
                        post("/api/v1/auth/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "user-create",
                                requestFields(
                                        fieldWithPath("tenantId").description("테넌트 ID (UUID 형식)"),
                                        fieldWithPath("organizationId")
                                                .description("조직 ID (UUID 형식)"),
                                        fieldWithPath("identifier")
                                                .description("사용자 식별자 (이메일 또는 사용자명, 1-255자)"),
                                        fieldWithPath("password").description("비밀번호 (8-100자)")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("data").description("응답 데이터"),
                                        fieldWithPath("data.userId").description("생성된 사용자 ID"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("PUT /api/v1/users/{userId} - 사용자 정보 수정")
    void updateUser() throws Exception {
        // given
        String userId = UUID.randomUUID().toString();
        String newIdentifier = "newuser@example.com";
        UpdateUserApiRequest request = new UpdateUserApiRequest(newIdentifier);

        UpdateUserCommand command = new UpdateUserCommand(UUID.fromString(userId), newIdentifier);

        given(mapper.toCommand(any(String.class), any(UpdateUserApiRequest.class)))
                .willReturn(command);

        // when & then
        mockMvc.perform(
                        put("/api/v1/auth/users/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "user-update",
                                pathParameters(
                                        parameterWithName("userId")
                                                .description("사용자 ID (UUID 형식)")),
                                requestFields(
                                        fieldWithPath("identifier")
                                                .description("새로운 사용자 식별자 (1-255자)")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("data").description("응답 데이터 (수정 시 null)"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/{userId}/status - 사용자 상태 변경")
    void updateUserStatus() throws Exception {
        // given
        String userId = UUID.randomUUID().toString();
        String status = "INACTIVE";
        UpdateUserStatusApiRequest request = new UpdateUserStatusApiRequest(status);

        UpdateUserStatusCommand command =
                new UpdateUserStatusCommand(UUID.fromString(userId), status);

        given(mapper.toStatusCommand(any(String.class), any(UpdateUserStatusApiRequest.class)))
                .willReturn(command);

        // when & then
        mockMvc.perform(
                        patch("/api/v1/auth/users/{userId}/status", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "user-update-status",
                                pathParameters(
                                        parameterWithName("userId")
                                                .description("사용자 ID (UUID 형식)")),
                                requestFields(
                                        fieldWithPath("status")
                                                .description("변경할 상태 (ACTIVE/INACTIVE/SUSPENDED)")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("data").description("응답 데이터 (수정 시 null)"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/{userId}/password - 사용자 비밀번호 변경")
    void updateUserPassword() throws Exception {
        // given
        String userId = UUID.randomUUID().toString();
        String currentPassword = "oldPassword123";
        String newPassword = "newPassword456";
        UpdateUserPasswordApiRequest request =
                new UpdateUserPasswordApiRequest(currentPassword, newPassword);

        UpdateUserPasswordCommand command =
                new UpdateUserPasswordCommand(
                        UUID.fromString(userId), currentPassword, newPassword);

        given(mapper.toPasswordCommand(any(String.class), any(UpdateUserPasswordApiRequest.class)))
                .willReturn(command);

        // when & then
        mockMvc.perform(
                        patch("/api/v1/auth/users/{userId}/password", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "user-update-password",
                                pathParameters(
                                        parameterWithName("userId")
                                                .description("사용자 ID (UUID 형식)")),
                                requestFields(
                                        fieldWithPath("currentPassword").description("현재 비밀번호"),
                                        fieldWithPath("newPassword")
                                                .description("새로운 비밀번호 (8-100자)")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("data").description("응답 데이터 (수정 시 null)"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/{userId}/delete - 사용자 삭제")
    void deleteUser() throws Exception {
        // given
        String userId = UUID.randomUUID().toString();
        DeleteUserCommand command = new DeleteUserCommand(UUID.fromString(userId));

        given(mapper.toDeleteCommand(any(String.class))).willReturn(command);

        // when & then
        mockMvc.perform(patch("/api/v1/auth/users/{userId}/delete", userId))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "user-delete",
                                pathParameters(
                                        parameterWithName("userId")
                                                .description("사용자 ID (UUID 형식)"))));
    }

    @Test
    @DisplayName("GET /api/v1/users/me - 내 정보 조회")
    void getMyInfo() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        Instant now = Instant.now();

        UserResponse useCaseResponse =
                new UserResponse(
                        userId, tenantId, organizationId, "me@example.com", "ACTIVE", now, now);
        UserApiResponse apiResponse =
                new UserApiResponse(
                        userId.toString(),
                        tenantId.toString(),
                        organizationId.toString(),
                        "me@example.com",
                        "ACTIVE",
                        now,
                        now);

        try (MockedStatic<SecurityContextHolder> mockedStatic =
                Mockito.mockStatic(SecurityContextHolder.class)) {
            mockedStatic
                    .when(SecurityContextHolder::getCurrentUserId)
                    .thenReturn(userId.toString());

            given(mapper.toGetQuery(userId.toString())).willReturn(new GetUserQuery(userId));
            given(getUserUseCase.execute(any(GetUserQuery.class))).willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(UserResponse.class))).willReturn(apiResponse);

            // when & then
            mockMvc.perform(get("/api/v1/auth/users/me").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "user-me",
                                    responseFields(
                                            fieldWithPath("success").description("API 성공 여부"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data.userId").description("사용자 ID"),
                                            fieldWithPath("data.tenantId").description("테넌트 ID"),
                                            fieldWithPath("data.organizationId")
                                                    .description("조직 ID"),
                                            fieldWithPath("data.identifier").description("사용자 식별자"),
                                            fieldWithPath("data.status").description("사용자 상태"),
                                            fieldWithPath("data.createdAt").description("생성 일시"),
                                            fieldWithPath("data.updatedAt").description("수정 일시"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Test
    @DisplayName("GET /api/v1/users/{userId} - 사용자 단건 조회")
    void getUser() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        Instant now = Instant.now();

        UserResponse useCaseResponse =
                new UserResponse(
                        userId, tenantId, organizationId, "test@example.com", "ACTIVE", now, now);
        UserApiResponse apiResponse =
                new UserApiResponse(
                        userId.toString(),
                        tenantId.toString(),
                        organizationId.toString(),
                        "test@example.com",
                        "ACTIVE",
                        now,
                        now);

        given(mapper.toGetQuery(userId.toString())).willReturn(new GetUserQuery(userId));
        given(getUserUseCase.execute(any(GetUserQuery.class))).willReturn(useCaseResponse);
        given(mapper.toApiResponse(any(UserResponse.class))).willReturn(apiResponse);

        // when & then
        mockMvc.perform(
                        get("/api/v1/auth/users/{userId}", userId)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "user-get",
                                pathParameters(
                                        parameterWithName("userId")
                                                .description("사용자 ID (UUID 형식)")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("data").description("응답 데이터"),
                                        fieldWithPath("data.userId").description("사용자 ID"),
                                        fieldWithPath("data.tenantId").description("테넌트 ID"),
                                        fieldWithPath("data.organizationId").description("조직 ID"),
                                        fieldWithPath("data.identifier").description("사용자 식별자"),
                                        fieldWithPath("data.status").description("사용자 상태"),
                                        fieldWithPath("data.createdAt").description("생성 일시"),
                                        fieldWithPath("data.updatedAt").description("수정 일시"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("GET /api/v1/users - 사용자 목록 검색")
    void searchUsers() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        Instant now = Instant.now();

        UserResponse response =
                new UserResponse(
                        userId, tenantId, organizationId, "test@example.com", "ACTIVE", now, now);
        UserApiResponse apiResponse =
                new UserApiResponse(
                        userId.toString(),
                        tenantId.toString(),
                        organizationId.toString(),
                        "test@example.com",
                        "ACTIVE",
                        now,
                        now);

        given(mapper.toQuery(any()))
                .willReturn(
                        new SearchUsersQuery(
                                tenantId, organizationId, "test@example.com", "ACTIVE", 0, 20));
        given(searchUsersUseCase.execute(any(SearchUsersQuery.class)))
                .willReturn(List.of(response));
        given(mapper.toApiResponseList(any())).willReturn(List.of(apiResponse));

        // when & then
        mockMvc.perform(
                        get("/api/v1/auth/users")
                                .param("tenantId", tenantId.toString())
                                .param("organizationId", organizationId.toString())
                                .param("identifier", "test@example.com")
                                .param("status", "ACTIVE")
                                .param("page", "0")
                                .param("size", "20")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "user-search",
                                queryParameters(
                                        parameterWithName("tenantId")
                                                .description("테넌트 ID 필터 (선택, UUID 형식)")
                                                .optional(),
                                        parameterWithName("organizationId")
                                                .description("조직 ID 필터 (선택, UUID 형식)")
                                                .optional(),
                                        parameterWithName("identifier")
                                                .description("사용자 식별자 필터 (선택)")
                                                .optional(),
                                        parameterWithName("status")
                                                .description(
                                                        "사용자 상태 필터 (선택, ACTIVE/INACTIVE/SUSPENDED)")
                                                .optional(),
                                        parameterWithName("page")
                                                .description("페이지 번호 (기본값: 0)")
                                                .optional(),
                                        parameterWithName("size")
                                                .description("페이지 크기 (기본값: 20)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("data").description("응답 데이터 (사용자 목록)"),
                                        fieldWithPath("data[].userId").description("사용자 ID"),
                                        fieldWithPath("data[].tenantId").description("테넌트 ID"),
                                        fieldWithPath("data[].organizationId").description("조직 ID"),
                                        fieldWithPath("data[].identifier").description("사용자 식별자"),
                                        fieldWithPath("data[].status").description("사용자 상태"),
                                        fieldWithPath("data[].createdAt").description("생성 일시"),
                                        fieldWithPath("data[].updatedAt").description("수정 일시"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("POST /api/v1/users/{userId}/roles - 사용자 역할 할당")
    void assignUserRole() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        AssignUserRoleApiRequest request = new AssignUserRoleApiRequest(roleId);

        AssignUserRoleCommand command = new AssignUserRoleCommand(userId, roleId);
        Instant assignedAt = Instant.now();
        UserRoleResponse useCaseResponse = new UserRoleResponse(userId, roleId, assignedAt);
        UserRoleApiResponse apiResponse = new UserRoleApiResponse(userId, roleId, assignedAt);

        given(mapper.toAssignRoleCommand(any(String.class), any(AssignUserRoleApiRequest.class)))
                .willReturn(command);
        given(assignUserRoleUseCase.execute(any(AssignUserRoleCommand.class)))
                .willReturn(useCaseResponse);
        given(mapper.toUserRoleApiResponse(any(UserRoleResponse.class))).willReturn(apiResponse);

        // when & then
        mockMvc.perform(
                        post("/api/v1/auth/users/{userId}/roles", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "user-role-assign",
                                pathParameters(
                                        parameterWithName("userId")
                                                .description("사용자 ID (UUID 형식)")),
                                requestFields(
                                        fieldWithPath("roleId").description("할당할 역할 ID (UUID 형식)")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("data").description("응답 데이터"),
                                        fieldWithPath("data.userId").description("사용자 ID"),
                                        fieldWithPath("data.roleId").description("역할 ID"),
                                        fieldWithPath("data.assignedAt").description("할당 일시"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/{userId}/roles/{roleId}/revoke - 사용자 역할 해제")
    void revokeUserRole() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        RevokeUserRoleCommand command = new RevokeUserRoleCommand(userId, roleId);

        given(mapper.toRevokeRoleCommand(any(String.class), any(String.class))).willReturn(command);

        // when & then
        mockMvc.perform(
                        patch(
                                "/api/v1/auth/users/{userId}/roles/{roleId}/revoke",
                                userId.toString(),
                                roleId.toString()))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "user-role-revoke",
                                pathParameters(
                                        parameterWithName("userId").description("사용자 ID (UUID 형식)"),
                                        parameterWithName("roleId")
                                                .description("해제할 역할 ID (UUID 형식)"))));
    }
}
