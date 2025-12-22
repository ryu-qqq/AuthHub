package com.ryuqq.auth.common.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("UserContextHolder")
class UserContextHolderTest {

    @AfterEach
    void tearDown() {
        UserContextHolder.clearContext();
    }

    @Nested
    @DisplayName("setContext / getContext")
    class SetAndGetContext {

        @Test
        @DisplayName("컨텍스트 설정 및 조회")
        void setAndGetContext() {
            UserContext context =
                    UserContext.builder().userId("user-123").tenantId("tenant-456").build();

            UserContextHolder.setContext(context);

            assertThat(UserContextHolder.getContext()).isEqualTo(context);
        }

        @Test
        @DisplayName("컨텍스트 없으면 anonymous 반환")
        void returnsAnonymousWhenNoContext() {
            UserContext context = UserContextHolder.getContext();

            assertThat(context).isNotNull();
            assertThat(context.getUserId()).isNull();
            assertThat(context.isAuthenticated()).isFalse();
        }

        @Test
        @DisplayName("null 컨텍스트 설정 시 무시")
        void ignoresNullContext() {
            UserContext context = UserContext.builder().userId("user-123").build();
            UserContextHolder.setContext(context);

            UserContextHolder.setContext(null);

            assertThat(UserContextHolder.getContext().getUserId()).isEqualTo("user-123");
        }
    }

    @Nested
    @DisplayName("getContextOptional")
    class GetContextOptional {

        @Test
        @DisplayName("컨텍스트 있으면 Optional.of 반환")
        void returnsOptionalWithContext() {
            UserContext context = UserContext.builder().userId("user-123").build();
            UserContextHolder.setContext(context);

            assertThat(UserContextHolder.getContextOptional()).isPresent();
            assertThat(UserContextHolder.getContextOptional().get()).isEqualTo(context);
        }

        @Test
        @DisplayName("컨텍스트 없으면 Optional.empty 반환")
        void returnsEmptyOptionalWhenNoContext() {
            assertThat(UserContextHolder.getContextOptional()).isEmpty();
        }
    }

    @Nested
    @DisplayName("clearContext")
    class ClearContext {

        @Test
        @DisplayName("컨텍스트 제거")
        void clearsContext() {
            UserContext context = UserContext.builder().userId("user-123").build();
            UserContextHolder.setContext(context);

            UserContextHolder.clearContext();

            assertThat(UserContextHolder.getContextOptional()).isEmpty();
        }
    }

    @Nested
    @DisplayName("편의 메서드")
    class ConvenienceMethods {

        @Test
        @DisplayName("isAuthenticated")
        void isAuthenticated() {
            assertThat(UserContextHolder.isAuthenticated()).isFalse();

            UserContextHolder.setContext(UserContext.builder().userId("user-123").build());

            assertThat(UserContextHolder.isAuthenticated()).isTrue();
        }

        @Test
        @DisplayName("getCurrentUserId")
        void getCurrentUserId() {
            assertThat(UserContextHolder.getCurrentUserId()).isNull();

            UserContextHolder.setContext(UserContext.builder().userId("user-123").build());

            assertThat(UserContextHolder.getCurrentUserId()).isEqualTo("user-123");
        }

        @Test
        @DisplayName("getCurrentTenantId")
        void getCurrentTenantId() {
            UserContextHolder.setContext(UserContext.builder().tenantId("tenant-456").build());

            assertThat(UserContextHolder.getCurrentTenantId()).isEqualTo("tenant-456");
        }

        @Test
        @DisplayName("getCurrentOrganizationId")
        void getCurrentOrganizationId() {
            UserContextHolder.setContext(UserContext.builder().organizationId("org-789").build());

            assertThat(UserContextHolder.getCurrentOrganizationId()).isEqualTo("org-789");
        }

        @Test
        @DisplayName("isServiceAccount")
        void isServiceAccount() {
            UserContextHolder.setContext(UserContext.builder().serviceAccount(true).build());

            assertThat(UserContextHolder.isServiceAccount()).isTrue();
        }

