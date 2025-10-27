package com.ryuqq.authhub.adapter.in.rest.filter;

import com.ryuqq.authhub.application.security.blacklist.port.in.CheckBlacklistUseCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * BlacklistCheckFilter Unit Test.
 *
 * <p>BlacklistCheckFilter의 기능을 검증하는 단위 테스트입니다.
 * Mock 객체를 사용하여 필터의 동작, 응답 상태 코드, 에러 메시지를 검증합니다.</p>
 *
 * <p><strong>테스트 시나리오:</strong></p>
 * <ul>
 *   <li>블랙리스트에 없는 토큰 - 다음 필터로 진행</li>
 *   <li>블랙리스트에 있는 토큰 - 401 Unauthorized 응답</li>
 *   <li>Authorization Header 없음 - 다음 필터로 진행</li>
 *   <li>Bearer Prefix 없음 - 다음 필터로 진행</li>
 *   <li>Public endpoints - 필터 제외</li>
 *   <li>JWT 파싱 실패 - 다음 필터로 진행</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ UseCase Mocking - 단위 테스트 격리</li>
 *   <li>✅ @DisplayName - 한글 테스트 설명</li>
 *   <li>✅ AssertJ - 가독성 높은 Assertion</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BlacklistCheckFilter 단위 테스트")
class BlacklistCheckFilterTest {

    @Mock
    private CheckBlacklistUseCase checkBlacklistUseCase;

    @Mock
    private FilterChain filterChain;

    private BlacklistCheckFilter blacklistCheckFilter;

    /**
     * 유효한 JWT 토큰 예시 (테스트용).
     * JTI: "test-jti-123"
     */
    private static final String VALID_JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiJ0ZXN0LWp0aS0xMjMiLCJzdWIiOiIxIiwiZXhwIjoxNzM1Njg5NjAwfQ.signature";

    /**
     * 블랙리스트에 등록된 JWT 토큰 예시 (테스트용).
     * JTI: "blacklisted-jti-456"
     */
    private static final String BLACKLISTED_JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiJibGFja2xpc3RlZC1qdGktNDU2Iiwic3ViIjoiMiIsImV4cCI6MTczNTY4OTYwMH0.signature";

    /**
     * 잘못된 형식의 JWT 토큰 (파싱 실패).
     */
    private static final String INVALID_JWT_TOKEN = "invalid.jwt.token";

    @BeforeEach
    void setUp() {
        blacklistCheckFilter = new BlacklistCheckFilter(checkBlacklistUseCase);
    }

    /**
     * 블랙리스트에 없는 토큰으로 요청 시 다음 필터로 진행하는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>유효한 JWT 토큰</li>
     *   <li>블랙리스트에 없음 (isBlacklisted = false)</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/users 요청</li>
     *   <li>Authorization: Bearer [valid-token]</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>CheckBlacklistUseCase.isBlacklisted() 호출됨</li>
     *   <li>FilterChain.doFilter() 호출됨 (다음 필터로 진행)</li>
     *   <li>HTTP 200 OK 응답 (응답 상태 코드 변경 없음)</li>
     * </ul>
     */
    @Test
    @DisplayName("블랙리스트에 없는 토큰 - 다음 필터로 진행")
    void testNonBlacklistedToken_ShouldContinueFilterChain() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/api/v1/users");
        request.addHeader("Authorization", "Bearer " + VALID_JWT_TOKEN);

        // CheckBlacklistUseCase Mock: 블랙리스트에 없음
        when(checkBlacklistUseCase.isBlacklisted(any(CheckBlacklistUseCase.Query.class)))
                .thenReturn(false);

        // When
        blacklistCheckFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(checkBlacklistUseCase, times(1))
                .isBlacklisted(any(CheckBlacklistUseCase.Query.class));
        verify(filterChain, times(1))
                .doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * 블랙리스트에 등록된 토큰으로 요청 시 401 응답을 반환하는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>JWT 토큰</li>
     *   <li>블랙리스트에 등록됨 (isBlacklisted = true)</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/users 요청</li>
     *   <li>Authorization: Bearer [blacklisted-token]</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>CheckBlacklistUseCase.isBlacklisted() 호출됨</li>
     *   <li>HTTP 401 Unauthorized 응답</li>
     *   <li>응답 Body: {"error": "Token has been revoked"}</li>
     *   <li>FilterChain.doFilter() 호출되지 않음 (필터 체인 중단)</li>
     * </ul>
     */
    @Test
    @DisplayName("블랙리스트 토큰 - 401 Unauthorized 응답")
    void testBlacklistedToken_ShouldReturn401() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/api/v1/users");
        request.addHeader("Authorization", "Bearer " + BLACKLISTED_JWT_TOKEN);

