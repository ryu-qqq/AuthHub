package com.ryuqq.authhub.application.onboarding.dto.command;

/**
 * TenantOnboardingCommand - 테넌트 온보딩 Command DTO
 *
 * <p>입점 승인 시 Tenant + Organization + User를 일괄 생성하기 위한 Command입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>public record 필수
 *   <li>Lombok 금지
 *   <li>Validation 어노테이션 금지 (REST API Layer 책임)
 *   <li>비즈니스 로직 금지
 * </ul>
 *
 * @param tenantName 테넌트(회사) 이름
 * @param masterEmail 마스터 관리자 이메일 (User identifier)
 * @param masterName 마스터 관리자 이름 (현재 미사용, 확장용)
 * @param defaultOrgName 기본 조직 이름
 * @author development-team
 * @since 1.0.0
 */
public record TenantOnboardingCommand(
        String tenantName, String masterEmail, String masterName, String defaultOrgName) {

    /**
     * TenantOnboardingCommand를 생성합니다.
     *
     * @param tenantName 테넌트(회사) 이름
     * @param masterEmail 마스터 관리자 이메일
     * @param masterName 마스터 관리자 이름
     * @param defaultOrgName 기본 조직 이름
     * @return TenantOnboardingCommand 인스턴스
     */
    public static TenantOnboardingCommand of(
            String tenantName, String masterEmail, String masterName, String defaultOrgName) {
        return new TenantOnboardingCommand(tenantName, masterEmail, masterName, defaultOrgName);
    }
}
