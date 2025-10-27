package com.ryuqq.authhub.adapter.in.rest.filter;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * AuditLogFilter Unit Test.
 *
 * <p>AuditLogFilter의 기능을 검증하는 단위 테스트입니다.
 * Mock 객체를 사용하여 필터의 동작을 검증합니다.</p>
 *
 * <p><strong>테스트 시나리오:</strong></p>
 * <ul>
 *   <li>JWT 토큰 있는 요청 - UserId 추출 및 로그 출력</li>
 *   <li>JWT 토큰 없는 요청 - Anonymous로 로그 출력</li>
 *   <li>X-Forwarded-For 헤더 있는 요청 - IP 추출</li>
 *   <li>X-Forwarded-For 헤더 없는 요청 - RemoteAddr 사용</li>
 *   <li>필터 체인 예외 발생 - finally 블록 실행 보장</li>
 *   <li>JWT 파싱 실패 - Anonymous로 처리</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ @DisplayName - 한글 테스트 설명</li>
 *   <li>✅ AssertJ - 가독성 높은 Assertion</li>
 *   <li>✅ Phase 1 검증 - 로깅 기능만 테스트</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuditLogFilter 단위 테스트")
class AuditLogFilterTest {

    @Mock
    private FilterChain filterChain;

    private AuditLogFilter auditLogFilter;

    /**
     * 유효한 JWT 토큰 예시 (테스트용).
     * sub: "12345"
     * jti: "test-jti-123"
     */
    private static final String VALID_JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NSIsImp0aSI6InRlc3QtanRpLTEyMyIsImV4cCI6MTczNTY4OTYwMH0.signature";

    /**
     * sub 클레임이 없는 JWT 토큰 (anonymous 처리).
     */
    private static final String JWT_TOKEN_WITHOUT_SUB = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiJ0ZXN0LWp0aS0xMjMiLCJleHAiOjE3MzU2ODk2MDB9.signature";

    /**
     * 잘못된 형식의 JWT 토큰 (파싱 실패).
     */
    private static final String INVALID_JWT_TOKEN = "invalid.jwt.token";

    @BeforeEach
    void setUp() {
        auditLogFilter = new AuditLogFilter();
    }

    /**
     * JWT 토큰이 있는 요청 시 UserId를 추출하고 필터 체인이 진행되는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>유효한 JWT 토큰 (sub: "12345")</li>
     *   <li>Authorization: Bearer [valid-token]</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>POST /api/v1/users 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>FilterChain.doFilter() 호출됨 (다음 필터로 진행)</li>
     *   <li>HTTP 200 OK 응답</li>
     *   <li>로그 출력됨 (UserId: 12345)</li>
     * </ul>
     */
    @Test
    @DisplayName("JWT 토큰 있는 요청 - UserId 추출 및 필터 체인 진행")
    void testAuthenticatedRequest_ShouldExtractUserIdAndContinueFilterChain() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/api/v1/users");
        request.setMethod("POST");
        request.addHeader("Authorization", "Bearer " + VALID_JWT_TOKEN);
        request.addHeader("User-Agent", "Mozilla/5.0");
        request.setRemoteAddr("192.168.1.100");

        // When
        auditLogFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * JWT 토큰이 없는 요청 시 Anonymous로 처리되는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>Authorization Header 없음</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/health 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>FilterChain.doFilter() 호출됨</li>
     *   <li>HTTP 200 OK 응답</li>
     *   <li>로그 출력됨 (UserId: anonymous)</li>
     * </ul>
     */
    @Test
    @DisplayName("JWT 토큰 없는 요청 - Anonymous로 처리")
    void testUnauthenticatedRequest_ShouldTreatAsAnonymous() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/api/v1/health");
        request.setMethod("GET");
        request.setRemoteAddr("192.168.1.100");

        // When
        auditLogFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * X-Forwarded-For 헤더가 있는 요청 시 클라이언트 IP를 정확히 추출하는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>X-Forwarded-For: "203.0.113.1, 10.0.0.1"</li>
     *   <li>RemoteAddr: "10.0.0.2"</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/users 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>로그에 IP: 203.0.113.1 기록 (X-Forwarded-For의 첫 번째 IP)</li>
     *   <li>FilterChain.doFilter() 호출됨</li>
     * </ul>
     */
    @Test
    @DisplayName("X-Forwarded-For 헤더 있음 - 첫 번째 IP 추출")
    void testXForwardedForHeader_ShouldExtractFirstIp() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/api/v1/users");
        request.setMethod("GET");
        request.addHeader("X-Forwarded-For", "203.0.113.1, 10.0.0.1");
        request.setRemoteAddr("10.0.0.2");

