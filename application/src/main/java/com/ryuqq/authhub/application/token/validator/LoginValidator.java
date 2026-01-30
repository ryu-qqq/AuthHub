package com.ryuqq.authhub.application.token.validator;

import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderClient;
import com.ryuqq.authhub.domain.auth.exception.InvalidCredentialsException;
import com.ryuqq.authhub.domain.user.aggregate.User;
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

    private final PasswordEncoderClient passwordEncoderClient;

    public LoginValidator(PasswordEncoderClient passwordEncoderClient) {
        this.passwordEncoderClient = passwordEncoderClient;
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
    public void validatePassword(
            String rawPassword, User user, String tenantId, String identifier) {
        if (!passwordEncoderClient.matches(rawPassword, user.getHashedPassword())) {
            throw new InvalidCredentialsException(tenantId, identifier);
        }
    }
}
