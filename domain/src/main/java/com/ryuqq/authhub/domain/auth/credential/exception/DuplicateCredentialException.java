package com.ryuqq.authhub.domain.auth.credential.exception;

import com.ryuqq.authhub.domain.auth.credential.CredentialType;
import com.ryuqq.authhub.domain.auth.credential.Identifier;

/**
 * 중복된 인증 정보에 대한 예외.
 *
 * <p>다음과 같은 경우 발생합니다:</p>
 * <ul>
 *   <li>동일한 이메일 주소가 이미 등록되어 있는 경우</li>
 *   <li>동일한 전화번호가 이미 등록되어 있는 경우</li>
 *   <li>동일한 사용자명이 이미 사용 중인 경우</li>
 * </ul>
 *
 * <p>이 예외는 주로 Application Layer나 Infrastructure Layer에서
 * 영속성 저장 전에 중복 체크 시 발생합니다.</p>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public class DuplicateCredentialException extends CredentialDomainException {

    private final CredentialType type;
    private final String identifier;

    /**
     * 기본 생성자.
     *
     * @param message 예외 메시지
     * @author AuthHub Team
     * @since 1.0.0
     */
    public DuplicateCredentialException(final String message) {
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
    public DuplicateCredentialException(
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
    public DuplicateCredentialException(
            final String message,
            final CredentialType type,
            final Identifier identifier
    ) {
        super(message);
        this.type = type;
        this.identifier = identifier != null ? identifier.value() : null;
    }

    /**
     * 간편 팩토리 메서드 - 이메일 중복.
     *
     * @param email 중복된 이메일 주소
     * @return DuplicateCredentialException 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static DuplicateCredentialException forEmail(final String email) {
        return new DuplicateCredentialException(
                "Email already exists: " + email,
                CredentialType.EMAIL,
                email
        );
    }

    /**
     * 간편 팩토리 메서드 - 전화번호 중복.
     *
     * @param phone 중복된 전화번호
     * @return DuplicateCredentialException 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static DuplicateCredentialException forPhone(final String phone) {
        return new DuplicateCredentialException(
                "Phone number already exists: " + phone,
                CredentialType.PHONE,
                phone
        );
    }

    /**
     * 간편 팩토리 메서드 - 사용자명 중복.
     *
     * @param username 중복된 사용자명
     * @return DuplicateCredentialException 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static DuplicateCredentialException forUsername(final String username) {
        return new DuplicateCredentialException(
                "Username already exists: " + username,
                CredentialType.USERNAME,
                username
        );
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
