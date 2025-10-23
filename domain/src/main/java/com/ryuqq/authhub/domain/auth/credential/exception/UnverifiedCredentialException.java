package com.ryuqq.authhub.domain.auth.credential.exception;

import com.ryuqq.authhub.domain.auth.credential.CredentialId;
import com.ryuqq.authhub.domain.auth.credential.CredentialType;

/**
 * 미검증 인증 정보에 대한 예외.
 *
 * <p>다음과 같은 경우 발생합니다:</p>
 * <ul>
 *   <li>이메일 인증을 완료하지 않은 상태에서 로그인 시도</li>
 *   <li>전화번호 SMS 인증을 완료하지 않은 상태에서 로그인 시도</li>
 *   <li>검증 필수 정책을 위반하는 경우</li>
 * </ul>
 *
 * <p>이 예외는 주로 Application Layer의 UseCase에서
 * 로그인 시도 시 검증 상태를 체크할 때 발생합니다.</p>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public class UnverifiedCredentialException extends CredentialDomainException {

    private final CredentialId credentialId;
    private final CredentialType type;

    /**
     * 기본 생성자.
     *
     * @param message 예외 메시지
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UnverifiedCredentialException(final String message) {
        super(message);
        this.credentialId = null;
        this.type = null;
    }

    /**
     * 인증 정보 ID와 타입을 포함한 생성자.
     *
     * @param message 예외 메시지
     * @param credentialId 인증 정보 식별자
     * @param type 인증 타입
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UnverifiedCredentialException(
            final String message,
            final CredentialId credentialId,
            final CredentialType type
    ) {
        super(message);
        this.credentialId = credentialId;
        this.type = type;
    }

    /**
     * 간편 팩토리 메서드 - 이메일 미검증.
     *
     * @param credentialId 인증 정보 식별자
     * @return UnverifiedCredentialException 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static UnverifiedCredentialException forEmail(final CredentialId credentialId) {
        return new UnverifiedCredentialException(
                "Email verification required. Please verify your email before logging in.",
                credentialId,
                CredentialType.EMAIL
        );
    }

    /**
     * 간편 팩토리 메서드 - 전화번호 미검증.
     *
     * @param credentialId 인증 정보 식별자
     * @return UnverifiedCredentialException 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static UnverifiedCredentialException forPhone(final CredentialId credentialId) {
        return new UnverifiedCredentialException(
                "Phone verification required. Please verify your phone number before logging in.",
                credentialId,
                CredentialType.PHONE
        );
    }

    /**
     * 인증 정보 식별자를 반환합니다.
     *
     * @return CredentialId (null 가능)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public CredentialId getCredentialId() {
        return this.credentialId;
    }

    /**
     * 인증 타입을 반환합니다.
     *
     * @return CredentialType (null 가능)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public CredentialType getType() {
        return this.type;
    }

    /**
     * 상세한 예외 정보를 문자열로 반환합니다.
     *
     * @return 예외 정보 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        String baseMessage = super.toString();
        if (credentialId != null && type != null) {
            return baseMessage + " [credentialId=" + credentialId + ", type=" + type + "]";
        }
        return baseMessage;
    }
}
