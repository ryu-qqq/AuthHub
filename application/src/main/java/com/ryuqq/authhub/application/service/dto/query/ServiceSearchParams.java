package com.ryuqq.authhub.application.service.dto.query;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import java.time.LocalDate;
import java.util.List;

/**
 * ServiceSearchParams - 서비스 목록 조회 SearchParams DTO
 *
 * <p>서비스 목록을 페이지 기반으로 조회하는 SearchParams DTO입니다.
 *
 * <p>APP-DTO-003: SearchParams는 CommonSearchParams 포함 필수.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 -> String으로 전달, Factory에서 변환.
 *
 * @param searchParams 공통 검색 파라미터 (페이징, 정렬, 날짜 범위)
 * @param searchWord 검색어 (null 허용)
 * @param searchField 검색 필드 - SERVICE_CODE, NAME (null 허용)
 * @param statuses 상태 필터 목록 - ACTIVE, INACTIVE (null 또는 빈 목록 시 전체)
 * @author development-team
 * @since 1.0.0
 */
public record ServiceSearchParams(
        CommonSearchParams searchParams,
        String searchWord,
        String searchField,
        List<String> statuses) {

    /**
     * ServiceSearchParams 생성 (전체 파라미터)
     *
     * @param searchParams 공통 검색 파라미터
     * @param searchWord 검색어
     * @param searchField 검색 필드
     * @param statuses 상태 필터 목록
     * @return ServiceSearchParams 인스턴스
     */
    public static ServiceSearchParams of(
            CommonSearchParams searchParams,
            String searchWord,
            String searchField,
            List<String> statuses) {
        return new ServiceSearchParams(searchParams, searchWord, searchField, statuses);
    }

    // ==================== Delegate Methods ====================

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
