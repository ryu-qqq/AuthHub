package com.ryuqq.authhub.application.permission.dto.query;

import java.time.Instant;
import java.util.List;

/**
 * SearchPermissionsQuery - 권한 목록 검색 Query DTO (Offset 기반 페이징)
 *
 * <p>REST API Layer로부터 전달받는 Query DTO입니다.
 *
 * <p><strong>필수 파라미터:</strong>
 *
 * <ul>
 *   <li>createdFrom, createdTo: 생성일시 범위 필터 (필수)
 *   <li>page, size: 페이지 정보
 * </ul>
 *
 * <p><strong>선택 파라미터:</strong>
 *
 * <ul>
 *   <li>resource: 리소스명 검색어
 *   <li>action: 액션명 검색어
 *   <li>searchType: 검색 방식 (PREFIX_LIKE, CONTAINS_LIKE)
 *   <li>types: 권한 유형 필터 (다중 선택)
 *   <li>sortBy, sortDirection: 정렬 옵션
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record
 *   <li>기본값 처리 금지 (REST API 책임)
 *   <li>Lombok 금지
 * </ul>
 *
 * @param resource 리소스명 필터 (null 허용, 부분 검색)
 * @param action 액션명 필터 (null 허용, 부분 검색)
 * @param searchType 검색 방식 (PREFIX_LIKE, CONTAINS_LIKE, null 시 CONTAINS_LIKE)
 * @param types 권한 유형 필터 목록 (null 또는 빈 목록 시 전체)
 * @param createdFrom 생성일시 시작 (필수)
 * @param createdTo 생성일시 종료 (필수)
 * @param sortBy 정렬 기준 (null 허용)
 * @param sortDirection 정렬 방향 (null 허용)
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
public record SearchPermissionsQuery(
        String resource,
        String action,
        String searchType,
        List<String> types,
        Instant createdFrom,
        Instant createdTo,
        String sortBy,
        String sortDirection,
        int page,
        int size) {

    /** 기본 페이지 크기 */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** 기본 정렬 기준 */
    public static final String DEFAULT_SORT_BY = "createdAt";

    /** 기본 정렬 방향 */
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    /** 기본 검색 타입 */
    public static final String DEFAULT_SEARCH_TYPE = "CONTAINS_LIKE";

    /**
     * 일반 API용 팩토리 메서드 (searchType 없음)
     *
     * @param resource 리소스명 필터
     * @param action 액션명 필터
     * @param types 권한 유형 필터 목록
     * @param createdFrom 생성일시 시작
     * @param createdTo 생성일시 종료
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return SearchPermissionsQuery
     */
    public static SearchPermissionsQuery of(
            String resource,
            String action,
            List<String> types,
            Instant createdFrom,
            Instant createdTo,
            int page,
            int size) {
        return new SearchPermissionsQuery(
                resource,
                action,
                DEFAULT_SEARCH_TYPE,
                types,
                createdFrom,
                createdTo,
                DEFAULT_SORT_BY,
                DEFAULT_SORT_DIRECTION,
                page,
                size);
    }

    /**
     * Admin API용 팩토리 메서드 (전체 파라미터)
     *
     * @param resource 리소스명 필터
     * @param action 액션명 필터
     * @param searchType 검색 방식
     * @param types 권한 유형 필터 목록
     * @param createdFrom 생성일시 시작
     * @param createdTo 생성일시 종료
     * @param sortBy 정렬 기준
     * @param sortDirection 정렬 방향
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return SearchPermissionsQuery
     */
    public static SearchPermissionsQuery ofAdmin(
            String resource,
            String action,
            String searchType,
            List<String> types,
            Instant createdFrom,
            Instant createdTo,
            String sortBy,
            String sortDirection,
            int page,
            int size) {
        return new SearchPermissionsQuery(
                resource,
                action,
                searchType != null ? searchType : DEFAULT_SEARCH_TYPE,
                types,
                createdFrom,
                createdTo,
                sortBy != null ? sortBy : DEFAULT_SORT_BY,
                sortDirection != null ? sortDirection : DEFAULT_SORT_DIRECTION,
                page,
                size);
    }
}
