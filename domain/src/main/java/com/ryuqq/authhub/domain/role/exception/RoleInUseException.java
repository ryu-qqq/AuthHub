package com.ryuqq.authhub.domain.role.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import java.util.Map;

/**
 * RoleInUseException - 사용 중인 역할 삭제 시도 시 발생하는 예외
 *
 * <p>User에게 할당된 역할은 삭제할 수 없습니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class RoleInUseException extends DomainException {

    public RoleInUseException(Long roleId) {
        super(RoleErrorCode.ROLE_IN_USE, Map.of("roleId", roleId));
    }

    public RoleInUseException(RoleId roleId) {
        this(roleId.value());
    }

    public RoleInUseException(RoleName roleName) {
        super(RoleErrorCode.ROLE_IN_USE, Map.of("roleName", roleName.value()));
    }
}
