package com.ryuqq.authhub.domain.auth.credential.exception;

import com.ryuqq.authhub.domain.auth.credential.CredentialType;
import com.ryuqq.authhub.domain.auth.credential.Identifier;

/**
 * 유효하지 않은 인증 정보에 대한 예외.
 *
 * <p>다음과 같은 경우 발생합니다:</p>
 * <ul>
 *   <li>Identifier가 CredentialType 형식에 맞지 않는 경우</li>
 *   <li>비밀번호 형식이 정책을 만족하지 않는 경우</li>
 *   <li>인증 정보의 도메인 불변식이 위반된 경우</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public class InvalidCredentialException extends CredentialDomainException {

    private final CredentialType type;
    private final String identifier;

    /**
     * 기본 생성자.
     *
     * @param message 예외 메시지
     * @author AuthHub Team
     * @since 1.0.0
     */
    public InvalidCredentialException(final String message) {
        super(message);
        this.type = null;
        this.identifier = null;
    }

    /**
     * 타입과 식별자 정보를 포함한 생성자.
     *
     * @param message 예외 메시지
     * @param type 인증 타입
     * @param identifier 식별자 값
     * @author AuthHub Team
     * @since 1.0.0
     */
    public InvalidCredentialException(
            final String message,
            final CredentialType type,
            final String identifier
    ) {
        super(message);
        this.type = type;
        this.identifier = identifier;
    }

    /**
     * 타입과 식별자 Value Object를 포함한 생성자.
     *
     * @param message 예외 메시지
     * @param type 인증 타입
     * @param identifier 식별자 Value Object
     * @author AuthHub Team
     * @since 1.0.0
     */
    public InvalidCredentialException(
            final String message,
            final CredentialType type,
            final Identifier identifier
    ) {
        super(message);
        this.type = type;
        this.identifier = identifier != null ? identifier.value() : null;
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
     * 식별자 값을 반환합니다.
     *
     * @return 식별자 문자열 (null 가능)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getIdentifier() {
        return this.identifier;
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
        if (type != null && identifier != null) {
            return baseMessage + " [type=" + type + ", identifier=" + identifier + "]";
        }
        return baseMessage;
    }
}
