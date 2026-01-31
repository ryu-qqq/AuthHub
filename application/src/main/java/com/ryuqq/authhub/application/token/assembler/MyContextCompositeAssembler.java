package com.ryuqq.authhub.application.token.assembler;

import com.ryuqq.authhub.application.token.dto.composite.MyContextComposite;
import com.ryuqq.authhub.application.token.dto.composite.UserContextComposite;
import com.ryuqq.authhub.application.token.dto.response.MyContextResponse;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * MyContextCompositeAssembler - MyContextComposite를 MyContextResponse로 변환하는 Assembler
 *
 * <p>Composite 패턴으로 조회된 데이터를 Response DTO로 조립합니다.
 *
 * <p><strong>변환 규칙:</strong>
 *
 * <ul>
 *   <li>UserContextComposite → userId, email, name, tenantId, tenantName, organizationId,
 *       organizationName
 *   <li>RolesAndPermissionsComposite → roles (RoleInfo 리스트), permissions (String 리스트)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class MyContextCompositeAssembler {

    /**
     * MyContextComposite를 MyContextResponse로 변환
     *
     * @param composite 조회된 Composite 데이터
     * @return 응답 DTO
     */
    public MyContextResponse toResponse(MyContextComposite composite) {
        UserContextComposite userContext = composite.userContext();
        RolesAndPermissionsComposite rolesAndPermissions = composite.rolesAndPermissions();

        List<MyContextResponse.RoleInfo> roles = toRoleInfoList(rolesAndPermissions);
        List<String> permissions = toPermissionList(rolesAndPermissions);

        return new MyContextResponse(
                userContext.userId(),
                userContext.email(),
                userContext.name(),
                userContext.tenantId(),
                userContext.tenantName(),
                userContext.organizationId(),
                userContext.organizationName(),
                roles,
                permissions);
    }

    private List<MyContextResponse.RoleInfo> toRoleInfoList(
            RolesAndPermissionsComposite rolesAndPermissions) {
        return rolesAndPermissions.roleNames().stream()
                .map(roleName -> new MyContextResponse.RoleInfo(null, roleName))
                .toList();
    }

    private List<String> toPermissionList(RolesAndPermissionsComposite rolesAndPermissions) {
        return List.copyOf(rolesAndPermissions.permissionKeys());
    }
}
