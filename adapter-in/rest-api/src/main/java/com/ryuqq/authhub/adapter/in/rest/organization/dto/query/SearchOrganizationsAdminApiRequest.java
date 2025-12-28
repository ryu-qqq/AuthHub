package com.ryuqq.authhub.adapter.in.rest.organization.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * SearchOrganizationsAdminApiRequest - Admin 조직 검색 요청 DTO
 *
 * <p>어드민 친화적 조직 목록 검색을 위한 요청 DTO입니다. 확장 필터(날짜 범위, 정렬)를 지원합니다.
 *
 * <p><strong>SearchOrganizationsApiRequest와의 차이점:</strong>
 *
 * <ul>
 *   <li>tenantId 선택 (전체 조회 가능)
 *   <li>createdFrom, createdTo 추가 (날짜 범위 필터)
 *   <li>sortBy, sortDirection 추가 (정렬 옵션)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiRequest 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 * </ul>
 *
 * @param tenantId 테넌트 ID 필터 (선택)
 * @param name 조직 이름 필터 (선택)
 * @param status 조직 상태 필터 (선택)
 * @param createdFrom 생성일시 시작 (선택)
 * @param createdTo 생성일시 종료 (선택)
 * @param sortBy 정렬 기준 (기본: createdAt)
 * @param sortDirection 정렬 방향 (기본: DESC)
 * @param page 페이지 번호 (기본: 0)
 * @param size 페이지 크기 (기본: 20)
 * @author development-team
 * @since 1.0.0
 * @see SearchOrganizationsApiRequest 기본 검색 요청 DTO
 */
@Schema(description = "조직 검색 조건 (Admin용)")
public record SearchOrganizationsAdminApiRequest(
        @Schema(description = "테넌트 ID 필터") String tenantId,
        @Schema(description = "조직 이름 필터") String name,
        @Schema(description = "조직 상태 필터 (ACTIVE/INACTIVE/SUSPENDED)") String status,
        @Schema(description = "생성일시 시작") Instant createdFrom,
        @Schema(description = "생성일시 종료") Instant createdTo,
        @Schema(
                        description = "정렬 기준 (name, status, createdAt, updatedAt)",
                        defaultValue = "createdAt")
                String sortBy,
        @Schema(description = "정렬 방향 (ASC/DESC)", defaultValue = "DESC") String sortDirection,
        @Schema(description = "페이지 번호", defaultValue = "0") Integer page,
        @Schema(description = "페이지 크기", defaultValue = "20") Integer size) {}
