package com.ryuqq.authhub.application.rolepermission.port.in.command;

import com.ryuqq.authhub.application.rolepermission.dto.command.GrantRolePermissionCommand;

/**
 * GrantRolePermissionUseCase - 역할에 권한 부여 Use Case
 *
 * <p>역할에 하나 이상의 권한을 부여하는 비즈니스 유스케이스입니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>역할 존재 여부 검증 (RoleValidator)
 *   <li>권한 존재 여부 검증 (PermissionValidator)
 *   <li>중복 부여 검증
 *   <li>역할-권한 관계 생성 및 저장
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GrantRolePermissionUseCase {

    /**
     * 역할에 권한 부여
     *
     * @param command 권한 부여 Command (roleId, permissionIds)
     */
    void grant(GrantRolePermissionCommand command);
}
