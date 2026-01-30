package com.ryuqq.authhub.domain.userrole.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import java.util.Map;

/**
 * RoleInUseException - 역할이 사용 중일 때 발생하는 예외
 *
 * <p>사용자에게 할당된 역할을 삭제하려 할 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class RoleInUseException extends DomainException {

    public RoleInUseException(RoleId roleId) {
        super(UserRoleErrorCode.ROLE_IN_USE, Map.of("roleId", roleId.value()));
    }

    public RoleInUseException(Long roleId) {
        super(UserRoleErrorCode.ROLE_IN_USE, Map.of("roleId", roleId));
    }
}
