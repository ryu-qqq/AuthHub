package com.ryuqq.authhub.application.auth.port.out.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserRolePersistencePort 인터페이스 설계 검증 테스트
 *
 * <p>PersistencePort Zero-Tolerance 규칙:
 * <ul>
 *   <li>인터페이스명: *PersistencePort</li>
 *   <li>패키지: application.*.port.out.command</li>
 *   <li>필수 메서드: persist(UserId, Set&lt;String&gt; roles)</li>
 *   <li>필수 메서드: deleteByUserId(UserId)</li>
 *   <li>Value Object 파라미터</li>
 *   <li>조회 메서드 금지 (QueryPort로 분리)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("port")
@DisplayName("UserRolePersistencePort 인터페이스 설계 테스트")
class UserRolePersistencePortTest {

    @Nested
    @DisplayName("인터페이스 구조 검증")
    class InterfaceStructureTest {

        @Test
        @DisplayName("[필수] UserRolePersistencePort는 인터페이스여야 한다")
        void shouldBeInterface() {
            // When & Then
            assertThat(UserRolePersistencePort.class.isInterface())
                    .as("UserRolePersistencePort는 interface로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] UserRolePersistencePort는 public이어야 한다")
        void shouldBePublic() {
            // When & Then
            assertThat(Modifier.isPublic(UserRolePersistencePort.class.getModifiers()))
                    .as("UserRolePersistencePort는 public으로 선언되어야 합니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("persist() 메서드 검증")
    class PersistMethodTest {

        @Test
        @DisplayName("[필수] persist(UserId, Set) 메서드가 존재해야 한다")
        void shouldHavePersistMethod() throws NoSuchMethodException {
            // When
            Method persistMethod = UserRolePersistencePort.class.getMethod(
                    "persist", UserId.class, Set.class);

            // Then
            assertThat(persistMethod).isNotNull();
        }

        @Test
        @DisplayName("[필수] persist() 메서드는 void를 반환해야 한다")
        void persistShouldReturnVoid() throws NoSuchMethodException {
            // When
            Method persistMethod = UserRolePersistencePort.class.getMethod(
                    "persist", UserId.class, Set.class);

            // Then
            assertThat(persistMethod.getReturnType())
                    .as("persist() 메서드는 void를 반환해야 합니다")
                    .isEqualTo(void.class);
        }

        @Test
        @DisplayName("[필수] persist() 메서드는 UserId와 Set을 파라미터로 받아야 한다")
        void persistShouldAcceptUserIdAndRolesParameter() throws NoSuchMethodException {
            // When
            Method persistMethod = UserRolePersistencePort.class.getMethod(
                    "persist", UserId.class, Set.class);
            Class<?>[] paramTypes = persistMethod.getParameterTypes();

            // Then
            assertThat(paramTypes).hasSize(2);
            assertThat(paramTypes[0])
                    .as("첫 번째 파라미터는 UserId (Value Object)여야 합니다")
                    .isEqualTo(UserId.class);
            assertThat(paramTypes[1])
                    .as("두 번째 파라미터는 Set (roles)이어야 합니다")
                    .isEqualTo(Set.class);
        }
    }

    @Nested
    @DisplayName("deleteByUserId() 메서드 검증")
    class DeleteByUserIdMethodTest {

        @Test
        @DisplayName("[필수] deleteByUserId(UserId) 메서드가 존재해야 한다")
        void shouldHaveDeleteByUserIdMethod() throws NoSuchMethodException {
            // When
            Method deleteMethod = UserRolePersistencePort.class.getMethod(
                    "deleteByUserId", UserId.class);

            // Then
            assertThat(deleteMethod).isNotNull();
        }

        @Test
        @DisplayName("[필수] deleteByUserId() 메서드는 void를 반환해야 한다")
        void deleteByUserIdShouldReturnVoid() throws NoSuchMethodException {
            // When
            Method deleteMethod = UserRolePersistencePort.class.getMethod(
                    "deleteByUserId", UserId.class);

            // Then
            assertThat(deleteMethod.getReturnType())
                    .as("deleteByUserId() 메서드는 void를 반환해야 합니다")
                    .isEqualTo(void.class);
        }

        @Test
        @DisplayName("[필수] deleteByUserId() 메서드는 UserId를 파라미터로 받아야 한다")
        void deleteByUserIdShouldAcceptUserIdParameter() throws NoSuchMethodException {
            // When
            Method deleteMethod = UserRolePersistencePort.class.getMethod(
                    "deleteByUserId", UserId.class);
            Class<?>[] paramTypes = deleteMethod.getParameterTypes();

            // Then
            assertThat(paramTypes).hasSize(1);
            assertThat(paramTypes[0])
                    .as("deleteByUserId() 메서드는 UserId (Value Object)를 파라미터로 받아야 합니다")
                    .isEqualTo(UserId.class);
        }
    }

    @Nested
    @DisplayName("금지된 메서드 검증")
    class ProhibitedMethodsTest {

        @Test
        @DisplayName("[금지] 조회 메서드(find/load/exists)가 존재하면 안 된다")
        void shouldNotHaveQueryMethods() {
            // When
            boolean hasQueryMethod = Arrays.stream(UserRolePersistencePort.class.getMethods())
                    .map(Method::getName)
                    .anyMatch(name -> name.startsWith("find") || name.startsWith("load")
                            || name.startsWith("exists") || name.startsWith("get"));

            // Then
            assertThat(hasQueryMethod)
                    .as("PersistencePort는 조회 메서드를 가지면 안 됩니다 (CQRS)")
                    .isFalse();
        }

        @Test
        @DisplayName("[금지] save/update 개별 메서드가 존재하면 안 된다")
        void shouldNotHaveSaveUpdateMethods() {
            // When
            List<String> prohibitedNames = List.of("save", "update");
            boolean hasProhibitedMethod = Arrays.stream(UserRolePersistencePort.class.getMethods())
                    .map(Method::getName)
                    .anyMatch(prohibitedNames::contains);

            // Then
            assertThat(hasProhibitedMethod)
                    .as("PersistencePort는 save/update 개별 메서드를 가지면 안 됩니다 (persist로 통합)")
                    .isFalse();
        }
    }
}
