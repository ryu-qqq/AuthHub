package com.ryuqq.authhub.application.onboarding.dto.response;

import java.util.UUID;

/**
 * TenantOnboardingResponse - 테넌트 온보딩 응답 DTO
 *
 * <p>테넌트 온보딩 완료 후 생성된 리소스 정보를 반환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지
 * </ul>
 *
 * @param tenantId 생성된 테넌트 ID
 * @param organizationId 생성된 조직 ID
 * @param userId 생성된 마스터 사용자 ID
 * @param temporaryPassword 임시 비밀번호 (호출자가 이메일 발송 필요)
 * @author development-team
 * @since 1.0.0
 */
public record TenantOnboardingResponse(
        UUID tenantId, UUID organizationId, UUID userId, String temporaryPassword) {}
