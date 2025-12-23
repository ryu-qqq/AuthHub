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
import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.RegisterPermissionUsageApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.ValidatePermissionsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalApiMapper;
import com.ryuqq.authhub.application.permission.dto.response.PermissionUsageResponse;
import com.ryuqq.authhub.application.permission.dto.response.ValidatePermissionsResult;
import com.ryuqq.authhub.application.permission.port.in.command.RegisterPermissionUsageUseCase;
import com.ryuqq.authhub.application.permission.port.in.command.ValidatePermissionsUseCase;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
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

    private static final String USAGES_URL_TEMPLATE =
            ApiPaths.Internal.BASE
                    + ApiPaths.Internal.PERMISSIONS
                    + "/%s"
                    + ApiPaths.Internal.USAGES;

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

    @Nested
    @DisplayName("POST /{permissionKey}/usages 엔드포인트는")
    class RegisterUsageEndpoint {

        @Test
        @DisplayName("ROLE_SERVICE 권한으로 사용 이력 등록에 성공하면 200을 반환한다")
        @WithMockUser(roles = "SERVICE")
        void shouldReturnOkWhenUsageRegisteredSuccessfully() throws Exception {
            // given
            String permissionKey = "product:read";
            RegisterPermissionUsageApiRequest request =
                    new RegisterPermissionUsageApiRequest(
                            "product-service",
                            List.of("ProductController.java:45", "OrderService.java:123"));

            UUID usageId = UUID.randomUUID();
            Instant now = Instant.now();
            PermissionUsageResponse result =
                    new PermissionUsageResponse(
                            usageId,
                            permissionKey,
                            "product-service",
                            List.of("ProductController.java:45", "OrderService.java:123"),
                            now,
                            now);

            when(registerPermissionUsageUseCase.execute(any())).thenReturn(result);

            String url = String.format(USAGES_URL_TEMPLATE, permissionKey);

            // when & then
            mockMvc.perform(
                            post(url)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.usageId").value(usageId.toString()))
                    .andExpect(jsonPath("$.data.permissionKey").value(permissionKey))
                    .andExpect(jsonPath("$.data.serviceName").value("product-service"))
                    .andExpect(jsonPath("$.data.locations").isArray())
                    .andExpect(jsonPath("$.data.locations[0]").value("ProductController.java:45"));
        }

        @Test
        @DisplayName("ROLE_SERVICE 권한으로 locations 없이도 등록에 성공한다")
        @WithMockUser(roles = "SERVICE")
        void shouldReturnOkWhenLocationsEmpty() throws Exception {
            // given
            String permissionKey = "product:write";
            RegisterPermissionUsageApiRequest request =
                    new RegisterPermissionUsageApiRequest("product-service", List.of());

            UUID usageId = UUID.randomUUID();
            Instant now = Instant.now();
            PermissionUsageResponse result =
                    new PermissionUsageResponse(
                            usageId, permissionKey, "product-service", List.of(), now, now);

            when(registerPermissionUsageUseCase.execute(any())).thenReturn(result);

            String url = String.format(USAGES_URL_TEMPLATE, permissionKey);

            // when & then
            mockMvc.perform(
                            post(url)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.usageId").value(usageId.toString()))
                    .andExpect(jsonPath("$.data.locations").isEmpty());
        }

        @Test
        @DisplayName("serviceName이 없으면 400을 반환한다")
        @WithMockUser(roles = "SERVICE")
        void shouldReturn400WhenServiceNameMissing() throws Exception {
            // given - serviceName 누락
            String permissionKey = "product:read";
            String requestBody =
                    """
                    {
                        "locations": ["ProductController.java:45"]
                    }
                    """;

            String url = String.format(USAGES_URL_TEMPLATE, permissionKey);

            // when & then
            mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("serviceName 형식이 잘못되면 400을 반환한다")
        @WithMockUser(roles = "SERVICE")
        void shouldReturn400WhenServiceNameFormatInvalid() throws Exception {
            // given - 대문자 포함 (소문자만 허용)
            String permissionKey = "product:read";
            String requestBody =
                    """
                    {
                        "serviceName": "ProductService",
                        "locations": []
                    }
                    """;

            String url = String.format(USAGES_URL_TEMPLATE, permissionKey);

            // when & then
            mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}
