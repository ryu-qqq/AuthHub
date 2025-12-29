package com.ryuqq.authhub.sdk.model.user;

import java.time.Instant;
import java.util.List;

/**
 * 사용자 상세 조회 응답 (Admin용).
 *
 * @param userId 사용자 ID
 * @param tenantId 테넌트 ID
 * @param tenantName 테넌트 이름
 * @param organizationId 조직 ID
 * @param organizationName 조직 이름
 * @param identifier 사용자 식별자
 * @param phoneNumber 핸드폰 번호
 * @param status 사용자 상태
 * @param roles 할당된 역할 목록
 * @param createdAt 생성 일시
 * @param updatedAt 수정 일시
 */
public record UserDetailResponse(
        String userId,
        String tenantId,
        String tenantName,
        String organizationId,
        String organizationName,
        String identifier,
        String phoneNumber,
        String status,
        List<UserRoleSummaryResponse> roles,
        Instant createdAt,
        Instant updatedAt) {}
