package com.ryuqq.auth.common.util;

import com.ryuqq.auth.common.constant.Permissions;
import com.ryuqq.auth.common.context.UserContext;
import java.util.Collection;
import java.util.Set;

/**
 * PermissionMatcher - 권한 매칭 유틸리티
 *
 * <p>사용자의 권한을 검증하고 매칭하는 유틸리티 클래스입니다. 와일드카드 패턴을 지원합니다.
 *
 * <p><strong>권한 형식:</strong>
 *
 * <ul>
 *   <li>domain:action - 특정 도메인의 특정 액션 (예: user:read)
 *   <li>domain:* - 특정 도메인의 모든 액션
 *   <li>*:action - 모든 도메인의 특정 액션
 *   <li>*:* - 모든 권한 (Super Admin)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionMatcher {

    private static final String WILDCARD = "*";
    private static final String SEPARATOR = ":";

    private PermissionMatcher() {
        throw new AssertionError("Utility class - cannot instantiate");
    }

    /**
     * 사용자가 특정 권한을 가지고 있는지 확인
     *
     * @param userContext 사용자 컨텍스트
     * @param requiredPermission 필요한 권한
     * @return 권한 보유 여부
     */
    public static boolean hasPermission(UserContext userContext, String requiredPermission) {
        if (userContext == null || requiredPermission == null) {
            return false;
        }
        return hasPermission(userContext.getPermissions(), requiredPermission);
    }

    /**
     * 권한 집합에서 특정 권한 보유 여부 확인
     *
     * @param userPermissions 사용자 권한 집합
     * @param requiredPermission 필요한 권한
     * @return 권한 보유 여부
     */
    public static boolean hasPermission(Set<String> userPermissions, String requiredPermission) {
        if (userPermissions == null || userPermissions.isEmpty() || requiredPermission == null) {
            return false;
        }

        if (userPermissions.contains(Permissions.ALL)) {
            return true;
        }

        if (userPermissions.contains(requiredPermission)) {
            return true;
        }

        String requiredDomain = Permissions.extractDomain(requiredPermission);
        String requiredAction = Permissions.extractAction(requiredPermission);

        for (String userPerm : userPermissions) {
            if (matchesWithWildcard(userPerm, requiredDomain, requiredAction)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 사용자가 모든 필요 권한을 가지고 있는지 확인 (AND 조건)
     *
     * @param userContext 사용자 컨텍스트
     * @param requiredPermissions 필요한 권한 목록
     * @return 모든 권한 보유 여부
     */
    public static boolean hasAllPermissions(
            UserContext userContext, Collection<String> requiredPermissions) {
        if (userContext == null || requiredPermissions == null) {
            return false;
        }

        for (String permission : requiredPermissions) {
            if (!hasPermission(userContext, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 사용자가 필요 권한 중 하나라도 가지고 있는지 확인 (OR 조건)
     *
     * @param userContext 사용자 컨텍스트
     * @param requiredPermissions 필요한 권한 목록
     * @return 하나 이상의 권한 보유 여부
     */
    public static boolean hasAnyPermission(
            UserContext userContext, Collection<String> requiredPermissions) {
        if (userContext == null || requiredPermissions == null || requiredPermissions.isEmpty()) {
            return false;
        }

        for (String permission : requiredPermissions) {
            if (hasPermission(userContext, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 사용자가 특정 도메인에 대한 권한을 가지고 있는지 확인
     *
     * @param userContext 사용자 컨텍스트
     * @param domain 도메인 (예: "user", "role", "organization")
     * @return 도메인에 대한 권한 보유 여부
     */
    public static boolean hasDomainPermission(UserContext userContext, String domain) {
        if (userContext == null || domain == null) {
            return false;
        }

        Set<String> permissions = userContext.getPermissions();
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        if (permissions.contains(Permissions.ALL)) {
            return true;
        }

        for (String perm : permissions) {
            String permDomain = Permissions.extractDomain(perm);
            if (WILDCARD.equals(permDomain) || domain.equals(permDomain)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 사용자가 특정 액션에 대한 권한을 가지고 있는지 확인
     *
     * @param userContext 사용자 컨텍스트
     * @param action 액션 (예: "read", "write", "delete")
     * @return 액션에 대한 권한 보유 여부
     */
    public static boolean hasActionPermission(UserContext userContext, String action) {
        if (userContext == null || action == null) {
            return false;
        }

        Set<String> permissions = userContext.getPermissions();
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        if (permissions.contains(Permissions.ALL)) {
            return true;
        }

        for (String perm : permissions) {
            String permAction = Permissions.extractAction(perm);
            if (WILDCARD.equals(permAction) || action.equals(permAction)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesWithWildcard(
            String userPermission, String requiredDomain, String requiredAction) {
        if (userPermission == null || !userPermission.contains(SEPARATOR)) {
            return false;
        }

        String userDomain = Permissions.extractDomain(userPermission);
        String userAction = Permissions.extractAction(userPermission);

        boolean domainMatches = WILDCARD.equals(userDomain) || userDomain.equals(requiredDomain);
        boolean actionMatches = WILDCARD.equals(userAction) || userAction.equals(requiredAction);

        return domainMatches && actionMatches;
    }
}
