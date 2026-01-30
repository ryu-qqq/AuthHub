package com.ryuqq.authhub.application.userrole.validator;

import com.ryuqq.authhub.application.role.internal.RoleUsageChecker;
import com.ryuqq.authhub.application.userrole.manager.UserRoleReadManager;
import com.ryuqq.authhub.domain.role.exception.RoleInUseException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import org.springframework.stereotype.Component;

/**
 * UserRoleValidator - 사용자-역할 관계 비즈니스 규칙 검증
 *
 * <p>조회가 필요한 검증 로직을 담당합니다.
 *
 * <p>RoleUsageChecker 인터페이스를 구현하여 Role 삭제 시 사용 여부를 검증합니다.
 *
 * <p>VAL-001: Validator는 @Component 어노테이션 사용.
 *
 * <p>VAL-002: Validator는 {Domain}Validator 네이밍 사용.
 *
 * <p>VAL-003: Validator는 ReadManager만 의존.
 *
 * <p>VAL-004: Validator는 void 반환, 실패 시 DomainException.
 *
 * <p>VAL-005: Validator 메서드는 validateXxx() 또는 checkXxx() 사용.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleValidator implements RoleUsageChecker {

    private final UserRoleReadManager readManager;

    public UserRoleValidator(UserRoleReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 역할이 사용 중인지 검증
     *
     * <p>역할이 User에 할당되어 있으면 RoleInUseException을 발생시킵니다.
     *
     * @param roleId 검증할 역할 ID
     * @throws RoleInUseException 사용 중인 경우
     */
    @Override
    public void validateNotInUse(RoleId roleId) {
        if (readManager.existsByRoleId(roleId)) {
            throw new RoleInUseException(roleId);
        }
    }
}
