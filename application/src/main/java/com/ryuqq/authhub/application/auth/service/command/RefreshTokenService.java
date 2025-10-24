package com.ryuqq.authhub.application.auth.service.command;

import com.ryuqq.authhub.application.auth.config.JwtProperties;
import com.ryuqq.authhub.application.auth.port.in.RefreshTokenUseCase;
import com.ryuqq.authhub.application.auth.port.out.CheckBlacklistPort;
import com.ryuqq.authhub.application.auth.port.out.GenerateTokenPort;
import com.ryuqq.authhub.application.auth.port.out.LoadRefreshTokenPort;
import com.ryuqq.authhub.application.auth.port.out.ValidateTokenPort;
import com.ryuqq.authhub.domain.auth.token.Token;
import com.ryuqq.authhub.domain.auth.token.TokenId;
import com.ryuqq.authhub.domain.auth.token.TokenType;
import com.ryuqq.authhub.domain.auth.token.exception.BlacklistedTokenException;
import com.ryuqq.authhub.domain.auth.token.exception.ExpiredTokenException;
import com.ryuqq.authhub.domain.auth.token.exception.InvalidTokenException;
import com.ryuqq.authhub.domain.auth.token.exception.TokenNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RefreshToken Service - RefreshTokenUseCase 구현체.
 *
 * <p>Refresh Token을 사용하여 새로운 Access Token을 재발급하는 Command Service입니다.
 * Refresh Token의 유효성을 검증하고 새로운 Access Token을 생성합니다.</p>
 *
 * <p><strong>비즈니스 로직 흐름:</strong></p>
 * <ol>
 *   <li>Refresh Token의 JWT 서명 및 형식 검증 (ValidateTokenPort)</li>
 *   <li>Token이 만료되지 않았는지 확인</li>
 *   <li>Redis에서 Refresh Token 존재 여부 확인 (LoadRefreshTokenPort)</li>
 *   <li>Blacklist에 등록되지 않았는지 확인 (CheckBlacklistPort)</li>
 *   <li>새로운 Access Token 생성 (GenerateTokenPort)</li>
 *   <li>Access Token 정보 반환 (Refresh Token은 재발급하지 않음)</li>
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ @Transactional 내 외부 API 호출 금지</li>
 *   <li>✅ Redis/MySQL은 내부 시스템으로 간주 (트랜잭션 내 호출 허용)</li>
 *   <li>✅ JWT 생성/검증은 메모리 계산 작업 (네트워크 I/O 없음)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Law of Demeter 준수 - Getter chaining 금지</li>
 * </ul>
 *
 * <p><strong>트랜잭션 경계:</strong></p>
 * <ul>
 *   <li>Token 검증: ValidateTokenPort (JWT 서명 검증 - 메모리 계산)</li>
 *   <li>Redis 조회: LoadRefreshTokenPort (Redis - 내부 I/O)</li>
 *   <li>Blacklist 확인: CheckBlacklistPort (Redis - 내부 I/O)</li>
 *   <li>Token 생성: GenerateTokenPort (JWT 서명 - 메모리 계산)</li>
 * </ul>
 *
 * <p><strong>보안 고려사항:</strong></p>
 * <ul>
 *   <li>Refresh Token은 Redis에 존재해야만 유효 (Rotation 전략)</li>
 *   <li>Blacklist에 등록된 Token은 사용 불가 (로그아웃)</li>
 *   <li>Refresh Token은 재발급하지 않음 (보안 강화)</li>
 *   <li>만료된 Token은 즉시 거부</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Service
public class RefreshTokenService implements RefreshTokenUseCase {

    // Token 검증 의존성
    private final ValidateTokenPort validateTokenPort;

    // Redis 조회 의존성
    private final LoadRefreshTokenPort loadRefreshTokenPort;
    private final CheckBlacklistPort checkBlacklistPort;

    // Token 생성 의존성
    private final GenerateTokenPort generateTokenPort;

    // JWT 토큰 유효 기간 설정 (외부 설정 파일에서 주입)
    private final JwtProperties jwtProperties;

