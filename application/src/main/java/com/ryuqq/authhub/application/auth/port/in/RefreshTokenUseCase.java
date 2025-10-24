package com.ryuqq.authhub.application.auth.port.in;

/**
 * RefreshToken UseCase - Refresh Token을 사용한 Access Token 재발급 유스케이스 Port Interface.
 *
 * <p>만료된 Access Token을 Refresh Token을 사용하여 재발급하는 Command UseCase입니다.
 * Command/Response는 UseCase 내부 Record로 정의되며, 외부 클래스로 분리하지 않습니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Command/Response는 UseCase 내부 Record로 정의</li>
 *   <li>✅ Lombok 금지 - Plain Java Record 사용</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드/Record에 @author, @since 포함</li>
 *   <li>✅ 구현체는 Service Layer에서 @Transactional 적용 (짧게 유지)</li>
 * </ul>
 *
 * <p><strong>비즈니스 로직 흐름:</strong></p>
 * <ol>
 *   <li>Refresh Token의 형식 및 서명 검증</li>
 *   <li>Redis에서 Refresh Token 존재 여부 확인</li>
 *   <li>Blacklist에 등록되지 않았는지 확인</li>
 *   <li>새로운 Access Token 생성</li>
 *   <li>Access Token 정보 반환 (Refresh Token은 재발급하지 않음)</li>
 * </ol>
 *
 * <p><strong>보안 고려사항:</strong></p>
 * <ul>
 *   <li>Refresh Token은 Redis에 저장되어 있어야 함 (Rotation 전략 지원)</li>
 *   <li>Blacklist에 등록된 토큰은 사용 불가 (로그아웃된 토큰)</li>
 *   <li>Refresh Token은 재발급하지 않음 (보안 강화 - 필요시 재로그인)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface RefreshTokenUseCase {

    /**
     * Refresh Token을 사용하여 새로운 Access Token을 발급합니다.
     *
     * <p>Refresh Token의 유효성을 검증하고, 새로운 Access Token을 생성하여 반환합니다.
     * Refresh Token 자체는 재발급하지 않으며, 클라이언트는 기존 Refresh Token을 계속 사용합니다.</p>
     *
     * @param command Refresh Token 재발급 요청 정보 (refreshToken)
     * @return Response 새로운 Access Token 정보 (accessToken, tokenType, expiresIn)
     * @throws com.ryuqq.authhub.domain.auth.token.exception.InvalidTokenException Refresh Token 형식이 잘못되었거나 서명 검증 실패
     * @throws com.ryuqq.authhub.domain.auth.token.exception.ExpiredTokenException Refresh Token이 만료된 경우
     * @throws com.ryuqq.authhub.domain.auth.token.exception.TokenNotFoundException Refresh Token이 Redis에 존재하지 않는 경우 (이미 사용되었거나 삭제됨)
     * @throws com.ryuqq.authhub.domain.auth.token.exception.BlacklistedTokenException Refresh Token이 Blacklist에 등록된 경우 (로그아웃됨)
     * @author AuthHub Team
     * @since 1.0.0
     */
    Response refresh(Command command);

    /**
     * Refresh Token 재발급 Command Record.
     *
     * <p>Refresh Token을 사용하여 Access Token을 재발급하기 위한 요청 정보입니다.
     * Record의 Compact Constructor에서 입력값 검증을 수행합니다.</p>
     *
     * @param refreshToken Refresh Token (JWT 형식) - null 불가, 공백 불가
     * @author AuthHub Team
     * @since 1.0.0
     */
    record Command(
            String refreshToken
    ) {
        /**
         * Compact Constructor - 입력값 검증.
         *
         * @throws IllegalArgumentException refreshToken이 null이거나 공백인 경우
         */
        public Command {
            if (refreshToken == null || refreshToken.isBlank()) {
                throw new IllegalArgumentException("refreshToken cannot be null or blank");
            }
        }
    }

    /**
     * Refresh Token 재발급 Response Record.
     *
     * <p>새로운 Access Token 정보를 반환합니다.
     * Refresh Token은 재발급하지 않으므로, Response에 포함되지 않습니다.</p>
     *
     * @param accessToken 새로 발급된 Access Token (JWT 형식)
     * @param tokenType Token 타입 (항상 "Bearer")
     * @param expiresIn Access Token 만료까지 남은 시간 (초 단위)
     * @author AuthHub Team
     * @since 1.0.0
     */
    record Response(
            String accessToken,
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

            if (tokenType == null || tokenType.isBlank()) {
                throw new IllegalArgumentException("tokenType cannot be null or blank");
            }

            if (expiresIn <= 0) {
                throw new IllegalArgumentException("expiresIn must be positive");
            }
        }
    }
}
