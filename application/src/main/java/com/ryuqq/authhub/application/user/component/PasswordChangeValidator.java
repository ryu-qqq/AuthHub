package com.ryuqq.authhub.application.user.component;

import com.ryuqq.authhub.application.common.component.PasswordHasher;
import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.InvalidPasswordException;
import org.springframework.stereotype.Component;

/**
 * PasswordChangeValidator - 비밀번호 변경 검증 컴포넌트
 *
 * <p>비밀번호 변경 시 현재 비밀번호 검증을 담당합니다.
 *
 * <p><strong>검증 규칙:</strong>
 *
 * <ul>
 *   <li>verified=false: 현재 비밀번호 검증 필요 (일반 변경)
 *   <li>verified=true: 검증 건너뜀 (비밀번호 재설정 - 본인 인증 완료)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PasswordChangeValidator {

    private final PasswordHasher passwordHasher;

    public PasswordChangeValidator(PasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    /**
     * 비밀번호 변경 검증
     *
     * <p>verified가 false인 경우에만 현재 비밀번호를 검증합니다.
     *
     * @param command 비밀번호 변경 커맨드
     * @param user 검증 대상 사용자
     * @throws InvalidPasswordException 현재 비밀번호가 일치하지 않는 경우
     */
    public void validate(ChangePasswordCommand command, User user) {
        if (command.verified()) {
            // 본인 인증 완료된 경우 (비밀번호 재설정) - 검증 건너뜀
            return;
        }

        // 일반 비밀번호 변경 - 현재 비밀번호 검증
        validateCurrentPassword(command, user);
    }

    private void validateCurrentPassword(ChangePasswordCommand command, User user) {
        String storedHashedPassword = user.getHashedPassword();
        if (!passwordHasher.matches(command.currentPassword(), storedHashedPassword)) {
            throw new InvalidPasswordException(command.userId());
        }
    }
}
