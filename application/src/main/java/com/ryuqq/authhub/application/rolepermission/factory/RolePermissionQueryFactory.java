package com.ryuqq.authhub.application.rolepermission.factory;

import com.ryuqq.authhub.application.rolepermission.dto.query.RolePermissionSearchParams;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.query.criteria.RolePermissionSearchCriteria;
import com.ryuqq.authhub.domain.rolepermission.vo.RolePermissionSortKey;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RolePermissionQueryFactory - 역할-권한 관계 SearchParams → Criteria 변환 Factory
 *
 * <p>Application Layer의 SearchParams DTO를 Domain Layer의 Criteria로 변환합니다.
 *
 * <p>DOM-CRI-010: Criteria는 QueryContext 등 공통 VO 사용 권장.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>roleId (Long) → RoleId
 *   <li>roleIds (List&lt;Long&gt;) → List&lt;RoleId&gt;
 *   <li>permissionId (Long) → PermissionId
 *   <li>permissionIds (List&lt;Long&gt;) → List&lt;PermissionId&gt;
 *   <li>CommonSearchParams → QueryContext (toQueryContext 사용)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 필수
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (단순 변환만)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionQueryFactory {

    /**
     * RolePermissionSearchParams → RolePermissionSearchCriteria 변환
     *
     * <p>CommonSearchParams.toQueryContext()를 사용하여 QueryContext 생성.
     *
     * <p>파싱 로직은 각 도메인 객체가 담당합니다.
     *
     * @param params Application SearchParams DTO
     * @return Domain Criteria
     */
    public RolePermissionSearchCriteria toCriteria(RolePermissionSearchParams params) {
        // CommonSearchParams.toQueryContext()를 사용하여 QueryContext 생성
        QueryContext<RolePermissionSortKey> queryContext =
                params.searchParams().toQueryContext(RolePermissionSortKey.class);

        return new RolePermissionSearchCriteria(
                toRoleId(params.roleId()),
                toRoleIds(params.roleIds()),
                toPermissionId(params.permissionId()),
                toPermissionIds(params.permissionIds()),
                queryContext);
    }

    // ==================== Private Helper Methods ====================

    /**
     * Long → RoleId 변환 (nullable)
     *
     * @param roleId 역할 ID (primitive)
     * @return RoleId 또는 null
     */
    private RoleId toRoleId(Long roleId) {
        return roleId != null ? RoleId.of(roleId) : null;
    }

    /**
     * List&lt;Long&gt; → List&lt;RoleId&gt; 변환 (nullable)
     *
     * @param roleIds 역할 ID 목록 (primitive)
     * @return RoleId 목록 또는 null
     */
    private List<RoleId> toRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return null;
        }
        return roleIds.stream().map(RoleId::of).toList();
    }

    /**
     * Long → PermissionId 변환 (nullable)
     *
     * @param permissionId 권한 ID (primitive)
     * @return PermissionId 또는 null
     */
    private PermissionId toPermissionId(Long permissionId) {
        return permissionId != null ? PermissionId.of(permissionId) : null;
    }

    /**
     * List&lt;Long&gt; → List&lt;PermissionId&gt; 변환 (nullable)
     *
     * @param permissionIds 권한 ID 목록 (primitive)
     * @return PermissionId 목록 또는 null
     */
    private List<PermissionId> toPermissionIds(List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return null;
        }
        return permissionIds.stream().map(PermissionId::of).toList();
    }
}
