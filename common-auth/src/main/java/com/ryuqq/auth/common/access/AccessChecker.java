package com.ryuqq.auth.common.access;

/**
 * AccessChecker - 리소스 접근 권한 검사 인터페이스
 *
 * <p>Spring Security @PreAuthorize에서 SpEL 표현식으로 사용되는 권한 검사 계약입니다.
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * // Controller에서 사용
 * @PreAuthorize("@access.authenticated()")
 * public void listItems() { ... }
 *
 * @PreAuthorize("@access.hasPermission('user:read')")
 * public void getUser() { ... }
 *
 * @PreAuthorize("@access.superAdmin() or @access.hasPermission('user:delete')")
 * public void deleteUser() { ... }
 *
 * @PreAuthorize("@access.sameTenant(#tenantId)")
 * public void getTenantData(@PathVariable String tenantId) { ... }
 * }</pre>
 *
 * <p><strong>구현 가이드:</strong>
 *
 * <ul>
 *   <li>@Component("access")로 빈 등록
 *   <li>UserContextHolder에서 현재 컨텍스트 조회
 *   <li>도메인별 확장 메서드는 서비스에서 추가 구현
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see BaseAccessChecker
 */
public interface AccessChecker {

    // ===== 인증 검사 =====

    /**
     * 현재 사용자가 인증되었는지 확인
     *
     * @return 인증되었으면 true
     */
    boolean authenticated();

    // ===== 역할 검사 =====

    /**
     * 현재 사용자가 SUPER_ADMIN인지 확인
     *
     * @return SUPER_ADMIN이면 true
     */
    boolean superAdmin();

    /**
     * 현재 사용자가 관리자 역할인지 확인 (SUPER_ADMIN, TENANT_ADMIN, ORG_ADMIN)
     *
     * @return 관리자 역할이면 true
     */
    boolean admin();

    /**
     * 특정 역할 보유 여부 확인
     *
     * @param role 확인할 역할
     * @return 역할이 있으면 true
     */
    boolean hasRole(String role);

    /**
     * 여러 역할 중 하나라도 보유 여부 확인
     *
     * @param roles 확인할 역할들
     * @return 하나라도 있으면 true
     */
    boolean hasAnyRole(String... roles);

    // ===== 권한 검사 =====

    /**
     * 특정 권한 보유 여부 확인
     *
     * <p>SUPER_ADMIN은 모든 권한을 가집니다.
     *
     * @param permission 확인할 권한 (예: user:read, product:write)
     * @return 권한이 있으면 true
     */
    boolean hasPermission(String permission);

    /**
     * 여러 권한 중 하나라도 보유 여부 확인
     *
     * @param permissions 확인할 권한들
     * @return 하나라도 있으면 true
     */
    boolean hasAnyPermission(String... permissions);

    /**
     * 모든 권한 보유 여부 확인
     *
     * @param permissions 확인할 권한들
     * @return 모두 있으면 true
     */
    boolean hasAllPermissions(String... permissions);

    // ===== 리소스 격리 검사 =====

    /**
     * 현재 사용자가 해당 테넌트 소속인지 확인
     *
     * <p>SUPER_ADMIN은 모든 테넌트에 접근 가능합니다.
     *
     * @param tenantId 확인할 테넌트 ID
     * @return 해당 테넌트 소속이거나 SUPER_ADMIN이면 true
     */
    boolean sameTenant(String tenantId);

    /**
     * 현재 사용자가 해당 조직 소속인지 확인
     *
     * <p>SUPER_ADMIN과 TENANT_ADMIN은 자기 테넌트 내 모든 조직에 접근 가능합니다.
     *
     * @param organizationId 확인할 조직 ID
     * @return 해당 조직 소속이거나 상위 권한이면 true
     */
    boolean sameOrganization(String organizationId);

    /**
     * 현재 사용자가 요청한 userId 본인인지 확인
     *
     * @param userId 확인할 사용자 ID
     * @return 본인이면 true
     */
    boolean myself(String userId);

    /**
     * 본인이거나 특정 권한이 있는지 확인
     *
     * @param userId 확인할 사용자 ID
     * @param permission 필요한 권한
     * @return 본인이거나 권한이 있으면 true
     */
    boolean myselfOr(String userId, String permission);

    // ===== 서비스 계정 =====

    /**
     * 현재 요청이 서비스 계정에 의한 것인지 확인
     *
     * @return 서비스 계정이면 true
     */
    boolean serviceAccount();
}
