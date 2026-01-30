package com.ryuqq.authhub.application.rolepermission.dto.command;

import java.util.List;

/**
 * GrantRolePermissionCommand - 역할에 권한 부여 Command
 *
 * <p>역할에 하나 이상의 권한을 부여할 때 사용하는 Command입니다.
 *
 * <p><strong>다건 부여 지원:</strong>
 *
 * <ul>
 *   <li>하나의 역할에 여러 권한을 한 번에 부여 가능
 *   <li>이미 부여된 권한은 무시하거나 예외 발생 (정책에 따라)
 * </ul>
 *
 * @param roleId 역할 ID (필수)
 * @param permissionIds 부여할 권한 ID 목록 (필수, 1개 이상)
 * @author development-team
 * @since 1.0.0
 */
public record GrantRolePermissionCommand(Long roleId, List<Long> permissionIds) {}
