package com.ryuqq.authhub.application.userrole.port.in.command;

import com.ryuqq.authhub.application.userrole.dto.command.AssignUserRoleCommand;

/**
 * AssignUserRoleUseCase - 사용자에게 역할 할당 Use Case
 *
 * <p>사용자에게 하나 이상의 역할을 할당하는 비즈니스 유스케이스입니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>사용자 존재 여부 검증 (UserValidator)
 *   <li>역할 존재 여부 검증 (RoleValidator)
 *   <li>중복 할당 필터링
 *   <li>사용자-역할 관계 생성 및 저장
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface AssignUserRoleUseCase {

    /**
     * 사용자에게 역할 할당
     *
     * @param command 역할 할당 Command (userId, roleIds)
     */
    void assign(AssignUserRoleCommand command);
}
