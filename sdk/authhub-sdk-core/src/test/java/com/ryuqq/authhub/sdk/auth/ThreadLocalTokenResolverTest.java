package com.ryuqq.authhub.sdk.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ThreadLocalTokenResolver")
class ThreadLocalTokenResolverTest {

    @AfterEach
    void tearDown() {
        ThreadLocalTokenResolver.clear();
    }

    @Nested
    @DisplayName("INSTANCE")
    class Instance {

        @Test
        @DisplayName("싱글톤 인스턴스를 반환한다")
        void shouldReturnSingletonInstance() {
            // when
            ThreadLocalTokenResolver instance1 = ThreadLocalTokenResolver.INSTANCE;
            ThreadLocalTokenResolver instance2 = ThreadLocalTokenResolver.INSTANCE;

            // then
            assertThat(instance1).isSameAs(instance2);
        }
    }

    @Nested
    @DisplayName("set/resolve 메서드")
    class SetAndResolve {

        @Test
        @DisplayName("설정된 토큰을 반환한다")
        void shouldReturnSetToken() {
            // given
            String token = "user-access-token";
            ThreadLocalTokenResolver.set(token);

            // when
            Optional<String> result = ThreadLocalTokenResolver.INSTANCE.resolve();

            // then
            assertThat(result).isPresent().contains(token);
        }

        @Test
        @DisplayName("토큰이 설정되지 않으면 empty를 반환한다")
        void shouldReturnEmptyWhenNotSet() {
            // when
            Optional<String> result = ThreadLocalTokenResolver.INSTANCE.resolve();

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("토큰을 덮어쓸 수 있다")
        void shouldOverwriteToken() {
            // given
            ThreadLocalTokenResolver.set("first-token");

            // when
            ThreadLocalTokenResolver.set("second-token");
            Optional<String> result = ThreadLocalTokenResolver.INSTANCE.resolve();

            // then
            assertThat(result).contains("second-token");
        }

        @Test
        @DisplayName("null을 설정하면 resolve시 empty를 반환한다")
        void shouldReturnEmptyWhenSetNull() {
            // given
            ThreadLocalTokenResolver.set("some-token");
            ThreadLocalTokenResolver.set(null);

            // when
            Optional<String> result = ThreadLocalTokenResolver.INSTANCE.resolve();

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("clear 메서드")
    class Clear {

        @Test
        @DisplayName("토큰을 제거한다")
        void shouldClearToken() {
            // given
            ThreadLocalTokenResolver.set("token-to-clear");

            // when
            ThreadLocalTokenResolver.clear();
            Optional<String> result = ThreadLocalTokenResolver.INSTANCE.resolve();

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("토큰이 없어도 예외가 발생하지 않는다")
        void shouldNotThrowWhenNothingToClear() {
            // when & then (no exception)
            ThreadLocalTokenResolver.clear();
            assertThat(ThreadLocalTokenResolver.INSTANCE.resolve()).isEmpty();
        }
    }

    @Nested
    @DisplayName("스레드 격리")
    class ThreadIsolation {

        @Test
        @DisplayName("각 스레드는 독립적인 토큰을 가진다")
        void shouldIsolateTokensPerThread() throws InterruptedException {
            // given
            String mainThreadToken = "main-thread-token";
            String otherThreadToken = "other-thread-token";
            ThreadLocalTokenResolver.set(mainThreadToken);

            AtomicReference<Optional<String>> otherThreadResult = new AtomicReference<>();
            CountDownLatch latch = new CountDownLatch(1);

            // when
            Thread otherThread =
                    new Thread(
                            () -> {
                                ThreadLocalTokenResolver.set(otherThreadToken);
                                otherThreadResult.set(ThreadLocalTokenResolver.INSTANCE.resolve());
                                latch.countDown();
                            });
            otherThread.start();
            latch.await();

            // then
            assertThat(ThreadLocalTokenResolver.INSTANCE.resolve()).contains(mainThreadToken);
            assertThat(otherThreadResult.get()).contains(otherThreadToken);
        }

        @Test
        @DisplayName("한 스레드에서 clear해도 다른 스레드에 영향 없다")
        void clearInOneThreadShouldNotAffectOther() throws InterruptedException {
            // given
            String mainToken = "main-token";
            ThreadLocalTokenResolver.set(mainToken);

            CountDownLatch clearLatch = new CountDownLatch(1);
            CountDownLatch checkLatch = new CountDownLatch(1);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            try {
                // when - 다른 스레드에서 clear 실행
                executor.submit(
                        () -> {
                            ThreadLocalTokenResolver.set("other-token");
                            ThreadLocalTokenResolver.clear();
                            clearLatch.countDown();
                            try {
                                checkLatch.await();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        });

                clearLatch.await();

                // then - 메인 스레드의 토큰은 유지
                assertThat(ThreadLocalTokenResolver.INSTANCE.resolve()).contains(mainToken);
                checkLatch.countDown();
            } finally {
                executor.shutdown();
            }
        }
    }
}
