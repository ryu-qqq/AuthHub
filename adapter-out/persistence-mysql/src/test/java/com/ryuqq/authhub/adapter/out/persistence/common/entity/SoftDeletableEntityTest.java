package com.ryuqq.authhub.adapter.out.persistence.common.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("SoftDeletableEntity 테스트")
class SoftDeletableEntityTest {

    @Nested
    @DisplayName("생성자 테스트")
    class ConstructorTest {

        @Test
        @DisplayName("[constructor] 기본 생성자는 null 값으로 초기화")
        void defaultConstructor_shouldInitializeWithNullValues() {
            // When
            TestSoftDeletableEntity entity = new TestSoftDeletableEntity();

            // Then
            assertThat(entity.getCreatedAt()).isNull();
            assertThat(entity.getUpdatedAt()).isNull();
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("[constructor] 감사 필드 생성자로 초기화 (삭제일 미포함)")
        void constructor_shouldInitializeAuditFieldsWithoutDeletedAt() {
            // Given
            LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
            LocalDateTime updatedAt = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

            // When
            TestSoftDeletableEntity entity = new TestSoftDeletableEntity(createdAt, updatedAt);

            // Then
            assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
            assertThat(entity.getUpdatedAt()).isEqualTo(updatedAt);
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("[constructor] 전체 필드 생성자로 초기화 (삭제일 포함)")
        void constructor_shouldInitializeAllFieldsIncludingDeletedAt() {
            // Given
            LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
            LocalDateTime updatedAt = LocalDateTime.of(2025, 1, 2, 15, 30, 0);
            LocalDateTime deletedAt = LocalDateTime.of(2025, 1, 3, 9, 0, 0);

            // When
            TestSoftDeletableEntity entity =
                    new TestSoftDeletableEntity(createdAt, updatedAt, deletedAt);

            // Then
            assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
            assertThat(entity.getUpdatedAt()).isEqualTo(updatedAt);
            assertThat(entity.getDeletedAt()).isEqualTo(deletedAt);
        }

        @Test
        @DisplayName("[constructor] 전체 필드 생성자에 deletedAt null 전달 시 활성 상태")
        void constructor_shouldBeActiveWhenDeletedAtIsNull() {
            // Given
            LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
            LocalDateTime updatedAt = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

            // When
            TestSoftDeletableEntity entity =
                    new TestSoftDeletableEntity(createdAt, updatedAt, null);

            // Then
            assertThat(entity.getDeletedAt()).isNull();
            assertThat(entity.isDeleted()).isFalse();
            assertThat(entity.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("isDeleted() 테스트")
    class IsDeletedTest {

        @Test
        @DisplayName("[isDeleted] deletedAt이 null이면 false 반환")
        void isDeleted_shouldReturnFalseWhenDeletedAtIsNull() {
            // Given
            TestSoftDeletableEntity entity =
                    new TestSoftDeletableEntity(LocalDateTime.now(), LocalDateTime.now());

            // When
            boolean result = entity.isDeleted();

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("[isDeleted] deletedAt이 설정되면 true 반환")
        void isDeleted_shouldReturnTrueWhenDeletedAtIsSet() {
            // Given
            TestSoftDeletableEntity entity =
                    new TestSoftDeletableEntity(
                            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

            // When
            boolean result = entity.isDeleted();

            // Then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("isActive() 테스트")
    class IsActiveTest {

        @Test
        @DisplayName("[isActive] deletedAt이 null이면 true 반환")
        void isActive_shouldReturnTrueWhenDeletedAtIsNull() {
            // Given
            TestSoftDeletableEntity entity =
                    new TestSoftDeletableEntity(LocalDateTime.now(), LocalDateTime.now());

            // When
            boolean result = entity.isActive();

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("[isActive] deletedAt이 설정되면 false 반환")
        void isActive_shouldReturnFalseWhenDeletedAtIsSet() {
            // Given
            TestSoftDeletableEntity entity =
                    new TestSoftDeletableEntity(
                            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

            // When
            boolean result = entity.isActive();

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("getDeletedAt() 테스트")
    class GetDeletedAtTest {

        @Test
        @DisplayName("[getDeletedAt] 삭제일시 반환")
        void getDeletedAt_shouldReturnDeletedAt() {
            // Given
            LocalDateTime deletedAt = LocalDateTime.of(2025, 6, 15, 12, 0, 0);
            TestSoftDeletableEntity entity =
                    new TestSoftDeletableEntity(
                            LocalDateTime.now(), LocalDateTime.now(), deletedAt);

            // When
            LocalDateTime result = entity.getDeletedAt();

            // Then
            assertThat(result).isEqualTo(deletedAt);
        }
    }

    /** 테스트용 SoftDeletableEntity 구현체 */
    @SuppressWarnings("PMD.TestClassWithoutTestCases")
    private static class TestSoftDeletableEntity extends SoftDeletableEntity {

        TestSoftDeletableEntity() {
            super();
        }

        TestSoftDeletableEntity(LocalDateTime createdAt, LocalDateTime updatedAt) {
            super(createdAt, updatedAt);
        }

        TestSoftDeletableEntity(
                LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
            super(createdAt, updatedAt, deletedAt);
        }
    }
}
