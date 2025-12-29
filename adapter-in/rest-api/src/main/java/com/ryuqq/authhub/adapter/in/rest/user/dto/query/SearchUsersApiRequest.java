package com.ryuqq.authhub.adapter.in.rest.user.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * SearchUsersApiRequest - 사용자 목록 검색 요청 DTO
 *
 * <p>사용자 목록 검색 API의 Query Parameter를 표현합니다.
 *
 * <p><strong>필수 필터:</strong>
 *
 * <ul>
 *   <li>createdFrom - 생성일 시작 (ISO-8601 형식, 필수)
 *   <li>createdTo - 생성일 종료 (ISO-8601 형식, 필수)
 * </ul>
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
@Schema(description = "사용자 검색 조건")
public record SearchUsersApiRequest(
        @Schema(description = "테넌트 ID 필터") String tenantId,
        @Schema(description = "조직 ID 필터") String organizationId,
        @Schema(description = "사용자 식별자 필터") String identifier,
        @Schema(
                        description = "상태 필터",
                        allowableValues = {"ACTIVE", "INACTIVE", "LOCKED", "WITHDRAWN"})
                String status,
        @Schema(description = "생성일 시작 (ISO-8601, 필수)", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "생성일 시작은 필수입니다")
                Instant createdFrom,
        @Schema(description = "생성일 종료 (ISO-8601, 필수)", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "생성일 종료는 필수입니다")
                Instant createdTo,
        @Schema(description = "페이지 번호", minimum = "0", defaultValue = "0") Integer page,
        @Schema(description = "페이지 크기", minimum = "1", maximum = "100", defaultValue = "20")
                Integer size) {}
