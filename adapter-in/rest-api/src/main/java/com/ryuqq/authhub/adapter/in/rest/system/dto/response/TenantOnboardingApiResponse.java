package com.ryuqq.authhub.adapter.in.rest.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/**
 * TenantOnboardingApiResponse - 테넌트 온보딩 API 응답 DTO
 *
 * <p>입점 승인으로 생성된 Tenant, Organization, User 정보를 담습니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>public record 필수
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지
 * </ul>
 *
 * @param tenantId 생성된 테넌트 ID
 * @param organizationId 생성된 조직 ID
 * @param userId 생성된 사용자 ID
 * @param temporaryPassword 임시 비밀번호 (호출자가 이메일 발송 책임)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "테넌트 온보딩 응답")
public record TenantOnboardingApiResponse(
        @Schema(description = "생성된 테넌트 ID", example = "550e8400-e29b-41d4-a716-446655440000")
                UUID tenantId,
        @Schema(description = "생성된 조직 ID", example = "550e8400-e29b-41d4-a716-446655440001")
                UUID organizationId,
        @Schema(description = "생성된 사용자 ID", example = "550e8400-e29b-41d4-a716-446655440002")
                UUID userId,
        @Schema(description = "임시 비밀번호 (이메일 발송용)", example = "a1b2c3d4e5f6")
                String temporaryPassword) {

    /**
     * TenantOnboardingApiResponse를 생성합니다.
     *
     * @param tenantId 테넌트 ID
     * @param organizationId 조직 ID
     * @param userId 사용자 ID
     * @param temporaryPassword 임시 비밀번호
     * @return TenantOnboardingApiResponse 인스턴스
     */
    public static TenantOnboardingApiResponse of(
            UUID tenantId, UUID organizationId, UUID userId, String temporaryPassword) {
        return new TenantOnboardingApiResponse(tenantId, organizationId, userId, temporaryPassword);
    }
}
