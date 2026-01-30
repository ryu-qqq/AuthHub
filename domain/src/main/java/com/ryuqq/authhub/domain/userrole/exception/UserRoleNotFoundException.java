package com.ryuqq.authhub.domain.userrole.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.util.Map;

/**
 * UserRoleNotFoundException - 사용자-역할 관계를 찾을 수 없을 때 발생하는 예외
 *
 * <p>존재하지 않는 사용자-역할 관계를 조회하거나 삭제하려 할 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class UserRoleNotFoundException extends DomainException {

    public UserRoleNotFoundException(UserId userId, RoleId roleId) {
        super(
                UserRoleErrorCode.USER_ROLE_NOT_FOUND,
                Map.of("userId", userId.value(), "roleId", roleId.value()));
    }

    public UserRoleNotFoundException(String userId, Long roleId) {
        super(UserRoleErrorCode.USER_ROLE_NOT_FOUND, Map.of("userId", userId, "roleId", roleId));
    }
}
