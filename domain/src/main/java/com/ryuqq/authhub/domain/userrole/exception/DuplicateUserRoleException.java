package com.ryuqq.authhub.domain.userrole.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.util.Map;

/**
 * DuplicateUserRoleException - 중복된 사용자-역할 관계 예외
 *
 * <p>이미 할당된 역할을 다시 할당하려 할 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicateUserRoleException extends DomainException {

    public DuplicateUserRoleException(UserId userId, RoleId roleId) {
        super(
                UserRoleErrorCode.DUPLICATE_USER_ROLE,
                Map.of("userId", userId.value(), "roleId", roleId.value()));
    }

    public DuplicateUserRoleException(String userId, Long roleId) {
        super(UserRoleErrorCode.DUPLICATE_USER_ROLE, Map.of("userId", userId, "roleId", roleId));
    }
}
