package com.ryuqq.authhub.adapter.in.rest.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Login API Request DTO - 로그인 요청 데이터 전송 객체.
 *
 * <p>클라이언트로부터 받은 로그인 요청 데이터를 담는 API Layer의 DTO입니다.
 * Jakarta Bean Validation을 사용하여 입력값을 검증합니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/setter 사용</li>
 *   <li>✅ Jakarta Bean Validation 사용 - {@code @NotBlank}</li>
 *   <li>✅ Javadoc 완비 - 모든 필드와 메서드에 문서화</li>
 *   <li>✅ API DTO는 Application Layer의 Command와 분리</li>
 * </ul>
 *
 * <p><strong>Validation 규칙:</strong></p>
 * <ul>
 *   <li>credentialType: null 불가, 공백 불가 (예: "EMAIL", "PHONE", "USERNAME")</li>
 *   <li>identifier: null 불가, 공백 불가 (예: "user@example.com", "01012345678")</li>
 *   <li>password: null 불가, 공백 불가 (평문 비밀번호)</li>
 *   <li>platform: null 불가, 공백 불가 (예: "WEB", "MOBILE_ANDROID", "MOBILE_IOS")</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public class LoginApiRequest {

    /**
     * 인증 타입 (예: EMAIL, PHONE, USERNAME).
     */
    @NotBlank(message = "credentialType cannot be null or blank")
    private String credentialType;

    /**
     * 식별자 (이메일, 전화번호, 사용자명 등).
     */
    @NotBlank(message = "identifier cannot be null or blank")
    private String identifier;

    /**
     * 평문 비밀번호.
     */
    @NotBlank(message = "password cannot be null or blank")
    private String password;

    /**
     * 플랫폼 정보 (예: WEB, MOBILE_ANDROID, MOBILE_IOS).
     */
    @NotBlank(message = "platform cannot be null or blank")
    private String platform;

    /**
     * 기본 생성자 (Jackson deserialization용).
     */
    public LoginApiRequest() {
    }

    /**
     * 전체 필드 생성자.
     *
     * @param credentialType 인증 타입
     * @param identifier 식별자
     * @param password 평문 비밀번호
     * @param platform 플랫폼 정보
     * @author AuthHub Team
     * @since 1.0.0
     */
    public LoginApiRequest(
            final String credentialType,
            final String identifier,
            final String password,
            final String platform
    ) {
        this.credentialType = credentialType;
        this.identifier = identifier;
        this.password = password;
        this.platform = platform;
    }

    /**
     * credentialType 값을 반환합니다.
     *
     * @return credentialType
     */
    public String getCredentialType() {
        return credentialType;
    }

    /**
     * credentialType 값을 설정합니다.
     *
     * @param credentialType 인증 타입
     */
    public void setCredentialType(final String credentialType) {
        this.credentialType = credentialType;
    }

    /**
     * identifier 값을 반환합니다.
     *
     * @return identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * identifier 값을 설정합니다.
     *
     * @param identifier 식별자
     */
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    /**
     * password 값을 반환합니다.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * password 값을 설정합니다.
     *
     * @param password 평문 비밀번호
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * platform 값을 반환합니다.
     *
     * @return platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * platform 값을 설정합니다.
     *
     * @param platform 플랫폼 정보
     */
    public void setPlatform(final String platform) {
        this.platform = platform;
    }
}
