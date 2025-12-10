package com.ryuqq.authhub.adapter.in.rest.permission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

/**
 * UserPermissionsApiResponse - 사용자 권한 응답 DTO
 *
 * <p>Gateway에서 사용자의 역할과 권한 목록을 조회하기 위한 응답 DTO입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiResponse 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 *   <li>Domain 변환 메서드 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "사용자 권한 응답")
public record UserPermissionsApiResponse(
        @Schema(description = "사용자 ID") String userId,
        @Schema(description = "역할 목록") Set<String> roles,
        @Schema(description = "권한 키 목록") Set<String> permissions) {}
