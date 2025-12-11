package com.ryuqq.authhub.adapter.in.rest.user.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * UpdateUserApiRequest - 사용자 수정 요청 DTO
 *
 * <p>사용자 정보 수정 API의 요청 본문을 표현합니다.
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
@Schema(description = "사용자 정보 수정 요청")
public record UpdateUserApiRequest(
        @Schema(description = "사용자 식별자", minLength = 1, maxLength = 255)
                @Size(min = 1, max = 255, message = "사용자 식별자는 1자 이상 255자 이하여야 합니다")
                String identifier) {}
