package com.ryuqq.authhub.application.userrole.dto.command;

import java.util.List;

/**
 * AssignUserRoleCommand - 사용자에게 역할 할당 Command
 *
 * <p>사용자에게 하나 이상의 역할을 할당할 때 사용하는 Command입니다.
 *
 * <p><strong>다건 할당 지원:</strong>
 *
 * <ul>
 *   <li>한 사용자에게 여러 역할을 한 번에 할당 가능
 *   <li>이미 할당된 역할은 무시 (중복 필터링)
 * </ul>
 *
 * @param userId 사용자 ID (필수)
 * @param roleIds 할당할 역할 ID 목록 (필수, 1개 이상)
 * @author development-team
 * @since 1.0.0
 */
public record AssignUserRoleCommand(String userId, List<Long> roleIds) {}
