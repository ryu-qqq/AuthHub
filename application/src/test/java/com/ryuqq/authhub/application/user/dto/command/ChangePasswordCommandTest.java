package com.ryuqq.authhub.application.user.dto.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ChangePasswordCommand DTO 설계 검증 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("dto")
@DisplayName("ChangePasswordCommand DTO 설계 테스트")
class ChangePasswordCommandTest {

    @Nested
    @DisplayName("Record 구조 검증")
    class RecordStructureTest {

        @Test
        @DisplayName("[필수] ChangePasswordCommand는 Record 타입이어야 한다")
        void shouldBeRecord() {
            assertThat(ChangePasswordCommand.class.isRecord())
                    .as("ChangePasswordCommand는 Record 타입으로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] ChangePasswordCommand는 public이어야 한다")
        void shouldBePublic() {
            assertThat(Modifier.isPublic(ChangePasswordCommand.class.getModifiers()))
                    .as("ChangePasswordCommand는 public으로 선언되어야 합니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("필드 검증")
    class FieldsTest {

        @Test
        @DisplayName("[필수] userId 필드가 존재해야 한다")
        void shouldHaveUserIdField() {
            RecordComponent[] components = ChangePasswordCommand.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("userId");

            RecordComponent component = findComponent(components, "userId");
            assertThat(component.getType())
                    .as("userId는 UUID 타입이어야 합니다")
                    .isEqualTo(UUID.class);
        }

        @Test
        @DisplayName("[필수] currentPassword 필드가 존재해야 한다")
        void shouldHaveCurrentPasswordField() {
            RecordComponent[] components = ChangePasswordCommand.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("currentPassword");

            RecordComponent component = findComponent(components, "currentPassword");
            assertThat(component.getType())
                    .as("currentPassword는 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }

        @Test
        @DisplayName("[필수] newPassword 필드가 존재해야 한다")
        void shouldHaveNewPasswordField() {
            RecordComponent[] components = ChangePasswordCommand.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("newPassword");

            RecordComponent component = findComponent(components, "newPassword");
            assertThat(component.getType())
                    .as("newPassword는 String 타입이어야 합니다")
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
            UUID userId = UUID.randomUUID();
            String currentPassword = "OldPassword123!";
            String newPassword = "NewPassword456!";

            // When
            ChangePasswordCommand command = new ChangePasswordCommand(
                    userId, currentPassword, newPassword
            );

            // Then
            assertThat(command.userId()).isEqualTo(userId);
            assertThat(command.currentPassword()).isEqualTo(currentPassword);
            assertThat(command.newPassword()).isEqualTo(newPassword);
        }
    }

    private RecordComponent findComponent(RecordComponent[] components, String name) {
        return java.util.Arrays.stream(components)
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Component not found: " + name));
    }
}
