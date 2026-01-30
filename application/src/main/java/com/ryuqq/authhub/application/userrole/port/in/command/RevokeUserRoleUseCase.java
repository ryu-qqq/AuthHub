package com.ryuqq.authhub.application.userrole.port.in.command;

import com.ryuqq.authhub.application.userrole.dto.command.RevokeUserRoleCommand;

/**
 * RevokeUserRoleUseCase - 사용자로부터 역할 철회 Use Case
 *
 * <p>사용자로부터 하나 이상의 역할을 철회하는 비즈니스 유스케이스입니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>사용자 존재 여부 검증 (UserValidator)
 *   <li>할당된 역할만 삭제 (할당되지 않은 역할은 무시)
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RevokeUserRoleUseCase {

    /**
     * 사용자로부터 역할 철회
     *
     * @param command 역할 철회 Command (userId, roleIds)
     */
    void revoke(RevokeUserRoleCommand command);
}
