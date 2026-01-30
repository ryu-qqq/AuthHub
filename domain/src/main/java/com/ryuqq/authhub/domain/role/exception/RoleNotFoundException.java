package com.ryuqq.authhub.domain.role.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import java.util.Map;

/**
 * RoleNotFoundException - 역할을 찾을 수 없을 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class RoleNotFoundException extends DomainException {

    public RoleNotFoundException(Long roleId) {
        super(RoleErrorCode.ROLE_NOT_FOUND, Map.of("roleId", roleId));
    }

    public RoleNotFoundException(RoleId roleId) {
        this(roleId.value());
    }

    public RoleNotFoundException(RoleName roleName) {
        super(RoleErrorCode.ROLE_NOT_FOUND, Map.of("roleName", roleName.value()));
    }

    public RoleNotFoundException(String roleName) {
        super(RoleErrorCode.ROLE_NOT_FOUND, Map.of("roleName", roleName));
    }
}
