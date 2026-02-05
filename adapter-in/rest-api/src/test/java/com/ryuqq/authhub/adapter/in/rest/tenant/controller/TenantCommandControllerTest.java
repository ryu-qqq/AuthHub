package com.ryuqq.authhub.adapter.in.rest.tenant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.tenant.TenantApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.tenant.controller.command.TenantCommandController;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.UpdateTenantNameApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.UpdateTenantStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.fixture.TenantApiFixture;
import com.ryuqq.authhub.adapter.in.rest.tenant.mapper.TenantCommandApiMapper;
import com.ryuqq.authhub.application.tenant.port.in.command.CreateTenantUseCase;
import com.ryuqq.authhub.application.tenant.port.in.command.UpdateTenantNameUseCase;
import com.ryuqq.authhub.application.tenant.port.in.command.UpdateTenantStatusUseCase;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
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
 * TenantCommandController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(TenantCommandController.class)
@Import({ControllerTestSecurityConfig.class, TenantCommandApiMapper.class})
@DisplayName("TenantCommandController 테스트")
class TenantCommandControllerTest extends RestDocsTestSupport {

    @MockBean private CreateTenantUseCase createTenantUseCase;

    @MockBean private UpdateTenantNameUseCase updateTenantNameUseCase;

    @MockBean private UpdateTenantStatusUseCase updateTenantStatusUseCase;

    @Nested
    @DisplayName("POST /api/v1/auth/tenants - 테넌트 생성")
    class CreateTests {

