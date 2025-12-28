package com.ryuqq.authhub.application.onboarding.dto.command;

/**
 * TenantOnboardingCommand - 테넌트 온보딩 Command DTO
 *
 * <p>입점 승인 시 Tenant, Organization, User를 일괄 생성하기 위한 Command입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param tenantName 테넌트(회사) 이름 (필수)
 * @param organizationName 기본 조직 이름 (필수)
 * @param masterEmail 마스터 관리자 이메일 (필수)
 * @author development-team
 * @since 1.0.0
 */
public record TenantOnboardingCommand(
        String tenantName, String organizationName, String masterEmail) {}
