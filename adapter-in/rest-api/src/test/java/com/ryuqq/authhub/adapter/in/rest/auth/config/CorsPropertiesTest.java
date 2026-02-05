package com.ryuqq.authhub.adapter.in.rest.auth.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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

    @Nested
    @DisplayName("Properties 바인딩")
    class PropertiesBinding {

        @Test
        @DisplayName("allowedOrigins를 설정하고 조회할 수 있다")
        void shouldSetAndGetAllowedOrigins() {
            CorsProperties properties = new CorsProperties();
            List<String> origins = List.of("http://localhost:3000", "https://app.example.com");

            properties.setAllowedOrigins(origins);

            assertThat(properties.getAllowedOrigins()).isEqualTo(origins);
        }

        @Test
        @DisplayName("allowedMethods를 설정하고 조회할 수 있다")
        void shouldSetAndGetAllowedMethods() {
            CorsProperties properties = new CorsProperties();
            List<String> methods = List.of("GET", "POST", "PUT");

            properties.setAllowedMethods(methods);

            assertThat(properties.getAllowedMethods()).isEqualTo(methods);
        }

        @Test
        @DisplayName("allowedHeaders를 설정하고 조회할 수 있다")
        void shouldSetAndGetAllowedHeaders() {
            CorsProperties properties = new CorsProperties();
            List<String> headers = List.of("Authorization", "Content-Type");

            properties.setAllowedHeaders(headers);

            assertThat(properties.getAllowedHeaders()).isEqualTo(headers);
        }

        @Test
        @DisplayName("exposedHeaders를 설정하고 조회할 수 있다")
        void shouldSetAndGetExposedHeaders() {
            CorsProperties properties = new CorsProperties();
            List<String> headers = List.of("Authorization");

            properties.setExposedHeaders(headers);

            assertThat(properties.getExposedHeaders()).isEqualTo(headers);
        }

        @Test
        @DisplayName("allowCredentials를 설정하고 조회할 수 있다")
        void shouldSetAndGetAllowCredentials() {
            CorsProperties properties = new CorsProperties();

            properties.setAllowCredentials(true);

            assertThat(properties.isAllowCredentials()).isTrue();
        }
    }

    @Nested
    @DisplayName("기본값")
    class DefaultValues {

        @Test
        @DisplayName("빈 리스트 기본값을 반환한다")
        void shouldReturnEmptyListDefaults() {
            CorsProperties properties = new CorsProperties();

            assertThat(properties.getAllowedOrigins()).isEmpty();
            assertThat(properties.getAllowedMethods()).isEmpty();
            assertThat(properties.getAllowedHeaders()).isEmpty();
            assertThat(properties.getExposedHeaders()).isEmpty();
        }

        @Test
        @DisplayName("allowCredentials 기본값은 false이다")
        void shouldHaveFalseDefaultForAllowCredentials() {
            CorsProperties properties = new CorsProperties();

            assertThat(properties.isAllowCredentials()).isFalse();
        }
    }
}
