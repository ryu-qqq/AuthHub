package com.ryuqq.authhub.domain.role.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import java.util.Map;

/**
 * SystemRoleNotModifiableException - 시스템 역할 수정 시도 시 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class SystemRoleNotModifiableException extends DomainException {

    public SystemRoleNotModifiableException(String roleName) {
        super(RoleErrorCode.SYSTEM_ROLE_NOT_MODIFIABLE, Map.of("roleName", roleName));
    }

    public SystemRoleNotModifiableException(RoleName roleName) {
        this(roleName.value());
    }
}
