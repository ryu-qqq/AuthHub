package com.ryuqq.authhub.adapter.in.rest.system.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * TenantOnboardingApiRequest - 테넌트 온보딩 API 요청 DTO
 *
 * <p>입점 승인 시 Tenant + Organization + User 일괄 생성을 위한 요청입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>public record 필수
 *   <li>Lombok 금지
 *   <li>Validation 어노테이션 필수 (@Valid와 함께 사용)
 *   <li>비즈니스 로직 금지
 * </ul>
 *
 * @param tenantName 테넌트(회사) 이름
 * @param masterEmail 마스터 관리자 이메일
 * @param masterName 마스터 관리자 이름
 * @param defaultOrgName 기본 조직 이름
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "테넌트 온보딩 요청")
public record TenantOnboardingApiRequest(
        @Schema(
                        description = "테넌트(회사) 이름",
                        example = "Acme Corporation",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "테넌트 이름은 필수입니다")
                @Size(min = 2, max = 100, message = "테넌트 이름은 2자 이상 100자 이하여야 합니다")
                String tenantName,
        @Schema(
                        description = "마스터 관리자 이메일",
                        example = "admin@acme.com",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "마스터 이메일은 필수입니다")
                @Email(message = "유효한 이메일 형식이어야 합니다")
                String masterEmail,
        @Schema(
                        description = "마스터 관리자 이름",
                        example = "홍길동",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "마스터 이름은 필수입니다")
                @Size(min = 1, max = 50, message = "마스터 이름은 1자 이상 50자 이하여야 합니다")
                String masterName,
        @Schema(
                        description = "기본 조직 이름",
                        example = "기본 조직",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "기본 조직 이름은 필수입니다")
                @Size(min = 2, max = 100, message = "기본 조직 이름은 2자 이상 100자 이하여야 합니다")
                String defaultOrgName) {}