        // CheckBlacklistUseCase Mock: 블랙리스트에 등록됨
        when(checkBlacklistUseCase.isBlacklisted(any(CheckBlacklistUseCase.Query.class)))
                .thenReturn(true);

        // When
        blacklistCheckFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(checkBlacklistUseCase, times(1))
                .isBlacklisted(any(CheckBlacklistUseCase.Query.class));
        verify(filterChain, never())
                .doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentAsString()).contains("Token has been revoked");
    }

    /**
     * Authorization Header가 없는 요청 시 다음 필터로 진행하는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>Authorization Header 없음</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/users 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>CheckBlacklistUseCase.isBlacklisted() 호출되지 않음</li>
     *   <li>FilterChain.doFilter() 호출됨 (다음 필터로 진행)</li>
     *   <li>HTTP 200 OK 응답</li>
     * </ul>
     */
    @Test
    @DisplayName("Authorization Header 없음 - 다음 필터로 진행")
    void testNoAuthorizationHeader_ShouldContinueFilterChain() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/api/v1/users");
        // Authorization Header 없음

        // When
        blacklistCheckFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(checkBlacklistUseCase, never())
                .isBlacklisted(any(CheckBlacklistUseCase.Query.class));
        verify(filterChain, times(1))
                .doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Bearer Prefix가 없는 Authorization Header 요청 시 다음 필터로 진행하는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>Authorization Header: "eyJhbGc..." (Bearer Prefix 없음)</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/users 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>CheckBlacklistUseCase.isBlacklisted() 호출되지 않음</li>
     *   <li>FilterChain.doFilter() 호출됨 (다음 필터로 진행)</li>
     *   <li>HTTP 200 OK 응답</li>
     * </ul>
     */
    @Test
    @DisplayName("Bearer Prefix 없음 - 다음 필터로 진행")
    void testNoBearerPrefix_ShouldContinueFilterChain() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/api/v1/users");
        request.addHeader("Authorization", VALID_JWT_TOKEN);  // Bearer Prefix 없음

        // When
        blacklistCheckFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(checkBlacklistUseCase, never())
                .isBlacklisted(any(CheckBlacklistUseCase.Query.class));
        verify(filterChain, times(1))
                .doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Public endpoint (로그인 API) 요청 시 필터를 제외하는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>요청 URI: /api/v1/auth/login</li>
     *   <li>Authorization Header 있음</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>POST /api/v1/auth/login 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>shouldNotFilter() = true</li>
     *   <li>doFilterInternal() 호출되지 않음</li>
     * </ul>
     */
    @Test
    @DisplayName("Public endpoint (로그인) - 필터 제외")
    void testPublicEndpoint_Login_ShouldNotFilter() throws ServletException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");

        // When
        final boolean shouldNotFilter = blacklistCheckFilter.shouldNotFilter(request);

        // Then
        assertThat(shouldNotFilter).isTrue();
    }

    /**
     * Public endpoint (회원가입 API) 요청 시 필터를 제외하는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>요청 URI: /api/v1/auth/register</li>
     *   <li>Authorization Header 있음</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>POST /api/v1/auth/register 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>shouldNotFilter() = true</li>
     *   <li>doFilterInternal() 호출되지 않음</li>
     * </ul>
     */
    @Test
    @DisplayName("Public endpoint (회원가입) - 필터 제외")
    void testPublicEndpoint_Register_ShouldNotFilter() throws ServletException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/register");

        // When
        final boolean shouldNotFilter = blacklistCheckFilter.shouldNotFilter(request);

        // Then
        assertThat(shouldNotFilter).isTrue();
    }

    /**
     * JWT 파싱 실패 시 다음 필터로 진행하는지 검증합니다 (Spring Security에서 처리).
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>잘못된 형식의 JWT 토큰</li>
     *   <li>JwtParser.extractJti() 실패</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/users 요청</li>
     *   <li>Authorization: Bearer [invalid-token]</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>JwtException 발생 (내부적으로 catch됨)</li>
     *   <li>CheckBlacklistUseCase.isBlacklisted() 호출되지 않음</li>
     *   <li>FilterChain.doFilter() 호출됨 (다음 필터로 진행)</li>
     *   <li>HTTP 200 OK 응답</li>
     * </ul>
     */
    @Test
    @DisplayName("JWT 파싱 실패 - 다음 필터로 진행 (Spring Security에서 처리)")
    void testInvalidJwtToken_ShouldContinueFilterChain() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/api/v1/users");
        request.addHeader("Authorization", "Bearer " + INVALID_JWT_TOKEN);

        // When
        blacklistCheckFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(checkBlacklistUseCase, never())
                .isBlacklisted(any(CheckBlacklistUseCase.Query.class));
        verify(filterChain, times(1))
                .doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
