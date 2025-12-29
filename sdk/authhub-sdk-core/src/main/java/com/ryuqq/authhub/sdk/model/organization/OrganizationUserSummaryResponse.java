package com.ryuqq.authhub.sdk.model.organization;

import java.time.Instant;

/**
 * 조직 상세 조회 시 포함되는 사용자 요약 정보.
 *
 * @param userId 사용자 ID
 * @param email 사용자 이메일
 * @param createdAt 소속 일시
 */
public record OrganizationUserSummaryResponse(String userId, String email, Instant createdAt) {}
