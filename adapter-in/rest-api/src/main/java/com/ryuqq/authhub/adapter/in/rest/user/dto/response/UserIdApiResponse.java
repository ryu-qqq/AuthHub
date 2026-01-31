package com.ryuqq.authhub.adapter.in.rest.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * UserIdApiResponse - 사용자 ID 응답 DTO
 *
 * <p>사용자 생성/수정 후 ID를 반환하는 응답 DTO입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiResponse 네이밍 규칙
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "사용자 ID 응답")
public record UserIdApiResponse(@Schema(description = "사용자 ID") String userId) {

    /**
     * 팩토리 메서드
     *
     * @param userId 사용자 ID
     * @return UserIdApiResponse
     */
    public static UserIdApiResponse of(String userId) {
        return new UserIdApiResponse(userId);
    }
}
