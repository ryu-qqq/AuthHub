package com.ryuqq.authhub.adapter.in.rest.auth.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * JwtValidationProperties 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("JwtValidationProperties 단위 테스트")
class JwtValidationPropertiesTest {

    @Nested
    @DisplayName("Properties 바인딩")
    class PropertiesBinding {

        @Test
        @DisplayName("secret을 설정하고 조회할 수 있다")
        void shouldSetAndGetSecret() {
            JwtValidationProperties properties = new JwtValidationProperties();
            String secret = "my-secret-key";

            properties.setSecret(secret);

            assertThat(properties.getSecret()).isEqualTo(secret);
        }

        @Test
        @DisplayName("issuer를 설정하고 조회할 수 있다")
        void shouldSetAndGetIssuer() {
            JwtValidationProperties properties = new JwtValidationProperties();
            String issuer = "custom-issuer";

            properties.setIssuer(issuer);

            assertThat(properties.getIssuer()).isEqualTo(issuer);
        }

        @Test
        @DisplayName("rsa를 설정하고 조회할 수 있다")
        void shouldSetAndGetRsa() {
            JwtValidationProperties properties = new JwtValidationProperties();
            JwtValidationProperties.RsaProperties rsa = new JwtValidationProperties.RsaProperties();

            properties.setRsa(rsa);

            assertThat(properties.getRsa()).isSameAs(rsa);
        }
    }

    @Nested
    @DisplayName("기본값")
    class DefaultValues {

        @Test
        @DisplayName("issuer 기본값은 authhub이다")
        void shouldHaveAuthhubDefaultForIssuer() {
            JwtValidationProperties properties = new JwtValidationProperties();

            assertThat(properties.getIssuer()).isEqualTo("authhub");
        }

        @Test
        @DisplayName("rsa 기본값은 새 인스턴스이다")
        void shouldHaveNonNullDefaultForRsa() {
            JwtValidationProperties properties = new JwtValidationProperties();

            assertThat(properties.getRsa()).isNotNull();
        }
    }

    @Nested
    @DisplayName("RsaProperties")
    class RsaProperties {

        @Test
        @DisplayName("enabled를 설정하고 조회할 수 있다")
        void shouldSetAndGetEnabled() {
            JwtValidationProperties.RsaProperties rsa = new JwtValidationProperties.RsaProperties();

            rsa.setEnabled(true);

            assertThat(rsa.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("publicKeyPath를 설정하고 조회할 수 있다")
        void shouldSetAndGetPublicKeyPath() {
            JwtValidationProperties.RsaProperties rsa = new JwtValidationProperties.RsaProperties();
            String path = "/path/to/public.pem";

            rsa.setPublicKeyPath(path);

            assertThat(rsa.getPublicKeyPath()).isEqualTo(path);
        }

        @Test
        @DisplayName("enabled 기본값은 false이다")
        void shouldHaveFalseDefaultForEnabled() {
            JwtValidationProperties.RsaProperties rsa = new JwtValidationProperties.RsaProperties();

            assertThat(rsa.isEnabled()).isFalse();
        }
    }
}
