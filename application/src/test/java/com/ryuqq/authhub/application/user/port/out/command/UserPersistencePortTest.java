package com.ryuqq.authhub.application.user.port.out.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserPersistencePort 인터페이스 설계 검증 테스트
 *
 * <p>PersistencePort Zero-Tolerance 규칙:
 * <ul>
 *   <li>인터페이스명: *PersistencePort</li>
 *   <li>패키지: application.*.port.out.command</li>
 *   <li>메서드: persist(User) → UserId 하나만</li>
 *   <li>save/update/delete 메서드 금지</li>
 *   <li>조회 메서드 금지 (QueryPort로 분리)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("port")
@DisplayName("UserPersistencePort 인터페이스 설계 테스트")
class UserPersistencePortTest {

    @Nested
    @DisplayName("인터페이스 구조 검증")
    class InterfaceStructureTest {

        @Test
        @DisplayName("[필수] UserPersistencePort는 인터페이스여야 한다")
        void shouldBeInterface() {
            // When & Then
            assertThat(UserPersistencePort.class.isInterface())
                    .as("UserPersistencePort는 interface로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] UserPersistencePort는 public이어야 한다")
        void shouldBePublic() {
            // When & Then
            assertThat(Modifier.isPublic(UserPersistencePort.class.getModifiers()))
                    .as("UserPersistencePort는 public으로 선언되어야 합니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("persist() 메서드 검증")
    class PersistMethodTest {

        @Test
        @DisplayName("[필수] persist(User) 메서드가 존재해야 한다")
        void shouldHavePersistMethod() throws NoSuchMethodException {
            // When
            Method persistMethod = UserPersistencePort.class.getMethod("persist", User.class);

            // Then
            assertThat(persistMethod).isNotNull();
        }

        @Test
        @DisplayName("[필수] persist() 메서드는 UserId를 반환해야 한다")
        void persistShouldReturnUserId() throws NoSuchMethodException {
            // When
            Method persistMethod = UserPersistencePort.class.getMethod("persist", User.class);

            // Then
            assertThat(persistMethod.getReturnType())
                    .as("persist() 메서드는 UserId를 반환해야 합니다")
                    .isEqualTo(UserId.class);
        }

        @Test
        @DisplayName("[필수] persist() 메서드는 User를 파라미터로 받아야 한다")
        void persistShouldAcceptUserParameter() throws NoSuchMethodException {
            // When
            Method persistMethod = UserPersistencePort.class.getMethod("persist", User.class);
            Class<?>[] paramTypes = persistMethod.getParameterTypes();

            // Then
            assertThat(paramTypes).hasSize(1);
            assertThat(paramTypes[0])
                    .as("persist() 메서드는 User (Domain Aggregate)를 파라미터로 받아야 합니다")
                    .isEqualTo(User.class);
        }
    }

    @Nested
    @DisplayName("금지된 메서드 검증")
    class ProhibitedMethodsTest {

        @Test
        @DisplayName("[금지] save() 메서드가 존재하면 안 된다")
        void shouldNotHaveSaveMethod() {
            // When
            boolean hasSaveMethod = hasMethodWithName("save");

            // Then
            assertThat(hasSaveMethod)
                    .as("PersistencePort는 save() 대신 persist()를 사용해야 합니다")
                    .isFalse();
        }

        @Test
        @DisplayName("[금지] update() 메서드가 존재하면 안 된다")
        void shouldNotHaveUpdateMethod() {
            // When
            boolean hasUpdateMethod = hasMethodWithName("update");

            // Then
            assertThat(hasUpdateMethod)
                    .as("PersistencePort는 update() 대신 persist()를 사용해야 합니다 (JPA merge)")
                    .isFalse();
        }

        @Test
        @DisplayName("[금지] delete() 메서드가 존재하면 안 된다")
        void shouldNotHaveDeleteMethod() {
            // When
            boolean hasDeleteMethod = hasMethodWithName("delete");

            // Then
            assertThat(hasDeleteMethod)
                    .as("삭제는 Domain에서 User.delete()로 논리 삭제 후 persist()로 저장해야 합니다")
                    .isFalse();
        }

        @Test
        @DisplayName("[금지] 조회 메서드(find/get/load)가 존재하면 안 된다")
        void shouldNotHaveFindMethods() {
            // When
            List<String> prohibitedPrefixes = List.of("find", "get", "load", "exists", "count");
            boolean hasProhibitedMethod = Arrays.stream(UserPersistencePort.class.getMethods())
                    .map(Method::getName)
                    .anyMatch(name -> prohibitedPrefixes.stream().anyMatch(name::startsWith));

            // Then
            assertThat(hasProhibitedMethod)
                    .as("조회 메서드는 QueryPort로 분리해야 합니다 (CQRS)")
                    .isFalse();
        }

        private boolean hasMethodWithName(String methodName) {
            return Arrays.stream(UserPersistencePort.class.getMethods())
                    .anyMatch(method -> method.getName().equals(methodName));
        }
    }
}
