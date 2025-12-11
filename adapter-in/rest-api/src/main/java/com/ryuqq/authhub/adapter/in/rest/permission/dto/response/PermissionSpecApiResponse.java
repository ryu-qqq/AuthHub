package com.ryuqq.authhub.adapter.in.rest.permission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

/**
 * PermissionSpecApiResponse - 권한 명세 API 응답 DTO
 *
 * <p>Gateway가 조회하는 전체 엔드포인트별 권한 명세입니다.
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
@Schema(description = "권한 명세 응답")
public record PermissionSpecApiResponse(
        @Schema(description = "명세 버전") int version,
        @Schema(description = "갱신 시간") Instant updatedAt,
        @Schema(description = "엔드포인트별 권한 목록") List<EndpointPermissionApiResponse> permissions) {}
