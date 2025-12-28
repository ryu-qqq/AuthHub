package com.ryuqq.authhub.adapter.in.rest.role.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;

/**
 * SearchRolesAdminApiRequest - 역할 목록 검색 요청 DTO (Admin용)
 *
 * <p>어드민 화면용 확장된 필터를 지원하는 검색 요청입니다.
 *
 * <p><strong>확장 필터:</strong>
 *
 * <ul>
 *   <li>날짜 범위 필터 (createdFrom, createdTo)
 *   <li>정렬 옵션 (sortBy, sortDirection)
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
 * @see SearchRolesApiRequest 기본 검색 요청 DTO
 */
@Schema(description = "역할 검색 조건 (Admin용)")
public record SearchRolesAdminApiRequest(
        @Schema(description = "테넌트 ID 필터") String tenantId,
        @Schema(description = "역할 이름 필터 (부분 일치)") String name,
        @Schema(
                        description = "역할 범위 필터",
                        allowableValues = {"TENANT", "ORGANIZATION", "SYSTEM"})
                @Pattern(regexp = "TENANT|ORGANIZATION|SYSTEM", message = "유효하지 않은 역할 범위입니다")
                String scope,
        @Schema(
                        description = "역할 유형 필터",
                        allowableValues = {"SYSTEM", "CUSTOM"})
                @Pattern(regexp = "SYSTEM|CUSTOM", message = "유효하지 않은 역할 유형입니다")
                String type,
        @Schema(description = "생성일 시작 (inclusive)") Instant createdFrom,
        @Schema(description = "생성일 종료 (inclusive)") Instant createdTo,
        @Schema(
                        description = "정렬 기준",
                        allowableValues = {"createdAt", "updatedAt", "name"},
                        defaultValue = "createdAt")
                String sortBy,
        @Schema(
                        description = "정렬 방향",
                        allowableValues = {"ASC", "DESC"},
                        defaultValue = "DESC")
                @Pattern(regexp = "ASC|DESC", message = "정렬 방향은 ASC 또는 DESC여야 합니다")
                String sortDirection,
        @Schema(description = "페이지 번호", minimum = "0", defaultValue = "0")
                @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
                Integer page,
        @Schema(description = "페이지 크기", minimum = "1", maximum = "100", defaultValue = "20")
                @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
                @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
                Integer size) {}