    /**
     * RefreshTokenService 생성자.
     * Spring의 생성자 주입을 통해 의존성을 주입받습니다.
     *
     * @param validateTokenPort Token 검증 Port
     * @param loadRefreshTokenPort Refresh Token 조회 Port
     * @param checkBlacklistPort Blacklist 확인 Port
     * @param generateTokenPort Token 생성 Port
     * @param jwtProperties JWT 토큰 유효 기간 설정
     * @author AuthHub Team
     * @since 1.0.0
     */
    public RefreshTokenService(
            final ValidateTokenPort validateTokenPort,
            final LoadRefreshTokenPort loadRefreshTokenPort,
            final CheckBlacklistPort checkBlacklistPort,
            final GenerateTokenPort generateTokenPort,
            final JwtProperties jwtProperties
    ) {
        this.validateTokenPort = validateTokenPort;
        this.loadRefreshTokenPort = loadRefreshTokenPort;
        this.checkBlacklistPort = checkBlacklistPort;
        this.generateTokenPort = generateTokenPort;
        this.jwtProperties = jwtProperties;
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token을 발급합니다.
     *
     * <p><strong>트랜잭션 범위:</strong></p>
     * <ul>
     *   <li>✅ Token 검증 (JWT 서명 - 메모리 계산, 외부 API 아님)</li>
     *   <li>✅ Redis 조회 (내부 시스템)</li>
     *   <li>✅ Blacklist 확인 (Redis - 내부 시스템)</li>
     *   <li>✅ Access Token 생성 (JWT 서명 - 메모리 계산)</li>
     *   <li>❌ 외부 API 호출 없음 (S3, HTTP, SQS 등)</li>
     * </ul>
     *
     * @param command Refresh Token 재발급 요청 정보
     * @return Response 새로운 Access Token 정보 (accessToken, tokenType, expiresIn)
     * @throws InvalidTokenException Refresh Token 형식이 잘못되었거나 서명 검증 실패
     * @throws ExpiredTokenException Refresh Token이 만료된 경우
     * @throws TokenNotFoundException Refresh Token이 Redis에 존재하지 않는 경우
     * @throws BlacklistedTokenException Refresh Token이 Blacklist에 등록된 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    @Transactional
    public Response refresh(final Command command) {
        // ✅ 1. Refresh Token 검증 (JWT 서명 및 형식 검증 - 메모리 계산)
        final Token refreshToken = validateAndLoadRefreshToken(command.refreshToken());

        // ✅ 2. 새로운 Access Token 생성 (JWT 서명 - 메모리 계산)
        final Token newAccessToken = generateTokenPort.generate(
                refreshToken.getUserId(),
                TokenType.ACCESS,
                jwtProperties.accessTokenValidity()
        );

        // ✅ 3. Response 변환 및 반환
        return toRefreshResponse(newAccessToken);
    }

    /**
     * Refresh Token을 검증하고 Redis에서 로드합니다.
     *
     * <p><strong>검증 단계:</strong></p>
     * <ol>
     *   <li>JWT 서명 및 형식 검증</li>
     *   <li>만료 시각 확인</li>
     *   <li>Token 타입 확인 (REFRESH 타입이어야 함)</li>
     *   <li>Redis 존재 여부 확인</li>
     *   <li>Blacklist 등록 여부 확인</li>
     * </ol>
     *
     * @param jwtTokenString JWT 형식의 Refresh Token 문자열
     * @return 검증된 Refresh Token Domain Aggregate
     * @throws InvalidTokenException Token 형식이 잘못되었거나 REFRESH 타입이 아닌 경우
     * @throws ExpiredTokenException Token이 만료된 경우
     * @throws TokenNotFoundException Redis에 존재하지 않는 경우
     * @throws BlacklistedTokenException Blacklist에 등록된 경우
     */
    private Token validateAndLoadRefreshToken(final String jwtTokenString) {
        // 1. JWT 서명 및 형식 검증 (메모리 계산 - ValidateTokenPort가 예외 발생시킴)
        final Token token = validateTokenPort.validate(jwtTokenString);

        // 2. Token 타입 확인 (REFRESH 타입이어야 함)
        if (!token.isRefreshToken()) {
            throw new InvalidTokenException(
                    "Token type must be REFRESH, but was: " + token.getType()
            );
        }

        // 3. Token 만료 확인 (이미 ValidateTokenPort에서 검증했지만, 도메인 규칙으로 재확인)
        if (token.isExpired()) {
            throw new ExpiredTokenException(
                    "Refresh token has expired at: " + token.getExpiresAt().value()
            );
        }

        // 4. Redis에서 Refresh Token 존재 여부 확인
        final TokenId tokenId = token.getId();
        loadRefreshTokenPort.load(tokenId)
                .orElseThrow(() -> new TokenNotFoundException(
                        "Refresh token not found in Redis: " + tokenId.value()
                ));

        // 5. Blacklist 확인 (로그아웃된 Token인지 확인)
        if (checkBlacklistPort.isBlacklisted(tokenId)) {
            throw new BlacklistedTokenException(
                    "Refresh token is blacklisted (logged out): " + tokenId.value()
            );
        }

        return token;
    }

    /**
     * Access Token을 RefreshTokenUseCase.Response로 변환합니다.
     *
     * <p>TokenAssembler를 사용하지 않고 직접 변환합니다.
     * Refresh Token은 재발급하지 않으므로 Access Token 정보만 포함합니다.</p>
     *
     * @param accessToken 새로 생성된 Access Token
     * @return RefreshTokenUseCase.Response
     * @throws IllegalArgumentException accessToken이 null이거나 ACCESS 타입이 아닌 경우
     */
    private Response toRefreshResponse(final Token accessToken) {
        if (accessToken == null) {
            throw new IllegalArgumentException("accessToken cannot be null");
        }

        if (!accessToken.isAccessToken()) {
            throw new IllegalArgumentException("Token must be ACCESS type");
        }

        // ✅ Value Object에서 Primitive 타입 추출
        final String accessTokenValue = accessToken.getJwtToken().value();

        // ✅ Token의 행위 메서드 활용 - remainingValidity()
        // Math.toIntExact() 사용으로 오버플로우 시 ArithmeticException 발생 (안전한 변환)
        final int expiresInSeconds = Math.toIntExact(accessToken.remainingValidity().toSeconds());

        return new Response(
                accessTokenValue,
                "Bearer",  // RFC 6750 OAuth 2.0 Bearer Token 표준
                expiresInSeconds
        );
    }
}
