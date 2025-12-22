package com.ryuqq.auth.common.access;

import com.ryuqq.auth.common.constant.Roles;
import com.ryuqq.auth.common.context.UserContext;
import com.ryuqq.auth.common.context.UserContextHolder;
import com.ryuqq.auth.common.util.PermissionMatcher;
import java.util.Arrays;
import java.util.Objects;

/**
 * BaseAccessChecker - 권한 검사 기본 구현
 *
 * <p>AccessChecker 인터페이스의 기본 구현을 제공하는 추상 클래스입니다. 서비스에서 이 클래스를 상속하여 도메인별 확장 메서드를 추가할 수 있습니다.
 *
 * <p><strong>사용 방법:</strong>
 *
 * <pre>{@code
 * @Component("access")
 * public class ResourceAccessChecker extends BaseAccessChecker {
 *
 *     // 도메인별 확장 메서드
 *     public boolean canReadFile() {
 *         return hasPermission("file:read");
 *     }
 *
 *     public boolean canWriteFile() {
 *         return hasPermission("file:write");
 *     }
 * }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public abstract class BaseAccessChecker implements AccessChecker {

    // ===== 인증 검사 =====

    @Override
    public boolean authenticated() {
        return getUserContext().isAuthenticated();
    }

    // ===== 역할 검사 =====

    @Override
    public boolean superAdmin() {
        return hasRole(Roles.SUPER_ADMIN);
    }

    @Override
    public boolean admin() {
        UserContext context = getUserContext();
        return context.hasRole(Roles.SUPER_ADMIN)
                || context.hasRole(Roles.TENANT_ADMIN)
                || context.hasRole(Roles.ORG_ADMIN);
    }

    @Override
    public boolean hasRole(String role) {
        return getUserContext().hasRole(role);
    }

    @Override
    public boolean hasAnyRole(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }
        UserContext context = getUserContext();
        return Arrays.stream(roles).anyMatch(context::hasRole);
    }

    // ===== 권한 검사 =====

    @Override
    public boolean hasPermission(String permission) {
        if (superAdmin()) {
            return true;
        }
        return PermissionMatcher.hasPermission(getUserContext(), permission);
    }

    @Override
    public boolean hasAnyPermission(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        if (superAdmin()) {
            return true;
        }
        UserContext context = getUserContext();
        return Arrays.stream(permissions)
                .anyMatch(p -> PermissionMatcher.hasPermission(context, p));
    }

    @Override
    public boolean hasAllPermissions(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return true;
        }
        if (superAdmin()) {
            return true;
        }
        UserContext context = getUserContext();
        return Arrays.stream(permissions)
                .allMatch(p -> PermissionMatcher.hasPermission(context, p));
    }

    // ===== 리소스 격리 검사 =====

    @Override
    public boolean sameTenant(String tenantId) {
        if (tenantId == null) {
            return false;
        }
        if (superAdmin()) {
            return true;
        }
        UserContext context = getUserContext();
        return Objects.equals(context.getTenantId(), tenantId);
    }

    @Override
    public boolean sameOrganization(String organizationId) {
        if (organizationId == null) {
            return false;
        }
        UserContext context = getUserContext();
        if (context.hasRole(Roles.SUPER_ADMIN) || context.hasRole(Roles.TENANT_ADMIN)) {
            return true;
        }
        return Objects.equals(context.getOrganizationId(), organizationId);
    }

    @Override
    public boolean myself(String userId) {
        if (userId == null) {
            return false;
        }
        UserContext context = getUserContext();
        return Objects.equals(context.getUserId(), userId);
    }

    @Override
    public boolean myselfOr(String userId, String permission) {
        return myself(userId) || hasPermission(permission);
    }

    // ===== 서비스 계정 =====

    @Override
    public boolean serviceAccount() {
        return getUserContext().isServiceAccount();
    }

    // ===== 헬퍼 메서드 =====

    /**
     * 현재 스레드의 UserContext를 반환합니다.
     *
     * <p>서브클래스에서 확장 메서드 구현 시 사용합니다.
     *
     * @return UserContext (미인증 시 anonymous 컨텍스트)
     */
    protected UserContext getUserContext() {
        return UserContextHolder.getContext();
    }

    /**
     * 현재 사용자 ID를 반환합니다.
     *
     * @return 사용자 ID (미인증 시 null)
     */
    protected String getCurrentUserId() {
        return getUserContext().getUserId();
    }

    /**
     * 현재 테넌트 ID를 반환합니다.
     *
     * @return 테넌트 ID (없으면 null)
     */
    protected String getCurrentTenantId() {
        return getUserContext().getTenantId();
    }

    /**
     * 현재 조직 ID를 반환합니다.
     *
     * @return 조직 ID (없으면 null)
     */
    protected String getCurrentOrganizationId() {
        return getUserContext().getOrganizationId();
    }
}
