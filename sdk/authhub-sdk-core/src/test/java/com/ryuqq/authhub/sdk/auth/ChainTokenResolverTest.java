package com.ryuqq.authhub.sdk.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ChainTokenResolver")
class ChainTokenResolverTest {

    @AfterEach
    void tearDown() {
        ThreadLocalTokenResolver.clear();
    }

    @Nested
    @DisplayName("of 팩토리 메서드")
    class Of {

        @Test
        @DisplayName("여러 resolver를 체인으로 연결한다")
        void shouldCreateChainWithMultipleResolvers() {
            // given
            TokenResolver first = new StaticTokenResolver("first");
            TokenResolver second = new StaticTokenResolver("second");

            // when
            ChainTokenResolver chain = ChainTokenResolver.of(first, second);

            // then
            assertThat(chain).isNotNull();
            assertThat(chain.resolve()).contains("first");
        }

        @Test
        @DisplayName("null resolvers 배열로 생성하면 NullPointerException이 발생한다")
        void shouldThrowExceptionWhenResolversIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> ChainTokenResolver.of((TokenResolver[]) null))
                    .withMessageContaining("resolvers must not be null");
        }

        @Test
        @DisplayName("빈 resolver 배열로 생성하면 empty를 반환한다")
        void shouldReturnEmptyWhenNoResolvers() {
            // when
            ChainTokenResolver chain = ChainTokenResolver.of();

            // then
            assertThat(chain.resolve()).isEmpty();
        }
    }

    @Nested
    @DisplayName("withFallback 팩토리 메서드")
    class WithFallback {

        @Test
        @DisplayName("ThreadLocal → ServiceToken 순서로 체인을 생성한다")
        void shouldCreateThreadLocalThenServiceTokenChain() {
            // given
            String serviceToken = "service-token";

            // when
            ChainTokenResolver chain = ChainTokenResolver.withFallback(serviceToken);

            // then - ThreadLocal이 비어있으면 서비스 토큰 반환
            assertThat(chain.resolve()).contains(serviceToken);
        }

        @Test
        @DisplayName("ThreadLocal에 토큰이 있으면 우선 사용한다")
        void shouldPrioritizeThreadLocalToken() {
            // given
            String serviceToken = "service-token";
            String userToken = "user-token";
            ChainTokenResolver chain = ChainTokenResolver.withFallback(serviceToken);
            ThreadLocalTokenResolver.set(userToken);

            // when
            Optional<String> result = chain.resolve();

            // then
            assertThat(result).contains(userToken);
        }

        @Test
        @DisplayName("null serviceToken으로 생성하면 NullPointerException이 발생한다")
        void shouldThrowExceptionWhenServiceTokenIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> ChainTokenResolver.withFallback(null))
                    .withMessageContaining("serviceToken must not be null");
        }
    }

    @Nested
    @DisplayName("resolve 메서드")
    class Resolve {

        @Test
        @DisplayName("첫 번째로 토큰을 반환하는 resolver의 결과를 사용한다")
        void shouldReturnFirstNonEmptyToken() {
            // given
            TokenResolver emptyResolver = Optional::empty;
            TokenResolver firstToken = () -> Optional.of("first-token");
            TokenResolver secondToken = () -> Optional.of("second-token");

            ChainTokenResolver chain =
                    ChainTokenResolver.of(emptyResolver, firstToken, secondToken);

            // when
            Optional<String> result = chain.resolve();

            // then
            assertThat(result).contains("first-token");
        }

        @Test
        @DisplayName("모든 resolver가 empty를 반환하면 empty를 반환한다")
        void shouldReturnEmptyWhenAllResolversReturnEmpty() {
            // given
            TokenResolver empty1 = Optional::empty;
            TokenResolver empty2 = Optional::empty;
            ChainTokenResolver chain = ChainTokenResolver.of(empty1, empty2);

            // when
            Optional<String> result = chain.resolve();

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("단일 resolver 체인도 정상 동작한다")
        void shouldWorkWithSingleResolver() {
            // given
            String token = "single-token";
            ChainTokenResolver chain = ChainTokenResolver.of(new StaticTokenResolver(token));

            // when
            Optional<String> result = chain.resolve();

            // then
            assertThat(result).contains(token);
        }
    }

    @Nested
    @DisplayName("일반적인 사용 시나리오")
    class UsageScenarios {

        @Test
        @DisplayName("API 게이트웨이: 사용자 토큰이 없으면 서비스 토큰 사용")
        void apiGatewayScenario() {
            // given
            String serviceToken = "service-account-token";
            ChainTokenResolver chain = ChainTokenResolver.withFallback(serviceToken);

            // when - 사용자 토큰 없음
            Optional<String> withoutUserToken = chain.resolve();

            // then
            assertThat(withoutUserToken).contains(serviceToken);

            // when - 사용자 토큰 설정
            ThreadLocalTokenResolver.set("user-jwt-token");
            Optional<String> withUserToken = chain.resolve();

            // then
            assertThat(withUserToken).contains("user-jwt-token");
        }

        @Test
        @DisplayName("배치 작업: 서비스 토큰만 사용")
        void batchJobScenario() {
            // given
            String batchServiceToken = "batch-service-token";
            TokenResolver resolver = new StaticTokenResolver(batchServiceToken);

            // when
            Optional<String> result = resolver.resolve();

            // then
            assertThat(result).contains(batchServiceToken);
        }
    }
}
