package com.ryuqq.authhub.adapter.in.rest.identity.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Register User API Request DTO - 사용자 등록 요청 데이터 전송 객체.
 *
 * <p>클라이언트로부터 받은 사용자 등록 요청 데이터를 담는 API Layer의 DTO입니다.
 * Jakarta Bean Validation을 사용하여 입력값을 검증합니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ "Api" prefix 필수 - RegisterUserApiRequest (ApiRequest suffix 아님!)</li>
 *   <li>✅ Java 21 Record 사용 - Boilerplate 코드 제거, 불변성 보장</li>
 *   <li>✅ Jakarta Bean Validation 사용</li>
 *   <li>✅ Javadoc 완비 - 모든 필드에 문서화</li>
 *   <li>✅ API DTO는 Application Layer의 Command와 분리</li>
 *   <li>✅ Lombok 금지 - Plain Java Record 사용</li>
 * </ul>
 *
 * <p><strong>Validation 규칙:</strong></p>
 * <ul>
 *   <li>credentialType: null 불가, 공백 불가, 최대 50자 (예: "EMAIL", "PHONE", "USERNAME")</li>
 *   <li>identifier: null 불가, 공백 불가, 최대 100자 (예: "user@example.com", "01012345678")</li>
 *   <li>password: null 불가, 공백 불가, 8~100자 (평문 비밀번호, 서버에서 암호화)</li>
 *   <li>nickname: null 불가, 공백 불가, 2~20자 (중복 불가, 서버에서 검증)</li>
 * </ul>
 *
 * <p><strong>비즈니스 검증:</strong></p>
 * <ul>
 *   <li>credentialType 유효성 (EMAIL, PHONE, USERNAME 등)</li>
 *   <li>identifier 중복 확인</li>
 *   <li>nickname 중복 확인</li>
 *   <li>비밀번호 강도 정책 (8자 이상)</li>
 * </ul>
 *
 * @param credentialType 인증 타입 (예: EMAIL, PHONE, USERNAME)
 * @param identifier 식별자 (이메일, 전화번호, 사용자명 등)
 * @param password 평문 비밀번호 (8~100자)
 * @param nickname 닉네임 (2~20자, 중복 불가)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record RegisterUserApiRequest(
        @NotBlank(message = "credentialType cannot be null or blank")
        @Size(max = 50, message = "credentialType cannot exceed 50 characters")
        String credentialType,

        @NotBlank(message = "identifier cannot be null or blank")
        @Size(max = 100, message = "identifier cannot exceed 100 characters")
        String identifier,

        @NotBlank(message = "password cannot be null or blank")
        @Size(min = 8, max = 100, message = "password must be between 8 and 100 characters")
        String password,

        @NotBlank(message = "nickname cannot be null or blank")
        @Size(min = 2, max = 20, message = "nickname must be between 2 and 20 characters")
        String nickname
) {
    // Record는 자동으로 다음을 생성합니다:
    // - Getter: credentialType(), identifier(), password(), nickname()
    // - Constructor: new RegisterUserApiRequest(credentialType, identifier, password, nickname)
    // - equals(), hashCode(), toString()
    // - 모든 필드는 private final (불변)
}
