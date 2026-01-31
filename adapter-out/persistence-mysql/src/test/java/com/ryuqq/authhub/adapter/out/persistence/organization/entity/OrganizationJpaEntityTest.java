package com.ryuqq.authhub.adapter.out.persistence.organization.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrganizationJpaEntity 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Entity는 데이터 컨테이너 → of() 팩토리, getter, 상태 확인 메서드 검증
 *   <li>Mock 불필요 (순수 객체 생성/조회)
 *   <li>SoftDeletableEntity 상속 기능 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("OrganizationJpaEntity 단위 테스트")
class OrganizationJpaEntityTest {

    private static final String ORGANIZATION_ID = "org-uuid-123";
    private static final String TENANT_ID = "tenant-uuid-456";
    private static final String NAME = "Test Organization";
    private static final OrganizationStatus STATUS = OrganizationStatus.ACTIVE;
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T00:00:00Z");

    @Nested
    @DisplayName("of() 팩토리 메서드")
    class OfFactoryMethod {

        @Test
        @DisplayName("성공: 모든 필드가 올바르게 설정됨")
        void shouldSetAllFields_Correctly() {
            // when
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            ORGANIZATION_ID, TENANT_ID, NAME, STATUS, CREATED_AT, UPDATED_AT, null);

            // then
            assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
            assertThat(entity.getTenantId()).isEqualTo(TENANT_ID);
            assertThat(entity.getName()).isEqualTo(NAME);
            assertThat(entity.getStatus()).isEqualTo(STATUS);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("deletedAt이 null이면 활성 상태")
        void shouldBeActive_WhenDeletedAtIsNull() {
            // when
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            ORGANIZATION_ID, TENANT_ID, NAME, STATUS, CREATED_AT, UPDATED_AT, null);

            // then
            assertThat(entity.isActive()).isTrue();
            assertThat(entity.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("deletedAt이 설정되면 삭제 상태")
        void shouldBeDeleted_WhenDeletedAtIsSet() {
            // given
            Instant deletedAt = Instant.parse("2025-01-03T00:00:00Z");

            // when
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            ORGANIZATION_ID,
                            TENANT_ID,
                            NAME,
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT,
                            deletedAt);

            // then
            assertThat(entity.isDeleted()).isTrue();
            assertThat(entity.isActive()).isFalse();
            assertThat(entity.getDeletedAt()).isEqualTo(deletedAt);
        }
    }

    @Nested
    @DisplayName("상태 관련 메서드")
    class StatusMethods {

        @Test
        @DisplayName("ACTIVE 상태가 올바르게 설정됨")
        void shouldSetActiveStatus() {
            // when
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            ORGANIZATION_ID,
                            TENANT_ID,
                            NAME,
                            OrganizationStatus.ACTIVE,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
        }

        @Test
        @DisplayName("INACTIVE 상태가 올바르게 설정됨")
        void shouldSetInactiveStatus() {
            // when
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            ORGANIZATION_ID,
                            TENANT_ID,
                            NAME,
                            OrganizationStatus.INACTIVE,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getStatus()).isEqualTo(OrganizationStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("감사 필드 (BaseAuditEntity 상속)")
    class AuditFields {

        @Test
        @DisplayName("createdAt이 올바르게 설정됨")
        void shouldSetCreatedAt() {
            // when
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            ORGANIZATION_ID, TENANT_ID, NAME, STATUS, CREATED_AT, UPDATED_AT, null);

            // then
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
        }

        @Test
        @DisplayName("updatedAt이 올바르게 설정됨")
        void shouldSetUpdatedAt() {
            // when
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            ORGANIZATION_ID, TENANT_ID, NAME, STATUS, CREATED_AT, UPDATED_AT, null);

            // then
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
        }

        @Test
        @DisplayName("createdAt과 updatedAt이 다를 수 있음")
        void shouldAllowDifferentCreatedAndUpdatedAt() {
            // given
            Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2025-06-15T12:30:00Z");

            // when
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            ORGANIZATION_ID, TENANT_ID, NAME, STATUS, createdAt, updatedAt, null);

            // then
            assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
            assertThat(entity.getUpdatedAt()).isEqualTo(updatedAt);
            assertThat(entity.getCreatedAt()).isNotEqualTo(entity.getUpdatedAt());
        }
    }
}
