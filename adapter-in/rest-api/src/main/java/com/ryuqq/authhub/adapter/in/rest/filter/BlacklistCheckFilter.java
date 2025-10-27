package com.ryuqq.authhub.adapter.in.rest.filter;

import com.ryuqq.authhub.adapter.in.rest.util.JwtParser;
import com.ryuqq.authhub.application.security.blacklist.port.in.CheckBlacklistUseCase;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * Blacklist Check Filter - JWT 토큰 블랙리스트 확인 필터.
 *
 * <p>Redis 기반 블랙리스트를 확인하여 무효화된 JWT 토큰의 재사용을 차단하는 Spring Filter입니다.
 * OncePerRequestFilter를 상속하여 요청당 한 번만 실행됨을 보장하며,
 * 블랙리스트에 등록된 토큰 감지 시 HTTP 401 Unauthorized 응답을 반환합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Authorization Header에서 Bearer 토큰 추출</li>
 *   <li>JWT에서 JTI(JWT ID) 추출 (JwtParser 유틸리티 사용)</li>
 *   <li>Redis 블랙리스트 확인 (CheckBlacklistUseCase)</li>
 *   <li>블랙리스트 토큰 감지 시 401 응답 반환 및 필터 체인 중단</li>
 *   <li>Public endpoints는 필터링 제외</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Law of Demeter 준수 - Getter chaining 금지</li>
 *   <li>✅ Port/Adapter 패턴 - UseCase를 통한 Application Layer 접근</li>
 *   <li>✅ @Transactional 미사용 - Filter는 트랜잭션 밖에서 실행</li>
 *   <li>✅ @Order(2) 설정 - SecurityHeaderFilter 이후 실행</li>
 * </ul>
 *
 * <p><strong>블랙리스트 정책:</strong></p>
 * <ul>
 *   <li>저장소: Redis SET (key: "blacklist:tokens")</li>
 *   <li>확인 복잡도: O(1) (SISMEMBER 명령)</li>
 *   <li>등록 사유: 로그아웃, 비밀번호 변경, 보안 침해 등</li>
 *   <li>TTL: JWT 만료 시간까지 유지 (자동 정리)</li>
 * </ul>
 *
 * <p><strong>실행 순서 (Filter Chain):</strong></p>
 * <ol>
 *   <li>@Order(1) - SecurityHeaderFilter (보안 헤더 추가)</li>
 *   <li><strong>@Order(2) - BlacklistCheckFilter (블랙리스트 확인)</strong></li>
 *   <li>@Order(3) - RateLimitFilter (Rate Limiting)</li>
 *   <li>Spring Security Filter Chain</li>
 * </ol>
 *
 * <p><strong>실행 흐름:</strong></p>
 * <ol>
 *   <li>Public endpoints 확인 (shouldNotFilter):
 *     <ul>
 *       <li>/api/v1/auth/login → 필터 제외</li>
 *       <li>/api/v1/auth/register → 필터 제외</li>
 *     </ul>
 *   </li>
 *   <li>Authorization Header에서 Bearer 토큰 추출</li>
 *   <li>토큰이 있는 경우:
 *     <ul>
 *       <li>JwtParser로 JTI 추출</li>
 *       <li>CheckBlacklistUseCase로 블랙리스트 확인</li>
 *       <li>블랙리스트에 있으면 → 401 응답 반환, 필터 체인 중단</li>
 *       <li>블랙리스트에 없으면 → 다음 필터로 진행</li>
 *     </ul>
 *   </li>
 *   <li>토큰이 없는 경우 → 다음 필터로 진행 (인증은 Spring Security에서 처리)</li>
 * </ol>
 *
 * <p><strong>에러 처리:</strong></p>
 * <ul>
 *   <li>JWT 파싱 실패 (JwtException) → 다음 필터로 진행 (Spring Security에서 처리)</li>
 *   <li>Redis 연결 실패 → 예외 전파 (Circuit Breaker에서 처리)</li>
 *   <li>블랙리스트 확인 실패 → 예외 전파 (안전을 위해 요청 차단)</li>
 * </ul>
 *
 * <p><strong>성능 고려사항:</strong></p>
 * <ul>
 *   <li>Redis SISMEMBER - O(1) 복잡도, 매우 빠름 (< 1ms)</li>
 *   <li>JWT 파싱 - 서명 검증 제외, Base64 디코딩만 수행 (< 1ms)</li>
 *   <li>블랙리스트 확인 후 즉시 401 반환 (빠른 실패)</li>
 *   <li>Public endpoints는 필터 제외 (불필요한 검사 생략)</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // Spring Boot에서 자동으로 Bean 등록 및 적용
 *
 * // 정상 토큰 (블랙리스트에 없음)
 * GET /api/v1/users
 * Authorization: Bearer eyJhbGc...valid-token
 * → 200 OK (다음 필터로 진행)
 *
 * // 블랙리스트 토큰 (로그아웃된 토큰)
 * GET /api/v1/users
 * Authorization: Bearer eyJhbGc...blacklisted-token
 * → 401 Unauthorized {"error": "Token has been revoked"}
 *
 * // Public endpoint (필터 제외)
 * POST /api/v1/auth/login
 * → 200 OK (필터 실행 안 됨)
 * </pre>
 *
 * <p><strong>의존성:</strong></p>
 * <ul>
 *   <li>CheckBlacklistUseCase - 블랙리스트 확인 (Query)</li>
 *   <li>JwtParser - JWT 파싱 및 JTI 추출 (Utility)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
@Order(2)
public class BlacklistCheckFilter extends OncePerRequestFilter {

    /**
     * Authorization Header 이름.
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Bearer 토큰 Prefix.
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Bearer Prefix 길이 (7글자).
     */
    private static final int BEARER_PREFIX_LENGTH = 7;

