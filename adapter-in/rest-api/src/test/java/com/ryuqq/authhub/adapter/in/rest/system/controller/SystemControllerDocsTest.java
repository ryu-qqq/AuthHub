package com.ryuqq.authhub.adapter.in.rest.system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.system.dto.command.TenantOnboardingApiRequest;
import com.ryuqq.authhub.adapter.in.rest.system.dto.response.TenantOnboardingApiResponse;
import com.ryuqq.authhub.adapter.in.rest.system.mapper.SystemApiMapper;
import com.ryuqq.authhub.application.onboarding.dto.command.TenantOnboardingCommand;
import com.ryuqq.authhub.application.onboarding.dto.response.TenantOnboardingResponse;
import com.ryuqq.authhub.application.onboarding.port.in.TenantOnboardingUseCase;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

/**
 * SystemTenantController REST API 문서화 테스트
 *
 * <p>Spring REST Docs를 사용하여 System API의 문서를 생성합니다.
 *
 * <p>생성되는 문서:
 *
 * <ul>
 *   <li>system-tenant-onboarding: POST /api/v1/auth/system/tenants/onboarding - 테넌트 온보딩
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(SystemTenantController.class)
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("SystemTenantController REST Docs")
@Tag("docs")
class SystemControllerDocsTest extends RestDocsTestSupport {

    @MockBean private TenantOnboardingUseCase tenantOnboardingUseCase;

    @MockBean private SystemApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Test
    @DisplayName("POST /api/v1/auth/system/tenants/onboarding - 테넌트 온보딩 API 문서")
    void tenantOnboarding() throws Exception {
        // Given
        TenantOnboardingApiRequest request =
                new TenantOnboardingApiRequest(
                        "커넥틀리", "본사", "admin@connectly.com", "010-1234-5678");

        UUID tenantId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String temporaryPassword = "Abc123!@#xyz";

        TenantOnboardingResponse useCaseResponse =
                new TenantOnboardingResponse(tenantId, organizationId, userId, temporaryPassword);
        TenantOnboardingApiResponse apiResponse =
                new TenantOnboardingApiResponse(
                        tenantId.toString(),
                        organizationId.toString(),
                        userId.toString(),
                        temporaryPassword);

        given(mapper.toCommand(any(TenantOnboardingApiRequest.class)))
                .willReturn(
                        new TenantOnboardingCommand(
                                "커넥틀리", "본사", "admin@connectly.com", "010-1234-5678"));
        given(tenantOnboardingUseCase.execute(any(TenantOnboardingCommand.class)))
                .willReturn(useCaseResponse);
        given(mapper.toApiResponse(any(TenantOnboardingResponse.class))).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(
                        post("/api/v1/auth/system/tenants/onboarding")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "system-tenant-onboarding",
                                requestFields(
                                        fieldWithPath("tenantName")
                                                .description("테넌트(회사) 이름 (2-100자)"),
                                        fieldWithPath("organizationName")
                                                .description("기본 조직 이름 (2-100자)"),
                                        fieldWithPath("masterEmail").description("마스터 관리자 이메일"),
                                        fieldWithPath("masterPhoneNumber")
                                                .description("마스터 관리자 핸드폰 번호 (10-20자)")),
                                responseFields(
                                        fieldWithPath("success").description("요청 성공 여부"),
                                        fieldWithPath("data").description("응답 데이터"),
                                        fieldWithPath("data.tenantId")
                                                .description("생성된 테넌트 ID (UUID)"),
                                        fieldWithPath("data.organizationId")
                                                .description("생성된 조직 ID (UUID)"),
                                        fieldWithPath("data.userId")
                                                .description("생성된 마스터 사용자 ID (UUID)"),
                                        fieldWithPath("data.temporaryPassword")
                                                .description("임시 비밀번호 (호출자가 이메일 발송 필요)"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }
}
