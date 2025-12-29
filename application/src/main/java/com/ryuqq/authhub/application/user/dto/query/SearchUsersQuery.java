package com.ryuqq.authhub.application.user.dto.query;

import java.time.Instant;
import java.util.UUID;

/**
 * SearchUsersQuery - 사용자 목록 검색 Query DTO (Admin-Friendly 확장)
 *
 * <p>어드민 화면에서 다양한 조건으로 사용자를 검색할 수 있도록 확장된 Query DTO입니다.
 *
 * <p><strong>확장된 필터:</strong>
 *
 * <ul>
 *   <li>날짜 범위 필터 (createdFrom, createdTo) - 필수
 *   <li>키워드 검색 (identifier 부분 일치)
 *   <li>역할 필터 (roleId)
 *   <li>정렬 옵션 (sortBy, sortDirection)
 *   <li>검색 타입 (searchType) - CONTAINS_LIKE, PREFIX_LIKE, MATCH_AGAINST
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param tenantId 테넌트 ID (필터)
 * @param organizationId 조직 ID (필터)
 * @param identifier 식별자 검색 (부분 일치)
 * @param searchType 검색 타입 (CONTAINS_LIKE, PREFIX_LIKE, MATCH_AGAINST)
 * @param status 상태 필터 (ACTIVE, INACTIVE, SUSPENDED)
 * @param roleId 역할 ID 필터 (해당 역할이 할당된 사용자만)
 * @param createdFrom 생성일 시작 (inclusive, 필수)
 * @param createdTo 생성일 종료 (inclusive, 필수)
 * @param sortBy 정렬 기준 (createdAt, updatedAt, identifier)
 * @param sortDirection 정렬 방향 (ASC, DESC)
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
public record SearchUsersQuery(
        UUID tenantId,
        UUID organizationId,
        String identifier,
        String searchType,
        String status,
        UUID roleId,
        Instant createdFrom,
        Instant createdTo,
        String sortBy,
        String sortDirection,
        int page,
        int size) {

    /** 기본 검색 타입 */
    public static final String DEFAULT_SEARCH_TYPE = "CONTAINS_LIKE";

    /** 기본 정렬 기준 */
    public static final String DEFAULT_SORT_BY = "createdAt";

    /** 기본 정렬 방향 */
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    /** 기본 페이지 크기 */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** 최대 페이지 크기 */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 일반 사용자용 팩토리 메서드 (필수 날짜 범위 포함)
     *
     * @param tenantId 테넌트 ID
     * @param organizationId 조직 ID
     * @param identifier 식별자
     * @param status 상태
     * @param createdFrom 생성일 시작 (필수)
     * @param createdTo 생성일 종료 (필수)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return SearchUsersQuery 인스턴스
     */
    public static SearchUsersQuery of(
            UUID tenantId,
            UUID organizationId,
            String identifier,
            String status,
            Instant createdFrom,
            Instant createdTo,
            int page,
            int size) {
        return new SearchUsersQuery(
                tenantId,
                organizationId,
                identifier,
                DEFAULT_SEARCH_TYPE,
                status,
                null,
                createdFrom,
                createdTo,
                DEFAULT_SORT_BY,
                DEFAULT_SORT_DIRECTION,
                page,
                size);
    }

    /**
     * Admin용 팩토리 메서드 (확장 필터 포함)
     *
     * @param tenantId 테넌트 ID
     * @param organizationId 조직 ID
     * @param identifier 식별자
     * @param searchType 검색 타입 (CONTAINS_LIKE, PREFIX_LIKE, MATCH_AGAINST)
     * @param status 상태
     * @param roleId 역할 ID 필터
     * @param createdFrom 생성일 시작 (필수)
     * @param createdTo 생성일 종료 (필수)
     * @param sortBy 정렬 기준
     * @param sortDirection 정렬 방향
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return SearchUsersQuery 인스턴스
     */
    public static SearchUsersQuery ofAdmin(
            UUID tenantId,
            UUID organizationId,
            String identifier,
            String searchType,
            String status,
            UUID roleId,
            Instant createdFrom,
            Instant createdTo,
            String sortBy,
            String sortDirection,
            int page,
            int size) {
        return new SearchUsersQuery(
                tenantId,
                organizationId,
                identifier,
                searchType != null ? searchType : DEFAULT_SEARCH_TYPE,
                status,
                roleId,
                createdFrom,
                createdTo,
                sortBy != null ? sortBy : DEFAULT_SORT_BY,
                sortDirection != null ? sortDirection : DEFAULT_SORT_DIRECTION,
                page,
                size);
    }
}
