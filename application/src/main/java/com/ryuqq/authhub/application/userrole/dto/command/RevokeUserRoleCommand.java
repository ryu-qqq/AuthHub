package com.ryuqq.authhub.application.userrole.dto.command;

import java.util.List;

/**
 * RevokeUserRoleCommand - 사용자로부터 역할 철회 Command
 *
 * <p>사용자로부터 하나 이상의 역할을 철회할 때 사용하는 Command입니다.
 *
 * <p><strong>다건 철회 지원:</strong>
 *
 * <ul>
 *   <li>한 사용자로부터 여러 역할을 한 번에 철회 가능
 *   <li>할당되지 않은 역할은 무시
 * </ul>
 *
 * @param userId 사용자 ID (필수)
 * @param roleIds 철회할 역할 ID 목록 (필수, 1개 이상)
 * @author development-team
 * @since 1.0.0
 */
public record RevokeUserRoleCommand(String userId, List<Long> roleIds) {}
