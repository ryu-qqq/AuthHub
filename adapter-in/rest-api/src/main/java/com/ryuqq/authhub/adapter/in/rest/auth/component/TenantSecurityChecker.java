package com.ryuqq.authhub.adapter.in.rest.auth.component;

import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * 테넌트 권한 검증 Bean
 *
 * <p>@PreAuthorize에서 SpEL로 호출하여 테넌트 권한을 검증합니다.
 *
 * <p>사용 예시:
 *
 * <pre>{@code
 * @PreAuthorize("@tenantSecurityChecker.canAccess(#tenantId)")
 * public Tenant getTenant(Long tenantId) { ... }
 *
 * @PreAuthorize("@tenantSecurityChecker.canAccessOrg(#orgId)")
 * public Organization getOrg(Long orgId) { ... }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component("tenantSecurityChecker")
public class TenantSecurityChecker {

    /**
     * 현재 사용자가 해당 테넌트에 접근 가능한지 검증
     *
     * <p>접근 규칙:
     *
     * <ul>
     *   <li>SUPER_ADMIN: 모든 테넌트 접근 가능
     *   <li>그 외: 자신의 테넌트만 접근 가능
     * </ul>
     *
     * @param tenantId 접근 대상 테넌트 ID
     * @return 접근 가능 여부
     */
    public boolean canAccess(String tenantId) {
        SecurityContext context = SecurityContextHolder.getContext();

        if (!context.isAuthenticated()) {
            return false;
        }

        return context.isSuperAdmin() || Objects.equals(context.getTenantId(), tenantId);
    }

    /**
     * 테넌트 목록 조회 시 필터링할 tenantId 반환
     *
     * <p>반환 규칙:
     *
     * <ul>
     *   <li>SUPER_ADMIN: null 반환 (전체 조회 허용)
     *   <li>그 외: 자신의 tenantId 반환
     * </ul>
     *
     * @return 필터링할 tenantId (SUPER_ADMIN이면 null)
     */
    public String getFilterTenantId() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context.isSuperAdmin()) {
            return null;
        }

        return context.getTenantId();
    }

    /**
     * 현재 사용자가 자신의 테넌트 내 리소스에만 접근 가능한지 확인
     *
     * <p>SUPER_ADMIN은 항상 true 반환
     *
     * @return 테넌트 바운드 접근인 경우 true
     */
    public boolean isTenantBound() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.isAuthenticated() && !context.isSuperAdmin();
    }

    /**
     * 현재 사용자의 테넌트 ID 반환
     *
     * @return 현재 테넌트 ID
     */
    public String getCurrentTenantId() {
        return SecurityContextHolder.getCurrentTenantId();
    }

    /**
     * 현재 사용자의 사용자 ID 반환 (UUID 문자열 형식)
     *
     * @return 현재 사용자 ID
     */
    public String getCurrentUserId() {
        return SecurityContextHolder.getCurrentUserId();
    }
}
