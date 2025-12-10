package com.ryuqq.authhub.adapter.in.rest.organization.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * UpdateOrganizationStatusApiRequest - 조직 상태 변경 요청 DTO
 *
 * <p>조직 상태 변경 API의 요청 본문을 표현합니다.
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
@Schema(description = "조직 상태 변경 요청")
public record UpdateOrganizationStatusApiRequest(
        @Schema(
                        description = "변경할 상태",
                        requiredMode = Schema.RequiredMode.REQUIRED,
                        allowableValues = {"ACTIVE", "INACTIVE"})
                @NotBlank(message = "상태는 필수입니다")
                @Pattern(regexp = "ACTIVE|INACTIVE", message = "상태는 ACTIVE 또는 INACTIVE만 가능합니다")
                String status) {}
