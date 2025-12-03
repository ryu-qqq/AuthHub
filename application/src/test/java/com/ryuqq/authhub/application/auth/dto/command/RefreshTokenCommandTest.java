package com.ryuqq.authhub.application.auth.dto.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RefreshTokenCommand DTO 설계 검증 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("dto")
@DisplayName("RefreshTokenCommand DTO 설계 테스트")
class RefreshTokenCommandTest {

    @Nested
    @DisplayName("Record 구조 검증")
    class RecordStructureTest {

        @Test
        @DisplayName("[필수] RefreshTokenCommand는 Record 타입이어야 한다")
        void shouldBeRecord() {
            assertThat(RefreshTokenCommand.class.isRecord())
                    .as("RefreshTokenCommand는 Record 타입으로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] RefreshTokenCommand는 public이어야 한다")
        void shouldBePublic() {
            assertThat(Modifier.isPublic(RefreshTokenCommand.class.getModifiers()))
                    .as("RefreshTokenCommand는 public으로 선언되어야 합니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("필드 검증")
    class FieldsTest {

        @Test
        @DisplayName("[필수] refreshToken 필드가 존재해야 한다")
        void shouldHaveRefreshTokenField() {
            RecordComponent[] components = RefreshTokenCommand.class.getRecordComponents();

            assertThat(components).extracting(RecordComponent::getName).contains("refreshToken");

            RecordComponent component = findComponent(components, "refreshToken");
            assertThat(component.getType())
                    .as("refreshToken은 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }
    }

    @Nested
    @DisplayName("최소 필드 원칙 검증")
    class MinimalFieldsTest {

        @Test
        @DisplayName("[권장] RefreshTokenCommand는 최소 필드만 포함해야 한다")
        void shouldHaveMinimalFields() {
            RecordComponent[] components = RefreshTokenCommand.class.getRecordComponents();

            // 토큰 갱신 요청은 refreshToken만 필요
            assertThat(components.length)
                    .as("RefreshTokenCommand는 최소한의 필드만 포함해야 합니다 (1개)")
                    .isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("불변성 검증")
    class ImmutabilityTest {

        @Test
        @DisplayName("[필수] Record 인스턴스 생성 후 필드 값이 유지되어야 한다")
        void shouldMaintainFieldValues() {
            // Given
            String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

            // When
            RefreshTokenCommand command = new RefreshTokenCommand(refreshToken);

            // Then
            assertThat(command.refreshToken()).isEqualTo(refreshToken);
        }
    }

    private RecordComponent findComponent(RecordComponent[] components, String name) {
        return java.util.Arrays.stream(components)
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Component not found: " + name));
    }
}
