package com.ryuqq.authhub.application.tenant.dto.query;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import java.time.LocalDate;
import java.util.List;

/**
 * TenantSearchParams - Tenant 목록 조회 SearchParams DTO
 *
 * <p>Tenant 목록을 페이지 기반으로 조회하는 SearchParams DTO입니다.
 *
 * <p>APP-DTO-003: SearchParams는 CommonSearchParams 포함 필수.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 → String으로 전달, Factory에서 변환.
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
 * @param searchWord 검색어 (null 허용)
 * @param searchField 검색 필드 (null 허용, 기본: NAME)
 * @param statuses 테넌트 상태 필터 목록 (null 또는 빈 목록 시 전체)
 * @author development-team
 * @since 1.0.0
 */
public record TenantSearchParams(
        CommonSearchParams searchParams,
        String searchWord,
        String searchField,
        List<String> statuses) {

    /**
     * TenantSearchParams 생성 (전체 파라미터)
     *
     * @param searchParams 공통 검색 파라미터
     * @param searchWord 검색어
     * @param searchField 검색 필드
     * @param statuses 테넌트 상태 필터 목록
     * @return TenantSearchParams 인스턴스
     */
    public static TenantSearchParams of(
            CommonSearchParams searchParams,
            String searchWord,
            String searchField,
            List<String> statuses) {
        return new TenantSearchParams(searchParams, searchWord, searchField, statuses);
    }

    /**
     * TenantSearchParams 생성 (간편 생성)
     *
     * @param searchWord 검색어
     * @param searchField 검색 필드
     * @param statuses 테넌트 상태 필터 목록
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return TenantSearchParams 인스턴스
     */
    public static TenantSearchParams of(
            String searchWord,
            String searchField,
            List<String> statuses,
            LocalDate startDate,
            LocalDate endDate,
            Integer page,
            Integer size) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(false, startDate, endDate, "createdAt", "DESC", page, size);
        return new TenantSearchParams(searchParams, searchWord, searchField, statuses);
    }

    // ==================== Delegate Methods ====================

    /**
     * 삭제된 항목 포함 여부 반환 (delegate)
     *
     * @return 삭제된 항목 포함 여부
     */
    public Boolean includeDeleted() {
        return searchParams.includeDeleted();
    }

    /**
     * 조회 시작일 반환 (delegate)
     *
     * @return 조회 시작일
     */
    public LocalDate startDate() {
        return searchParams.startDate();
    }

    /**
     * 조회 종료일 반환 (delegate)
     *
     * @return 조회 종료일
     */
    public LocalDate endDate() {
        return searchParams.endDate();
    }

    /**
     * 정렬 기준 반환 (delegate)
     *
     * @return 정렬 기준
     */
    public String sortKey() {
        return searchParams.sortKey();
    }

    /**
     * 정렬 방향 반환 (delegate)
     *
     * @return 정렬 방향
     */
    public String sortDirection() {
        return searchParams.sortDirection();
    }

    /**
     * 페이지 번호 반환 (delegate)
     *
     * @return 페이지 번호
     */
    public Integer page() {
        return searchParams.page();
    }

    /**
     * 페이지 크기 반환 (delegate)
     *
     * @return 페이지 크기
     */
    public Integer size() {
        return searchParams.size();
    }

    /**
     * 검색어가 있는지 확인
     *
     * @return searchWord가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasSearchWord() {
        return searchWord != null && !searchWord.isBlank();
    }

    /**
     * 상태 필터가 있는지 확인
     *
     * @return statuses가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasStatuses() {
        return statuses != null && !statuses.isEmpty();
    }
}
