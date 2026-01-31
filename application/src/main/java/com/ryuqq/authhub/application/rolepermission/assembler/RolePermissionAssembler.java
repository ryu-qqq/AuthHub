package com.ryuqq.authhub.application.rolepermission.assembler;

import com.ryuqq.authhub.application.rolepermission.dto.response.RolePermissionPageResult;
import com.ryuqq.authhub.application.rolepermission.dto.response.RolePermissionResult;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RolePermissionAssembler - 역할-권한 관계 Assembler
 *
 * <p>Domain 객체를 Response DTO로 변환하는 Assembler입니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>RolePermission → RolePermissionResult 변환
 *   <li>List<RolePermission> → RolePermissionPageResult 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionAssembler {

    /**
     * RolePermission을 RolePermissionResult로 변환
     *
     * @param rolePermission 역할-권한 관계 Domain
     * @return RolePermissionResult
     */
    public RolePermissionResult toResult(RolePermission rolePermission) {
        return new RolePermissionResult(
                rolePermission.rolePermissionIdValue(),
                rolePermission.roleIdValue(),
                rolePermission.permissionIdValue(),
                rolePermission.createdAt());
    }

    /**
     * RolePermission 목록을 RolePermissionResult 목록으로 변환
     *
     * @param rolePermissions 역할-권한 관계 목록
     * @return RolePermissionResult 목록
     */
    public List<RolePermissionResult> toResults(List<RolePermission> rolePermissions) {
        return rolePermissions.stream().map(this::toResult).toList();
    }

    /**
     * RolePermission 목록을 페이지 결과로 변환
     *
     * @param rolePermissions 역할-권한 관계 목록
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @param totalElements 전체 요소 개수
     * @return RolePermissionPageResult
     */
    public RolePermissionPageResult toPageResult(
            List<RolePermission> rolePermissions,
            int pageNumber,
            int pageSize,
            long totalElements) {
        List<RolePermissionResult> results = toResults(rolePermissions);
        return RolePermissionPageResult.of(results, pageNumber, pageSize, totalElements);
    }
}
