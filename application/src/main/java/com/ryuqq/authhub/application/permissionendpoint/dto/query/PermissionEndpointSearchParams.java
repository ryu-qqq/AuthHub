package com.ryuqq.authhub.application.permissionendpoint.dto.query;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import java.time.LocalDate;
import java.util.List;

/**
 * PermissionEndpointSearchParams - 엔드포인트 목록 조회 SearchParams DTO
 *
 * <p>엔드포인트 목록을 페이지 기반으로 조회하는 SearchParams DTO입니다.
 *
 * <p>APP-DTO-003: SearchParams는 CommonSearchParams 포함 필수.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 -> String으로 전달, Factory에서 변환.
 *
 * <p><strong>사용 규칙:</strong>
 *
 * <ul>
 *   <li>CommonSearchParams를 필드로 포함 (페이징, 정렬, 날짜 범위)
 *   <li>delegate 메서드를 통해 직접 접근 허용
 *   <li>중첩 접근(params.searchParams().page()) 금지 - delegate 사용
 *   <li>필터 값은 String으로 전달, 도메인 타입 변환은 Factory에서 수행
 * </ul>
 *
 * @param searchParams 공통 검색 파라미터 (페이징, 정렬, 날짜 범위)
 * @param permissionIds 권한 ID 필터 목록 (복수)
 * @param searchWord 검색어 (null 허용)
 * @param searchField 검색 필드 - URL_PATTERN, HTTP_METHOD, DESCRIPTION (null 허용)
 * @param httpMethods HTTP 메서드 필터 목록 (null 또는 빈 목록 시 전체)
 * @author development-team
 * @since 1.0.0
 */
public record PermissionEndpointSearchParams(
        CommonSearchParams searchParams,
        List<Long> permissionIds,
        String searchWord,
        String searchField,
        List<String> httpMethods) {

    /**
     * 전체 파라미터로 생성
     *
     * @param searchParams 공통 검색 파라미터
     * @param permissionIds 권한 ID 목록
     * @param searchWord 검색어
     * @param searchField 검색 필드
     * @param httpMethods HTTP 메서드 필터 목록
     * @return PermissionEndpointSearchParams 인스턴스
     */
    public static PermissionEndpointSearchParams of(
            CommonSearchParams searchParams,
            List<Long> permissionIds,
            String searchWord,
            String searchField,
            List<String> httpMethods) {
        return new PermissionEndpointSearchParams(
                searchParams, permissionIds, searchWord, searchField, httpMethods);
    }

    /**
     * 특정 권한의 엔드포인트 조회용 간편 생성
     *
     * @param permissionId 권한 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return PermissionEndpointSearchParams 인스턴스
     */
    public static PermissionEndpointSearchParams forPermission(
            Long permissionId, Integer page, Integer size) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
        return new PermissionEndpointSearchParams(
                searchParams, List.of(permissionId), null, null, null);
    }

    /**
     * 기본 조회용 간편 생성
     *
     * @param searchWord 검색어
     * @param httpMethods HTTP 메서드 필터 목록
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return PermissionEndpointSearchParams 인스턴스
     */
    public static PermissionEndpointSearchParams ofDefault(
            String searchWord,
            List<String> httpMethods,
            LocalDate startDate,
            LocalDate endDate,
            Integer page,
            Integer size) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(false, startDate, endDate, "createdAt", "DESC", page, size);
        return new PermissionEndpointSearchParams(
                searchParams, null, searchWord, null, httpMethods);
    }

    // ==================== Delegate Methods ====================

    public Boolean includeDeleted() {
        return searchParams.includeDeleted();
    }

    public LocalDate startDate() {
        return searchParams.startDate();
    }

    public LocalDate endDate() {
        return searchParams.endDate();
    }

    public String sortKey() {
        return searchParams.sortKey();
    }

    public String sortDirection() {
        return searchParams.sortDirection();
    }

    public Integer page() {
        return searchParams.page();
    }

    public Integer size() {
        return searchParams.size();
    }

    public boolean hasPermissionIds() {
        return permissionIds != null && !permissionIds.isEmpty();
    }

    public boolean hasSearchWord() {
        return searchWord != null && !searchWord.isBlank();
    }

    public boolean hasHttpMethods() {
        return httpMethods != null && !httpMethods.isEmpty();
    }
}
