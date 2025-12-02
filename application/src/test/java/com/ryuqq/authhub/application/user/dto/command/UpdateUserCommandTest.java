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
 * UpdateUserCommand DTO 설계 검증 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("dto")
@DisplayName("UpdateUserCommand DTO 설계 테스트")
class UpdateUserCommandTest {

    @Nested
    @DisplayName("Record 구조 검증")
    class RecordStructureTest {

        @Test
        @DisplayName("[필수] UpdateUserCommand는 Record 타입이어야 한다")
        void shouldBeRecord() {
            assertThat(UpdateUserCommand.class.isRecord())
                    .as("UpdateUserCommand는 Record 타입으로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] UpdateUserCommand는 public이어야 한다")
        void shouldBePublic() {
            assertThat(Modifier.isPublic(UpdateUserCommand.class.getModifiers()))
                    .as("UpdateUserCommand는 public으로 선언되어야 합니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("필드 검증")
    class FieldsTest {

        @Test
        @DisplayName("[필수] userId 필드가 존재해야 한다")
        void shouldHaveUserIdField() {
            RecordComponent[] components = UpdateUserCommand.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("userId");

            RecordComponent component = findComponent(components, "userId");
            assertThat(component.getType())
                    .as("userId는 UUID 타입이어야 합니다")
                    .isEqualTo(UUID.class);
        }

        @Test
        @DisplayName("[선택] name 필드가 존재해야 한다")
        void shouldHaveNameField() {
            RecordComponent[] components = UpdateUserCommand.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("name");
        }

        @Test
        @DisplayName("[선택] nickname 필드가 존재해야 한다")
        void shouldHaveNicknameField() {
            RecordComponent[] components = UpdateUserCommand.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("nickname");
        }

        @Test
        @DisplayName("[선택] profileImageUrl 필드가 존재해야 한다")
        void shouldHaveProfileImageUrlField() {
            RecordComponent[] components = UpdateUserCommand.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("profileImageUrl");
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
            String name = "홍길동";
            String nickname = "길동이";
            String profileImageUrl = "https://example.com/profile.jpg";

            // When
            UpdateUserCommand command = new UpdateUserCommand(
                    userId, name, nickname, profileImageUrl
            );

            // Then
            assertThat(command.userId()).isEqualTo(userId);
            assertThat(command.name()).isEqualTo(name);
            assertThat(command.nickname()).isEqualTo(nickname);
            assertThat(command.profileImageUrl()).isEqualTo(profileImageUrl);
        }
    }

    private RecordComponent findComponent(RecordComponent[] components, String name) {
        return java.util.Arrays.stream(components)
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Component not found: " + name));
    }
}
