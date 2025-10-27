package com.ryuqq.authhub.adapter.in.rest.filter;

import com.ryuqq.authhub.adapter.in.rest.config.RateLimitProperties;
import com.ryuqq.authhub.application.security.ratelimit.port.in.CheckRateLimitUseCase;
import com.ryuqq.authhub.application.security.ratelimit.port.in.IncrementRateLimitUseCase;
import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitType;
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
import static org.mockito.Mockito.*;

/**
 * RateLimitFilter Unit Test.
 *
 * <p>RateLimitFilter의 기능을 검증하는 단위 테스트입니다.
 * Mock 객체를 사용하여 필터의 동작, 응답 상태 코드, Response Header를 검증합니다.</p>
 *
 * <p><strong>테스트 시나리오:</strong></p>
 * <ul>
 *   <li>제한 이내 요청 - 200 OK 응답</li>
 *   <li>제한 초과 요청 - 429 Too Many Requests 응답</li>
 *   <li>Response Header 검증 (X-RateLimit-*)</li>
 *   <li>IP 주소 추출 검증 (X-Forwarded-For)</li>
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
@DisplayName("RateLimitFilter 단위 테스트")
class RateLimitFilterTest {

    @Mock
    private CheckRateLimitUseCase checkRateLimitUseCase;

    @Mock
    private IncrementRateLimitUseCase incrementRateLimitUseCase;

    @Mock
    private RateLimitProperties rateLimitProperties;

    @Mock
    private FilterChain filterChain;

    private RateLimitFilter rateLimitFilter;

    @BeforeEach
    void setUp() {
        // RateLimitProperties Mock 기본값 설정 (lenient()로 모든 테스트에서 사용 가능)
        lenient().when(rateLimitProperties.getTimeWindowSeconds()).thenReturn(60L);
        lenient().when(rateLimitProperties.getIpBasedLimit()).thenReturn(100);

        rateLimitFilter = new RateLimitFilter(
                checkRateLimitUseCase,
                incrementRateLimitUseCase,
                rateLimitProperties
        );
    }

    /**
     * Rate Limit 이내 요청 시 정상 처리를 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>현재 요청 횟수: 50회</li>
     *   <li>제한 횟수: 100회</li>
     *   <li>남은 횟수: 50회</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/test 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>HTTP 200 OK 응답</li>
     *   <li>X-RateLimit-Limit: 100</li>
     *   <li>X-RateLimit-Remaining: 50</li>
     *   <li>IncrementRateLimitUseCase 호출됨</li>
     *   <li>FilterChain 계속 진행</li>
     * </ul>
     */
    @Test
    @DisplayName("제한 이내 요청 시 정상 처리되어야 한다")
    void shouldAllowRequestWhenWithinLimit() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");
        request.addHeader("X-Forwarded-For", "192.168.1.100");

        final MockHttpServletResponse response = new MockHttpServletResponse();

        final CheckRateLimitUseCase.Result result = new CheckRateLimitUseCase.Result(
                false,      // exceeded
                50,         // currentCount
                100,        // limitCount
                50L,        // remainingCount
                60L         // timeWindowSeconds
        );

        when(checkRateLimitUseCase.checkRateLimit(any(CheckRateLimitUseCase.Command.class)))
                .thenReturn(result);

        // When
        rateLimitFilter.doFilter(request, response, filterChain);

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getHeader("X-RateLimit-Limit")).isEqualTo("100");
        assertThat(response.getHeader("X-RateLimit-Remaining")).isEqualTo("50");
        assertThat(response.getHeader("X-RateLimit-Reset")).isNotNull();

        verify(checkRateLimitUseCase, times(1))
                .checkRateLimit(any(CheckRateLimitUseCase.Command.class));
        verify(incrementRateLimitUseCase, times(1))
                .incrementRateLimit(any(IncrementRateLimitUseCase.Command.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Rate Limit 초과 요청 시 429 응답을 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>현재 요청 횟수: 100회</li>
     *   <li>제한 횟수: 100회</li>
     *   <li>남은 횟수: 0회</li>
     *   <li>제한 초과: true</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/test 요청 (101회째)</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>HTTP 429 Too Many Requests 응답</li>
     *   <li>X-RateLimit-Limit: 100</li>
     *   <li>X-RateLimit-Remaining: 0</li>
     *   <li>IncrementRateLimitUseCase 호출 안됨</li>
     *   <li>FilterChain 중단</li>
     * </ul>
     */
    @Test
    @DisplayName("제한 초과 시 429 Too Many Requests 응답을 반환해야 한다")
    void shouldReturn429WhenLimitExceeded() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");
        request.addHeader("X-Forwarded-For", "192.168.1.100");

        final MockHttpServletResponse response = new MockHttpServletResponse();

        final CheckRateLimitUseCase.Result result = new CheckRateLimitUseCase.Result(
                true,       // exceeded
                100,        // currentCount
                100,        // limitCount
                0L,         // remainingCount
                60L         // timeWindowSeconds
        );

        when(checkRateLimitUseCase.checkRateLimit(any(CheckRateLimitUseCase.Command.class)))
                .thenReturn(result);

        // When
        rateLimitFilter.doFilter(request, response, filterChain);

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(response.getHeader("X-RateLimit-Limit")).isEqualTo("100");
        assertThat(response.getHeader("X-RateLimit-Remaining")).isEqualTo("0");
        assertThat(response.getHeader("X-RateLimit-Reset")).isNotNull();

        verify(checkRateLimitUseCase, times(1))
                .checkRateLimit(any(CheckRateLimitUseCase.Command.class));
        verify(incrementRateLimitUseCase, never())
                .incrementRateLimit(any(IncrementRateLimitUseCase.Command.class));
        verify(filterChain, never()).doFilter(request, response);
    }

    /**
     * Response Header에 Rate Limit 정보가 포함되는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>현재 요청 횟수: 25회</li>
     *   <li>제한 횟수: 100회</li>
     *   <li>남은 횟수: 75회</li>
     *   <li>시간 윈도우: 60초</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/test 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>X-RateLimit-Limit: "100"</li>
     *   <li>X-RateLimit-Remaining: "75"</li>
     *   <li>X-RateLimit-Reset: Unix Timestamp (현재 + 60초)</li>
     * </ul>
     */
    @Test
    @DisplayName("Response Header에 Rate Limit 정보가 포함되어야 한다")
    void shouldAddRateLimitHeaders() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");

        final MockHttpServletResponse response = new MockHttpServletResponse();

        final CheckRateLimitUseCase.Result result = new CheckRateLimitUseCase.Result(
                false,      // exceeded
                25,         // currentCount
                100,        // limitCount
                75L,        // remainingCount
                60L         // timeWindowSeconds
        );

        when(checkRateLimitUseCase.checkRateLimit(any(CheckRateLimitUseCase.Command.class)))
                .thenReturn(result);

        // When
        rateLimitFilter.doFilter(request, response, filterChain);

        // Then
        assertThat(response.getHeader("X-RateLimit-Limit")).isNotNull();
        assertThat(response.getHeader("X-RateLimit-Remaining")).isNotNull();
        assertThat(response.getHeader("X-RateLimit-Reset")).isNotNull();
        assertThat(response.getHeader("X-RateLimit-Limit")).isEqualTo("100");
        assertThat(response.getHeader("X-RateLimit-Remaining")).isEqualTo("75");
    }

    /**
     * X-Forwarded-For 헤더에서 클라이언트 IP를 정확히 추출하는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>X-Forwarded-For: "192.168.1.100, 10.0.0.1, 172.16.0.1"</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/test 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>CheckRateLimitUseCase.Command의 identifier가 "192.168.1.100"이어야 함</li>
     *   <li>프록시 체인의 첫 번째 IP만 사용</li>
     * </ul>
     */
    @Test
    @DisplayName("X-Forwarded-For 헤더에서 클라이언트 IP를 정확히 추출해야 한다")
    void shouldExtractClientIpFromXForwardedFor() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");
        request.addHeader("X-Forwarded-For", "192.168.1.100, 10.0.0.1, 172.16.0.1");

        final MockHttpServletResponse response = new MockHttpServletResponse();

        final CheckRateLimitUseCase.Result result = new CheckRateLimitUseCase.Result(
                false, 1, 100, 99L, 60L
        );

        when(checkRateLimitUseCase.checkRateLimit(any(CheckRateLimitUseCase.Command.class)))
                .thenReturn(result);

        // When
        rateLimitFilter.doFilter(request, response, filterChain);

        // Then - identifier가 첫 번째 IP인 "192.168.1.100"인지 검증
        verify(checkRateLimitUseCase).checkRateLimit(
                argThat(command ->
                        "192.168.1.100".equals(command.getIdentifier()) &&
                                RateLimitType.IP_BASED.equals(command.getType())
                )
        );
    }

    /**
     * X-Forwarded-For 헤더가 없을 때 RemoteAddr을 사용하는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>X-Forwarded-For 헤더 없음</li>
     *   <li>RemoteAddr: "127.0.0.1"</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/test 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>CheckRateLimitUseCase.Command의 identifier가 "127.0.0.1"이어야 함</li>
     * </ul>
     */
    @Test
    @DisplayName("X-Forwarded-For 헤더가 없을 때 RemoteAddr을 사용해야 한다")
    void shouldUseRemoteAddrWhenXForwardedForIsAbsent() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");
        request.setRemoteAddr("127.0.0.1");

        final MockHttpServletResponse response = new MockHttpServletResponse();

        final CheckRateLimitUseCase.Result result = new CheckRateLimitUseCase.Result(
                false, 1, 100, 99L, 60L
        );

        when(checkRateLimitUseCase.checkRateLimit(any(CheckRateLimitUseCase.Command.class)))
                .thenReturn(result);

        // When
        rateLimitFilter.doFilter(request, response, filterChain);

        // Then - identifier가 RemoteAddr인 "127.0.0.1"인지 검증
        verify(checkRateLimitUseCase).checkRateLimit(
                argThat(command ->
                        "127.0.0.1".equals(command.getIdentifier()) &&
                                RateLimitType.IP_BASED.equals(command.getType())
                )
        );
    }

    /**
     * 여러 엔드포인트에 대해 독립적으로 Rate Limit이 적용되는지 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>동일 IP에서 서로 다른 엔드포인트 요청</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>GET /api/v1/users</li>
     *   <li>GET /api/v1/orders</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>각 엔드포인트마다 CheckRateLimitUseCase 호출됨</li>
     *   <li>엔드포인트 정보가 Command에 포함됨</li>
     * </ul>
     */
    @Test
    @DisplayName("서로 다른 엔드포인트에 대해 독립적으로 Rate Limit이 적용되어야 한다")
    void shouldApplyRateLimitIndependentlyPerEndpoint() throws ServletException, IOException {
        // Given
        final CheckRateLimitUseCase.Result result = new CheckRateLimitUseCase.Result(
                false, 1, 100, 99L, 60L
        );

        when(checkRateLimitUseCase.checkRateLimit(any(CheckRateLimitUseCase.Command.class)))
                .thenReturn(result);

        // When - /api/v1/users 요청
        final MockHttpServletRequest request1 = new MockHttpServletRequest();
        request1.setRequestURI("/api/v1/users");
        request1.addHeader("X-Forwarded-For", "192.168.1.100");

        final MockHttpServletResponse response1 = new MockHttpServletResponse();

        rateLimitFilter.doFilter(request1, response1, filterChain);

        // When - /api/v1/orders 요청
        final MockHttpServletRequest request2 = new MockHttpServletRequest();
        request2.setRequestURI("/api/v1/orders");
        request2.addHeader("X-Forwarded-For", "192.168.1.100");

        final MockHttpServletResponse response2 = new MockHttpServletResponse();

        rateLimitFilter.doFilter(request2, response2, filterChain);

        // Then - 각 엔드포인트마다 호출됨
        verify(checkRateLimitUseCase).checkRateLimit(
                argThat(command -> "/api/v1/users".equals(command.getEndpoint()))
        );

        verify(checkRateLimitUseCase).checkRateLimit(
                argThat(command -> "/api/v1/orders".equals(command.getEndpoint()))
        );
    }
}
