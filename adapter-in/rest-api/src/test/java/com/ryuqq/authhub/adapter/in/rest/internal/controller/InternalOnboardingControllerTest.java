package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.OnboardingApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.fixture.InternalApiFixture;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalOnboardingApiMapper;
import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import com.ryuqq.authhub.application.tenant.port.in.command.OnboardingUseCase;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

/**
 * InternalOnboardingController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(InternalOnboardingController.class)
@Import({ControllerTestSecurityConfig.class, InternalOnboardingApiMapper.class})
@DisplayName("InternalOnboardingController 테스트")
class InternalOnboardingControllerTest extends RestDocsTestSupport {

    private static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";

    @MockBean private OnboardingUseCase onboardingUseCase;

    @Nested
    @DisplayName("POST /api/v1/internal/onboarding - 온보딩")
    class OnboardingTests {

        @Test
        @DisplayName("유효한 요청으로 테넌트와 조직을 한 번에 생성한다")
        void shouldOnboardSuccessfully() throws Exception {
            // given
            OnboardingApiRequest request = InternalApiFixture.onboardingRequest();
            OnboardingResult result =
                    new OnboardingResult(
                            "01933abc-1234-7000-8000-000000000001",
                            "01933abc-1234-7000-8000-000000000002");
            given(onboardingUseCase.execute(any())).willReturn(result);
            String idempotencyKey = UUID.randomUUID().toString();

            // when & then
            mockMvc.perform(
                            post(InternalApiEndpoints.ONBOARDING)
                                    .header(IDEMPOTENCY_KEY_HEADER, idempotencyKey)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(
                            jsonPath("$.data.tenantId")
                                    .value("01933abc-1234-7000-8000-000000000001"))
                    .andExpect(
                            jsonPath("$.data.organizationId")
                                    .value("01933abc-1234-7000-8000-000000000002"))
                    .andDo(
                            document(
                                    "internal/onboarding/onboard",
                                    requestHeaders(
                                            headerWithName(IDEMPOTENCY_KEY_HEADER)
                                                    .description("멱등키 (필수, UUID 권장, 24시간 유효)")),
                                    requestFields(
                                            fieldWithPath("tenantName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 이름 (필수)"),
                                            fieldWithPath("organizationName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("조직 이름 (필수)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.tenantId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성된 테넌트 ID (UUIDv7)"),
                                            fieldWithPath("data.organizationId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성된 조직 ID (UUIDv7)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("X-Idempotency-Key 헤더가 없으면 400 Bad Request")
        void shouldFailWhenIdempotencyKeyIsMissing() throws Exception {
            // given
            OnboardingApiRequest request = InternalApiFixture.onboardingRequest();

            // when & then
            mockMvc.perform(
                            post(InternalApiEndpoints.ONBOARDING)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("tenantName이 빈 문자열이면 400 Bad Request")
        void shouldFailWhenTenantNameIsBlank() throws Exception {
            // given
            OnboardingApiRequest request =
                    InternalApiFixture.onboardingRequest("", InternalApiFixture.defaultOrgName());
            String idempotencyKey = UUID.randomUUID().toString();

            // when & then
            mockMvc.perform(
                            post(InternalApiEndpoints.ONBOARDING)
                                    .header(IDEMPOTENCY_KEY_HEADER, idempotencyKey)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("organizationName이 빈 문자열이면 400 Bad Request")
        void shouldFailWhenOrganizationNameIsBlank() throws Exception {
            // given
            OnboardingApiRequest request =
                    InternalApiFixture.onboardingRequest(
                            InternalApiFixture.defaultTenantName(), "");
            String idempotencyKey = UUID.randomUUID().toString();

            // when & then
            mockMvc.perform(
                            post(InternalApiEndpoints.ONBOARDING)
                                    .header(IDEMPOTENCY_KEY_HEADER, idempotencyKey)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("tenantName이 null이면 400 Bad Request")
        void shouldFailWhenTenantNameIsNull() throws Exception {
            // given - JSON에서 tenantName 필드 누락
            String requestBody = "{\"organizationName\":\"default-org\"}";
            String idempotencyKey = UUID.randomUUID().toString();

            // when & then
            mockMvc.perform(
                            post(InternalApiEndpoints.ONBOARDING)
                                    .header(IDEMPOTENCY_KEY_HEADER, idempotencyKey)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("organizationName이 null이면 400 Bad Request")
        void shouldFailWhenOrganizationNameIsNull() throws Exception {
            // given - JSON에서 organizationName 필드 누락
            String requestBody = "{\"tenantName\":\"my-tenant\"}";
            String idempotencyKey = UUID.randomUUID().toString();

            // when & then
            mockMvc.perform(
                            post(InternalApiEndpoints.ONBOARDING)
                                    .header(IDEMPOTENCY_KEY_HEADER, idempotencyKey)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andExpect(status().isBadRequest());
        }
    }
}
