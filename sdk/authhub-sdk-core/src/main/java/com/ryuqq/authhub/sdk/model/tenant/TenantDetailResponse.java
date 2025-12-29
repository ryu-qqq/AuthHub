package com.ryuqq.authhub.sdk.model.tenant;

import java.time.Instant;
import java.util.List;

/**
 * 테넌트 상세 조회 응답 (Admin용).
 *
 * @param tenantId 테넌트 ID
 * @param name 테넌트 이름
 * @param status 테넌트 상태 (ACTIVE, INACTIVE)
 * @param organizations 소속 조직 목록 (최근 N개)
 * @param organizationCount 테넌트에 소속된 전체 조직 수
 * @param createdAt 생성 일시
 * @param updatedAt 수정 일시
 */
public record TenantDetailResponse(
        String tenantId,
        String name,
        String status,
        List<TenantOrganizationSummaryResponse> organizations,
        int organizationCount,
        Instant createdAt,
        Instant updatedAt) {}
