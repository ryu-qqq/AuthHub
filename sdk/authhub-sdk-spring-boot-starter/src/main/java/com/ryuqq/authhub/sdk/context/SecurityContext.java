package com.ryuqq.authhub.sdk.context;

import java.util.Set;

/**
 * SecurityContext - 보안 컨텍스트 인터페이스
 *
 * <p>권한 검사에 필요한 최소 계약을 정의합니다. 각 프로젝트는 자체 UserContext를 유지하면서 이 인터페이스만 구현하면 common-auth의 권한 검사 기능을
 * 사용할 수 있습니다.
 *
 * <p><strong>사용 목적:</strong>
 *
 * <ul>
 *   <li>프로젝트별 UserContext 독립성 유지
 *   <li>공통 권한 검사 로직 재사용
 *   <li>PermissionMatcher, AccessChecker와의 연동
 * </ul>
 *
 * <p><strong>구현 예시:</strong>
 *
 * <pre>{@code
 * // 프로젝트별 UserContext가 SecurityContext 구현
 * public record UserContext(
 *     Tenant tenant,
 *     Organization organization,
 *     List<String> permissions
 * ) implements SecurityContext {
 *
 *     @Override
 *     public Set<String> getPermissions() {
 *         return Set.copyOf(permissions);
 *     }
 *
 *     @Override
 *     public String getUserId() {
 *         return tenant.userId();
 *     }
 *     // ... 나머지 구현
 * }
 * }</pre>
 *
 * @author development-team
 * @since 1.1.0
 * @see UserContext
 * @see UserContextHolder
 */
public interface SecurityContext {

    /**
     * 사용자 권한 목록 조회
     *
     * @return 권한 집합 (예: user:read, file:write)
     */
    Set<String> getPermissions();

    /**
     * 사용자 역할 목록 조회
     *
     * @return 역할 집합 (예: SUPER_ADMIN, TENANT_ADMIN)
     */
    Set<String> getRoles();

    /**
     * 사용자 ID 조회
     *
     * @return 사용자 ID (미인증 시 null)
     */
    String getUserId();

    /**
     * 테넌트 ID 조회
     *
     * @return 테넌트 ID (없으면 null)
     */
    String getTenantId();

    /**
     * 조직 ID 조회
     *
     * @return 조직 ID (없으면 null)
     */
    String getOrganizationId();

    /**
     * 서비스 계정 여부 확인
     *
     * @return 서비스 계정이면 true
     */
    boolean isServiceAccount();

    /**
     * 인증 여부 확인
     *
     * @return 인증되었으면 true
     */
    boolean isAuthenticated();

    /**
     * 특정 역할 보유 여부 확인
     *
     * @param role 역할 이름
     * @return 역할 보유 여부
     */
    default boolean hasRole(String role) {
        Set<String> roles = getRoles();
        return roles != null && roles.contains(role);
    }

    /**
     * 특정 권한 보유 여부 확인 (기본 구현)
     *
     * <p><strong>지원 범위:</strong>
     *
     * <ul>
     *   <li>정확한 일치: "file:read" → 보유 권한에 "file:read" 있으면 true
     *   <li>전역 와일드카드: "*:*" → 모든 권한 허용
     * </ul>
     *
     * <p><strong>미지원 패턴:</strong>
     *
     * <ul>
     *   <li>도메인 와일드카드: "file:*" (PermissionMatcher 필요)
     *   <li>액션 와일드카드: "*:read" (PermissionMatcher 필요)
     *   <li>복합 와일드카드: "file-*:read" (PermissionMatcher 필요)
     * </ul>
     *
     * <p>고급 와일드카드 패턴이 필요한 경우 {@link com.ryuqq.authhub.sdk.util.PermissionMatcher}를 사용하세요.
     *
     * @param permission 권한 이름 (예: "file:read", "user:write")
     * @return 권한 보유 여부
     * @see com.ryuqq.authhub.sdk.util.PermissionMatcher#hasPermission(SecurityContext, String)
     */
    default boolean hasPermission(String permission) {
        Set<String> permissions = getPermissions();
        return permissions != null
                && (permissions.contains(permission) || permissions.contains("*:*"));
    }
}
