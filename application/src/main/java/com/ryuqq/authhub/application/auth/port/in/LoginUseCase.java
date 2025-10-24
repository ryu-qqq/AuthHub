package com.ryuqq.authhub.application.auth.port.in;

/**
 * Login UseCase - 로그인 유스케이스 Port Interface.
 *
 * <p>사용자 인증을 수행하고 JWT 토큰을 발급하는 Command UseCase입니다.
 * Command/Response는 UseCase 내부 Record로 정의되며, 외부 클래스로 분리하지 않습니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Command/Response는 UseCase 내부 Record로 정의</li>
 *   <li>✅ Lombok 금지 - Plain Java Record 사용</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드/Record에 @author, @since 포함</li>
 *   <li>✅ 구현체는 Service Layer에서 @Transactional 적용</li>
 * </ul>
 *
 * <p><strong>비즈니스 로직 흐름:</strong></p>
 * <ol>
 *   <li>Credential 조회 (credentialType + identifier)</li>
 *   <li>비밀번호 검증</li>
 *   <li>User 상태 확인 (ACTIVE 여부)</li>
 *   <li>Access Token + Refresh Token 생성</li>
 *   <li>Refresh Token을 Redis에 저장</li>
 * </ol>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface LoginUseCase {

    /**
     * 로그인을 수행하고 JWT 토큰을 발급합니다.
     *
     * @param command 로그인 요청 정보 (credentialType, identifier, password, platform)
     * @return Response JWT 토큰 정보 (accessToken, refreshToken, tokenType, expiresIn)
     * @throws com.ryuqq.authhub.domain.auth.credential.exception.CredentialNotFoundException 인증 정보가 존재하지 않는 경우
     * @throws com.ryuqq.authhub.domain.auth.credential.exception.InvalidCredentialException 비밀번호가 일치하지 않는 경우
     * @throws com.ryuqq.authhub.domain.auth.user.exception.InvalidUserStatusException 사용자가 비활성화 상태인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    Response login(Command command);

    /**
     * 로그인 Command Record.
     *
     * <p>로그인 요청에 필요한 모든 정보를 담고 있습니다.
     * Record의 Compact Constructor에서 입력값 검증을 수행합니다.</p>
     *
     * @param credentialType 인증 타입 (EMAIL, PHONE, USERNAME 등) - null 불가, 공백 불가
     * @param identifier 식별자 (이메일, 전화번호, 사용자명) - null 불가, 공백 불가
     * @param password 평문 비밀번호 - null 불가, 공백 불가
     * @param platform 플랫폼 정보 (WEB, MOBILE_ANDROID, MOBILE_IOS 등) - null 불가, 공백 불가
     * @author AuthHub Team
     * @since 1.0.0
     */
    record Command(
            String credentialType,
            String identifier,
            String password,
            String platform
    ) {
        /**
         * Compact Constructor - 입력값 검증.
         *
         * @throws IllegalArgumentException 필수 파라미터가 null이거나 공백인 경우
         */
        public Command {
            if (credentialType == null || credentialType.isBlank()) {
                throw new IllegalArgumentException("credentialType cannot be null or blank");
            }

            if (identifier == null || identifier.isBlank()) {
                throw new IllegalArgumentException("identifier cannot be null or blank");
            }

            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("password cannot be null or blank");
            }

            if (platform == null || platform.isBlank()) {
                throw new IllegalArgumentException("platform cannot be null or blank");
            }
        }
    }

    /**
     * 로그인 Response Record.
     *
     * <p>로그인 성공 시 반환되는 JWT 토큰 정보입니다.
     * Access Token과 Refresh Token을 모두 포함합니다.</p>
     *
     * @param accessToken Access Token (JWT) - 짧은 유효 기간 (예: 15분)
     * @param refreshToken Refresh Token (JWT) - 긴 유효 기간 (예: 7일)
     * @param tokenType Token 타입 (항상 "Bearer")
     * @param expiresIn Access Token 만료까지 남은 시간 (초 단위)
     * @author AuthHub Team
     * @since 1.0.0
     */
    record Response(
            String accessToken,
            String refreshToken,
            String tokenType,
            int expiresIn
    ) {
        /**
         * Compact Constructor - 입력값 검증.
         *
         * @throws IllegalArgumentException 필수 파라미터가 null이거나 expiresIn이 음수인 경우
         */
        public Response {
            if (accessToken == null || accessToken.isBlank()) {
                throw new IllegalArgumentException("accessToken cannot be null or blank");
            }

            if (refreshToken == null || refreshToken.isBlank()) {
                throw new IllegalArgumentException("refreshToken cannot be null or blank");
            }

            if (tokenType == null || tokenType.isBlank()) {
                throw new IllegalArgumentException("tokenType cannot be null or blank");
            }

            if (expiresIn <= 0) {
                throw new IllegalArgumentException("expiresIn must be positive");
            }
        }
    }
}
