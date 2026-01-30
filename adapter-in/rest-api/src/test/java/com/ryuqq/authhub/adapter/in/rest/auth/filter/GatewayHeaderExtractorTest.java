package com.ryuqq.authhub.adapter.in.rest.auth.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * GatewayHeaderExtractor 단위 테스트
 *
 * <p>GatewayHeaderExtractor는 package-private이므로 동일 패키지에서 테스트합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GatewayHeaderExtractor 단위 테스트")
class GatewayHeaderExtractorTest {

    @Nested
    @DisplayName("getUserId() 메서드는")
    class GetUserIdMethod {

        @Test
        @DisplayName("X-User-Id 헤더 값을 반환한다")
        void shouldReturnUserIdFromHeader() {
            // Given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(GatewayHeaderExtractor.HEADER_USER_ID, "user-123");

            // When
            String result = GatewayHeaderExtractor.getUserId(request);

            // Then
            assertThat(result).isEqualTo("user-123");
        }

        @Test
        @DisplayName("헤더가 없으면 null을 반환한다")
        void shouldReturnNullWhenHeaderMissing() {
            MockHttpServletRequest request = new MockHttpServletRequest();

            assertThat(GatewayHeaderExtractor.getUserId(request)).isNull();
        }
    }

    @Nested
    @DisplayName("getTenantId() 메서드는")
    class GetTenantIdMethod {

        @Test
        @DisplayName("X-Tenant-Id 헤더 값을 반환한다")
        void shouldReturnTenantIdFromHeader() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(GatewayHeaderExtractor.HEADER_TENANT_ID, "tenant-456");

            assertThat(GatewayHeaderExtractor.getTenantId(request)).isEqualTo("tenant-456");
        }

        @Test
        @DisplayName("빈 헤더는 null을 반환한다")
        void shouldReturnNullWhenHeaderIsBlank() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(GatewayHeaderExtractor.HEADER_TENANT_ID, "   ");

            assertThat(GatewayHeaderExtractor.getTenantId(request)).isNull();
        }
    }

    @Nested
    @DisplayName("getRoles() 메서드는")
    class GetRolesMethod {

        @Test
        @DisplayName("콤마 구분 역할 문자열을 파싱한다")
        void shouldParseCommaSeparatedRoles() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(GatewayHeaderExtractor.HEADER_ROLES, "ADMIN, USER");

            Set<String> result = GatewayHeaderExtractor.getRoles(request);

            assertThat(result).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        }

        @Test
        @DisplayName("ROLE_ prefix가 있으면 그대로 유지한다")
        void shouldPreserveRolePrefix() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(GatewayHeaderExtractor.HEADER_ROLES, "ROLE_ADMIN");

            Set<String> result = GatewayHeaderExtractor.getRoles(request);

            assertThat(result).containsExactly("ROLE_ADMIN");
        }

        @Test
        @DisplayName("헤더가 없으면 빈 Set을 반환한다")
        void shouldReturnEmptySetWhenHeaderMissing() {
            MockHttpServletRequest request = new MockHttpServletRequest();

            assertThat(GatewayHeaderExtractor.getRoles(request)).isEmpty();
        }
    }

    @Nested
    @DisplayName("getPermissions() 메서드는")
    class GetPermissionsMethod {

        @Test
        @DisplayName("콤마 구분 권한 문자열을 파싱한다")
        void shouldParseCommaSeparatedPermissions() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(GatewayHeaderExtractor.HEADER_PERMISSIONS, "user:read, user:write");

            Set<String> result = GatewayHeaderExtractor.getPermissions(request);

            assertThat(result).containsExactlyInAnyOrder("user:read", "user:write");
        }

        @Test
        @DisplayName("헤더가 없으면 빈 Set을 반환한다")
        void shouldReturnEmptySetWhenHeaderMissing() {
            MockHttpServletRequest request = new MockHttpServletRequest();

            assertThat(GatewayHeaderExtractor.getPermissions(request)).isEmpty();
        }
    }

    @Nested
    @DisplayName("getTraceId() 메서드는")
    class GetTraceIdMethod {

        @Test
        @DisplayName("X-Trace-Id 헤더 값을 반환한다")
        void shouldReturnTraceIdFromHeader() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(GatewayHeaderExtractor.HEADER_TRACE_ID, "trace-001");

            assertThat(GatewayHeaderExtractor.getTraceId(request)).isEqualTo("trace-001");
        }
    }
}