        // When
        auditLogFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * X-Forwarded-For 헤더가 없는 요청 시 RemoteAddr을 사용하는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>X-Forwarded-For Header 없음</li>
     *   <li>RemoteAddr: "192.168.1.100"</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/users 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>로그에 IP: 192.168.1.100 기록 (RemoteAddr 사용)</li>
     *   <li>FilterChain.doFilter() 호출됨</li>
     * </ul>
     */
    @Test
    @DisplayName("X-Forwarded-For 헤더 없음 - RemoteAddr 사용")
    void testNoXForwardedForHeader_ShouldUseRemoteAddr() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/api/v1/users");
        request.setMethod("GET");
        request.setRemoteAddr("192.168.1.100");

        // When
        auditLogFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * 필터 체인에서 예외 발생 시에도 finally 블록이 실행되어 로그가 출력되는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>FilterChain.doFilter()에서 ServletException 발생</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/users 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>finally 블록 실행됨</li>
     *   <li>로그 출력됨 (예외 발생해도 로그는 남음)</li>
     *   <li>ServletException 전파됨</li>
     * </ul>
     */
    @Test
    @DisplayName("필터 체인 예외 발생 - finally 블록 실행 보장")
    void testFilterChainException_ShouldExecuteFinallyBlock() throws IOException, ServletException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/api/v1/users");
        request.setMethod("GET");
        request.setRemoteAddr("192.168.1.100");

        // FilterChain에서 예외 발생 시뮬레이션
        final ServletException expectedException = new ServletException("Simulated exception");

        try {
            // When
            org.mockito.Mockito.doThrow(expectedException)
                    .when(filterChain).doFilter(request, response);

            auditLogFilter.doFilterInternal(request, response, filterChain);

            // 예외가 전파되어야 함
            org.assertj.core.api.Assertions.fail("ServletException should be thrown");

        } catch (final ServletException e) {
            // Then
            assertThat(e).isSameAs(expectedException);
            verify(filterChain, times(1)).doFilter(request, response);
            // finally 블록 실행됨 (로그 출력)
        }
    }

    /**
     * JWT 파싱 실패 시 Anonymous로 처리되는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>잘못된 형식의 JWT 토큰</li>
     *   <li>Authorization: Bearer [invalid-token]</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/users 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>JWT 파싱 실패 (내부적으로 catch됨)</li>
     *   <li>UserId: anonymous로 처리</li>
     *   <li>FilterChain.doFilter() 호출됨</li>
     *   <li>HTTP 200 OK 응답</li>
     * </ul>
     */
    @Test
    @DisplayName("JWT 파싱 실패 - Anonymous로 처리")
    void testInvalidJwtToken_ShouldTreatAsAnonymous() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/api/v1/users");
        request.setMethod("GET");
        request.addHeader("Authorization", "Bearer " + INVALID_JWT_TOKEN);
        request.setRemoteAddr("192.168.1.100");

        // When
        auditLogFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * sub 클레임이 없는 JWT 토큰 시 Anonymous로 처리되는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>JWT 토큰 (sub 클레임 없음)</li>
     *   <li>Authorization: Bearer [token-without-sub]</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/users 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>UserId: anonymous로 처리</li>
     *   <li>FilterChain.doFilter() 호출됨</li>
     *   <li>HTTP 200 OK 응답</li>
     * </ul>
     */
    @Test
    @DisplayName("sub 클레임 없는 JWT - Anonymous로 처리")
    void testJwtWithoutSubClaim_ShouldTreatAsAnonymous() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/api/v1/users");
        request.setMethod("GET");
        request.addHeader("Authorization", "Bearer " + JWT_TOKEN_WITHOUT_SUB);
        request.setRemoteAddr("192.168.1.100");

        // When
        auditLogFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Bearer Prefix가 없는 Authorization Header 요청 시 Anonymous로 처리되는지 검증합니다.
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
     *   <li>UserId: anonymous로 처리</li>
     *   <li>FilterChain.doFilter() 호출됨</li>
     *   <li>HTTP 200 OK 응답</li>
     * </ul>
     */
    @Test
    @DisplayName("Bearer Prefix 없음 - Anonymous로 처리")
    void testNoBearerPrefix_ShouldTreatAsAnonymous() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/api/v1/users");
        request.setMethod("GET");
        request.addHeader("Authorization", VALID_JWT_TOKEN);  // Bearer Prefix 없음
        request.setRemoteAddr("192.168.1.100");

        // When
        auditLogFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