        @Test
        @DisplayName("hasPermission")
        void hasPermission() {
            UserContextHolder.setContext(
                    UserContext.builder().permissions(Set.of("user:read")).build());

            assertThat(UserContextHolder.hasPermission("user:read")).isTrue();
            assertThat(UserContextHolder.hasPermission("user:write")).isFalse();
        }

        @Test
        @DisplayName("hasRole")
        void hasRole() {
            UserContextHolder.setContext(UserContext.builder().roles(Set.of("ROLE_ADMIN")).build());

            assertThat(UserContextHolder.hasRole("ROLE_ADMIN")).isTrue();
            assertThat(UserContextHolder.hasRole("ROLE_USER")).isFalse();
        }
    }

    @Nested
    @DisplayName("runWithContext")
    class RunWithContext {

        @Test
        @DisplayName("지정된 컨텍스트로 작업 실행")
        void runsWithSpecifiedContext() {
            UserContext context = UserContext.builder().userId("temp-user").build();
            AtomicReference<String> capturedUserId = new AtomicReference<>();

            UserContextHolder.runWithContext(
                    context,
                    () -> {
                        capturedUserId.set(UserContextHolder.getCurrentUserId());
                    });

            assertThat(capturedUserId.get()).isEqualTo("temp-user");
        }

        @Test
        @DisplayName("작업 완료 후 이전 컨텍스트 복원")
        void restoresPreviousContext() {
            UserContext original = UserContext.builder().userId("original").build();
            UserContext temp = UserContext.builder().userId("temp").build();
            UserContextHolder.setContext(original);

            UserContextHolder.runWithContext(
                    temp,
                    () -> {
                        assertThat(UserContextHolder.getCurrentUserId()).isEqualTo("temp");
                    });

            assertThat(UserContextHolder.getCurrentUserId()).isEqualTo("original");
        }

        @Test
        @DisplayName("이전 컨텍스트 없으면 clear")
        void clearsWhenNoPreviousContext() {
            UserContext temp = UserContext.builder().userId("temp").build();

            UserContextHolder.runWithContext(
                    temp,
                    () -> {
                        assertThat(UserContextHolder.getCurrentUserId()).isEqualTo("temp");
                    });

            assertThat(UserContextHolder.getContextOptional()).isEmpty();
        }
    }

    @Nested
    @DisplayName("callWithContext")
    class CallWithContext {

        @Test
        @DisplayName("지정된 컨텍스트로 작업 실행 후 결과 반환")
        void callsWithSpecifiedContext() {
            UserContext context = UserContext.builder().userId("temp-user").build();

            String result =
                    UserContextHolder.callWithContext(context, UserContextHolder::getCurrentUserId);

            assertThat(result).isEqualTo("temp-user");
        }

        @Test
        @DisplayName("작업 완료 후 이전 컨텍스트 복원")
        void restoresPreviousContext() {
            UserContext original = UserContext.builder().userId("original").build();
            UserContext temp = UserContext.builder().userId("temp").build();
            UserContextHolder.setContext(original);

            UserContextHolder.callWithContext(temp, () -> "result");

            assertThat(UserContextHolder.getCurrentUserId()).isEqualTo("original");
        }
    }

    @Nested
    @DisplayName("스레드 격리")
    class ThreadIsolation {

        @Test
        @DisplayName("다른 스레드에서 컨텍스트 격리")
        void isolatesContextBetweenThreads() throws InterruptedException {
            UserContext mainContext = UserContext.builder().userId("main-user").build();
            UserContextHolder.setContext(mainContext);

            AtomicReference<String> otherThreadUserId = new AtomicReference<>();

            Thread otherThread =
                    new Thread(
                            () -> {
                                otherThreadUserId.set(UserContextHolder.getCurrentUserId());
                            });
            otherThread.start();
            otherThread.join();

            assertThat(UserContextHolder.getCurrentUserId()).isEqualTo("main-user");
            assertThat(otherThreadUserId.get()).isNull();
        }
    }

    @Nested
    @DisplayName("유틸리티 클래스")
    class UtilityClass {

        @Test
        @DisplayName("인스턴스화 불가")
        void cannotInstantiate() throws Exception {
            Constructor<UserContextHolder> constructor =
                    UserContextHolder.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(AssertionError.class);
        }
    }
}
