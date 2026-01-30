package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.internal.fixture.InternalApiFixture;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalTenantConfigApiMapper;
import com.ryuqq.authhub.application.tenant.dto.response.TenantConfigResult;
import com.ryuqq.authhub.application.tenant.port.in.query.GetTenantConfigUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.JsonFieldType;

/**
 * InternalTenantConfigController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(InternalTenantConfigController.class)
@Import({ControllerTestSecurityConfig.class, InternalTenantConfigApiMapper.class})
@DisplayName("InternalTenantConfigController 테스트")
class InternalTenantConfigControllerTest extends RestDocsTestSupport {

    @MockBean private GetTenantConfigUseCase getTenantConfigUseCase;

    @Nested
    @DisplayName("GET /api/v1/internal/tenants/{tenantId}/config - 테넌트 설정 조회")
    class GetConfigTests {

        @Test
        @DisplayName("유효한 요청으로 활성 테넌트 설정을 조회한다")
        void shouldGetActiveConfigSuccessfully() throws Exception {
            // given
            String tenantId = InternalApiFixture.defaultTenantId();
            TenantConfigResult result =
                    new TenantConfigResult(
                            tenantId,
                            InternalApiFixture.defaultTenantName(),
                            InternalApiFixture.defaultStatus(),
                            true);
            given(getTenantConfigUseCase.getByTenantId(tenantId)).willReturn(result);

            // when & then
            mockMvc.perform(get(InternalApiEndpoints.TENANTS + "/{tenantId}/config", tenantId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.tenantId").value(tenantId))
                    .andExpect(
                            jsonPath("$.data.name").value(InternalApiFixture.defaultTenantName()))
                    .andExpect(jsonPath("$.data.status").value(InternalApiFixture.defaultStatus()))
                    .andExpect(jsonPath("$.data.active").value(true))
                    .andDo(
                            document(
                                    "internal/tenant-config/get",
                                    pathParameters(
                                            parameterWithName("tenantId")
                                                    .description("테넌트 ID (UUID)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.tenantId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 ID"),
                                            fieldWithPath("data.name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 이름"),
                                            fieldWithPath("data.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 상태 (ACTIVE, INACTIVE)"),
                                            fieldWithPath("data.active")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("활성 여부"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("비활성 테넌트 설정을 조회한다")
        void shouldGetInactiveConfigSuccessfully() throws Exception {
            // given
            String tenantId = InternalApiFixture.defaultTenantId();
            TenantConfigResult result =
                    new TenantConfigResult(
                            tenantId, InternalApiFixture.defaultTenantName(), "INACTIVE", false);
            given(getTenantConfigUseCase.getByTenantId(tenantId)).willReturn(result);

            // when & then
            mockMvc.perform(get(InternalApiEndpoints.TENANTS + "/{tenantId}/config", tenantId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.tenantId").value(tenantId))
                    .andExpect(jsonPath("$.data.status").value("INACTIVE"))
                    .andExpect(jsonPath("$.data.active").value(false));
        }
    }
}
