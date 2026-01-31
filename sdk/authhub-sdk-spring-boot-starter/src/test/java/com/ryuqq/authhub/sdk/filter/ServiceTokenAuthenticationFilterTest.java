package com.ryuqq.authhub.sdk.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.sdk.context.UserContext;
import com.ryuqq.authhub.sdk.context.UserContextHolder;
import com.ryuqq.authhub.sdk.header.SecurityHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceTokenAuthenticationFilter")
class ServiceTokenAuthenticationFilterTest {

    private ServiceTokenAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        UserContextHolder.clearContext();
    }

    @Nested
    @DisplayName("doFilterInternal")
    class DoFilterInternal {

        @Test
        @DisplayName("유효한 서비스 토큰으로 UserContext 설정")
        void setsUserContextWithValidServiceToken() throws ServletException, IOException {
            filter =
                    new ServiceTokenAuthenticationFilter(
                            (serviceName, token) ->
                                    "my-service".equals(serviceName) && "secret".equals(token));
            request = new MockHttpServletRequest("GET", "/api/v1/internal/endpoints");
            response = new MockHttpServletResponse();
            AtomicReference<UserContext> capturedContext = new AtomicReference<>();
            filterChain = (req, res) -> capturedContext.set(UserContextHolder.getContext());

            request.addHeader(SecurityHeaders.SERVICE_NAME, "my-service");
            request.addHeader(SecurityHeaders.SERVICE_TOKEN, "secret");

            filter.doFilter(request, response, filterChain);

            UserContext context = capturedContext.get();
            assertThat(context).isNotNull();
            assertThat(context.isServiceAccount()).isTrue();
            assertThat(context.getRequestSource()).isEqualTo("my-service");
        }

        @Test
        @DisplayName("원본 사용자 헤더 보존")
        void preservesOriginalUserHeaders() throws ServletException, IOException {
            filter = new ServiceTokenAuthenticationFilter((serviceName, token) -> true);
            request = new MockHttpServletRequest("GET", "/api/v1/internal/endpoints");
            response = new MockHttpServletResponse();
            AtomicReference<UserContext> capturedContext = new AtomicReference<>();
            filterChain = (req, res) -> capturedContext.set(UserContextHolder.getContext());

            request.addHeader(SecurityHeaders.SERVICE_NAME, "my-service");
            request.addHeader(SecurityHeaders.SERVICE_TOKEN, "secret");
            request.addHeader(SecurityHeaders.ORIGINAL_USER_ID, "user-123");
            request.addHeader(SecurityHeaders.ORIGINAL_TENANT_ID, "tenant-456");
            request.addHeader(SecurityHeaders.CORRELATION_ID, "corr-abc");

            filter.doFilter(request, response, filterChain);

            UserContext context = capturedContext.get();
            assertThat(context).isNotNull();
            assertThat(context.getUserId()).isEqualTo("user-123");
            assertThat(context.getTenantId()).isEqualTo("tenant-456");
            assertThat(context.getCorrelationId()).isEqualTo("corr-abc");
        }

        @Test
        @DisplayName("서비스 토큰 없으면 다음 필터로")
        void passesToNextFilterWhenNoServiceToken() throws ServletException, IOException {
            filter = new ServiceTokenAuthenticationFilter((serviceName, token) -> true);
            request = new MockHttpServletRequest("GET", "/api/v1/internal/endpoints");
            response = new MockHttpServletResponse();
            filterChain = mock(FilterChain.class);

            filter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(eq(request), eq(response));
            assertThat(UserContextHolder.getContextOptional()).isEmpty();
        }

        @Test
        @DisplayName("유효하지 않은 토큰이면 401 반환")
        void returns401WhenInvalidToken() throws ServletException, IOException {
            filter = new ServiceTokenAuthenticationFilter((serviceName, token) -> false);
            request = new MockHttpServletRequest("GET", "/api/v1/internal/endpoints");
            response = new MockHttpServletResponse();
            filterChain = mock(FilterChain.class);

            request.addHeader(SecurityHeaders.SERVICE_NAME, "my-service");
            request.addHeader(SecurityHeaders.SERVICE_TOKEN, "invalid-token");

            filter.doFilter(request, response, filterChain);

            assertThat(response.getStatus()).isEqualTo(401);
            assertThat(response.getContentAsString()).contains("Invalid service token");
        }

        @Test
        @DisplayName("필터 처리 후 UserContext 정리")
        void clearsContextAfterFilter() throws ServletException, IOException {
            filter = new ServiceTokenAuthenticationFilter((serviceName, token) -> true);
            request = new MockHttpServletRequest("GET", "/api/v1/internal/endpoints");
            response = new MockHttpServletResponse();
            filterChain = mock(FilterChain.class);

            request.addHeader(SecurityHeaders.SERVICE_NAME, "my-service");
            request.addHeader(SecurityHeaders.SERVICE_TOKEN, "secret");

            filter.doFilter(request, response, filterChain);

            assertThat(UserContextHolder.getContextOptional()).isEmpty();
        }
    }

    @Nested
    @DisplayName("shouldNotFilter")
    class ShouldNotFilter {

        @Test
        @DisplayName("서비스 토큰 헤더 없으면 필터 스킵")
        void skipsWhenNoServiceTokenHeader() throws ServletException, IOException {
            filter = new ServiceTokenAuthenticationFilter((serviceName, token) -> true);
            request = new MockHttpServletRequest("GET", "/api/v1/internal/endpoints");
            response = new MockHttpServletResponse();
            filterChain = mock(FilterChain.class);

            filter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(eq(request), eq(response));
            assertThat(UserContextHolder.getContextOptional()).isEmpty();
        }
    }
}