        @Test
        @DisplayName("유효한 요청으로 테넌트를 생성한다")
        void shouldCreateTenantSuccessfully() throws Exception {
            // given
            CreateTenantApiRequest request = TenantApiFixture.createTenantRequest();
            String tenantId = TenantApiFixture.defaultTenantIdString();
            given(createTenantUseCase.execute(any())).willReturn(tenantId);

            // when & then
            mockMvc.perform(
                            post(TenantApiEndpoints.TENANTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.tenantId").value(tenantId))
                    .andDo(
                            document(
                                    "tenant/create",
                                    requestFields(
                                            fieldWithPath("name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("테넌트 이름 (필수, 2~100자)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.tenantId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성된 Tenant ID (UUID)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("name이 빈 문자열이면 400 Bad Request")
        void shouldFailWhenNameIsBlank() throws Exception {
            // given
            CreateTenantApiRequest request = TenantApiFixture.createTenantRequest("");

            // when & then
            mockMvc.perform(
                            post(TenantApiEndpoints.TENANTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("name이 1자이면 400 Bad Request")
        void shouldFailWhenNameIsTooShort() throws Exception {
            // given
            CreateTenantApiRequest request = TenantApiFixture.createTenantRequest("A");

            // when & then
            mockMvc.perform(
                            post(TenantApiEndpoints.TENANTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("중복된 테넌트 이름이면 409 Conflict")
        void shouldFailWhenTenantNameIsDuplicate() throws Exception {
            // given
            CreateTenantApiRequest request = TenantApiFixture.createTenantRequest();
            willThrow(new DuplicateTenantNameException(TenantApiFixture.defaultTenantName()))
                    .given(createTenantUseCase)
                    .execute(any());

            // when & then
            mockMvc.perform(
                            post(TenantApiEndpoints.TENANTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(409));
        }

        @Test
        @DisplayName("name이 최대 길이(100자)이면 성공한다")
        void shouldSucceedWhenNameIsMaxLength() throws Exception {
            // given
            String maxLengthName = "A".repeat(100);
            CreateTenantApiRequest request = TenantApiFixture.createTenantRequest(maxLengthName);
            String tenantId = TenantApiFixture.defaultTenantIdString();
            given(createTenantUseCase.execute(any())).willReturn(tenantId);

            // when & then
            mockMvc.perform(
                            post(TenantApiEndpoints.TENANTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.tenantId").value(tenantId));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/auth/tenants/{tenantId}/name - 테넌트 이름 수정")
    class UpdateNameTests {

        @Test
        @DisplayName("유효한 요청으로 테넌트 이름을 수정한다")
        void shouldUpdateTenantNameSuccessfully() throws Exception {
            // given
            UUID tenantId = TenantApiFixture.defaultTenantId();
            UpdateTenantNameApiRequest request = TenantApiFixture.updateTenantNameRequest();
            doNothing().when(updateTenantNameUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            put(TenantApiEndpoints.TENANTS + "/{tenantId}/name", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.tenantId").value(tenantId.toString()))
                    .andDo(
                            document(
                                    "tenant/update-name",
                                    pathParameters(
                                            parameterWithName("tenantId")
                                                    .description("수정할 Tenant ID (UUID)")),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .type(JsonFieldType.STRING)
                                                    .description("변경할 테넌트 이름 (필수, 2~100자)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.tenantId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정된 Tenant ID (UUID)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("name이 빈 문자열이면 400 Bad Request")
        void shouldFailWhenNameIsBlank() throws Exception {
            // given
            UUID tenantId = TenantApiFixture.defaultTenantId();
            UpdateTenantNameApiRequest request = TenantApiFixture.updateTenantNameRequest("");

            // when & then
            mockMvc.perform(
                            put(TenantApiEndpoints.TENANTS + "/{tenantId}/name", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("테넌트를 찾을 수 없으면 404 Not Found")
        void shouldFailWhenTenantNotFound() throws Exception {
            // given
            UUID tenantId = TenantApiFixture.defaultTenantId();
            UpdateTenantNameApiRequest request = TenantApiFixture.updateTenantNameRequest();
            willThrow(new TenantNotFoundException(tenantId.toString()))
                    .given(updateTenantNameUseCase)
                    .execute(any());

            // when & then
            mockMvc.perform(
                            put(TenantApiEndpoints.TENANTS + "/{tenantId}/name", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/auth/tenants/{tenantId}/status - 테넌트 상태 수정")
    class UpdateStatusTests {

        @Test
        @DisplayName("유효한 요청으로 테넌트 상태를 수정한다")
        void shouldUpdateTenantStatusSuccessfully() throws Exception {
            // given
            UUID tenantId = TenantApiFixture.defaultTenantId();
            UpdateTenantStatusApiRequest request = TenantApiFixture.updateTenantStatusRequest();
            doNothing().when(updateTenantStatusUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            patch(TenantApiEndpoints.TENANTS + "/{tenantId}/status", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.tenantId").value(tenantId.toString()))
                    .andDo(
                            document(
                                    "tenant/update-status",
                                    pathParameters(
                                            parameterWithName("tenantId")
                                                    .description("수정할 Tenant ID (UUID)")),
                                    requestFields(
                                            fieldWithPath("status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("변경할 상태 (필수, ACTIVE|INACTIVE)")),
                                    responseFields(
                                            fieldWithPath("success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("요청 성공 여부"),
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.tenantId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정된 Tenant ID (UUID)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("status가 유효하지 않으면 400 Bad Request")
        void shouldFailWhenStatusIsInvalid() throws Exception {
            // given
            UUID tenantId = TenantApiFixture.defaultTenantId();
            UpdateTenantStatusApiRequest request =
                    TenantApiFixture.updateTenantStatusRequest("INVALID");

            // when & then
            mockMvc.perform(
                            patch(TenantApiEndpoints.TENANTS + "/{tenantId}/status", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("status가 빈 문자열이면 400 Bad Request")
        void shouldFailWhenStatusIsBlank() throws Exception {
            // given
            UUID tenantId = TenantApiFixture.defaultTenantId();
            UpdateTenantStatusApiRequest request = TenantApiFixture.updateTenantStatusRequest("");

            // when & then
            mockMvc.perform(
                            patch(TenantApiEndpoints.TENANTS + "/{tenantId}/status", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("테넌트를 찾을 수 없으면 404 Not Found")
        void shouldFailWhenTenantNotFound() throws Exception {
            // given
            UUID tenantId = TenantApiFixture.defaultTenantId();
            UpdateTenantStatusApiRequest request = TenantApiFixture.updateTenantStatusRequest();
            willThrow(new TenantNotFoundException(tenantId.toString()))
                    .given(updateTenantStatusUseCase)
                    .execute(any());

            // when & then
            mockMvc.perform(
                            patch(TenantApiEndpoints.TENANTS + "/{tenantId}/status", tenantId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type").exists())
                    .andExpect(jsonPath("$.title").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}
