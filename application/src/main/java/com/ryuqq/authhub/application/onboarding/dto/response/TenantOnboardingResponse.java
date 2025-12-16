package com.ryuqq.authhub.application.onboarding.dto.response;

import java.util.UUID;

/**
 * TenantOnboardingResponse - 테넌트 온보딩 결과 DTO
 *
 * <p>입점 승인으로 생성된 Tenant, Organization, User 정보를 담습니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>public record 필수
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지 (REST API Layer 책임)
 *   <li>비즈니스 로직 금지
 * </ul>
 *
 * @param tenantId 생성된 테넌트 ID (UUID)
 * @param organizationId 생성된 조직 ID (UUID)
 * @param userId 생성된 사용자 ID (UUID)
 * @param temporaryPassword 임시 비밀번호 (호출자가 이메일 발송 책임)
 * @author development-team
 * @since 1.0.0
 */
public record TenantOnboardingResponse(
        UUID tenantId, UUID organizationId, UUID userId, String temporaryPassword) {

    /**
     * TenantOnboardingResponse를 생성합니다.
     *
     * @param tenantId 생성된 테넌트 ID
     * @param organizationId 생성된 조직 ID
     * @param userId 생성된 사용자 ID
     * @param temporaryPassword 임시 비밀번호
     * @return TenantOnboardingResponse 인스턴스
     */
    public static TenantOnboardingResponse of(
            UUID tenantId, UUID organizationId, UUID userId, String temporaryPassword) {
        return new TenantOnboardingResponse(tenantId, organizationId, userId, temporaryPassword);
    }
}
