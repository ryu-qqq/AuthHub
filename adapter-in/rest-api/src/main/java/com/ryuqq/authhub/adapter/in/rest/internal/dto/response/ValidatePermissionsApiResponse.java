package com.ryuqq.authhub.adapter.in.rest.internal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * ValidatePermissionsApiResponse - 권한 검증 API 응답 DTO
 *
 * <p>권한 검증 결과를 반환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiResponse 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>Domain 변환 메서드 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "권한 검증 결과")
public record ValidatePermissionsApiResponse(
        @Schema(description = "검증 성공 여부", example = "false") boolean valid,
        @Schema(description = "검증한 서비스명", example = "product-service") String serviceName,
        @Schema(description = "검증 요청한 권한 총 개수", example = "10") int totalCount,
        @Schema(description = "DB에 존재하는 권한 개수", example = "8") int existingCount,
        @Schema(description = "DB에 없는 권한 개수", example = "2") int missingCount,
        @Schema(description = "DB에 존재하는 권한 키 목록") List<String> existing,
        @Schema(description = "DB에 없는 권한 키 목록 (누락된 권한)") List<String> missing,
        @Schema(description = "검증 결과 메시지") String message) {}
