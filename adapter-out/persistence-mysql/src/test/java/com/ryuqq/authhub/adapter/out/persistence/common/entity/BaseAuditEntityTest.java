package com.ryuqq.authhub.adapter.out.persistence.common.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("BaseAuditEntity 테스트")
class BaseAuditEntityTest {

    @Nested
    @DisplayName("생성자 테스트")
    class ConstructorTest {

        @Test
        @DisplayName("[constructor] 감사 필드 생성자로 createdAt, updatedAt 초기화")
        void constructor_shouldInitializeAuditFields() {
            // Given
            LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
            LocalDateTime updatedAt = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

            // When
            TestBaseAuditEntity entity = new TestBaseAuditEntity(createdAt, updatedAt);

            // Then
            assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
            assertThat(entity.getUpdatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("[constructor] 기본 생성자는 null 값으로 초기화")
        void defaultConstructor_shouldInitializeWithNullValues() {
            // When
            TestBaseAuditEntity entity = new TestBaseAuditEntity();

            // Then
            assertThat(entity.getCreatedAt()).isNull();
            assertThat(entity.getUpdatedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("Getter 테스트")
    class GetterTest {

        @Test
        @DisplayName("[getCreatedAt] 생성일시 반환")
        void getCreatedAt_shouldReturnCreatedAt() {
            // Given
            LocalDateTime createdAt = LocalDateTime.of(2025, 6, 15, 12, 0, 0);
            TestBaseAuditEntity entity = new TestBaseAuditEntity(createdAt, LocalDateTime.now());

            // When
            LocalDateTime result = entity.getCreatedAt();

            // Then
            assertThat(result).isEqualTo(createdAt);
        }

        @Test
        @DisplayName("[getUpdatedAt] 수정일시 반환")
        void getUpdatedAt_shouldReturnUpdatedAt() {
            // Given
            LocalDateTime updatedAt = LocalDateTime.of(2025, 6, 20, 18, 30, 0);
            TestBaseAuditEntity entity = new TestBaseAuditEntity(LocalDateTime.now(), updatedAt);

            // When
            LocalDateTime result = entity.getUpdatedAt();

            // Then
            assertThat(result).isEqualTo(updatedAt);
        }
    }

    /** 테스트용 BaseAuditEntity 구현체 */
    @SuppressWarnings("PMD.TestClassWithoutTestCases")
    private static class TestBaseAuditEntity extends BaseAuditEntity {

        TestBaseAuditEntity() {
            super();
        }

        TestBaseAuditEntity(LocalDateTime createdAt, LocalDateTime updatedAt) {
            super(createdAt, updatedAt);
        }
    }
}
