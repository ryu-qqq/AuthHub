package com.ryuqq.authhub.application.rolepermission.port.in.command;

import com.ryuqq.authhub.application.rolepermission.dto.command.RevokeRolePermissionCommand;

/**
 * RevokeRolePermissionUseCase - 역할에서 권한 제거 Use Case
 *
 * <p>역할에서 하나 이상의 권한을 제거하는 비즈니스 유스케이스입니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>역할 존재 여부 검증 (RoleValidator)
 *   <li>역할-권한 관계 존재 여부 확인
 *   <li>역할-권한 관계 삭제
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RevokeRolePermissionUseCase {

    /**
     * 역할에서 권한 제거
     *
     * @param command 권한 제거 Command (roleId, permissionIds)
     */
    void revoke(RevokeRolePermissionCommand command);
}
