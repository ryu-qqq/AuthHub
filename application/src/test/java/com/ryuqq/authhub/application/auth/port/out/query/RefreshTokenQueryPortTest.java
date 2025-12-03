package com.ryuqq.authhub.application.auth.port.out.query;

import static org.assertj.core.api.Assertions.assertThat;

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
 * RefreshTokenQueryPort 인터페이스 설계 검증 테스트
 *
 * <p>QueryPort Zero-Tolerance 규칙:
 *
 * <ul>
 *   <li>인터페이스명: *QueryPort
 *   <li>패키지: application.*.port.out.query
 *   <li>필수 메서드: findByUserId(UserId) → Optional&lt;String&gt;
 *   <li>필수 메서드: existsByUserId(UserId) → boolean
 *   <li>Value Object 파라미터
 *   <li>저장/수정/삭제 메서드 금지 (CQRS 분리)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("port")
@DisplayName("RefreshTokenQueryPort 인터페이스 설계 테스트")
class RefreshTokenQueryPortTest {

    @Nested
    @DisplayName("인터페이스 구조 검증")
    class InterfaceStructureTest {

        @Test
        @DisplayName("[필수] RefreshTokenQueryPort는 인터페이스여야 한다")
        void shouldBeInterface() {
            // When & Then
            assertThat(RefreshTokenQueryPort.class.isInterface())
                    .as("RefreshTokenQueryPort는 interface로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] RefreshTokenQueryPort는 public이어야 한다")
        void shouldBePublic() {
            // When & Then
            assertThat(Modifier.isPublic(RefreshTokenQueryPort.class.getModifiers()))
                    .as("RefreshTokenQueryPort는 public으로 선언되어야 합니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("findByUserId() 메서드 검증")
    class FindByUserIdMethodTest {

        @Test
        @DisplayName("[필수] findByUserId(UserId) 메서드가 존재해야 한다")
        void shouldHaveFindByUserIdMethod() throws NoSuchMethodException {
            // When
            Method findMethod = RefreshTokenQueryPort.class.getMethod("findByUserId", UserId.class);

            // Then
            assertThat(findMethod).isNotNull();
        }

        @Test
        @DisplayName("[필수] findByUserId() 메서드는 Optional<String>를 반환해야 한다")
        void findByUserIdShouldReturnOptionalString() throws NoSuchMethodException {
            // When
            Method findMethod = RefreshTokenQueryPort.class.getMethod("findByUserId", UserId.class);
            Type returnType = findMethod.getGenericReturnType();

            // Then
            assertThat(findMethod.getReturnType())
                    .as("findByUserId() 메서드는 Optional을 반환해야 합니다")
                    .isEqualTo(Optional.class);

            // Generic 타입 검증 (Optional<String>)
            if (returnType instanceof ParameterizedType parameterizedType) {
                Type[] typeArgs = parameterizedType.getActualTypeArguments();
                assertThat(typeArgs).hasSize(1);
                assertThat(typeArgs[0])
                        .as("Optional의 Generic 타입은 String이어야 합니다")
                        .isEqualTo(String.class);
            }
        }

        @Test
        @DisplayName("[필수] findByUserId() 메서드는 UserId를 파라미터로 받아야 한다")
        void findByUserIdShouldAcceptUserIdParameter() throws NoSuchMethodException {
            // When
            Method findMethod = RefreshTokenQueryPort.class.getMethod("findByUserId", UserId.class);
            Class<?>[] paramTypes = findMethod.getParameterTypes();

            // Then
            assertThat(paramTypes).hasSize(1);
            assertThat(paramTypes[0])
                    .as("findByUserId() 메서드는 UserId (Value Object)를 파라미터로 받아야 합니다")
                    .isEqualTo(UserId.class);
        }
    }

    @Nested
    @DisplayName("existsByUserId() 메서드 검증")
    class ExistsByUserIdMethodTest {

        @Test
        @DisplayName("[필수] existsByUserId(UserId) 메서드가 존재해야 한다")
        void shouldHaveExistsByUserIdMethod() throws NoSuchMethodException {
            // When
            Method existsMethod =
                    RefreshTokenQueryPort.class.getMethod("existsByUserId", UserId.class);

            // Then
            assertThat(existsMethod).isNotNull();
        }

        @Test
        @DisplayName("[필수] existsByUserId() 메서드는 boolean을 반환해야 한다")
        void existsByUserIdShouldReturnBoolean() throws NoSuchMethodException {
            // When
            Method existsMethod =
                    RefreshTokenQueryPort.class.getMethod("existsByUserId", UserId.class);

            // Then
            assertThat(existsMethod.getReturnType())
                    .as("existsByUserId() 메서드는 boolean을 반환해야 합니다")
                    .isEqualTo(boolean.class);
        }

        @Test
        @DisplayName("[필수] existsByUserId() 메서드는 UserId를 파라미터로 받아야 한다")
        void existsByUserIdShouldAcceptUserIdParameter() throws NoSuchMethodException {
            // When
            Method existsMethod =
                    RefreshTokenQueryPort.class.getMethod("existsByUserId", UserId.class);
            Class<?>[] paramTypes = existsMethod.getParameterTypes();

            // Then
            assertThat(paramTypes).hasSize(1);
            assertThat(paramTypes[0])
                    .as("existsByUserId() 메서드는 UserId (Value Object)를 파라미터로 받아야 합니다")
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
                    Arrays.stream(RefreshTokenQueryPort.class.getMethods())
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
                    Arrays.stream(RefreshTokenQueryPort.class.getMethods())
                            .anyMatch(method -> method.getName().equals("findAll"));

            // Then
            assertThat(hasFindAllMethod).as("findAll()은 OOM 위험이 있어 금지입니다").isFalse();
        }
    }
}
