package com.ryuqq.authhub.adapter.in.rest.permission.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * SearchPermissionsApiRequest - 권한 검색 요청 DTO
 *
 * <p>권한 검색 API의 요청 파라미터를 표현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiRequest 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 *   <li>Domain 변환 메서드 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "권한 검색 조건")
public record SearchPermissionsApiRequest(
        @Schema(description = "리소스명 필터") String resource,
        @Schema(description = "액션명 필터") String action,
        @Schema(
                        description = "권한 유형 필터",
                        allowableValues = {"SYSTEM", "CUSTOM"})
                String type,
        @Schema(description = "페이지 번호", minimum = "0", defaultValue = "0") int page,
        @Schema(description = "페이지 크기", minimum = "1", maximum = "100", defaultValue = "20")
                int size) {}
