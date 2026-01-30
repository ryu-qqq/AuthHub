package com.ryuqq.authhub.sdk.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.sdk.context.UserContext;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("GatewayHeaderParser")
class GatewayHeaderParserTest {

    @Nested
    @DisplayName("parse")
    class Parse {

        @Test
        @DisplayName("모든 헤더 파싱")
        void parsesAllHeaders() {
            Map<String, String> headers = new HashMap<>();
            headers.put(SecurityHeaders.USER_ID, "user-123");
            headers.put(SecurityHeaders.TENANT_ID, "tenant-456");
            headers.put(SecurityHeaders.ORGANIZATION_ID, "org-789");
            headers.put(SecurityHeaders.USER_EMAIL, "test@example.com");
            headers.put(SecurityHeaders.ROLES, "ADMIN,USER");
            headers.put(SecurityHeaders.PERMISSIONS, "user:read,user:write");
            headers.put(SecurityHeaders.CORRELATION_ID, "corr-abc");
            headers.put(SecurityHeaders.REQUEST_SOURCE, "web");

            UserContext context = GatewayHeaderParser.parse(headers::get);

            assertThat(context.getUserId()).isEqualTo("user-123");
            assertThat(context.getTenantId()).isEqualTo("tenant-456");
            assertThat(context.getOrganizationId()).isEqualTo("org-789");
            assertThat(context.getEmail()).isEqualTo("test@example.com");
            assertThat(context.getRoles()).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
            assertThat(context.getPermissions())
                    .containsExactlyInAnyOrder("user:read", "user:write");
            assertThat(context.getCorrelationId()).isEqualTo("corr-abc");
            assertThat(context.getRequestSource()).isEqualTo("web");
            assertThat(context.isServiceAccount()).isFalse();
        }

        @Test
        @DisplayName("서비스 토큰이 있으면 서비스 계정")
        void detectsServiceAccount() {
            Map<String, String> headers = new HashMap<>();
            headers.put(SecurityHeaders.SERVICE_TOKEN, "service-token-xyz");

            UserContext context = GatewayHeaderParser.parse(headers::get);

            assertThat(context.isServiceAccount()).isTrue();
        }

        @Test
        @DisplayName("빈 헤더 처리")
        void handlesEmptyHeaders() {
            Map<String, String> headers = new HashMap<>();

            UserContext context = GatewayHeaderParser.parse(headers::get);

            assertThat(context.getUserId()).isNull();
            assertThat(context.getRoles()).isEmpty();
            assertThat(context.getPermissions()).isEmpty();
        }

        @Test
        @DisplayName("공백 값 처리")
        void handlesBlankValues() {
            Map<String, String> headers = new HashMap<>();
            headers.put(SecurityHeaders.USER_ID, "   ");
            headers.put(SecurityHeaders.ROLES, "");

            UserContext context = GatewayHeaderParser.parse(headers::get);

            assertThat(context.getUserId()).isNull();
            assertThat(context.getRoles()).isEmpty();
        }

        @Test
        @DisplayName("null headerGetter 처리")
        void handlesNullHeaderGetter() {
            UserContext context = GatewayHeaderParser.parse(null);

            assertThat(context.getUserId()).isNull();
            assertThat(context.isAuthenticated()).isFalse();
        }
    }

    @Nested
    @DisplayName("parseWithScope")
    class ParseWithScope {

        @Test
        @DisplayName("Scope 포함 파싱")
        void parsesWithScope() {
            Map<String, String> headers = new HashMap<>();
            headers.put(SecurityHeaders.USER_ID, "user-123");

            UserContext context = GatewayHeaderParser.parseWithScope(headers::get, "TENANT");

            assertThat(context.getUserId()).isEqualTo("user-123");
            assertThat(context.getScope()).isEqualTo("TENANT");
        }
    }

    @Nested
    @DisplayName("parseCommaSeparated")
    class ParseCommaSeparated {

        @Test
        @DisplayName("쉼표 구분 문자열 파싱")
        void parsesCommaSeparatedValues() {
            Set<String> result = GatewayHeaderParser.parseCommaSeparated("a,b,c");

            assertThat(result).containsExactlyInAnyOrder("a", "b", "c");
        }

