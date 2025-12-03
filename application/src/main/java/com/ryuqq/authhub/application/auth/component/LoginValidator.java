package com.ryuqq.authhub.application.auth.component;

import com.ryuqq.authhub.application.common.component.PasswordHasher;
import com.ryuqq.authhub.domain.auth.exception.InvalidCredentialsException;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.InvalidUserStateException;
import org.springframework.stereotype.Component;

/**
 * LoginValidator - 로그인 검증 컴포넌트
 *
 * <p>로그인 시 필요한 검증 로직을 담당합니다.
 *
 * <p><strong>검증 범위:</strong>
 *
 * <ul>
 *   <li>비밀번호 일치 여부 검증
 *   <li>사용자 활성 상태 검증
 * </ul>
 *
 * <p><strong>보안 고려사항:</strong>
 *
 * <ul>
 *   <li>비밀번호 불일치 시 "사용자 없음"과 동일한 예외 사용 (정보 노출 방지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class LoginValidator {

    private final PasswordHasher passwordHasher;

    public LoginValidator(PasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    /**
     * 비밀번호 검증
     *
     * <p>보안상 비밀번호 불일치와 사용자 미존재를 구분하지 않습니다.
     *
     * @param rawPassword 입력된 평문 비밀번호
     * @param user 검증 대상 사용자
     * @param tenantId 테넌트 ID (예외 메시지용)
     * @param identifier 사용자 식별자 (예외 메시지용)
     * @throws InvalidCredentialsException 비밀번호가 일치하지 않는 경우
     */
    public void validatePassword(String rawPassword, User user, long tenantId, String identifier) {
        if (!passwordHasher.matches(rawPassword, user.getHashedPassword())) {
            throw new InvalidCredentialsException(tenantId, identifier);
        }
    }

    /**
     * 사용자 활성 상태 검증
     *
     * @param user 검증 대상 사용자
     * @throws InvalidUserStateException 사용자가 활성 상태가 아닌 경우
     */
    public void validateActiveStatus(User user) {
        if (!user.isActive()) {
            throw new InvalidUserStateException(user.getUserStatus(), "로그인은 활성 상태의 사용자만 가능합니다");
        }
    }

    /**
     * 로그인 전체 검증 (비밀번호 + 활성 상태)
     *
     * @param rawPassword 입력된 평문 비밀번호
     * @param user 검증 대상 사용자
     * @param tenantId 테넌트 ID (예외 메시지용)
     * @param identifier 사용자 식별자 (예외 메시지용)
     * @throws InvalidCredentialsException 비밀번호가 일치하지 않는 경우
     * @throws InvalidUserStateException 사용자가 활성 상태가 아닌 경우
     */
    public void validate(String rawPassword, User user, long tenantId, String identifier) {
        validatePassword(rawPassword, user, tenantId, identifier);
        validateActiveStatus(user);
    }
}
