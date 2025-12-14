package com.ryuqq.authhub.adapter.in.rest.auth.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.auth.component.JwtClaimsExtractor;
import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContext;
import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContextHolder;
import com.ryuqq.authhub.domain.auth.vo.Role;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

/**
 * GatewayAuthenticationFilter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@DisplayName("GatewayAuthenticationFilter 단위 테스트")
@SuppressWarnings("PMD.SingularField")
class GatewayAuthenticationFilterTest {

    private static final String TEST_USER_ID = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1b";
    private static final String TEST_TENANT_ID = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1c";
    private static final String TEST_ORGANIZATION_ID = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1d";

    private GatewayAuthenticationFilter filter;
    private ObjectMapper objectMapper;
    private JwtClaimsExtractor jwtClaimsExtractor;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        jwtClaimsExtractor = mock(JwtClaimsExtractor.class);
        when(jwtClaimsExtractor.extractClaims(anyString())).thenReturn(Optional.empty());
        filter = new GatewayAuthenticationFilter(objectMapper, jwtClaimsExtractor);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Gateway 헤더가 있는 경우")
    class WithGatewayHeadersTest {

        @Test
        @DisplayName("X-User-Id 헤더가 있으면 인증된 SecurityContext를 설정한다 (UUID 형식)")
        void doFilter_withUserIdHeader_shouldSetAuthenticatedContext()
                throws ServletException, IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", TEST_USER_ID);
            request.addHeader("X-Tenant-Id", TEST_TENANT_ID);
            request.addHeader("X-Organization-Id", TEST_ORGANIZATION_ID);
            request.addHeader("X-User-Roles", "TENANT_ADMIN");
            request.addHeader("X-Trace-Id", "trace-123");

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.isAuthenticated()).isTrue();
            assertThat(capturedContext.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(capturedContext.getTenantId()).isEqualTo(TEST_TENANT_ID);
            assertThat(capturedContext.getOrganizationId()).isEqualTo(TEST_ORGANIZATION_ID);
            assertThat(capturedContext.getRoles()).contains(Role.TENANT_ADMIN);
            assertThat(capturedContext.getTraceId()).isEqualTo("trace-123");
        }

        @Test
        @DisplayName("X-User-Id만 있어도 인증된 SecurityContext를 설정한다")
        void doFilter_withOnlyUserId_shouldSetAuthenticatedContext()
                throws ServletException, IOException {
            // given
            String userId = UUID.randomUUID().toString();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", userId);

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.isAuthenticated()).isTrue();
            assertThat(capturedContext.getUserId()).isEqualTo(userId);
            assertThat(capturedContext.getTenantId()).isNull();
            assertThat(capturedContext.getOrganizationId()).isNull();
            assertThat(capturedContext.getRoles()).isEmpty();
            assertThat(capturedContext.getPermissions()).isEmpty();
        }

        @Test
        @DisplayName("여러 역할이 있으면 모두 파싱된다")
        void doFilter_withMultipleRoles_shouldParseAllRoles() throws ServletException, IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", TEST_USER_ID);
            request.addHeader("X-User-Roles", "SUPER_ADMIN,TENANT_ADMIN");

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.getRoles())
                    .containsExactlyInAnyOrder(Role.SUPER_ADMIN, Role.TENANT_ADMIN);
        }

        @Test
        @DisplayName("X-Organization-Id 헤더가 있으면 조직 ID가 파싱된다 (UUIDv7 문자열)")
        void doFilter_withOrganizationIdHeader_shouldParseOrganizationId()
                throws ServletException, IOException {
            // given
            String organizationId = "01912f58-7b9c-7d4e-8b4a-000000000300";
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", TEST_USER_ID);
            request.addHeader("X-Organization-Id", organizationId);

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.getOrganizationId()).isEqualTo(organizationId);
        }

        @Test
        @DisplayName("X-Permissions 헤더가 있으면 권한이 파싱된다")
        void doFilter_withPermissions_shouldParsePermissions()
                throws ServletException, IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", TEST_USER_ID);
            request.addHeader("X-Permissions", "user:read,order:write,tenant:create");

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.getPermissions())
                    .containsExactlyInAnyOrder("user:read", "order:write", "tenant:create");
        }

        @Test
        @DisplayName("Spring SecurityContextHolder와 동기화된다")
        void doFilter_shouldSynchronizeWithSpringSecurityContext()
                throws ServletException, IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", TEST_USER_ID);
            request.addHeader("X-User-Roles", "TENANT_ADMIN");
            request.addHeader("X-Permissions", "user:read");

            MockHttpServletResponse response = new MockHttpServletResponse();
            SpringSecurityContextCapturingFilterChain filterChain =
                    new SpringSecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            Authentication capturedAuth = filterChain.getCapturedAuthentication();
            assertThat(capturedAuth).isNotNull();
            assertThat(capturedAuth.getPrincipal()).isEqualTo(TEST_USER_ID);
            assertThat(capturedAuth.getAuthorities())
                    .anyMatch(auth -> auth.getAuthority().equals(Role.TENANT_ADMIN));
            assertThat(capturedAuth.getAuthorities())
                    .anyMatch(auth -> auth.getAuthority().equals("user:read"));
        }
    }

    @Nested
    @DisplayName("Gateway 헤더가 없는 경우")
    class WithoutGatewayHeadersTest {

        @Test
        @DisplayName("X-User-Id 헤더가 없으면 Anonymous SecurityContext를 설정한다")
        void doFilter_withoutUserIdHeader_shouldSetAnonymousContext()
                throws ServletException, IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.isAuthenticated()).isFalse();
            assertThat(capturedContext.getUserId()).isNull();
        }

        @Test
        @DisplayName("빈 X-User-Id 헤더면 Anonymous SecurityContext를 설정한다")
        void doFilter_withEmptyUserIdHeader_shouldSetAnonymousContext()
                throws ServletException, IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", "");

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.isAuthenticated()).isFalse();
        }

        @Test
        @DisplayName("공백만 있는 X-User-Id 헤더면 Anonymous SecurityContext를 설정한다")
        void doFilter_withWhitespaceUserIdHeader_shouldSetAnonymousContext()
                throws ServletException, IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", "   ");

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.isAuthenticated()).isFalse();
        }
    }

    @Nested
    @DisplayName("헤더 파싱 에러 처리")
    class HeaderParsingErrorTest {

        @Test
        @DisplayName("잘못된 X-Roles JSON 형식이면 빈 역할로 처리한다")
        void doFilter_withInvalidRolesJson_shouldHaveEmptyRoles()
                throws ServletException, IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", TEST_USER_ID);
            request.addHeader("X-Roles", "invalid-json");

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.isAuthenticated()).isTrue();
            assertThat(capturedContext.getRoles()).isEmpty();
        }

        @Test
        @DisplayName("X-Tenant-Id 헤더 값은 문자열로 그대로 저장된다 (UUIDv7 문자열)")
        void doFilter_withTenantIdHeader_shouldStoreAsString()
                throws ServletException, IOException {
            // given
            String tenantId = "any-string-value";
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", TEST_USER_ID);
            request.addHeader("X-Tenant-Id", tenantId);

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.isAuthenticated()).isTrue();
            assertThat(capturedContext.getTenantId()).isEqualTo(tenantId);
        }

        @Test
        @DisplayName("X-Organization-Id 헤더 값은 문자열로 그대로 저장된다 (UUIDv7 문자열)")
        void doFilter_withOrganizationIdHeader_shouldStoreAsString()
                throws ServletException, IOException {
            // given
            String organizationId = "any-string-value";
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", TEST_USER_ID);
            request.addHeader("X-Organization-Id", organizationId);

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.isAuthenticated()).isTrue();
            assertThat(capturedContext.getOrganizationId()).isEqualTo(organizationId);
        }

        @Test
        @DisplayName("빈 X-Organization-Id 헤더면 null로 처리한다")
        void doFilter_withEmptyOrganizationIdHeader_shouldHaveNullOrganizationId()
                throws ServletException, IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", TEST_USER_ID);
            request.addHeader("X-Organization-Id", "");

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.isAuthenticated()).isTrue();
            assertThat(capturedContext.getOrganizationId()).isNull();
        }

        @Test
        @DisplayName("빈 X-Permissions 헤더면 빈 권한으로 처리한다")
        void doFilter_withEmptyPermissions_shouldHaveEmptyPermissions()
                throws ServletException, IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", TEST_USER_ID);
            request.addHeader("X-Permissions", "");

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.isAuthenticated()).isTrue();
            assertThat(capturedContext.getPermissions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("JWT Fallback 테스트")
    class JwtFallbackTest {

        @Test
        @DisplayName("X-User-Id 없이 유효한 JWT가 있으면 인증된 SecurityContext를 설정한다")
        void doFilter_withValidJwt_shouldSetAuthenticatedContext()
                throws ServletException, IOException {
            // given
            JwtClaimsExtractor.JwtClaims jwtClaims =
                    new JwtClaimsExtractor.JwtClaims(
                            TEST_USER_ID,
                            TEST_TENANT_ID,
                            TEST_ORGANIZATION_ID,
                            Set.of(Role.TENANT_ADMIN),
                            Set.of("user:read"));
            when(jwtClaimsExtractor.extractClaims("valid-token"))
                    .thenReturn(Optional.of(jwtClaims));

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer valid-token");

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.isAuthenticated()).isTrue();
            assertThat(capturedContext.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(capturedContext.getTenantId()).isEqualTo(TEST_TENANT_ID);
            assertThat(capturedContext.getOrganizationId()).isEqualTo(TEST_ORGANIZATION_ID);
            assertThat(capturedContext.getRoles()).contains(Role.TENANT_ADMIN);
            assertThat(capturedContext.getPermissions()).contains("user:read");
        }

        @Test
        @DisplayName("X-User-Id가 있으면 JWT보다 Gateway 인증이 우선한다")
        void doFilter_withBothGatewayAndJwt_gatewayHasPriority()
                throws ServletException, IOException {
            // given
            String gatewayUserId = "gateway-user-id";
            JwtClaimsExtractor.JwtClaims jwtClaims =
                    new JwtClaimsExtractor.JwtClaims("jwt-user-id", null, null, Set.of(), Set.of());
            when(jwtClaimsExtractor.extractClaims("valid-token"))
                    .thenReturn(Optional.of(jwtClaims));

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", gatewayUserId);
            request.addHeader("Authorization", "Bearer valid-token");

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.getUserId()).isEqualTo(gatewayUserId);
        }

        @Test
        @DisplayName("유효하지 않은 JWT면 Anonymous SecurityContext를 설정한다")
        void doFilter_withInvalidJwt_shouldSetAnonymousContext()
                throws ServletException, IOException {
            // given
            when(jwtClaimsExtractor.extractClaims("invalid-token")).thenReturn(Optional.empty());

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer invalid-token");

            MockHttpServletResponse response = new MockHttpServletResponse();
            SecurityContextCapturingFilterChain filterChain =
                    new SecurityContextCapturingFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            SecurityContext capturedContext = filterChain.getCapturedContext();
            assertThat(capturedContext.isAuthenticated()).isFalse();
        }
    }

    @Nested
    @DisplayName("컨텍스트 정리 테스트")
    class ContextCleanupTest {

        @Test
        @DisplayName("필터 체인 완료 후 SecurityContextHolder가 정리된다")
        void doFilter_afterCompletion_shouldClearContext() throws ServletException, IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", TEST_USER_ID);

            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain filterChain = new MockFilterChain();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then - 필터 완료 후 컨텍스트가 정리됨
            assertThat(SecurityContextHolder.getContext().isAuthenticated()).isFalse();
        }

        @Test
        @DisplayName("예외 발생 시에도 SecurityContextHolder가 정리된다")
        @SuppressWarnings("PMD.AvoidCatchingGenericException")
        void doFilter_onException_shouldClearContext() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-User-Id", TEST_USER_ID);

            MockHttpServletResponse response = new MockHttpServletResponse();
            ExceptionThrowingFilterChain filterChain = new ExceptionThrowingFilterChain();

            // when & then
            try {
                filter.doFilterInternal(request, response, filterChain);
            } catch (ServletException | IOException | RuntimeException ignored) {
                // 테스트에서 의도적으로 예외를 무시
            }

            // then - 예외 발생 후에도 컨텍스트가 정리됨
            assertThat(SecurityContextHolder.getContext().isAuthenticated()).isFalse();
        }
    }

    /** FilterChain 처리 중 SecurityContext를 캡처하는 테스트용 FilterChain */
    private static class SecurityContextCapturingFilterChain extends MockFilterChain {
        private SecurityContext capturedContext;

        @Override
        public void doFilter(
                jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response) {
            this.capturedContext = SecurityContextHolder.getContext();
        }

        public SecurityContext getCapturedContext() {
            return capturedContext;
        }
    }

    /** FilterChain 처리 중 Spring Authentication을 캡처하는 테스트용 FilterChain */
    private static class SpringSecurityContextCapturingFilterChain extends MockFilterChain {
        private Authentication capturedAuthentication;

        @Override
        public void doFilter(
                jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response) {
            this.capturedAuthentication =
                    org.springframework.security.core.context.SecurityContextHolder.getContext()
                            .getAuthentication();
        }

        public Authentication getCapturedAuthentication() {
            return capturedAuthentication;
        }
    }

    /** 예외를 발생시키는 테스트용 FilterChain */
    private static class ExceptionThrowingFilterChain extends MockFilterChain {
        @Override
        public void doFilter(
                jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response)
                throws ServletException {
            throw new ServletException("Test exception");
        }
    }
}
