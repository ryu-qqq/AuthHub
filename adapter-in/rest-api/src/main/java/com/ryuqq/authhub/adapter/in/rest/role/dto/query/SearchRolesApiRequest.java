package com.ryuqq.authhub.adapter.in.rest.role.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * SearchRolesApiRequest - 역할 검색 요청 DTO
 *
 * <p>역할 검색 API의 쿼리 파라미터를 표현합니다.
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
@Schema(description = "역할 목록 검색 조건")
public record SearchRolesApiRequest(
        @Schema(description = "테넌트 ID 필터") String tenantId,
        @Schema(description = "역할 이름 필터") String name,
        @Schema(
                        description = "역할 범위 필터",
                        allowableValues = {"TENANT", "ORGANIZATION", "SYSTEM"})
                String scope,
        @Schema(
                        description = "역할 유형 필터",
                        allowableValues = {"SYSTEM", "CUSTOM"})
                String type,
        @Schema(description = "페이지 번호", minimum = "0", defaultValue = "0")
                @Min(value = 0, message = "페이지는 0 이상이어야 합니다")
                Integer page,
        @Schema(description = "페이지 크기", minimum = "1", maximum = "100", defaultValue = "20")
                @Min(value = 1, message = "사이즈는 1 이상이어야 합니다")
                @Max(value = 100, message = "사이즈는 100 이하여야 합니다")
                Integer size) {}
