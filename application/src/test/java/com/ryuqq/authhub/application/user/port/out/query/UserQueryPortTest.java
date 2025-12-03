package com.ryuqq.authhub.application.user.port.out.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserQueryPort 인터페이스 설계 검증 테스트
 *
 * <p>QueryPort Zero-Tolerance 규칙:
 *
 * <ul>
 *   <li>인터페이스명: *QueryPort
 *   <li>패키지: application.*.port.out.query
 *   <li>필수 메서드: findById(UserId) → Optional&lt;User&gt;
 *   <li>필수 메서드: existsById(UserId) → boolean
 *   <li>Value Object 파라미터, Domain 반환
 *   <li>저장/수정/삭제 메서드 금지 (CQRS 분리)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("port")
@DisplayName("UserQueryPort 인터페이스 설계 테스트")
class UserQueryPortTest {

    @Nested
    @DisplayName("인터페이스 구조 검증")
    class InterfaceStructureTest {

        @Test
        @DisplayName("[필수] UserQueryPort는 인터페이스여야 한다")
        void shouldBeInterface() {
            // When & Then
            assertThat(UserQueryPort.class.isInterface())
                    .as("UserQueryPort는 interface로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] UserQueryPort는 public이어야 한다")
        void shouldBePublic() {
            // When & Then
            assertThat(Modifier.isPublic(UserQueryPort.class.getModifiers()))
                    .as("UserQueryPort는 public으로 선언되어야 합니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("findById() 메서드 검증")
    class FindByIdMethodTest {

        @Test
        @DisplayName("[필수] findById(UserId) 메서드가 존재해야 한다")
        void shouldHaveFindByIdMethod() throws NoSuchMethodException {
            // When
            Method findByIdMethod = UserQueryPort.class.getMethod("findById", UserId.class);

            // Then
            assertThat(findByIdMethod).isNotNull();
        }

        @Test
        @DisplayName("[필수] findById() 메서드는 Optional<User>를 반환해야 한다")
        void findByIdShouldReturnOptionalUser() throws NoSuchMethodException {
            // When
            Method findByIdMethod = UserQueryPort.class.getMethod("findById", UserId.class);
            Type returnType = findByIdMethod.getGenericReturnType();

            // Then
            assertThat(findByIdMethod.getReturnType())
                    .as("findById() 메서드는 Optional을 반환해야 합니다")
                    .isEqualTo(Optional.class);

            // Generic 타입 검증 (Optional<User>)
            if (returnType instanceof ParameterizedType parameterizedType) {
                Type[] typeArgs = parameterizedType.getActualTypeArguments();
                assertThat(typeArgs).hasSize(1);
                assertThat(typeArgs[0])
                        .as("Optional의 Generic 타입은 User여야 합니다")
                        .isEqualTo(User.class);
            }
        }

        @Test
        @DisplayName("[필수] findById() 메서드는 UserId를 파라미터로 받아야 한다")
        void findByIdShouldAcceptUserIdParameter() throws NoSuchMethodException {
            // When
            Method findByIdMethod = UserQueryPort.class.getMethod("findById", UserId.class);
            Class<?>[] paramTypes = findByIdMethod.getParameterTypes();

            // Then
            assertThat(paramTypes).hasSize(1);
            assertThat(paramTypes[0])
                    .as("findById() 메서드는 UserId (Value Object)를 파라미터로 받아야 합니다")
                    .isEqualTo(UserId.class);
        }
    }

    @Nested
    @DisplayName("existsById() 메서드 검증")
    class ExistsByIdMethodTest {

        @Test
        @DisplayName("[필수] existsById(UserId) 메서드가 존재해야 한다")
        void shouldHaveExistsByIdMethod() throws NoSuchMethodException {
            // When
            Method existsByIdMethod = UserQueryPort.class.getMethod("existsById", UserId.class);

            // Then
            assertThat(existsByIdMethod).isNotNull();
        }

        @Test
        @DisplayName("[필수] existsById() 메서드는 boolean을 반환해야 한다")
        void existsByIdShouldReturnBoolean() throws NoSuchMethodException {
            // When
            Method existsByIdMethod = UserQueryPort.class.getMethod("existsById", UserId.class);

            // Then
            assertThat(existsByIdMethod.getReturnType())
                    .as("existsById() 메서드는 boolean을 반환해야 합니다")
                    .isEqualTo(boolean.class);
        }

        @Test
        @DisplayName("[필수] existsById() 메서드는 UserId를 파라미터로 받아야 한다")
        void existsByIdShouldAcceptUserIdParameter() throws NoSuchMethodException {
            // When
            Method existsByIdMethod = UserQueryPort.class.getMethod("existsById", UserId.class);
            Class<?>[] paramTypes = existsByIdMethod.getParameterTypes();

            // Then
            assertThat(paramTypes).hasSize(1);
            assertThat(paramTypes[0])
                    .as("existsById() 메서드는 UserId (Value Object)를 파라미터로 받아야 합니다")
                    .isEqualTo(UserId.class);
        }
    }

    @Nested
    @DisplayName("금지된 메서드 검증")
    class ProhibitedMethodsTest {

        @Test
        @DisplayName("[금지] 저장 메서드(save/persist)가 존재하면 안 된다")
        void shouldNotHaveSaveOrPersistMethod() {
            // When
            List<String> prohibitedNames = List.of("save", "persist", "update", "delete", "remove");
            boolean hasProhibitedMethod =
                    Arrays.stream(UserQueryPort.class.getMethods())
                            .map(Method::getName)
                            .anyMatch(prohibitedNames::contains);

            // Then
            assertThat(hasProhibitedMethod)
                    .as("QueryPort는 저장/수정/삭제 메서드를 가지면 안 됩니다 (CQRS)")
                    .isFalse();
        }

        @Test
        @DisplayName("[금지] findAll() 메서드가 존재하면 안 된다")
        void shouldNotHaveFindAllMethod() {
            // When
            boolean hasFindAllMethod =
                    Arrays.stream(UserQueryPort.class.getMethods())
                            .anyMatch(method -> method.getName().equals("findAll"));

            // Then
            assertThat(hasFindAllMethod)
                    .as("findAll()은 OOM 위험이 있어 금지입니다. 페이징 처리된 search()를 사용하세요")
                    .isFalse();
        }
    }
}
