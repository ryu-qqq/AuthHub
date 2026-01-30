package com.ryuqq.authhub.application.rolepermission.dto.command;

import java.util.List;

/**
 * RevokeRolePermissionCommand - 역할에서 권한 제거 Command
 *
 * <p>역할에서 하나 이상의 권한을 제거할 때 사용하는 Command입니다.
 *
 * <p><strong>다건 제거 지원:</strong>
 *
 * <ul>
 *   <li>하나의 역할에서 여러 권한을 한 번에 제거 가능
 *   <li>부여되지 않은 권한은 무시하거나 예외 발생 (정책에 따라)
 * </ul>
 *
 * @param roleId 역할 ID (필수)
 * @param permissionIds 제거할 권한 ID 목록 (필수, 1개 이상)
 * @author development-team
 * @since 1.0.0
 */
public record RevokeRolePermissionCommand(Long roleId, List<Long> permissionIds) {}
