package com.ryuqq.authhub.application.user.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CreateUserResponse DTO 설계 검증 테스트
 *
 * <p>사용자 생성 응답은 최소 정보만 반환합니다 (생성된 userId 중심)
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("dto")
@DisplayName("CreateUserResponse DTO 설계 테스트")
class CreateUserResponseTest {

    @Nested
    @DisplayName("Record 구조 검증")
    class RecordStructureTest {

        @Test
        @DisplayName("[필수] CreateUserResponse는 Record 타입이어야 한다")
        void shouldBeRecord() {
            assertThat(CreateUserResponse.class.isRecord())
                    .as("CreateUserResponse는 Record 타입으로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] CreateUserResponse는 public이어야 한다")
        void shouldBePublic() {
            assertThat(Modifier.isPublic(CreateUserResponse.class.getModifiers()))
                    .as("CreateUserResponse는 public으로 선언되어야 합니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("필드 검증")
    class FieldsTest {

        @Test
        @DisplayName("[필수] userId 필드가 존재해야 한다")
        void shouldHaveUserIdField() {
            RecordComponent[] components = CreateUserResponse.class.getRecordComponents();

            assertThat(components).extracting(RecordComponent::getName).contains("userId");

            RecordComponent component = findComponent(components, "userId");
            assertThat(component.getType()).as("userId는 UUID 타입이어야 합니다").isEqualTo(UUID.class);
        }

        @Test
        @DisplayName("[필수] createdAt 필드가 존재해야 한다")
        void shouldHaveCreatedAtField() {
            RecordComponent[] components = CreateUserResponse.class.getRecordComponents();

            assertThat(components).extracting(RecordComponent::getName).contains("createdAt");

            RecordComponent component = findComponent(components, "createdAt");
            assertThat(component.getType())
                    .as("createdAt은 Instant 타입이어야 합니다")
                    .isEqualTo(Instant.class);
        }
    }

    @Nested
    @DisplayName("최소 응답 원칙 검증")
    class MinimalResponseTest {

        @Test
        @DisplayName("[권장] CreateUserResponse는 최소 필드만 포함해야 한다")
        void shouldHaveMinimalFields() {
            RecordComponent[] components = CreateUserResponse.class.getRecordComponents();

            // 생성 응답은 userId, createdAt 정도만 필요
            assertThat(components.length)
                    .as("CreateUserResponse는 최소한의 필드만 포함해야 합니다 (2-3개)")
                    .isLessThanOrEqualTo(3);
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
            Instant createdAt = Instant.now();

            // When
            CreateUserResponse response = new CreateUserResponse(userId, createdAt);

            // Then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.createdAt()).isEqualTo(createdAt);
        }
    }

    private RecordComponent findComponent(RecordComponent[] components, String name) {
        return java.util.Arrays.stream(components)
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Component not found: " + name));
    }
}
