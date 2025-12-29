package com.ryuqq.authhub.application.onboarding.dto.bundle;

import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.vo.UserRole;

/**
 * TenantOnboardingPersistBundle - 테넌트 온보딩 영속화 Bundle
 *
 * <p>테넌트 온보딩 시 영속화할 모든 도메인 객체를 묶는 Record입니다.
 *
 * <p><strong>포함 객체:</strong>
 *
 * <ul>
 *   <li>Tenant - 테넌트 Aggregate (ID 할당됨)
 *   <li>Organization - 조직 Aggregate (TenantId 포함)
 *   <li>User - 사용자 Aggregate (TenantId, OrganizationId 포함)
 *   <li>UserRole - 사용자 역할 (UserId, RoleId 포함)
 *   <li>temporaryPassword - 임시 비밀번호 (응답용)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입
 *   <li>모든 ID가 Factory에서 미리 생성됨
 *   <li>불변성 유지
 *   <li>비즈니스 로직 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @param tenant 테넌트 Aggregate (ID 할당됨)
 * @param organization 조직 Aggregate
 * @param user 사용자 Aggregate
 * @param userRole 사용자 역할
 * @param temporaryPassword 임시 비밀번호
 * @author development-team
 * @since 1.0.0
 */
public record TenantOnboardingPersistBundle(
        Tenant tenant,
        Organization organization,
        User user,
        UserRole userRole,
        String temporaryPassword) {

    /**
     * Bundle 생성
     *
     * @param tenant 테넌트
     * @param organization 조직
     * @param user 사용자
     * @param userRole 사용자 역할
     * @param temporaryPassword 임시 비밀번호
     * @return Bundle
     */
    public static TenantOnboardingPersistBundle of(
            Tenant tenant,
            Organization organization,
            User user,
            UserRole userRole,
            String temporaryPassword) {
        return new TenantOnboardingPersistBundle(
                tenant, organization, user, userRole, temporaryPassword);
    }
}
