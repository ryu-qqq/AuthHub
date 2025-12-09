package com.ryuqq.authhub.domain.role.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.vo.RoleName;

import java.util.Map;
import java.util.UUID;

/**
 * DuplicateRoleNameException - 역할 이름이 중복될 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicateRoleNameException extends DomainException {

    public DuplicateRoleNameException(String roleName) {
        super(RoleErrorCode.DUPLICATE_ROLE_NAME, Map.of("roleName", roleName));
    }

    public DuplicateRoleNameException(RoleName roleName) {
        this(roleName.value());
    }

    public DuplicateRoleNameException(UUID tenantId, String roleName) {
        super(RoleErrorCode.DUPLICATE_ROLE_NAME, Map.of("tenantId", tenantId, "roleName", roleName));
    }
}
