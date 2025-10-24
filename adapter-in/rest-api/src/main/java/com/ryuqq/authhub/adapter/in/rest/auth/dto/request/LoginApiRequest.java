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
 *   <li>✅ Java 21 Record 사용 - Boilerplate 코드 제거, 불변성 보장</li>
 *   <li>✅ Jakarta Bean Validation 사용 - {@code @NotBlank}</li>
 *   <li>✅ Javadoc 완비 - 모든 필드에 문서화</li>
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
 * @param credentialType 인증 타입 (예: EMAIL, PHONE, USERNAME)
 * @param identifier 식별자 (이메일, 전화번호, 사용자명 등)
 * @param password 평문 비밀번호
 * @param platform 플랫폼 정보 (예: WEB, MOBILE_ANDROID, MOBILE_IOS)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record LoginApiRequest(
        @NotBlank(message = "credentialType cannot be null or blank")
        String credentialType,

        @NotBlank(message = "identifier cannot be null or blank")
        String identifier,

        @NotBlank(message = "password cannot be null or blank")
        String password,

        @NotBlank(message = "platform cannot be null or blank")
        String platform
) {
    // Record는 자동으로 다음을 생성합니다:
    // - Getter: credentialType(), identifier(), password(), platform()
    // - Constructor: new LoginApiRequest(credentialType, identifier, password, platform)
    // - equals(), hashCode(), toString()
    // - 모든 필드는 private final (불변)
}