    /**
     * Public endpoint - 로그인 API.
     */
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";

    /**
     * Public endpoint - 회원가입 API.
     */
    private static final String REGISTER_ENDPOINT = "/api/v1/auth/register";

    /**
     * 에러 응답 Content-Type.
     */
    private static final String ERROR_CONTENT_TYPE = "application/json;charset=UTF-8";

    /**
     * 에러 응답 메시지 - 토큰 무효화.
     */
    private static final String ERROR_MESSAGE_REVOKED = "{\"error\": \"Token has been revoked\"}";

    private final CheckBlacklistUseCase checkBlacklistUseCase;

    /**
     * BlacklistCheckFilter 생성자.
     *
     * @param checkBlacklistUseCase 블랙리스트 확인 UseCase
     * @throws NullPointerException 파라미터가 null인 경우
     */
    public BlacklistCheckFilter(final CheckBlacklistUseCase checkBlacklistUseCase) {
        this.checkBlacklistUseCase = Objects.requireNonNull(
                checkBlacklistUseCase,
                "CheckBlacklistUseCase cannot be null"
        );
    }

    /**
     * 요청 필터링 로직을 수행합니다.
     *
     * <p>OncePerRequestFilter에 의해 요청당 한 번만 실행됩니다.
     * JWT 토큰 블랙리스트를 확인하여 무효화된 토큰의 재사용을 차단합니다.</p>
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 다음 필터 체인
     * @throws ServletException 서블릿 처리 중 예외 발생 시
     * @throws IOException I/O 처리 중 예외 발생 시
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Authorization Header에서 Bearer 토큰 추출
        final String token = this.extractToken(request);

        // 2. 토큰이 있는 경우에만 블랙리스트 확인
        if (token != null) {
            try {
                // 3. JWT에서 JTI 추출
                final String jti = JwtParser.extractJti(token);

                // 4. 블랙리스트 확인
                final CheckBlacklistUseCase.Query query = new CheckBlacklistUseCase.Query(jti);
                final boolean isBlacklisted = this.checkBlacklistUseCase.isBlacklisted(query);

                // 5. 블랙리스트에 등록된 토큰이면 401 응답 반환
                if (isBlacklisted) {
                    this.handleBlacklistedToken(response);
                    return;  // 필터 체인 중단
                }

            } catch (final JwtException e) {
                // JWT 파싱 실패 - 다음 필터로 진행 (Spring Security에서 처리)
                // 로그는 생략 (성능 고려, Spring Security에서 처리)
            }
        }

        // 6. 다음 필터 실행
        filterChain.doFilter(request, response);
    }

    /**
     * Public endpoints는 필터링을 제외합니다.
     *
     * <p>로그인, 회원가입 등 인증이 필요 없는 Public API는
     * 블랙리스트 확인을 수행하지 않습니다.</p>
     *
     * <p><strong>제외 대상:</strong></p>
     * <ul>
     *   <li>/api/v1/auth/login - 로그인 API</li>
     *   <li>/api/v1/auth/register - 회원가입 API</li>
     * </ul>
     *
     * @param request HTTP 요청 객체
     * @return Public endpoint이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        final String path = request.getRequestURI();
        return path.startsWith(LOGIN_ENDPOINT) || path.startsWith(REGISTER_ENDPOINT);
    }

    /**
     * Authorization Header에서 Bearer 토큰을 추출합니다.
     *
     * <p>Authorization Header에서 "Bearer " Prefix를 제거하고
     * 실제 JWT 토큰 문자열만 추출합니다.</p>
     *
     * <p><strong>추출 로직:</strong></p>
     * <ol>
     *   <li>Authorization Header 확인</li>
     *   <li>"Bearer " Prefix 확인</li>
     *   <li>Prefix 제거 후 토큰 문자열 반환</li>
     * </ol>
     *
     * <p><strong>Authorization Header 예시:</strong></p>
     * <ul>
     *   <li>"Bearer eyJhbGc..." → "eyJhbGc..."</li>
     *   <li>"eyJhbGc..." (Bearer 없음) → null</li>
     *   <li>null 또는 빈 문자열 → null</li>
     * </ul>
     *
     * @param request HTTP 요청 객체
     * @return JWT 토큰 문자열 (Bearer Prefix 제거), 토큰이 없으면 null
     * @author AuthHub Team
     * @since 1.0.0
     */
    private String extractToken(final HttpServletRequest request) {
        final String header = request.getHeader(AUTHORIZATION_HEADER);

        // Authorization Header가 없거나 빈 문자열이면 null 반환
        if (header == null || header.isBlank()) {
            return null;
        }

        // Bearer Prefix가 있는 경우에만 토큰 추출
        if (header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX_LENGTH);
        }

        // Bearer Prefix가 없으면 null 반환
        return null;
    }

    /**
     * 블랙리스트 토큰 감지 시 401 응답을 처리합니다.
     *
     * <p>HTTP 401 Unauthorized 상태 코드를 설정하고
     * JSON 형식의 에러 메시지를 응답 Body에 포함합니다.</p>
     *
     * <p><strong>응답 형식:</strong></p>
     * <pre>
     * HTTP/1.1 401 Unauthorized
     * Content-Type: application/json;charset=UTF-8
     *
     * {"error": "Token has been revoked"}
     * </pre>
     *
     * @param response HTTP 응답 객체
     * @throws IOException I/O 처리 중 예외 발생 시
     * @author AuthHub Team
     * @since 1.0.0
     */
    private void handleBlacklistedToken(final HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(ERROR_CONTENT_TYPE);
        response.getWriter().write(ERROR_MESSAGE_REVOKED);
    }
}
