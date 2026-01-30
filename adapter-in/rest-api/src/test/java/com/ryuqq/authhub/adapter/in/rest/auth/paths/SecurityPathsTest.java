package com.ryuqq.authhub.adapter.in.rest.auth.paths;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SecurityPaths 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SecurityPaths 단위 테스트")
class SecurityPathsTest {

    @Nested
    @DisplayName("Public.PATTERNS")
    class PublicPatterns {

        @Test
        @DisplayName("Public 경로 목록이 비어있지 않다")
        void shouldNotBeEmpty() {
            assertThat(SecurityPaths.Public.PATTERNS).isNotEmpty();
        }

        @Test
        @DisplayName("헬스체크 경로를 포함한다")
        void shouldContainHealthPath() {
            assertThat(SecurityPaths.Public.PATTERNS.stream().anyMatch(p -> p.contains("health")))
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("Docs.PATTERNS")
    class DocsPatterns {

        @Test
        @DisplayName("Docs 경로 목록이 비어있지 않다")
        void shouldNotBeEmpty() {
            assertThat(SecurityPaths.Docs.PATTERNS).isNotEmpty();
        }

        @Test
        @DisplayName("Swagger UI 경로를 포함한다")
        void shouldContainSwaggerPath() {
            assertThat(SecurityPaths.Docs.PATTERNS.stream().anyMatch(p -> p.contains("swagger")))
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("Internal.PATTERNS")
    class InternalPatterns {

        @Test
        @DisplayName("Internal 경로 목록이 비어있지 않다")
        void shouldNotBeEmpty() {
            assertThat(SecurityPaths.Internal.PATTERNS).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Headers")
    class Headers {

        @Test
        @DisplayName("헤더 상수가 정의되어 있다")
        void shouldDefineHeaderConstants() {
            assertThat(SecurityPaths.Headers.USER_ID).isEqualTo("X-User-Id");
            assertThat(SecurityPaths.Headers.TENANT_ID).isEqualTo("X-Tenant-Id");
            assertThat(SecurityPaths.Headers.ORGANIZATION_ID).isEqualTo("X-Organization-Id");
        }
    }
}
