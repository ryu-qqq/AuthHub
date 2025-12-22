package com.ryuqq.authhub.adapter.in.rest.auth.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.auth.common.constant.Roles;
import com.ryuqq.auth.common.header.SecurityHeaders;
import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContext;
import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContextHolder;
import com.ryuqq.authhub.adapter.in.rest.auth.config.ServiceTokenProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ServiceTokenFilter")
class ServiceTokenFilterTest {

    private static final String VALID_SECRET = "test-service-secret";
    private static final String INVALID_SECRET = "wrong-secret";
    private static final String SERVICE_NAME = "crawlinghub";
    private static final String ORIGINAL_USER_ID = "user-123";
    private static final String ORIGINAL_TENANT_ID = "tenant-456";
    private static final String ORIGINAL_ORG_ID = "org-789";
    private static final String CORRELATION_ID = "trace-abc";

    private ServiceTokenFilter filter;
    private ServiceTokenProperties properties;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        properties = new ServiceTokenProperties();
        properties.setEnabled(true);
        properties.setSecret(VALID_SECRET);

        filter = new ServiceTokenFilter(properties);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("shouldNotFilter")
    class ShouldNotFilter {

        @Test
        @DisplayName("Service Token 헤더가 없으면 필터를 건너뜀")
        void skipWhenNoServiceToken() {
            when(request.getHeader(SecurityHeaders.SERVICE_TOKEN)).thenReturn(null);

            assertThat(filter.shouldNotFilter(request)).isTrue();
        }

        @Test
        @DisplayName("Service Token 헤더가 있으면 필터 실행")
        void processWhenServiceTokenPresent() {
            when(request.getHeader(SecurityHeaders.SERVICE_TOKEN)).thenReturn(VALID_SECRET);

            assertThat(filter.shouldNotFilter(request)).isFalse();
        }
    }

    @Nested
    @DisplayName("doFilterInternal")
    class DoFilterInternal {

        @Test
        @DisplayName("비활성화 시 바로 체인 통과")
        void passWhenDisabled() throws Exception {
            properties.setEnabled(false);
            when(request.getHeader(SecurityHeaders.SERVICE_TOKEN)).thenReturn(VALID_SECRET);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("유효하지 않은 토큰은 인증 없이 체인 통과")
        void passWithInvalidToken() throws Exception {
            when(request.getHeader(SecurityHeaders.SERVICE_TOKEN)).thenReturn(INVALID_SECRET);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("유효한 토큰으로 서비스 계정 SecurityContext 생성")
        void createServiceAccountContext() throws Exception {
            when(request.getHeader(SecurityHeaders.SERVICE_TOKEN)).thenReturn(VALID_SECRET);
            when(request.getHeader(SecurityHeaders.SERVICE_NAME)).thenReturn(SERVICE_NAME);
            when(request.getHeader(SecurityHeaders.ORIGINAL_USER_ID)).thenReturn(ORIGINAL_USER_ID);
            when(request.getHeader(SecurityHeaders.ORIGINAL_TENANT_ID))
                    .thenReturn(ORIGINAL_TENANT_ID);
            when(request.getHeader(SecurityHeaders.ORIGINAL_ORGANIZATION_ID))
                    .thenReturn(ORIGINAL_ORG_ID);
            when(request.getHeader(SecurityHeaders.CORRELATION_ID)).thenReturn(CORRELATION_ID);
            when(request.getHeader(SecurityHeaders.REQUEST_SOURCE)).thenReturn(SERVICE_NAME);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("원본 사용자 정보 없이 서비스 계정만으로도 인증")
        void authenticateWithoutOriginalUser() throws Exception {
            when(request.getHeader(SecurityHeaders.SERVICE_TOKEN)).thenReturn(VALID_SECRET);
            when(request.getHeader(SecurityHeaders.SERVICE_NAME)).thenReturn(SERVICE_NAME);
            when(request.getHeader(SecurityHeaders.ORIGINAL_USER_ID)).thenReturn(null);
            when(request.getHeader(SecurityHeaders.ORIGINAL_TENANT_ID)).thenReturn(null);
            when(request.getHeader(SecurityHeaders.ORIGINAL_ORGANIZATION_ID)).thenReturn(null);
            when(request.getHeader(SecurityHeaders.CORRELATION_ID)).thenReturn(null);
            when(request.getHeader(SecurityHeaders.REQUEST_SOURCE)).thenReturn(null);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("SecurityContext 검증")
    class SecurityContextValidation {

        @Test
        @DisplayName("서비스 계정 컨텍스트 필드 확인")
        void validateServiceAccountContextFields() throws Exception {
            when(request.getHeader(SecurityHeaders.SERVICE_TOKEN)).thenReturn(VALID_SECRET);
            when(request.getHeader(SecurityHeaders.SERVICE_NAME)).thenReturn(SERVICE_NAME);
            when(request.getHeader(SecurityHeaders.ORIGINAL_USER_ID)).thenReturn(ORIGINAL_USER_ID);
            when(request.getHeader(SecurityHeaders.ORIGINAL_TENANT_ID))
                    .thenReturn(ORIGINAL_TENANT_ID);
            when(request.getHeader(SecurityHeaders.ORIGINAL_ORGANIZATION_ID))
                    .thenReturn(ORIGINAL_ORG_ID);
            when(request.getHeader(SecurityHeaders.CORRELATION_ID)).thenReturn(CORRELATION_ID);
            when(request.getHeader(SecurityHeaders.REQUEST_SOURCE)).thenReturn(SERVICE_NAME);

            // Use a holder to capture the context during filter execution
            final SecurityContext[] capturedContext = new SecurityContext[1];

            FilterChain capturingChain =
                    (req, res) -> {
                        capturedContext[0] = SecurityContextHolder.getContext();
                    };

            filter.doFilterInternal(request, response, capturingChain);

            SecurityContext context = capturedContext[0];
            assertThat(context).isNotNull();
            assertThat(context.isServiceAccount()).isTrue();
            assertThat(context.getUserId()).isEqualTo(ORIGINAL_USER_ID);
            assertThat(context.getTenantId()).isEqualTo(ORIGINAL_TENANT_ID);
            assertThat(context.getOrganizationId()).isEqualTo(ORIGINAL_ORG_ID);
            assertThat(context.getCorrelationId()).isEqualTo(CORRELATION_ID);
            assertThat(context.getRequestSource()).isEqualTo(SERVICE_NAME);
            assertThat(context.getRoles()).contains(Roles.SERVICE);
        }

        @Test
        @DisplayName("RequestSource 미설정 시 ServiceName 사용")
        void useServiceNameAsRequestSourceWhenMissing() throws Exception {
            when(request.getHeader(SecurityHeaders.SERVICE_TOKEN)).thenReturn(VALID_SECRET);
            when(request.getHeader(SecurityHeaders.SERVICE_NAME)).thenReturn(SERVICE_NAME);
            when(request.getHeader(SecurityHeaders.REQUEST_SOURCE)).thenReturn(null);

            final SecurityContext[] capturedContext = new SecurityContext[1];
            FilterChain capturingChain =
                    (req, res) -> {
                        capturedContext[0] = SecurityContextHolder.getContext();
                    };

            filter.doFilterInternal(request, response, capturingChain);

            assertThat(capturedContext[0].getRequestSource()).isEqualTo(SERVICE_NAME);
        }
    }
}
