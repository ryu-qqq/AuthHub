package com.ryuqq.authhub.application.role.assembler;

import com.ryuqq.authhub.application.role.dto.response.RolePermissionResponse;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RolePermissionAssembler - 역할 권한 Domain/DTO 변환기
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Domain → Response DTO 변환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionAssembler {

    /**
     * RolePermission 도메인 객체를 Response DTO로 변환합니다.
     *
     * @param rolePermission 역할 권한 도메인 객체
     * @return Response DTO
     */
    public RolePermissionResponse toResponse(RolePermission rolePermission) {
        return new RolePermissionResponse(
                rolePermission.roleIdValue(),
                rolePermission.permissionIdValue(),
                rolePermission.getGrantedAt());
    }

    /**
     * RolePermission 도메인 객체 목록을 Response DTO 목록으로 변환합니다.
     *
     * @param rolePermissions 역할 권한 도메인 객체 목록
     * @return Response DTO 목록
     */
    public List<RolePermissionResponse> toResponseList(List<RolePermission> rolePermissions) {
        return rolePermissions.stream().map(this::toResponse).toList();
    }
}
