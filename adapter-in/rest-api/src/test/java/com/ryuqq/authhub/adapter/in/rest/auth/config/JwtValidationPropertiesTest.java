package com.ryuqq.authhub.adapter.in.rest.auth.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
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

    private JwtValidationProperties properties;

    @BeforeEach
    void setUp() {
        properties = new JwtValidationProperties();
    }

    @Nested
    @DisplayName("기본값 테스트")
    class DefaultValueTest {

        @Test
        @DisplayName("issuer 기본값은 authhub이다")
        void issuer_defaultValue_shouldBeAuthub() {
            // given & when & then
            assertThat(properties.getIssuer()).isEqualTo("authhub");
        }

        @Test
        @DisplayName("secret 기본값은 null이다")
        void secret_defaultValue_shouldBeNull() {
            // given & when & then
            assertThat(properties.getSecret()).isNull();
        }

        @Test
        @DisplayName("RSA 기본값은 비활성화 상태이다")
        void rsa_defaultValue_shouldBeDisabled() {
            // given & when & then
            assertThat(properties.getRsa()).isNotNull();
            assertThat(properties.getRsa().isEnabled()).isFalse();
            assertThat(properties.getRsa().getPublicKeyPath()).isNull();
        }
    }

    @Nested
    @DisplayName("setter/getter 테스트")
    class SetterGetterTest {

        @Test
        @DisplayName("secret을 설정하고 조회할 수 있다")
        void secret_setAndGet_shouldWork() {
            // given
            String secret = "test-secret-key";

            // when
            properties.setSecret(secret);

            // then
            assertThat(properties.getSecret()).isEqualTo(secret);
        }

        @Test
        @DisplayName("issuer를 설정하고 조회할 수 있다")
        void issuer_setAndGet_shouldWork() {
            // given
            String issuer = "custom-issuer";

            // when
            properties.setIssuer(issuer);

            // then
            assertThat(properties.getIssuer()).isEqualTo(issuer);
        }

        @Test
        @DisplayName("RSA 설정을 변경할 수 있다")
        void rsa_setAndGet_shouldWork() {
            // given
            JwtValidationProperties.RsaProperties rsa = new JwtValidationProperties.RsaProperties();
            rsa.setEnabled(true);
            rsa.setPublicKeyPath("/path/to/public.pem");

            // when
            properties.setRsa(rsa);

            // then
            assertThat(properties.getRsa().isEnabled()).isTrue();
            assertThat(properties.getRsa().getPublicKeyPath()).isEqualTo("/path/to/public.pem");
        }
    }

    @Nested
    @DisplayName("RsaProperties 테스트")
    class RsaPropertiesTest {

        @Test
        @DisplayName("enabled를 설정하고 조회할 수 있다")
        void enabled_setAndGet_shouldWork() {
            // given
            JwtValidationProperties.RsaProperties rsa = new JwtValidationProperties.RsaProperties();

            // when
            rsa.setEnabled(true);

            // then
            assertThat(rsa.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("publicKeyPath를 설정하고 조회할 수 있다")
        void publicKeyPath_setAndGet_shouldWork() {
            // given
            JwtValidationProperties.RsaProperties rsa = new JwtValidationProperties.RsaProperties();
            String path = "/etc/authhub/jwt/public.pem";

            // when
            rsa.setPublicKeyPath(path);

            // then
            assertThat(rsa.getPublicKeyPath()).isEqualTo(path);
        }

        @Test
        @DisplayName("기본 생성자로 인스턴스를 생성할 수 있다")
        void constructor_default_shouldCreateInstance() {
            // given & when
            JwtValidationProperties.RsaProperties rsa = new JwtValidationProperties.RsaProperties();

            // then
            assertThat(rsa).isNotNull();
            assertThat(rsa.isEnabled()).isFalse();
            assertThat(rsa.getPublicKeyPath()).isNull();
        }
    }
}
