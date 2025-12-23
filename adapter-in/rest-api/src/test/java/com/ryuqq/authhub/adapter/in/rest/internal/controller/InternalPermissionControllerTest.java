package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.ValidatePermissionsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalApiMapper;
import com.ryuqq.authhub.application.permission.dto.response.ValidatePermissionsResult;
import com.ryuqq.authhub.application.permission.port.in.command.RegisterPermissionUsageUseCase;
import com.ryuqq.authhub.application.permission.port.in.command.ValidatePermissionsUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InternalPermissionController.class)
@AutoConfigureMockMvc
@Import({InternalApiMapper.class, ControllerTestSecurityConfig.class})
@DisplayName("InternalPermissionController 단위 테스트")
class InternalPermissionControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private ValidatePermissionsUseCase validatePermissionsUseCase;

    @MockBean private RegisterPermissionUsageUseCase registerPermissionUsageUseCase;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    private static final String VALIDATE_URL =
            ApiPaths.Internal.BASE + ApiPaths.Internal.PERMISSIONS + ApiPaths.Internal.VALIDATE;

    @Nested
    @DisplayName("POST /validate 엔드포인트는")
    class ValidateEndpoint {

        @Test
        @DisplayName("ROLE_SERVICE 권한으로 모든 권한이 존재하면 valid=true를 반환한다")
        @WithMockUser(roles = "SERVICE")
        void shouldReturnValidTrueWhenAllPermissionsExist() throws Exception {
            // given
            ValidatePermissionsApiRequest request =
                    new ValidatePermissionsApiRequest(
                            "product-service",
                            List.of(
                                    new ValidatePermissionsApiRequest.PermissionEntryApiRequest(
                                            "product:read", List.of("ProductController.java:45")),
                                    new ValidatePermissionsApiRequest.PermissionEntryApiRequest(
                                            "product:write",
                                            List.of("ProductController.java:67"))));

            ValidatePermissionsResult result =
                    ValidatePermissionsResult.allValid(
                            "product-service", List.of("product:read", "product:write"));

            when(validatePermissionsUseCase.execute(any())).thenReturn(result);

            // when & then
            mockMvc.perform(
                            post(VALIDATE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.valid").value(true))
                    .andExpect(jsonPath("$.data.serviceName").value("product-service"))
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.existingCount").value(2))
                    .andExpect(jsonPath("$.data.missingCount").value(0))
                    .andExpect(jsonPath("$.data.missing").isEmpty());
        }

        @Test
        @DisplayName("ROLE_SERVICE 권한으로 누락된 권한이 있으면 valid=false를 반환한다")
        @WithMockUser(roles = "SERVICE")
        void shouldReturnValidFalseWhenSomePermissionsMissing() throws Exception {
            // given
            ValidatePermissionsApiRequest request =
                    new ValidatePermissionsApiRequest(
                            "product-service",
                            List.of(
                                    new ValidatePermissionsApiRequest.PermissionEntryApiRequest(
                                            "product:read", List.of()),
                                    new ValidatePermissionsApiRequest.PermissionEntryApiRequest(
                                            "product:admin", List.of())));

            ValidatePermissionsResult result =
                    ValidatePermissionsResult.withMissing(
                            "product-service", List.of("product:read"), List.of("product:admin"));

            when(validatePermissionsUseCase.execute(any())).thenReturn(result);

            // when & then
            mockMvc.perform(
                            post(VALIDATE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.valid").value(false))
                    .andExpect(jsonPath("$.data.missingCount").value(1))
                    .andExpect(jsonPath("$.data.missing[0]").value("product:admin"))
                    .andExpect(jsonPath("$.data.message").exists());
        }

        // Note: 403 권한 검증 테스트는 통합 테스트에서 수행
        // ControllerTestSecurityConfig가 모든 요청을 허용하므로
        // 메서드 레벨 보안(@PreAuthorize)은 별도 통합 테스트에서 검증

        @Test
        @DisplayName("serviceName이 없으면 400을 반환한다")
        @WithMockUser(roles = "SERVICE")
        void shouldReturn400WhenServiceNameMissing() throws Exception {
            // given - serviceName 누락
            String requestBody =
                    """
                    {
                        "permissions": [
                            {"key": "product:read", "locations": []}
                        ]
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post(VALIDATE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("permissions가 비어있으면 400을 반환한다")
        @WithMockUser(roles = "SERVICE")
        void shouldReturn400WhenPermissionsEmpty() throws Exception {
            // given - permissions 빈 배열
            String requestBody =
                    """
                    {
                        "serviceName": "product-service",
                        "permissions": []
                    }
                    """;

            // when & then
            mockMvc.perform(
                            post(VALIDATE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}
