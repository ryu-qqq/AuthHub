package com.ryuqq.authhub.adapter.in.rest.permission.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * CreatePermissionApiRequest - 권한 생성 요청 DTO
 *
 * <p>권한 등록 API의 요청 본문을 표현합니다.
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
@Schema(description = "권한 생성 요청")
public record CreatePermissionApiRequest(
        @Schema(
                        description = "리소스명",
                        requiredMode = Schema.RequiredMode.REQUIRED,
                        minLength = 1,
                        maxLength = 50)
                @NotBlank(message = "리소스는 필수입니다")
                @Size(min = 1, max = 50, message = "리소스는 1자 이상 50자 이하여야 합니다")
                String resource,
        @Schema(
                        description = "액션명",
                        requiredMode = Schema.RequiredMode.REQUIRED,
                        minLength = 1,
                        maxLength = 50)
                @NotBlank(message = "액션은 필수입니다")
                @Size(min = 1, max = 50, message = "액션은 1자 이상 50자 이하여야 합니다")
                String action,
        @Schema(description = "권한 설명", maxLength = 500)
                @Size(max = 500, message = "설명은 500자 이하여야 합니다")
                String description,
        @Schema(description = "시스템 권한 여부") Boolean isSystem) {}
