package com.ryuqq.authhub.application.tenant.dto.query;

import java.time.Instant;

/**
 * SearchTenantsQuery - 테넌트 목록 조회 Query DTO (Offset 기반 페이징)
 *
 * <p>Admin 확장 필터를 포함하는 Query DTO입니다.
 *
 * <p><strong>확장 필터 (Admin):</strong>
 *
 * <ul>
 *   <li>createdFrom, createdTo: 생성일시 범위 필터
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
 * @param name 테넌트 이름 필터 (null 허용, 부분 검색)
 * @param status 테넌트 상태 필터 (null 허용)
 * @param createdFrom 생성일시 시작 (null 허용)
 * @param createdTo 생성일시 종료 (null 허용)
 * @param sortBy 정렬 기준 (null 허용)
 * @param sortDirection 정렬 방향 (null 허용)
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
public record SearchTenantsQuery(
        String name,
        String status,
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

    /**
     * 기존 API 호환용 생성자 (Admin 확장 필터 없음)
     *
     * @param name 테넌트 이름 필터
     * @param status 테넌트 상태 필터
     * @param page 페이지 번호
     * @param size 페이지 크기
     */
    public SearchTenantsQuery(String name, String status, Integer page, Integer size) {
        this(
                name,
                status,
                null,
                null,
                DEFAULT_SORT_BY,
                DEFAULT_SORT_DIRECTION,
                page != null ? page : 0,
                size != null ? size : DEFAULT_PAGE_SIZE);
    }
}
