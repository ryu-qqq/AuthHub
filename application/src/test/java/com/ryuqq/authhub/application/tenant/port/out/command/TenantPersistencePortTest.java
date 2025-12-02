package com.ryuqq.authhub.application.tenant.port.out.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantPersistencePort 인터페이스 설계 검증 테스트
 *
 * <p>PersistencePort Zero-Tolerance 규칙:
 * <ul>
 *   <li>인터페이스명: *PersistencePort</li>
 *   <li>패키지: application.*.port.out.command</li>
 *   <li>필수 메서드: persist(Tenant) → TenantId</li>
 *   <li>Domain 파라미터, Value Object 반환</li>
 *   <li>save/update/delete 개별 메서드 금지 (persist로 통합)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("port")
@DisplayName("TenantPersistencePort 인터페이스 설계 테스트")
class TenantPersistencePortTest {

    @Nested
    @DisplayName("인터페이스 구조 검증")
    class InterfaceStructureTest {

        @Test
        @DisplayName("[필수] TenantPersistencePort는 인터페이스여야 한다")
        void shouldBeInterface() {
            // When & Then
            assertThat(TenantPersistencePort.class.isInterface())
                    .as("TenantPersistencePort는 interface로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] TenantPersistencePort는 public이어야 한다")
        void shouldBePublic() {
            // When & Then
            assertThat(Modifier.isPublic(TenantPersistencePort.class.getModifiers()))
                    .as("TenantPersistencePort는 public으로 선언되어야 합니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("persist() 메서드 검증")
    class PersistMethodTest {

        @Test
        @DisplayName("[필수] persist(Tenant) 메서드가 존재해야 한다")
        void shouldHavePersistMethod() throws NoSuchMethodException {
            // When
            Method persistMethod = TenantPersistencePort.class.getMethod("persist", Tenant.class);

            // Then
            assertThat(persistMethod).isNotNull();
        }

        @Test
        @DisplayName("[필수] persist() 메서드는 TenantId를 반환해야 한다")
        void persistShouldReturnTenantId() throws NoSuchMethodException {
            // When
            Method persistMethod = TenantPersistencePort.class.getMethod("persist", Tenant.class);

            // Then
            assertThat(persistMethod.getReturnType())
                    .as("persist() 메서드는 TenantId를 반환해야 합니다")
                    .isEqualTo(TenantId.class);
        }

        @Test
        @DisplayName("[필수] persist() 메서드는 Tenant를 파라미터로 받아야 한다")
        void persistShouldAcceptTenantParameter() throws NoSuchMethodException {
            // When
            Method persistMethod = TenantPersistencePort.class.getMethod("persist", Tenant.class);
            Class<?>[] paramTypes = persistMethod.getParameterTypes();

            // Then
            assertThat(paramTypes).hasSize(1);
            assertThat(paramTypes[0])
                    .as("persist() 메서드는 Tenant (Domain Aggregate)를 파라미터로 받아야 합니다")
                    .isEqualTo(Tenant.class);
        }
    }

    @Nested
    @DisplayName("금지된 메서드 검증")
    class ProhibitedMethodsTest {

        @Test
        @DisplayName("[금지] save/update/delete 개별 메서드가 존재하면 안 된다")
        void shouldNotHaveSaveUpdateDeleteMethods() {
            // When
            List<String> prohibitedNames = List.of("save", "update", "delete", "remove");
            boolean hasProhibitedMethod = Arrays.stream(TenantPersistencePort.class.getMethods())
                    .map(Method::getName)
                    .anyMatch(prohibitedNames::contains);

            // Then
            assertThat(hasProhibitedMethod)
                    .as("PersistencePort는 save/update/delete 개별 메서드를 가지면 안 됩니다 (persist로 통합)")
                    .isFalse();
        }

        @Test
        @DisplayName("[금지] 조회 메서드(find/load/exists)가 존재하면 안 된다")
        void shouldNotHaveQueryMethods() {
            // When
            boolean hasQueryMethod = Arrays.stream(TenantPersistencePort.class.getMethods())
                    .map(Method::getName)
                    .anyMatch(name -> name.startsWith("find") || name.startsWith("load")
                            || name.startsWith("exists") || name.startsWith("get"));

            // Then
            assertThat(hasQueryMethod)
                    .as("PersistencePort는 조회 메서드를 가지면 안 됩니다 (CQRS)")
                    .isFalse();
        }
    }
}
