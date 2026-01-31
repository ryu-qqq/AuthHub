package com.ryuqq.authhub.application.role.internal;

import com.ryuqq.authhub.domain.role.id.RoleId;

/**
 * RoleUsageChecker - 역할 사용 여부 검증 인터페이스
 *
 * <p>Role이 다른 도메인(User 등)에서 사용 중인지 검증합니다.
 *
 * <p>UserRole 도메인 구현 시 UserRoleValidator가 이 인터페이스를 구현합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RoleUsageChecker {

    /**
     * 역할이 사용 중인지 검증
     *
     * <p>역할이 User에 할당되어 있으면 RoleInUseException을 발생시킵니다.
     *
     * @param roleId 검증할 역할 ID
     * @throws com.ryuqq.authhub.domain.role.exception.RoleInUseException 사용 중인 경우
     */
    void validateNotInUse(RoleId roleId);
}
