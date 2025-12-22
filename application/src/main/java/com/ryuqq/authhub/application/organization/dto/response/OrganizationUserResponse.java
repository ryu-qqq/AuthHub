package com.ryuqq.authhub.application.organization.dto.response;

import java.time.Instant;

/**
 * OrganizationUserResponse - 조직 소속 사용자 응답 DTO
 *
 * <p>조직별 사용자 목록 조회 시 반환되는 Application Layer 응답 객체입니다.
 *
 * @param userId 사용자 ID (문자열)
 * @param email 사용자 이메일
 * @param tenantId 테넌트 ID (문자열)
 * @param createdAt 사용자 생성 일시
 * @author development-team
 * @since 1.0.0
 */
public record OrganizationUserResponse(
        String userId, String email, String tenantId, Instant createdAt) {}
