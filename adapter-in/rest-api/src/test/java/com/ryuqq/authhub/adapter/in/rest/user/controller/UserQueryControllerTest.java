package com.ryuqq.authhub.adapter.in.rest.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.mapper.UserApiMapper;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.user.dto.query.GetUserQuery;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.port.in.query.GetUserDetailUseCase;
import com.ryuqq.authhub.application.user.port.in.query.GetUserUseCase;
import com.ryuqq.authhub.application.user.port.in.query.SearchUsersAdminUseCase;
import com.ryuqq.authhub.application.user.port.in.query.SearchUsersUseCase;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * UserQueryController 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>HTTP 요청/응답 매핑
 *   <li>Path Variable 및 Query Parameter 바인딩
 *   <li>Response DTO 직렬화
 *   <li>HTTP Status Code
 *   <li>UseCase 호출 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(UserQueryController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserQueryController 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
class UserQueryControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private GetUserUseCase getUserUseCase;

    @MockBean private GetUserDetailUseCase getUserDetailUseCase;

    @MockBean private SearchUsersUseCase searchUsersUseCase;

    @MockBean private SearchUsersAdminUseCase searchUsersAdminUseCase;

    @MockBean private UserApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/users/{userId} - 사용자 단건 조회")
    class GetUserTest {

        @Test
        @DisplayName("[성공] 존재하는 사용자 ID로 조회 시 200 OK 반환")
        void getUser_withExistingId_returns200Ok() throws Exception {
            // Given
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            Instant now = Instant.now();

            UserResponse useCaseResponse =
                    new UserResponse(
                            userId,
                            tenantId,
                            organizationId,
                            "test@example.com",
                            "010-1234-5678",
                            "ACTIVE",
                            now,
                            now);
            UserApiResponse apiResponse =
                    new UserApiResponse(
                            userId.toString(),
                            tenantId.toString(),
                            organizationId.toString(),
                            "test@example.com",
                            "010-1234-5678",
                            "ACTIVE",
                            now,
                            now);

            given(mapper.toGetQuery(userId.toString())).willReturn(new GetUserQuery(userId));
            given(getUserUseCase.execute(any(GetUserQuery.class))).willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(UserResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/users/{userId}", userId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                    .andExpect(jsonPath("$.data.tenantId").value(tenantId.toString()))
                    .andExpect(jsonPath("$.data.organizationId").value(organizationId.toString()))
                    .andExpect(jsonPath("$.data.identifier").value("test@example.com"))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"));

            verify(mapper).toGetQuery(userId.toString());
            verify(getUserUseCase).execute(any(GetUserQuery.class));
            verify(mapper).toApiResponse(any(UserResponse.class));
        }

        @Test
        @DisplayName("[실패] 존재하지 않는 사용자 ID로 조회 시 404 Not Found 반환")
        void getUser_withNonExistingId_returns404NotFound() throws Exception {
            // Given
            UUID userId = UUID.randomUUID();
            given(mapper.toGetQuery(userId.toString())).willReturn(new GetUserQuery(userId));
            given(getUserUseCase.execute(any(GetUserQuery.class)))
                    .willThrow(new UserNotFoundException(UserId.of(userId)));

            // ErrorMapperRegistry mock 설정 - 404 응답 매핑
            ErrorMapper.MappedError mappedError =
                    new ErrorMapper.MappedError(
                            HttpStatus.NOT_FOUND,
                            "User Not Found",
                            "User not found",
                            URI.create("https://authhub.ryuqq.com/errors/user-not-found"));
            given(errorMapperRegistry.map(any(), any())).willReturn(Optional.of(mappedError));

            // When & Then
            // GlobalExceptionHandler가 ProblemDetail (RFC 7807) 형식으로 반환
            mockMvc.perform(
                            get("/api/v1/auth/users/{userId}", userId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.title").value("User Not Found"));

            verify(mapper).toGetQuery(userId.toString());
            verify(getUserUseCase).execute(any(GetUserQuery.class));
        }

        @Test
        @DisplayName("[실패] 잘못된 UUID 형식으로 조회 시 400 Bad Request 반환")
        void getUser_withInvalidUuid_returns400BadRequest() throws Exception {
            // Given
            String invalidUuid = "not-a-valid-uuid";

            given(mapper.toGetQuery(any(String.class)))
                    .willThrow(new IllegalArgumentException("Invalid UUID format"));

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/users/{userId}", invalidUuid)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/users - 사용자 목록 검색")
    class SearchUsersTest {

        private static final Instant CREATED_FROM = Instant.parse("2025-01-01T00:00:00Z");
        private static final Instant CREATED_TO = Instant.parse("2025-12-31T23:59:59Z");

        @Test
        @DisplayName("[성공] 필수 날짜 범위 파라미터로 조회 시 200 OK 반환")
        void searchUsers_withRequiredDateRange_returns200Ok() throws Exception {
            // Given
            UUID userId = UUID.randomUUID();
            UUID tenantId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            Instant now = Instant.now();

            UserResponse response =
                    new UserResponse(
                            userId,
                            tenantId,
                            organizationId,
                            "test@example.com",
                            "010-1234-5678",
                            "ACTIVE",
                            now,
                            now);
            UserApiResponse apiResponse =
                    new UserApiResponse(
                            userId.toString(),
                            tenantId.toString(),
                            organizationId.toString(),
                            "test@example.com",
                            "010-1234-5678",
                            "ACTIVE",
                            now,
                            now);

            PageResponse<UserResponse> pageResponse =
                    PageResponse.of(List.of(response), 0, 20, 1L, 1, true, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchUsersQuery.of(
                                    null, null, null, null, CREATED_FROM, CREATED_TO, 0, 20));
            given(searchUsersUseCase.execute(any(SearchUsersQuery.class))).willReturn(pageResponse);
            given(mapper.toApiResponse(any(UserResponse.class))).willReturn(apiResponse);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/users")
                                    .param("createdFrom", CREATED_FROM.toString())
                                    .param("createdTo", CREATED_TO.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].userId").value(userId.toString()))
                    .andExpect(jsonPath("$.data.content[0].identifier").value("test@example.com"))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(20))
                    .andExpect(jsonPath("$.data.totalElements").value(1));

            verify(mapper).toQuery(any());
            verify(searchUsersUseCase).execute(any(SearchUsersQuery.class));
        }

        @Test
        @DisplayName("[성공] 테넌트 ID 필터로 조회 시 200 OK 반환")
        void searchUsers_withTenantIdFilter_returns200Ok() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            PageResponse<UserResponse> emptyPage =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchUsersQuery.of(
                                    tenantId, null, null, null, CREATED_FROM, CREATED_TO, 0, 20));
            given(searchUsersUseCase.execute(any(SearchUsersQuery.class))).willReturn(emptyPage);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/users")
                                    .param("tenantId", tenantId.toString())
                                    .param("createdFrom", CREATED_FROM.toString())
                                    .param("createdTo", CREATED_TO.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray());

            verify(mapper).toQuery(any());
            verify(searchUsersUseCase).execute(any(SearchUsersQuery.class));
        }

        @Test
        @DisplayName("[성공] 조직 ID 필터로 조회 시 200 OK 반환")
        void searchUsers_withOrganizationIdFilter_returns200Ok() throws Exception {
            // Given
            UUID organizationId = UUID.randomUUID();
            PageResponse<UserResponse> emptyPage =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchUsersQuery.of(
                                    null,
                                    organizationId,
                                    null,
                                    null,
                                    CREATED_FROM,
                                    CREATED_TO,
                                    0,
                                    20));
            given(searchUsersUseCase.execute(any(SearchUsersQuery.class))).willReturn(emptyPage);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/users")
                                    .param("organizationId", organizationId.toString())
                                    .param("createdFrom", CREATED_FROM.toString())
                                    .param("createdTo", CREATED_TO.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray());

            verify(mapper).toQuery(any());
            verify(searchUsersUseCase).execute(any(SearchUsersQuery.class));
        }

        @Test
        @DisplayName("[성공] 식별자 필터로 조회 시 200 OK 반환")
        void searchUsers_withIdentifierFilter_returns200Ok() throws Exception {
            // Given
            String identifier = "test@example.com";
            PageResponse<UserResponse> emptyPage =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchUsersQuery.of(
                                    null, null, identifier, null, CREATED_FROM, CREATED_TO, 0, 20));
            given(searchUsersUseCase.execute(any(SearchUsersQuery.class))).willReturn(emptyPage);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/users")
                                    .param("identifier", identifier)
                                    .param("createdFrom", CREATED_FROM.toString())
                                    .param("createdTo", CREATED_TO.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray());

            verify(mapper).toQuery(any());
            verify(searchUsersUseCase).execute(any(SearchUsersQuery.class));
        }

        @Test
        @DisplayName("[성공] 상태 필터로 조회 시 200 OK 반환")
        void searchUsers_withStatusFilter_returns200Ok() throws Exception {
            // Given
            PageResponse<UserResponse> emptyPage =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchUsersQuery.of(
                                    null, null, null, "ACTIVE", CREATED_FROM, CREATED_TO, 0, 20));
            given(searchUsersUseCase.execute(any(SearchUsersQuery.class))).willReturn(emptyPage);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/users")
                                    .param("status", "ACTIVE")
                                    .param("createdFrom", CREATED_FROM.toString())
                                    .param("createdTo", CREATED_TO.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray());

            verify(mapper).toQuery(any());
            verify(searchUsersUseCase).execute(any(SearchUsersQuery.class));
        }

        @Test
        @DisplayName("[성공] 페이징 파라미터로 조회 시 200 OK 반환")
        void searchUsers_withPagingParams_returns200Ok() throws Exception {
            // Given
            PageResponse<UserResponse> emptyPage =
                    PageResponse.of(List.of(), 1, 10, 0L, 0, false, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchUsersQuery.of(
                                    null, null, null, null, CREATED_FROM, CREATED_TO, 1, 10));
            given(searchUsersUseCase.execute(any(SearchUsersQuery.class))).willReturn(emptyPage);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/users")
                                    .param("page", "1")
                                    .param("size", "10")
                                    .param("createdFrom", CREATED_FROM.toString())
                                    .param("createdTo", CREATED_TO.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.page").value(1))
                    .andExpect(jsonPath("$.data.size").value(10));

            verify(mapper).toQuery(any());
            verify(searchUsersUseCase).execute(any(SearchUsersQuery.class));
        }

        @Test
        @DisplayName("[성공] 모든 필터와 페이징 파라미터로 조회 시 200 OK 반환")
        void searchUsers_withAllParams_returns200Ok() throws Exception {
            // Given
            UUID tenantId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            String identifier = "test@example.com";
            PageResponse<UserResponse> emptyPage =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchUsersQuery.of(
                                    tenantId,
                                    organizationId,
                                    identifier,
                                    "ACTIVE",
                                    CREATED_FROM,
                                    CREATED_TO,
                                    0,
                                    20));
            given(searchUsersUseCase.execute(any(SearchUsersQuery.class))).willReturn(emptyPage);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/users")
                                    .param("tenantId", tenantId.toString())
                                    .param("organizationId", organizationId.toString())
                                    .param("identifier", identifier)
                                    .param("status", "ACTIVE")
                                    .param("page", "0")
                                    .param("size", "20")
                                    .param("createdFrom", CREATED_FROM.toString())
                                    .param("createdTo", CREATED_TO.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray());

            verify(mapper).toQuery(any());
            verify(searchUsersUseCase).execute(any(SearchUsersQuery.class));
        }

        @Test
        @DisplayName("[성공] 빈 결과 반환 시 200 OK 반환")
        void searchUsers_withNoResults_returns200Ok() throws Exception {
            // Given
            PageResponse<UserResponse> emptyPage =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);

            given(mapper.toQuery(any()))
                    .willReturn(
                            SearchUsersQuery.of(
                                    null, null, null, null, CREATED_FROM, CREATED_TO, 0, 20));
            given(searchUsersUseCase.execute(any(SearchUsersQuery.class))).willReturn(emptyPage);

            // When & Then
            mockMvc.perform(
                            get("/api/v1/auth/users")
                                    .param("createdFrom", CREATED_FROM.toString())
                                    .param("createdTo", CREATED_TO.toString())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));

            verify(mapper).toQuery(any());
            verify(searchUsersUseCase).execute(any(SearchUsersQuery.class));
        }
    }
}
