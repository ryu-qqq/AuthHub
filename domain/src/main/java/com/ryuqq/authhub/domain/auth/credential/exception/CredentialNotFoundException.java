package com.ryuqq.authhub.domain.auth.credential.exception;

import com.ryuqq.authhub.domain.auth.credential.CredentialId;
import com.ryuqq.authhub.domain.auth.credential.CredentialType;
import com.ryuqq.authhub.domain.auth.credential.Identifier;

/**
 * 인증 정보를 찾을 수 없을 때 발생하는 예외.
 *
 * <p>다음과 같은 경우 발생합니다:</p>
 * <ul>
 *   <li>존재하지 않는 이메일로 로그인 시도</li>
 *   <li>존재하지 않는 전화번호로 로그인 시도</li>
 *   <li>존재하지 않는 사용자명으로 로그인 시도</li>
 *   <li>CredentialId로 조회했으나 존재하지 않는 경우</li>
 * </ul>
 *
 * <p>이 예외는 주로 Application Layer나 Infrastructure Layer에서
 * 영속성 저장소 조회 시 발생합니다.</p>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public class CredentialNotFoundException extends CredentialDomainException {

    private final CredentialId credentialId;
    private final CredentialType type;
    private final String identifier;

    /**
     * 기본 생성자.
     *
     * @param message 예외 메시지
     * @author AuthHub Team
     * @since 1.0.0
     */
    public CredentialNotFoundException(final String message) {
        super(message);
        this.credentialId = null;
        this.type = null;
        this.identifier = null;
    }

    /**
     * CredentialId를 포함한 생성자.
     *
     * @param message 예외 메시지
     * @param credentialId 인증 정보 식별자
     * @author AuthHub Team
     * @since 1.0.0
     */
    public CredentialNotFoundException(
            final String message,
            final CredentialId credentialId
    ) {
        super(message);
        this.credentialId = credentialId;
        this.type = null;
        this.identifier = null;
    }

    /**
     * 타입과 식별자를 포함한 생성자.
     *
     * @param message 예외 메시지
     * @param type 인증 타입
     * @param identifier 식별자 값
     * @author AuthHub Team
     * @since 1.0.0
     */
    public CredentialNotFoundException(
            final String message,
            final CredentialType type,
            final String identifier
    ) {
        super(message);
        this.credentialId = null;
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
    public CredentialNotFoundException(
            final String message,
            final CredentialType type,
            final Identifier identifier
    ) {
        super(message);
        this.credentialId = null;
        this.type = type;
        this.identifier = identifier != null ? identifier.value() : null;
    }

    /**
     * 간편 팩토리 메서드 - CredentialId로 찾을 수 없음.
     *
     * @param credentialId 인증 정보 식별자
     * @return CredentialNotFoundException 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static CredentialNotFoundException byId(final CredentialId credentialId) {
        return new CredentialNotFoundException(
                "Credential not found with ID: " + credentialId.asString(),
                credentialId
        );
    }

    /**
     * 간편 팩토리 메서드 - 이메일로 찾을 수 없음.
     *
     * @param email 이메일 주소
     * @return CredentialNotFoundException 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static CredentialNotFoundException byEmail(final String email) {
        return new CredentialNotFoundException(
                "Credential not found with email: " + email,
                CredentialType.EMAIL,
                email
        );
    }

    /**
     * 간편 팩토리 메서드 - 전화번호로 찾을 수 없음.
     *
     * @param phone 전화번호
     * @return CredentialNotFoundException 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static CredentialNotFoundException byPhone(final String phone) {
        return new CredentialNotFoundException(
                "Credential not found with phone: " + phone,
                CredentialType.PHONE,
                phone
        );
    }

    /**
     * 간편 팩토리 메서드 - 사용자명으로 찾을 수 없음.
     *
     * @param username 사용자명
     * @return CredentialNotFoundException 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static CredentialNotFoundException byUsername(final String username) {
        return new CredentialNotFoundException(
                "Credential not found with username: " + username,
                CredentialType.USERNAME,
                username
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
        if (credentialId != null) {
            return baseMessage + " [credentialId=" + credentialId + "]";
        }
        if (type != null && identifier != null) {
            return baseMessage + " [type=" + type + ", identifier=" + identifier + "]";
        }
        return baseMessage;
    }
}
