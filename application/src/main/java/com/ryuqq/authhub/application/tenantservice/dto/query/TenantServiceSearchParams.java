package com.ryuqq.authhub.application.tenantservice.dto.query;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import java.time.LocalDate;
import java.util.List;

/**
 * TenantServiceSearchParams - 테넌트-서비스 구독 목록 조회 SearchParams DTO
 *
 * <p>APP-DTO-003: SearchParams는 CommonSearchParams 포함 필수.
 *
 * <p>QDTO-001: Query DTO는 Record로 정의.
 *
 * <p>QDTO-005: Query DTO는 Domain 타입 의존 금지 -> String으로 전달, Factory에서 변환.
 *
 * @param searchParams 공통 검색 파라미터 (페이징, 정렬, 날짜 범위)
 * @param tenantId 테넌트 ID 필터 (null 허용)
 * @param serviceId 서비스 ID 필터 (null 허용)
 * @param statuses 상태 필터 목록 - ACTIVE, INACTIVE, SUSPENDED (null 또는 빈 목록 시 전체)
 * @author development-team
 * @since 1.0.0
 */
public record TenantServiceSearchParams(
        CommonSearchParams searchParams, String tenantId, Long serviceId, List<String> statuses) {

    /**
     * TenantServiceSearchParams 생성 (전체 파라미터)
     *
     * @param searchParams 공통 검색 파라미터
     * @param tenantId 테넌트 ID 필터
     * @param serviceId 서비스 ID 필터
     * @param statuses 상태 필터 목록
     * @return TenantServiceSearchParams 인스턴스
     */
    public static TenantServiceSearchParams of(
            CommonSearchParams searchParams,
            String tenantId,
            Long serviceId,
            List<String> statuses) {
        return new TenantServiceSearchParams(searchParams, tenantId, serviceId, statuses);
    }

    // ==================== Delegate Methods ====================

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

    public boolean hasTenantId() {
        return tenantId != null && !tenantId.isBlank();
    }

    public boolean hasServiceId() {
        return serviceId != null;
    }

    public boolean hasStatuses() {
        return statuses != null && !statuses.isEmpty();
    }
}