        @Test
        @DisplayName("공백 제거")
        void trimsWhitespace() {
            Set<String> result = GatewayHeaderParser.parseCommaSeparated("  a  ,  b  ,  c  ");

            assertThat(result).containsExactlyInAnyOrder("a", "b", "c");
        }

        @Test
        @DisplayName("빈 요소 필터링")
        void filtersEmptyElements() {
            Set<String> result = GatewayHeaderParser.parseCommaSeparated("a,,b,  ,c");

            assertThat(result).containsExactlyInAnyOrder("a", "b", "c");
        }

        @Test
        @DisplayName("null 처리")
        void handlesNull() {
            Set<String> result = GatewayHeaderParser.parseCommaSeparated(null);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 문자열 처리")
        void handlesEmptyString() {
            Set<String> result = GatewayHeaderParser.parseCommaSeparated("");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("공백만 있는 문자열 처리")
        void handlesBlankString() {
            Set<String> result = GatewayHeaderParser.parseCommaSeparated("   ");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("normalizeRoles")
    class NormalizeRoles {

        @Test
        @DisplayName("ROLE_ 접두사 추가")
        void addsRolePrefix() {
            Set<String> roles = Set.of("ADMIN", "USER");

            Set<String> result = GatewayHeaderParser.normalizeRoles(roles);

            assertThat(result).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        }

        @Test
        @DisplayName("이미 ROLE_ 접두사가 있으면 유지")
        void keepsExistingPrefix() {
            Set<String> roles = Set.of("ROLE_ADMIN", "USER");

            Set<String> result = GatewayHeaderParser.normalizeRoles(roles);

            assertThat(result).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        }

        @Test
        @DisplayName("null 처리")
        void handlesNull() {
            Set<String> result = GatewayHeaderParser.normalizeRoles(null);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 Set 처리")
        void handlesEmptySet() {
            Set<String> result = GatewayHeaderParser.normalizeRoles(Set.of());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("normalizeRole")
    class NormalizeRole {

        @Test
        @DisplayName("ROLE_ 접두사 추가")
        void addsRolePrefix() {
            assertThat(GatewayHeaderParser.normalizeRole("ADMIN")).isEqualTo("ROLE_ADMIN");
        }

        @Test
        @DisplayName("이미 ROLE_ 접두사가 있으면 유지")
        void keepsExistingPrefix() {
            assertThat(GatewayHeaderParser.normalizeRole("ROLE_ADMIN")).isEqualTo("ROLE_ADMIN");
        }

        @Test
        @DisplayName("공백 제거")
        void trimsWhitespace() {
            assertThat(GatewayHeaderParser.normalizeRole("  ADMIN  ")).isEqualTo("ROLE_ADMIN");
        }

        @Test
        @DisplayName("null 처리")
        void handlesNull() {
            assertThat(GatewayHeaderParser.normalizeRole(null)).isNull();
        }
    }

    @Nested
    @DisplayName("stripRolePrefix")
    class StripRolePrefix {

        @Test
        @DisplayName("ROLE_ 접두사 제거")
        void removesRolePrefix() {
            assertThat(GatewayHeaderParser.stripRolePrefix("ROLE_ADMIN")).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("접두사 없으면 그대로 반환")
        void returnsAsIsWhenNoPrefix() {
            assertThat(GatewayHeaderParser.stripRolePrefix("ADMIN")).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("공백 제거")
        void trimsWhitespace() {
            assertThat(GatewayHeaderParser.stripRolePrefix("  ROLE_ADMIN  ")).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("null 처리")
        void handlesNull() {
            assertThat(GatewayHeaderParser.stripRolePrefix(null)).isNull();
        }
    }

    @Nested
    @DisplayName("유틸리티 클래스")
    class UtilityClass {

        @Test
        @DisplayName("인스턴스화 불가")
        void cannotInstantiate() throws Exception {
            Constructor<GatewayHeaderParser> constructor =
                    GatewayHeaderParser.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(AssertionError.class);
        }
    }
}
