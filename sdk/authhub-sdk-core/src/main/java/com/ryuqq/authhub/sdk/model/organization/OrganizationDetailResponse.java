package com.ryuqq.authhub.sdk.model.organization;

import java.time.Instant;
import java.util.List;

/**
 * 조직 상세 조회 응답 (Admin용).
 *
 * @param organizationId 조직 ID
 * @param tenantId 테넌트 ID
 * @param tenantName 테넌트 이름
 * @param name 조직 이름
 * @param status 조직 상태 (ACTIVE, INACTIVE, SUSPENDED)
 * @param users 소속 사용자 목록 (최근 N명)
 * @param userCount 조직에 소속된 전체 사용자 수
 * @param createdAt 생성 일시
 * @param updatedAt 수정 일시
 */
public record OrganizationDetailResponse(
        String organizationId,
        String tenantId,
        String tenantName,
        String name,
        String status,
        List<OrganizationUserSummaryResponse> users,
        int userCount,
        Instant createdAt,
        Instant updatedAt) {}
