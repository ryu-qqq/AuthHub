package com.ryuqq.authhub.application.user.dto.query;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import java.time.LocalDate;
import java.util.List;

/**
 * UserSearchParams - 사용자 목록 조회 SearchParams DTO
 *
 * <p>사용자 목록을 페이지 기반으로 조회하는 SearchParams DTO입니다.
 *
 * <p>APP-DTO-003: SearchParams는 CommonSearchParams 포함 필수.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 -> String으로 전달, Factory에서 변환.
 *
 * @param searchParams 공통 검색 파라미터 (페이징, 정렬, 날짜 범위)
 * @param organizationId 조직 ID (null이면 전체 조회)
 * @param searchWord 검색어 (null 허용)
 * @param searchField 검색 필드 - IDENTIFIER, PHONE_NUMBER (null 허용)
 * @param statuses 상태 필터 목록 - ACTIVE, INACTIVE, SUSPENDED (null 또는 빈 목록 시 전체)
 * @author development-team
 * @since 1.0.0
 */
public record UserSearchParams(
        CommonSearchParams searchParams,
        String organizationId,
        String searchWord,
        String searchField,
        List<String> statuses) {

    /**
     * UserSearchParams 생성 (전체 파라미터)
     *
     * @param searchParams 공통 검색 파라미터
     * @param organizationId 조직 ID
     * @param searchWord 검색어
     * @param searchField 검색 필드
     * @param statuses 상태 필터 목록
     * @return UserSearchParams 인스턴스
     */
    public static UserSearchParams of(
            CommonSearchParams searchParams,
            String organizationId,
            String searchWord,
            String searchField,
            List<String> statuses) {
        return new UserSearchParams(
                searchParams, organizationId, searchWord, searchField, statuses);
    }

    /**
     * UserSearchParams 생성 (간편 생성)
     *
     * @param organizationId 조직 ID
     * @param searchWord 검색어
     * @param statuses 상태 필터 목록
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return UserSearchParams 인스턴스
     */
    public static UserSearchParams ofOrganization(
            String organizationId,
            String searchWord,
            List<String> statuses,
            LocalDate startDate,
            LocalDate endDate,
            Integer page,
            Integer size) {
        CommonSearchParams searchParams =
                CommonSearchParams.of(false, startDate, endDate, "createdAt", "DESC", page, size);
        return new UserSearchParams(searchParams, organizationId, searchWord, null, statuses);
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
     * 조직 필터가 있는지 확인
     *
     * @return organizationId가 null이 아니면 true
     */
    public boolean hasOrganizationId() {
        return organizationId != null && !organizationId.isBlank();
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
