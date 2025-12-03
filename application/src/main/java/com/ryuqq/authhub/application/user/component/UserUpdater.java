package com.ryuqq.authhub.application.user.component;

import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.vo.Password;
import com.ryuqq.authhub.domain.user.vo.UserProfile;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import org.springframework.stereotype.Component;

/**
 * UserUpdater - User Domain 업데이트 담당 컴포넌트
 *
 * <p>User의 상태 변경, 프로필 업데이트 등 Domain 업데이트 로직을 수행합니다. Clock 처리를 캡슐화하여 Service가 시간 관련 의존성을 갖지 않도록 합니다.
 *
 * <p><strong>역할:</strong>
 *
 * <ul>
 *   <li>프로필 업데이트 (updateProfile)
 *   <li>상태 변경 (changeStatus)
 *   <li>비밀번호 변경 (changePassword)
 *   <li>Clock 처리 캡슐화
 * </ul>
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>Validator는 검증, Updater는 업데이트, Assembler는 변환
 *   <li>비즈니스 규칙 검증은 Domain에 위임
 *   <li>Service는 Orchestration만 담당
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserUpdater {

    private final ClockHolder clockHolder;

    public UserUpdater(ClockHolder clockHolder) {
        this.clockHolder = clockHolder;
    }

    /**
     * 사용자 프로필 업데이트
     *
     * <p>Domain의 updateProfile 메서드를 호출하여 프로필을 업데이트합니다. ACTIVE 상태 검증은 Domain에서 수행합니다.
     *
     * @param user 기존 User
     * @param newProfile 새로운 프로필 정보
     * @return 프로필이 업데이트된 User
     */
    public User updateProfile(User user, UserProfile newProfile) {
        return user.updateProfile(newProfile, clockHolder.clock());
    }

    /**
     * 사용자 상태 변경
     *
     * <p>대상 상태에 따라 적절한 Domain 메서드를 호출합니다. 상태 전환 가능 여부 검증은 Domain에서 수행합니다.
     *
     * @param user 기존 User
     * @param targetStatus 변경할 상태
     * @return 상태가 변경된 User
     */
    public User changeStatus(User user, UserStatus targetStatus) {
        return switch (targetStatus) {
            case ACTIVE -> user.activate(clockHolder.clock());
            case INACTIVE -> user.deactivate(clockHolder.clock());
            case SUSPENDED -> user.suspend(clockHolder.clock());
            case DELETED -> user.delete(clockHolder.clock());
        };
    }

    /**
     * 사용자 비밀번호 변경
     *
     * <p>Domain의 changePassword 메서드를 호출하여 비밀번호를 변경합니다. ACTIVE 상태 검증은 Domain에서 수행합니다.
     *
     * @param user 기존 User
     * @param newHashedPassword 새로운 해시된 비밀번호
     * @return 비밀번호가 변경된 User
     */
    public User changePassword(User user, Password newHashedPassword) {
        return user.changePassword(newHashedPassword, clockHolder.clock());
    }
}
