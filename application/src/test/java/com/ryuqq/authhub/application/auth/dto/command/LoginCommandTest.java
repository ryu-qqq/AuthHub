package com.ryuqq.authhub.application.auth.dto.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LoginCommand DTO 설계 검증 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("dto")
@DisplayName("LoginCommand DTO 설계 테스트")
class LoginCommandTest {

    @Nested
    @DisplayName("Record 구조 검증")
    class RecordStructureTest {

        @Test
        @DisplayName("[필수] LoginCommand는 Record 타입이어야 한다")
        void shouldBeRecord() {
            assertThat(LoginCommand.class.isRecord())
                    .as("LoginCommand는 Record 타입으로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] LoginCommand는 public이어야 한다")
        void shouldBePublic() {
            assertThat(Modifier.isPublic(LoginCommand.class.getModifiers()))
                    .as("LoginCommand는 public으로 선언되어야 합니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("필드 검증")
    class FieldsTest {

        @Test
        @DisplayName("[필수] tenantId 필드가 존재해야 한다")
        void shouldHaveTenantIdField() {
            RecordComponent[] components = LoginCommand.class.getRecordComponents();

            assertThat(components).extracting(RecordComponent::getName).contains("tenantId");

            RecordComponent component = findComponent(components, "tenantId");
            assertThat(component.getType()).as("tenantId는 Long 타입이어야 합니다").isEqualTo(Long.class);
        }

        @Test
        @DisplayName("[필수] identifier 필드가 존재해야 한다 (이메일 또는 전화번호)")
        void shouldHaveIdentifierField() {
            RecordComponent[] components = LoginCommand.class.getRecordComponents();

            assertThat(components).extracting(RecordComponent::getName).contains("identifier");

            RecordComponent component = findComponent(components, "identifier");
            assertThat(component.getType())
                    .as("identifier는 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }

        @Test
        @DisplayName("[필수] password 필드가 존재해야 한다")
        void shouldHavePasswordField() {
            RecordComponent[] components = LoginCommand.class.getRecordComponents();

            assertThat(components).extracting(RecordComponent::getName).contains("password");

            RecordComponent component = findComponent(components, "password");
            assertThat(component.getType())
                    .as("password는 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }
    }

    @Nested
    @DisplayName("불변성 검증")
    class ImmutabilityTest {

        @Test
        @DisplayName("[필수] Record 인스턴스 생성 후 필드 값이 유지되어야 한다")
        void shouldMaintainFieldValues() {
            // Given
            Long tenantId = 1L;
            String identifier = "user@example.com";
            String password = "SecurePassword123!";

            // When
            LoginCommand command = new LoginCommand(tenantId, identifier, password);

            // Then
            assertThat(command.tenantId()).isEqualTo(tenantId);
            assertThat(command.identifier()).isEqualTo(identifier);
            assertThat(command.password()).isEqualTo(password);
        }
    }

    private RecordComponent findComponent(RecordComponent[] components, String name) {
        return java.util.Arrays.stream(components)
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Component not found: " + name));
    }
}
