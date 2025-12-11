package com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * SearchEndpointPermissionsApiRequest - 엔드포인트 권한 검색 API 요청 DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지
 *   <li>Record 사용
 * </ul>
 *
 * @param serviceName 서비스 이름 필터 (정확한 매칭, 선택)
 * @param pathPattern 경로 패턴 필터 (부분 일치, 선택)
 * @param method HTTP 메서드 필터 (선택)
 * @param isPublic 공개 여부 필터 (선택)
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "엔드포인트 권한 검색 요청")
public record SearchEndpointPermissionsApiRequest(
        @Schema(description = "서비스 이름 필터", example = "auth-hub") String serviceName,
        @Schema(description = "경로 패턴 필터 (부분 일치)", example = "/api/v1/users") String pathPattern,
        @Schema(description = "HTTP 메서드 필터", example = "GET") String method,
        @Schema(description = "공개 여부 필터", example = "false") Boolean isPublic,
        @Schema(description = "페이지 번호", example = "0") int page,
        @Schema(description = "페이지 크기", example = "20") int size) {}
