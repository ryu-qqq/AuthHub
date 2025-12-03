package com.ryuqq.authhub.adapter.in.rest.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
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
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.port.in.GetUserUseCase;

/**
 * UserQueryController 단위 테스트
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
@WebMvcTest(UserQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("unit")
@Tag("adapter-rest")
@Tag("controller")
@DisplayName("UserQueryController 테스트")
class UserQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetUserUseCase getUserUseCase;

    @MockBean
    private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/users/{userId} 테스트")
    class GetUserTest {

        @Test
        @DisplayName("사용자 조회 성공 (200 OK)")
        void givenValidUserId_whenGetUser_thenReturns200() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            Instant now = Instant.now();

            UserResponse useCaseResponse = new UserResponse(
                    userId, 1L, 10L, "PUBLIC", "ACTIVE",
                    "홍길동", "010-1234-5678", now, now);

            given(getUserUseCase.execute(any(UUID.class)))
                    .willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(get("/api/v1/users/{userId}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                    .andExpect(jsonPath("$.data.tenantId").value(1))
                    .andExpect(jsonPath("$.data.organizationId").value(10))
                    .andExpect(jsonPath("$.data.userType").value("PUBLIC"))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                    .andExpect(jsonPath("$.data.name").value("홍길동"))
                    .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"));

            verify(getUserUseCase).execute(userId);
        }

        @Test
        @DisplayName("잘못된 UUID 형식 (400 Bad Request)")
        void givenInvalidUuid_whenGetUser_thenReturns400() throws Exception {
            // when & then
            mockMvc.perform(get("/api/v1/users/{userId}", "invalid-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }
}
