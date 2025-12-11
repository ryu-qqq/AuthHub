package com.ryuqq.authhub.adapter.in.rest.tenant.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * UpdateTenantNameApiRequest - 테넌트 이름 변경 요청 DTO
 *
 * <p>테넌트 이름 변경 API의 요청 본문을 표현합니다.
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
@Schema(description = "테넌트 이름 변경 요청")
public record UpdateTenantNameApiRequest(
        @Schema(
                        description = "변경할 테넌트 이름",
                        requiredMode = Schema.RequiredMode.REQUIRED,
                        minLength = 2,
                        maxLength = 100)
                @NotBlank(message = "테넌트 이름은 필수입니다")
                @Size(min = 2, max = 100, message = "테넌트 이름은 2자 이상 100자 이하여야 합니다")
                String name) {}
