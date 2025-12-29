package com.ryuqq.authhub.sdk.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AuthHubProperties")
class AuthHubPropertiesTest {

    @Nested
    @DisplayName("기본 속성")
    class BaseProperties {

        @Test
        @DisplayName("baseUrl을 설정하고 조회할 수 있다")
        void shouldSetAndGetBaseUrl() {
            // given
            AuthHubProperties properties = new AuthHubProperties();
            String baseUrl = "https://authhub.example.com";

            // when
            properties.setBaseUrl(baseUrl);

            // then
            assertThat(properties.getBaseUrl()).isEqualTo(baseUrl);
        }

        @Test
        @DisplayName("serviceToken을 설정하고 조회할 수 있다")
        void shouldSetAndGetServiceToken() {
            // given
            AuthHubProperties properties = new AuthHubProperties();
            String token = "my-service-token";

            // when
            properties.setServiceToken(token);

            // then
            assertThat(properties.getServiceToken()).isEqualTo(token);
        }

        @Test
        @DisplayName("초기 상태에서 baseUrl과 serviceToken은 null이다")
        void shouldHaveNullDefaultsForBaseUrlAndToken() {
            // given
            AuthHubProperties properties = new AuthHubProperties();

            // then
            assertThat(properties.getBaseUrl()).isNull();
            assertThat(properties.getServiceToken()).isNull();
        }
    }

    @Nested
    @DisplayName("Timeout 설정")
    class TimeoutSettings {

        @Test
        @DisplayName("기본 연결 타임아웃은 5초이다")
        void defaultConnectTimeoutIsFiveSeconds() {
            // given
            AuthHubProperties properties = new AuthHubProperties();

            // then
            assertThat(properties.getTimeout().getConnect()).isEqualTo(Duration.ofSeconds(5));
        }

        @Test
        @DisplayName("기본 읽기 타임아웃은 30초이다")
        void defaultReadTimeoutIsThirtySeconds() {
            // given
            AuthHubProperties properties = new AuthHubProperties();

            // then
            assertThat(properties.getTimeout().getRead()).isEqualTo(Duration.ofSeconds(30));
        }

        @Test
        @DisplayName("연결 타임아웃을 변경할 수 있다")
        void shouldSetConnectTimeout() {
            // given
            AuthHubProperties properties = new AuthHubProperties();
            Duration newTimeout = Duration.ofSeconds(10);

            // when
            properties.getTimeout().setConnect(newTimeout);

            // then
            assertThat(properties.getTimeout().getConnect()).isEqualTo(newTimeout);
        }

        @Test
        @DisplayName("읽기 타임아웃을 변경할 수 있다")
        void shouldSetReadTimeout() {
            // given
            AuthHubProperties properties = new AuthHubProperties();
            Duration newTimeout = Duration.ofMinutes(1);

            // when
            properties.getTimeout().setRead(newTimeout);

            // then
            assertThat(properties.getTimeout().getRead()).isEqualTo(newTimeout);
        }

        @Test
        @DisplayName("Timeout 객체는 항상 존재한다 (null이 아님)")
        void timeoutShouldNeverBeNull() {
            // given
            AuthHubProperties properties = new AuthHubProperties();

            // then
            assertThat(properties.getTimeout()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Retry 설정")
    class RetrySettings {

        @Test
        @DisplayName("기본 재시도 활성화 상태는 true이다")
        void defaultRetryEnabledIsTrue() {
            // given
            AuthHubProperties properties = new AuthHubProperties();

            // then
            assertThat(properties.getRetry().isEnabled()).isTrue();
        }

        @Test
        @DisplayName("기본 최대 재시도 횟수는 3이다")
        void defaultMaxAttemptsIsThree() {
            // given
            AuthHubProperties properties = new AuthHubProperties();

            // then
            assertThat(properties.getRetry().getMaxAttempts()).isEqualTo(3);
        }

        @Test
        @DisplayName("기본 재시도 대기 시간은 1초이다")
        void defaultRetryDelayIsOneSecond() {
            // given
            AuthHubProperties properties = new AuthHubProperties();

            // then
            assertThat(properties.getRetry().getDelay()).isEqualTo(Duration.ofSeconds(1));
        }

        @Test
        @DisplayName("재시도 활성화 상태를 변경할 수 있다")
        void shouldSetRetryEnabled() {
            // given
            AuthHubProperties properties = new AuthHubProperties();

            // when
            properties.getRetry().setEnabled(false);

            // then
            assertThat(properties.getRetry().isEnabled()).isFalse();
        }

        @Test
        @DisplayName("최대 재시도 횟수를 변경할 수 있다")
        void shouldSetMaxAttempts() {
            // given
            AuthHubProperties properties = new AuthHubProperties();

            // when
            properties.getRetry().setMaxAttempts(5);

            // then
            assertThat(properties.getRetry().getMaxAttempts()).isEqualTo(5);
        }

        @Test
        @DisplayName("재시도 대기 시간을 변경할 수 있다")
        void shouldSetRetryDelay() {
            // given
            AuthHubProperties properties = new AuthHubProperties();
            Duration newDelay = Duration.ofMillis(500);

            // when
            properties.getRetry().setDelay(newDelay);

            // then
            assertThat(properties.getRetry().getDelay()).isEqualTo(newDelay);
        }

        @Test
        @DisplayName("Retry 객체는 항상 존재한다 (null이 아님)")
        void retryShouldNeverBeNull() {
            // given
            AuthHubProperties properties = new AuthHubProperties();

            // then
            assertThat(properties.getRetry()).isNotNull();
        }
    }
}
