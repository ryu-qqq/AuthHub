package com.ryuqq.authhub.application.permission.internal;

import com.ryuqq.authhub.domain.permission.id.PermissionId;

/**
 * PermissionUsageChecker - 권한 사용 여부 검증 인터페이스
 *
 * <p>Permission이 다른 도메인(Role 등)에서 사용 중인지 검증합니다.
 *
 * <p>Role 도메인 구현 시 RoleValidator가 이 인터페이스를 구현합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface PermissionUsageChecker {

    /**
     * 권한이 사용 중인지 검증
     *
     * <p>권한이 Role에 할당되어 있으면 PermissionInUseException을 발생시킵니다.
     *
     * @param permissionId 검증할 권한 ID
     * @throws com.ryuqq.authhub.domain.permission.exception.PermissionInUseException 사용 중인 경우
     */
    void validateNotInUse(PermissionId permissionId);
}
