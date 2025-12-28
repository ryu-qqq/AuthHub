package com.ryuqq.authhub.adapter.in.rest.system.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * TenantOnboardingApiRequest - 테넌트 온보딩 API 요청 DTO
 *
 * <p>입점 승인 시 Tenant, Organization, User를 일괄 생성하기 위한 API 요청입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiRequest 네이밍 규칙
 *   <li>Bean Validation 어노테이션 사용
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 *   <li>Domain 변환 메서드 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "테넌트 온보딩 요청")
public record TenantOnboardingApiRequest(
        @Schema(
                        description = "테넌트(회사) 이름",
                        example = "커넥틀리",
                        requiredMode = Schema.RequiredMode.REQUIRED,
                        minLength = 2,
                        maxLength = 100)
                @NotBlank(message = "테넌트 이름은 필수입니다")
                @Size(min = 2, max = 100, message = "테넌트 이름은 2자 이상 100자 이하여야 합니다")
                String tenantName,
        @Schema(
                        description = "기본 조직 이름",
                        example = "본사",
                        requiredMode = Schema.RequiredMode.REQUIRED,
                        minLength = 2,
                        maxLength = 100)
                @NotBlank(message = "조직 이름은 필수입니다")
                @Size(min = 2, max = 100, message = "조직 이름은 2자 이상 100자 이하여야 합니다")
                String organizationName,
        @Schema(
                        description = "마스터 관리자 이메일",
                        example = "admin@connectly.com",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "마스터 이메일은 필수입니다")
                @Email(message = "올바른 이메일 형식이 아닙니다")
                String masterEmail) {}
