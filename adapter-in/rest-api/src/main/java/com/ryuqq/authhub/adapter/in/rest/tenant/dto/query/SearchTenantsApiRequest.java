package com.ryuqq.authhub.adapter.in.rest.tenant.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * SearchTenantsApiRequest - 테넌트 목록 조회 요청 DTO
 *
 * <p>테넌트 목록 조회 API의 쿼리 파라미터를 표현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>Bean Validation 어노테이션 사용
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "테넌트 목록 조회 조건")
public record SearchTenantsApiRequest(
        @Schema(description = "테넌트 이름 필터") String name,
        @Schema(
                        description = "상태 필터",
                        allowableValues = {"ACTIVE", "INACTIVE"})
                String status,
        @Schema(description = "페이지 번호", minimum = "0", defaultValue = "0")
                @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
                Integer page,
        @Schema(description = "페이지 크기", minimum = "1", maximum = "100", defaultValue = "20")
                @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
                @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
                Integer size) {}
