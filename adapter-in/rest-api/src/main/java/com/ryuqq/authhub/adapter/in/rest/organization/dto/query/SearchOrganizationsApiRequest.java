package com.ryuqq.authhub.adapter.in.rest.organization.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * SearchOrganizationsApiRequest - 조직 검색 요청 DTO
 *
 * <p>조직 목록 검색 API의 쿼리 파라미터를 표현합니다.
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
@Schema(description = "조직 목록 검색 조건")
public record SearchOrganizationsApiRequest(
        @Schema(description = "테넌트 ID", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "테넌트 ID는 필수입니다")
                String tenantId,
        @Schema(description = "조직 이름 필터") String name,
        @Schema(
                        description = "상태 필터",
                        allowableValues = {"ACTIVE", "INACTIVE"})
                String status,
        @Schema(description = "페이지 번호", minimum = "0", defaultValue = "0")
                @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
                int page,
        @Schema(description = "페이지 크기", minimum = "1", maximum = "100", defaultValue = "20")
                @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
                @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
                int size) {}
