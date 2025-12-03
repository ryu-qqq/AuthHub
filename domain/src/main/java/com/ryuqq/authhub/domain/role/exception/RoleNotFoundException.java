package com.ryuqq.authhub.domain.role.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * RoleNotFoundException - 역할을 찾을 수 없는 예외
 *
 * <p>요청한 역할이 존재하지 않을 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class RoleNotFoundException extends DomainException {

    public RoleNotFoundException(Long roleId) {
        super(RoleErrorCode.ROLE_NOT_FOUND, Map.of("roleId", roleId));
    }

    public RoleNotFoundException(String roleName) {
        super(RoleErrorCode.ROLE_NOT_FOUND, Map.of("roleName", roleName));
    }
}
