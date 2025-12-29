package com.ryuqq.authhub.sdk.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.sdk.auth.ThreadLocalTokenResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@DisplayName("AuthHubTokenContextFilter")
@ExtendWith(MockitoExtension.class)
class AuthHubTokenContextFilterTest {

    private AuthHubTokenContextFilter filter;

    @Mock private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new AuthHubTokenContextFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() {
        ThreadLocalTokenResolver.clear();
    }

    @Nested
    @DisplayName("Bearer 토큰 추출")
    class BearerTokenExtraction {

        @Test
        @DisplayName("Authorization 헤더의 Bearer 토큰을 ThreadLocal에 설정한다")
        void shouldSetBearerTokenToThreadLocal() throws ServletException, IOException {
            // given
            String token = "user-jwt-token";
            request.addHeader("Authorization", "Bearer " + token);

            // when
            filter.doFilter(
                    request,
                    response,
                    (req, res) -> {
                        // then - 필터 체인 내에서 토큰 확인
                        assertThat(ThreadLocalTokenResolver.INSTANCE.resolve()).contains(token);
                    });
        }

        @Test
        @DisplayName("Authorization 헤더가 없으면 ThreadLocal에 토큰을 설정하지 않는다")
        void shouldNotSetTokenWhenNoAuthHeader() throws ServletException, IOException {
            // when
            filter.doFilter(
                    request,
                    response,
                    (req, res) -> {
                        // then
                        assertThat(ThreadLocalTokenResolver.INSTANCE.resolve()).isEmpty();
                    });
        }

        @Test
        @DisplayName("Bearer prefix가 없으면 ThreadLocal에 토큰을 설정하지 않는다")
        void shouldNotSetTokenWhenNoBearerPrefix() throws ServletException, IOException {
            // given
            request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

            // when
            filter.doFilter(
                    request,
                    response,
                    (req, res) -> {
                        // then
                        assertThat(ThreadLocalTokenResolver.INSTANCE.resolve()).isEmpty();
                    });
        }

        @Test
        @DisplayName("빈 토큰도 처리할 수 있다")
        void shouldHandleEmptyToken() throws ServletException, IOException {
            // given
            request.addHeader("Authorization", "Bearer ");

            // when
            filter.doFilter(
                    request,
                    response,
                    (req, res) -> {
                        // then - 빈 문자열도 토큰으로 설정됨
                        assertThat(ThreadLocalTokenResolver.INSTANCE.resolve()).contains("");
                    });
        }
    }

    @Nested
    @DisplayName("필터 체인 실행")
    class FilterChainExecution {

        @Test
        @DisplayName("필터 체인을 호출한다")
        void shouldCallFilterChain() throws ServletException, IOException {
            // when
            filter.doFilter(request, response, filterChain);

            // then
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("토큰이 있어도 필터 체인을 호출한다")
        void shouldCallFilterChainWithToken() throws ServletException, IOException {
            // given
            request.addHeader("Authorization", "Bearer test-token");

            // when
            filter.doFilter(request, response, filterChain);

            // then
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("ThreadLocal 정리")
    class ThreadLocalCleanup {

        @Test
        @DisplayName("필터 완료 후 ThreadLocal을 정리한다")
        void shouldClearThreadLocalAfterFilter() throws ServletException, IOException {
            // given
            request.addHeader("Authorization", "Bearer test-token");

            // when
            filter.doFilter(request, response, filterChain);

            // then - 필터 완료 후 토큰 정리됨
            assertThat(ThreadLocalTokenResolver.INSTANCE.resolve()).isEmpty();
        }

        @Test
        @DisplayName("예외가 발생해도 ThreadLocal을 정리한다")
        void shouldClearThreadLocalEvenOnException() {
            // given
            request.addHeader("Authorization", "Bearer test-token");
            ServletException expectedException = new ServletException("Test exception");

            // when & then
            try {
                filter.doFilter(
                        request,
                        response,
                        (req, res) -> {
                            // 토큰이 설정되었는지 확인
                            assertThat(ThreadLocalTokenResolver.INSTANCE.resolve())
                                    .contains("test-token");
                            throw expectedException;
                        });
            } catch (Exception e) {
                assertThat(e).isSameAs(expectedException);
            }

            // ThreadLocal이 정리되었는지 확인
            assertThat(ThreadLocalTokenResolver.INSTANCE.resolve()).isEmpty();
        }
    }

    @Nested
    @DisplayName("여러 요청 처리")
    class MultipleRequests {

        @Test
        @DisplayName("각 요청은 독립적인 토큰을 가진다")
        void shouldHandleMultipleRequestsIndependently() throws ServletException, IOException {
            // given
            MockHttpServletRequest request1 = new MockHttpServletRequest();
            MockHttpServletRequest request2 = new MockHttpServletRequest();
            request1.addHeader("Authorization", "Bearer token1");
            request2.addHeader("Authorization", "Bearer token2");

            // when & then
            filter.doFilter(
                    request1,
                    response,
                    (req, res) -> {
                        assertThat(ThreadLocalTokenResolver.INSTANCE.resolve()).contains("token1");
                    });

            // 첫 번째 요청 후 정리됨
            assertThat(ThreadLocalTokenResolver.INSTANCE.resolve()).isEmpty();

            filter.doFilter(
                    request2,
                    response,
                    (req, res) -> {
                        assertThat(ThreadLocalTokenResolver.INSTANCE.resolve()).contains("token2");
                    });
        }
    }
}
