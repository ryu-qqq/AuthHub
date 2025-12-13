package com.ryuqq.authhub.adapter.in.rest.auth.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CorsProperties 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("CorsProperties 단위 테스트")
class CorsPropertiesTest {

    private CorsProperties properties;

    @BeforeEach
    void setUp() {
        properties = new CorsProperties();
    }

    @Nested
    @DisplayName("기본값 테스트")
    class DefaultValueTest {

        @Test
        @DisplayName("allowedOrigins 기본값은 빈 리스트이다")
        void allowedOrigins_defaultValue_shouldBeEmpty() {
            // given & when & then
            assertThat(properties.getAllowedOrigins()).isEmpty();
        }

        @Test
        @DisplayName("allowedMethods 기본값은 빈 리스트이다")
        void allowedMethods_defaultValue_shouldBeEmpty() {
            // given & when & then
            assertThat(properties.getAllowedMethods()).isEmpty();
        }

        @Test
        @DisplayName("allowedHeaders 기본값은 빈 리스트이다")
        void allowedHeaders_defaultValue_shouldBeEmpty() {
            // given & when & then
            assertThat(properties.getAllowedHeaders()).isEmpty();
        }

        @Test
        @DisplayName("exposedHeaders 기본값은 빈 리스트이다")
        void exposedHeaders_defaultValue_shouldBeEmpty() {
            // given & when & then
            assertThat(properties.getExposedHeaders()).isEmpty();
        }

        @Test
        @DisplayName("allowCredentials 기본값은 false이다")
        void allowCredentials_defaultValue_shouldBeFalse() {
            // given & when & then
            assertThat(properties.isAllowCredentials()).isFalse();
        }
    }

    @Nested
    @DisplayName("setter/getter 테스트")
    class SetterGetterTest {

        @Test
        @DisplayName("allowedOrigins를 설정하고 조회할 수 있다")
        void allowedOrigins_setAndGet_shouldWork() {
            // given
            List<String> origins = List.of("http://localhost:3000", "https://example.com");

            // when
            properties.setAllowedOrigins(origins);

            // then
            assertThat(properties.getAllowedOrigins())
                    .containsExactly("http://localhost:3000", "https://example.com");
        }

        @Test
        @DisplayName("allowedMethods를 설정하고 조회할 수 있다")
        void allowedMethods_setAndGet_shouldWork() {
            // given
            List<String> methods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");

            // when
            properties.setAllowedMethods(methods);

            // then
            assertThat(properties.getAllowedMethods())
                    .containsExactly("GET", "POST", "PUT", "DELETE", "OPTIONS");
        }

        @Test
        @DisplayName("allowedHeaders를 설정하고 조회할 수 있다")
        void allowedHeaders_setAndGet_shouldWork() {
            // given
            List<String> headers = List.of("Authorization", "Content-Type", "X-Requested-With");

            // when
            properties.setAllowedHeaders(headers);

            // then
            assertThat(properties.getAllowedHeaders())
                    .containsExactly("Authorization", "Content-Type", "X-Requested-With");
        }

        @Test
        @DisplayName("exposedHeaders를 설정하고 조회할 수 있다")
        void exposedHeaders_setAndGet_shouldWork() {
            // given
            List<String> headers = List.of("X-Request-Id", "X-Trace-Id");

            // when
            properties.setExposedHeaders(headers);

            // then
            assertThat(properties.getExposedHeaders())
                    .containsExactly("X-Request-Id", "X-Trace-Id");
        }

        @Test
        @DisplayName("allowCredentials를 설정하고 조회할 수 있다")
        void allowCredentials_setAndGet_shouldWork() {
            // given & when
            properties.setAllowCredentials(true);

            // then
            assertThat(properties.isAllowCredentials()).isTrue();
        }

        @Test
        @DisplayName("와일드카드 설정을 지원한다")
        void wildcardSettings_shouldWork() {
            // given
            List<String> wildcardOrigins = List.of("*");
            List<String> wildcardHeaders = List.of("*");

            // when
            properties.setAllowedOrigins(wildcardOrigins);
            properties.setAllowedHeaders(wildcardHeaders);

            // then
            assertThat(properties.getAllowedOrigins()).containsExactly("*");
            assertThat(properties.getAllowedHeaders()).containsExactly("*");
        }
    }

    @Nested
    @DisplayName("일반적인 CORS 설정 시나리오 테스트")
    class CorsScenarioTest {

        @Test
        @DisplayName("로컬 개발 환경 CORS 설정")
        void localDevelopmentConfig_shouldWork() {
            // given
            properties.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080"));
            properties.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            properties.setAllowedHeaders(List.of("*"));
            properties.setExposedHeaders(List.of("Authorization"));
            properties.setAllowCredentials(true);

            // then
            assertThat(properties.getAllowedOrigins()).hasSize(2);
            assertThat(properties.getAllowedMethods()).hasSize(5);
            assertThat(properties.isAllowCredentials()).isTrue();
        }

        @Test
        @DisplayName("프로덕션 환경 CORS 설정")
        void productionConfig_shouldWork() {
            // given
            properties.setAllowedOrigins(List.of("https://app.example.com"));
            properties.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
            properties.setAllowedHeaders(List.of("Authorization", "Content-Type"));
            properties.setExposedHeaders(List.of("X-Request-Id"));
            properties.setAllowCredentials(true);

            // then
            assertThat(properties.getAllowedOrigins()).containsExactly("https://app.example.com");
            assertThat(properties.getAllowedMethods()).hasSize(4);
            assertThat(properties.getAllowedHeaders()).hasSize(2);
            assertThat(properties.getExposedHeaders()).hasSize(1);
        }

        @Test
        @DisplayName("빈 CORS 설정 (기본값)")
        void emptyConfig_shouldWork() {
            // given - default state

            // then
            assertThat(properties.getAllowedOrigins()).isEmpty();
            assertThat(properties.getAllowedMethods()).isEmpty();
            assertThat(properties.getAllowedHeaders()).isEmpty();
            assertThat(properties.getExposedHeaders()).isEmpty();
            assertThat(properties.isAllowCredentials()).isFalse();
        }
    }
}
