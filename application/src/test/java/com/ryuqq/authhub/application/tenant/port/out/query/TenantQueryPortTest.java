package com.ryuqq.authhub.application.tenant.port.out.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
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
 * TenantQueryPort 인터페이스 설계 검증 테스트
 *
 * <p>QueryPort Zero-Tolerance 규칙:
 * <ul>
 *   <li>인터페이스명: *QueryPort</li>
 *   <li>패키지: application.*.port.out.query</li>
 *   <li>필수 메서드: findById(TenantId) → Optional&lt;Tenant&gt;</li>
 *   <li>필수 메서드: existsById(TenantId) → boolean</li>
 *   <li>Value Object 파라미터, Domain 반환</li>
 *   <li>저장/수정/삭제 메서드 금지 (CQRS 분리)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("port")
@DisplayName("TenantQueryPort 인터페이스 설계 테스트")
class TenantQueryPortTest {

    @Nested
    @DisplayName("인터페이스 구조 검증")
    class InterfaceStructureTest {

        @Test
        @DisplayName("[필수] TenantQueryPort는 인터페이스여야 한다")
        void shouldBeInterface() {
            // When & Then
            assertThat(TenantQueryPort.class.isInterface())
                    .as("TenantQueryPort는 interface로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] TenantQueryPort는 public이어야 한다")
        void shouldBePublic() {
            // When & Then
            assertThat(Modifier.isPublic(TenantQueryPort.class.getModifiers()))
                    .as("TenantQueryPort는 public으로 선언되어야 합니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("findById() 메서드 검증")
    class FindByIdMethodTest {

        @Test
        @DisplayName("[필수] findById(TenantId) 메서드가 존재해야 한다")
        void shouldHaveFindByIdMethod() throws NoSuchMethodException {
            // When
            Method findByIdMethod = TenantQueryPort.class.getMethod("findById", TenantId.class);

            // Then
            assertThat(findByIdMethod).isNotNull();
        }

        @Test
        @DisplayName("[필수] findById() 메서드는 Optional<Tenant>를 반환해야 한다")
        void findByIdShouldReturnOptionalTenant() throws NoSuchMethodException {
            // When
            Method findByIdMethod = TenantQueryPort.class.getMethod("findById", TenantId.class);
            Type returnType = findByIdMethod.getGenericReturnType();

            // Then
            assertThat(findByIdMethod.getReturnType())
                    .as("findById() 메서드는 Optional을 반환해야 합니다")
                    .isEqualTo(Optional.class);

            // Generic 타입 검증 (Optional<Tenant>)
            if (returnType instanceof ParameterizedType parameterizedType) {
                Type[] typeArgs = parameterizedType.getActualTypeArguments();
                assertThat(typeArgs).hasSize(1);
                assertThat(typeArgs[0])
                        .as("Optional의 Generic 타입은 Tenant여야 합니다")
                        .isEqualTo(Tenant.class);
            }
        }

        @Test
        @DisplayName("[필수] findById() 메서드는 TenantId를 파라미터로 받아야 한다")
        void findByIdShouldAcceptTenantIdParameter() throws NoSuchMethodException {
            // When
            Method findByIdMethod = TenantQueryPort.class.getMethod("findById", TenantId.class);
            Class<?>[] paramTypes = findByIdMethod.getParameterTypes();

            // Then
            assertThat(paramTypes).hasSize(1);
            assertThat(paramTypes[0])
                    .as("findById() 메서드는 TenantId (Value Object)를 파라미터로 받아야 합니다")
                    .isEqualTo(TenantId.class);
        }
    }

    @Nested
    @DisplayName("existsById() 메서드 검증")
    class ExistsByIdMethodTest {

        @Test
        @DisplayName("[필수] existsById(TenantId) 메서드가 존재해야 한다")
        void shouldHaveExistsByIdMethod() throws NoSuchMethodException {
            // When
            Method existsByIdMethod = TenantQueryPort.class.getMethod("existsById", TenantId.class);

            // Then
            assertThat(existsByIdMethod).isNotNull();
        }

        @Test
        @DisplayName("[필수] existsById() 메서드는 boolean을 반환해야 한다")
        void existsByIdShouldReturnBoolean() throws NoSuchMethodException {
            // When
            Method existsByIdMethod = TenantQueryPort.class.getMethod("existsById", TenantId.class);

            // Then
            assertThat(existsByIdMethod.getReturnType())
                    .as("existsById() 메서드는 boolean을 반환해야 합니다")
                    .isEqualTo(boolean.class);
        }

        @Test
        @DisplayName("[필수] existsById() 메서드는 TenantId를 파라미터로 받아야 한다")
        void existsByIdShouldAcceptTenantIdParameter() throws NoSuchMethodException {
            // When
            Method existsByIdMethod = TenantQueryPort.class.getMethod("existsById", TenantId.class);
            Class<?>[] paramTypes = existsByIdMethod.getParameterTypes();

            // Then
            assertThat(paramTypes).hasSize(1);
            assertThat(paramTypes[0])
                    .as("existsById() 메서드는 TenantId (Value Object)를 파라미터로 받아야 합니다")
                    .isEqualTo(TenantId.class);
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
            boolean hasProhibitedMethod = Arrays.stream(TenantQueryPort.class.getMethods())
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
            boolean hasFindAllMethod = Arrays.stream(TenantQueryPort.class.getMethods())
                    .anyMatch(method -> method.getName().equals("findAll"));

            // Then
            assertThat(hasFindAllMethod)
                    .as("findAll()은 OOM 위험이 있어 금지입니다. 페이징 처리된 search()를 사용하세요")
                    .isFalse();
        }
    }
}
