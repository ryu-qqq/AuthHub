package com.ryuqq.authhub.adapter.in.rest.role.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * CreateRoleApiRequest - 역할 생성 요청 DTO
 *
 * <p>역할 등록 API의 요청 본문을 표현합니다.
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
@Schema(description = "역할 생성 요청")
public record CreateRoleApiRequest(
        @Schema(description = "테넌트 ID (선택, null인 경우 시스템 역할)") String tenantId,
        @Schema(
                        description = "역할 이름",
                        requiredMode = Schema.RequiredMode.REQUIRED,
                        minLength = 1,
                        maxLength = 100)
                @NotBlank(message = "역할 이름은 필수입니다")
                @Size(min = 1, max = 100, message = "역할 이름은 1자 이상 100자 이하여야 합니다")
                String name,
        @Schema(description = "역할 설명", maxLength = 500)
                @Size(max = 500, message = "설명은 500자 이하여야 합니다")
                String description,
        @Schema(
                        description = "역할 범위",
                        allowableValues = {"TENANT", "ORGANIZATION", "SYSTEM"})
                String scope,
        @Schema(description = "시스템 역할 여부") Boolean isSystem) {}
