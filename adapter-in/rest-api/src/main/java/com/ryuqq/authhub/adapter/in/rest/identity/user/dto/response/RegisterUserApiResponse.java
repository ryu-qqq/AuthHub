package com.ryuqq.authhub.adapter.in.rest.identity.user.dto.response;

/**
 * Register User API Response DTO - 사용자 등록 응답 데이터 전송 객체.
 *
 * <p>사용자 등록 성공 시 클라이언트에게 반환하는 사용자 정보를 담는 API Layer의 DTO입니다.
 * 생성된 User와 UserCredential의 ID를 포함합니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ "Api" prefix 필수 - RegisterUserApiResponse (ApiResponse suffix 아님!)</li>
 *   <li>✅ Java 21 Record 사용 - Boilerplate 코드 제거, 불변성 보장</li>
 *   <li>✅ Javadoc 완비 - 모든 필드에 문서화</li>
 *   <li>✅ API DTO는 Application Layer의 Response와 분리</li>
 *   <li>✅ Lombok 금지 - Plain Java Record 사용</li>
 * </ul>
 *
 * <p><strong>응답 필드:</strong></p>
 * <ul>
 *   <li>userId: 생성된 User의 UUID (문자열 형식)</li>
 *   <li>credentialId: 생성된 UserCredential의 UUID (문자열 형식)</li>
 * </ul>
 *
 * <p><strong>보안 고려사항:</strong></p>
 * <ul>
 *   <li>비밀번호는 절대 반환하지 않음</li>
 *   <li>민감한 개인정보는 제외 (이메일, 전화번호 등)</li>
 *   <li>ID만 반환하여 추가 조회 필요 시 별도 API 사용</li>
 * </ul>
 *
 * @param userId 생성된 User의 UUID (문자열 형식)
 * @param credentialId 생성된 UserCredential의 UUID (문자열 형식)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record RegisterUserApiResponse(
        String userId,
        String credentialId
) {
    // Record는 자동으로 다음을 생성합니다:
    // - Getter: userId(), credentialId()
    // - Constructor: new RegisterUserApiResponse(userId, credentialId)
    // - equals(), hashCode(), toString()
    // - 모든 필드는 private final (불변)
}
