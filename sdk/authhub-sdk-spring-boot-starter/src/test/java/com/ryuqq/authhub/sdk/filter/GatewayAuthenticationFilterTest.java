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
@DisplayName("GatewayAuthenticationFilter")
class GatewayAuthenticationFilterTest {

    private GatewayAuthenticationFilter filter;
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
        @DisplayName("Gateway 헤더 파싱 후 UserContext 설정")
        void setsUserContextFromGatewayHeaders() throws ServletException, IOException {
            filter = new GatewayAuthenticationFilter();
            request = new MockHttpServletRequest("GET", "/api/v1/users");
            response = new MockHttpServletResponse();
            AtomicReference<UserContext> capturedContext = new AtomicReference<>();
            filterChain = (req, res) -> capturedContext.set(UserContextHolder.getContext());

            request.addHeader(SecurityHeaders.USER_ID, "user-123");
            request.addHeader(SecurityHeaders.TENANT_ID, "tenant-456");
            request.addHeader(SecurityHeaders.ORGANIZATION_ID, "org-789");

            filter.doFilter(request, response, filterChain);

            UserContext context = capturedContext.get();
            assertThat(context).isNotNull();
            assertThat(context.getUserId()).isEqualTo("user-123");
            assertThat(context.getTenantId()).isEqualTo("tenant-456");
            assertThat(context.getOrganizationId()).isEqualTo("org-789");
        }

        @Test
        @DisplayName("필터 처리 후 UserContext 정리")
        void clearsContextAfterFilter() throws ServletException, IOException {
            filter = new GatewayAuthenticationFilter();
            request = new MockHttpServletRequest("GET", "/api/v1/users");
            response = new MockHttpServletResponse();
            filterChain = mock(FilterChain.class);

            request.addHeader(SecurityHeaders.USER_ID, "user-123");

            filter.doFilter(request, response, filterChain);

            assertThat(UserContextHolder.getContextOptional()).isEmpty();
        }

        @Test
        @DisplayName("헤더 없으면 anonymous 컨텍스트 설정")
        void setsAnonymousContextWhenNoHeaders() throws ServletException, IOException {
            filter = new GatewayAuthenticationFilter();
            request = new MockHttpServletRequest("GET", "/api/v1/users");
            response = new MockHttpServletResponse();
            AtomicReference<UserContext> capturedContext = new AtomicReference<>();
            filterChain = (req, res) -> capturedContext.set(UserContextHolder.getContext());

            filter.doFilter(request, response, filterChain);

            UserContext context = capturedContext.get();
            assertThat(context).isNotNull();
            assertThat(context.getUserId()).isNull();
            assertThat(context.isAuthenticated()).isFalse();
        }
    }

    @Nested
    @DisplayName("shouldNotFilter")
    class ShouldNotFilter {

        @Test
        @DisplayName("/actuator/ 경로는 필터 스킵")
        void skipsActuatorPaths() throws ServletException, IOException {
            filter = new GatewayAuthenticationFilter();
            request = new MockHttpServletRequest("GET", "/actuator/health");
            response = new MockHttpServletResponse();
            filterChain = mock(FilterChain.class);

            filter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(eq(request), eq(response));
            assertThat(UserContextHolder.getContextOptional()).isEmpty();
        }

        @Test
        @DisplayName("/health 경로는 필터 스킵")
        void skipsHealthPath() throws ServletException, IOException {
            filter = new GatewayAuthenticationFilter();
            request = new MockHttpServletRequest("GET", "/health");
            response = new MockHttpServletResponse();
            filterChain = mock(FilterChain.class);

            filter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(eq(request), eq(response));
            assertThat(UserContextHolder.getContextOptional()).isEmpty();
        }

        @Test
        @DisplayName("/api/ 경로는 필터 적용")
        void appliesFilterToApiPaths() throws ServletException, IOException {
            filter = new GatewayAuthenticationFilter();
            request = new MockHttpServletRequest("GET", "/api/v1/users");
            response = new MockHttpServletResponse();
            filterChain = mock(FilterChain.class);

            filter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(eq(request), eq(response));
        }
    }
}
