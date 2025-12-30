package com.ryuqq.authhub.adapter.out.persistence.tenant.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantJpaEntity 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantJpaEntity 단위 테스트")
class TenantJpaEntityTest {

    private static final UUID TENANT_UUID = UUID.fromString("01941234-5678-7000-8000-123456789abc");
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @Nested
    @DisplayName("of 메서드")
    class OfTest {

        @Test
        @DisplayName("모든 필드로 Entity를 생성한다")
        void shouldCreateEntityWithAllFields() {
            // given
            String name = "Test Tenant";
            TenantStatus status = TenantStatus.ACTIVE;

            // when
            TenantJpaEntity entity =
                    TenantJpaEntity.of(TENANT_UUID, name, status, FIXED_TIME, FIXED_TIME);

            // then
            assertThat(entity.getTenantId()).isEqualTo(TENANT_UUID);
            assertThat(entity.getName()).isEqualTo(name);
            assertThat(entity.getStatus()).isEqualTo(status);
            assertThat(entity.getCreatedAt()).isEqualTo(FIXED_TIME);
            assertThat(entity.getUpdatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("신규 Entity도 UUID를 가진다")
        void shouldCreateNewEntityWithUuid() {
            // given
            String name = "New Tenant";
            TenantStatus status = TenantStatus.ACTIVE;

            // when
            TenantJpaEntity entity =
                    TenantJpaEntity.of(TENANT_UUID, name, status, FIXED_TIME, FIXED_TIME);

            // then
            assertThat(entity.getTenantId()).isEqualTo(TENANT_UUID);
            assertThat(entity.getName()).isEqualTo(name);
        }

        @Test
        @DisplayName("다양한 상태로 Entity를 생성할 수 있다")
        void shouldCreateEntityWithDifferentStatus() {
            // given & when
            TenantJpaEntity activeEntity =
                    TenantJpaEntity.of(
                            TENANT_UUID, "Active", TenantStatus.ACTIVE, FIXED_TIME, FIXED_TIME);
            TenantJpaEntity inactiveEntity =
                    TenantJpaEntity.of(
                            UUID.fromString("01941234-5678-7000-8000-123456789abd"),
                            "Inactive",
                            TenantStatus.INACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);
            TenantJpaEntity deletedEntity =
                    TenantJpaEntity.of(
                            UUID.fromString("01941234-5678-7000-8000-123456789abe"),
                            "Deleted",
                            TenantStatus.DELETED,
                            FIXED_TIME,
                            FIXED_TIME);

            // then
            assertThat(activeEntity.getStatus()).isEqualTo(TenantStatus.ACTIVE);
            assertThat(inactiveEntity.getStatus()).isEqualTo(TenantStatus.INACTIVE);
            assertThat(deletedEntity.getStatus()).isEqualTo(TenantStatus.DELETED);
        }
    }

    @Nested
    @DisplayName("Getters")
    class GettersTest {

        @Test
        @DisplayName("모든 getter가 올바른 값을 반환한다")
        void shouldReturnCorrectValuesFromGetters() {
            // given
            String name = "Test Tenant";
            TenantStatus status = TenantStatus.ACTIVE;
            LocalDateTime createdAt = FIXED_TIME;
            LocalDateTime updatedAt = FIXED_TIME.plusHours(1);

            TenantJpaEntity entity =
                    TenantJpaEntity.of(TENANT_UUID, name, status, createdAt, updatedAt);

            // when & then
            assertThat(entity.getTenantId()).isEqualTo(TENANT_UUID);
            assertThat(entity.getName()).isEqualTo(name);
            assertThat(entity.getStatus()).isEqualTo(status);
            assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
            assertThat(entity.getUpdatedAt()).isEqualTo(updatedAt);
        }
    }
}
