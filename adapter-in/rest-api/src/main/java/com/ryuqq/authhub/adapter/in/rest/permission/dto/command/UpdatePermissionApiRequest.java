package com.ryuqq.authhub.adapter.in.rest.permission.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * UpdatePermissionApiRequest - 권한 수정 요청 DTO
 *
 * <p>권한 수정 API의 요청 본문을 표현합니다.
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
@Schema(description = "권한 수정 요청")
public record UpdatePermissionApiRequest(
        @Schema(description = "권한 설명", maxLength = 500)
                @Size(max = 500, message = "설명은 500자 이하여야 합니다")
                String description) {}
