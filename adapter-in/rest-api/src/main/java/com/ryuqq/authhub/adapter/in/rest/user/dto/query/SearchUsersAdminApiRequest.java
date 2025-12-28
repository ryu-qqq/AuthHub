package com.ryuqq.authhub.adapter.in.rest.user.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * SearchUsersAdminApiRequest - 사용자 목록 검색 요청 DTO (Admin용)
 *
 * <p>어드민 화면용 확장된 필터를 지원하는 검색 요청입니다.
 *
 * <p><strong>확장 필터:</strong>
 *
 * <ul>
 *   <li>날짜 범위 필터 (createdFrom, createdTo)
 *   <li>역할 필터 (roleId)
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
 * @see SearchUsersApiRequest 기본 검색 요청 DTO
 */
@Schema(description = "사용자 검색 조건 (Admin용)")
public record SearchUsersAdminApiRequest(
        @Schema(description = "테넌트 ID 필터") String tenantId,
        @Schema(description = "조직 ID 필터") String organizationId,
        @Schema(description = "사용자 식별자 필터 (부분 일치)") String identifier,
        @Schema(
                        description = "상태 필터",
                        allowableValues = {"ACTIVE", "INACTIVE", "LOCKED", "WITHDRAWN"})
                String status,
        @Schema(description = "역할 ID 필터 (해당 역할이 할당된 사용자만)") String roleId,
        @Schema(description = "생성일 시작 (inclusive)") Instant createdFrom,
        @Schema(description = "생성일 종료 (inclusive)") Instant createdTo,
        @Schema(
                        description = "정렬 기준",
                        allowableValues = {"createdAt", "updatedAt", "identifier"},
                        defaultValue = "createdAt")
                String sortBy,
        @Schema(
                        description = "정렬 방향",
                        allowableValues = {"ASC", "DESC"},
                        defaultValue = "DESC")
                String sortDirection,
        @Schema(description = "페이지 번호", minimum = "0", defaultValue = "0") Integer page,
        @Schema(description = "페이지 크기", minimum = "1", maximum = "100", defaultValue = "20")
                Integer size) {}
