package com.ryuqq.authhub.sdk.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AuthHubConfig")
class AuthHubConfigTest {

    @Nested
    @DisplayName("기본 상수")
    class DefaultConstants {

        @Test
        @DisplayName("기본 연결 타임아웃은 5초이다")
        void defaultConnectTimeoutIsFiveSeconds() {
            assertThat(AuthHubConfig.DEFAULT_CONNECT_TIMEOUT).isEqualTo(Duration.ofSeconds(5));
        }

        @Test
        @DisplayName("기본 읽기 타임아웃은 30초이다")
        void defaultReadTimeoutIsThirtySeconds() {
            assertThat(AuthHubConfig.DEFAULT_READ_TIMEOUT).isEqualTo(Duration.ofSeconds(30));
        }
    }

    @Nested
    @DisplayName("생성자")
    class Constructor {

        @Test
        @DisplayName("모든 값을 지정하여 생성할 수 있다")
        void shouldCreateWithAllValues() {
            // given
            String baseUrl = "https://authhub.example.com";
            Duration connectTimeout = Duration.ofSeconds(10);
            Duration readTimeout = Duration.ofSeconds(60);

            // when
            AuthHubConfig config = new AuthHubConfig(baseUrl, connectTimeout, readTimeout);

            // then
            assertThat(config.baseUrl()).isEqualTo(baseUrl);
            assertThat(config.connectTimeout()).isEqualTo(connectTimeout);
            assertThat(config.readTimeout()).isEqualTo(readTimeout);
        }

        @Test
        @DisplayName("타임아웃이 null이면 기본값을 사용한다")
        void shouldUseDefaultTimeoutsWhenNull() {
            // given
            String baseUrl = "https://authhub.example.com";

            // when
            AuthHubConfig config = new AuthHubConfig(baseUrl, null, null);

            // then
            assertThat(config.connectTimeout()).isEqualTo(AuthHubConfig.DEFAULT_CONNECT_TIMEOUT);
            assertThat(config.readTimeout()).isEqualTo(AuthHubConfig.DEFAULT_READ_TIMEOUT);
        }

        @Test
        @DisplayName("baseUrl이 null이면 NullPointerException이 발생한다")
        void shouldThrowExceptionWhenBaseUrlIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> new AuthHubConfig(null, null, null))
                    .withMessageContaining("baseUrl must not be null");
        }

        @Test
        @DisplayName("baseUrl이 빈 문자열이면 IllegalArgumentException이 발생한다")
        void shouldThrowExceptionWhenBaseUrlIsEmpty() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new AuthHubConfig("", null, null))
                    .withMessageContaining("baseUrl must not be blank");
        }

        @Test
        @DisplayName("baseUrl이 공백 문자열이면 IllegalArgumentException이 발생한다")
        void shouldThrowExceptionWhenBaseUrlIsBlank() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new AuthHubConfig("   ", null, null))
                    .withMessageContaining("baseUrl must not be blank");
        }
    }

    @Nested
    @DisplayName("of 팩토리 메서드")
    class OfMethod {

        @Test
        @DisplayName("baseUrl만으로 기본 설정을 생성한다")
        void shouldCreateWithDefaultSettings() {
            // given
            String baseUrl = "https://authhub.example.com";

            // when
            AuthHubConfig config = AuthHubConfig.of(baseUrl);

            // then
            assertThat(config.baseUrl()).isEqualTo(baseUrl);
            assertThat(config.connectTimeout()).isEqualTo(AuthHubConfig.DEFAULT_CONNECT_TIMEOUT);
            assertThat(config.readTimeout()).isEqualTo(AuthHubConfig.DEFAULT_READ_TIMEOUT);
        }

        @Test
        @DisplayName("baseUrl이 null이면 NullPointerException이 발생한다")
        void shouldThrowExceptionWhenBaseUrlIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> AuthHubConfig.of(null))
                    .withMessageContaining("baseUrl must not be null");
        }
    }

    @Nested
    @DisplayName("Record 동등성")
    class Equality {

        @Test
        @DisplayName("같은 값을 가진 인스턴스는 동등하다")
        void shouldBeEqualWithSameValues() {
            // given
            String baseUrl = "https://authhub.example.com";
            Duration connectTimeout = Duration.ofSeconds(10);
            Duration readTimeout = Duration.ofSeconds(60);

            // when
            AuthHubConfig config1 = new AuthHubConfig(baseUrl, connectTimeout, readTimeout);
            AuthHubConfig config2 = new AuthHubConfig(baseUrl, connectTimeout, readTimeout);

            // then
            assertThat(config1).isEqualTo(config2);
            assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 인스턴스는 동등하지 않다")
        void shouldNotBeEqualWithDifferentValues() {
            // given
            AuthHubConfig config1 = AuthHubConfig.of("https://api1.example.com");
            AuthHubConfig config2 = AuthHubConfig.of("https://api2.example.com");

            // then
            assertThat(config1).isNotEqualTo(config2);
        }
    }
}
