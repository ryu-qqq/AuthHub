package com.ryuqq.authhub.adapter.in.rest.role.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import com.ryuqq.authhub.application.role.port.in.GetUserRolesUseCase;

/**
 * RoleQueryController 단위 테스트
 *
 * <p>검증 범위:
 * <ul>
 *   <li>HTTP 요청/응답 매핑</li>
 *   <li>Response DTO 직렬화</li>
 *   <li>HTTP Status Code</li>
 *   <li>UseCase 호출 검증</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(RoleQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
@DisplayName("RoleQueryController 테스트")
class RoleQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetUserRolesUseCase getUserRolesUseCase;

    @MockBean
    private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/users/{userId}/roles 테스트")
    class GetUserRolesTest {

        @Test
        @DisplayName("사용자 권한 조회 성공 (200 OK)")
        void givenValidUserId_whenGetUserRoles_thenReturns200() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            Set<String> roles = Set.of("ROLE_USER", "ROLE_ADMIN");
            Set<String> permissions = Set.of("user:read", "user:write");

            UserRolesResponse useCaseResponse = new UserRolesResponse(userId, roles, permissions);

            given(getUserRolesUseCase.execute(any(UUID.class)))
                    .willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(get("/api/v1/users/{userId}/roles", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                    .andExpect(jsonPath("$.data.roles").isArray())
                    .andExpect(jsonPath("$.data.permissions").isArray());

            verify(getUserRolesUseCase).execute(userId);
        }

        @Test
        @DisplayName("빈 권한 목록 조회 성공 (200 OK)")
        void givenUserWithNoRoles_whenGetUserRoles_thenReturnsEmptyArrays() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            Set<String> emptyRoles = Set.of();
            Set<String> emptyPermissions = Set.of();

            UserRolesResponse useCaseResponse = new UserRolesResponse(userId, emptyRoles, emptyPermissions);

            given(getUserRolesUseCase.execute(any(UUID.class)))
                    .willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(get("/api/v1/users/{userId}/roles", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                    .andExpect(jsonPath("$.data.roles").isEmpty())
                    .andExpect(jsonPath("$.data.permissions").isEmpty());

            verify(getUserRolesUseCase).execute(userId);
        }

        @Test
        @DisplayName("잘못된 UUID 형식 (400 Bad Request)")
        void givenInvalidUuid_whenGetUserRoles_thenReturns400() throws Exception {
            // when & then
            mockMvc.perform(get("/api/v1/users/{userId}/roles", "invalid-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }
}
