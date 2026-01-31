package com.ryuqq.authhub.adapter.in.rest.user.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * UpdateUserApiRequest - 사용자 정보 수정 요청 DTO
 *
 * <p>사용자 정보 수정 API의 요청 본문을 표현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiRequest 네이밍 규칙
 *   <li>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달
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
        @Schema(
                        description = "전화번호 (null이면 변경 안 함)",
                        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
                String phoneNumber) {}
