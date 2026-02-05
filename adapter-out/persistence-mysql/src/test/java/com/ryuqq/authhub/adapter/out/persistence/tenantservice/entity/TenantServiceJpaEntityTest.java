package com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantServiceJpaEntity 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Entity는 데이터 컨테이너 → of() 팩토리, getter, 감사 필드 검증
 *   <li>Mock 불필요 (순수 객체 생성/조회)
 *   <li>BaseAuditEntity 상속 기능 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantServiceJpaEntity 단위 테스트")
class TenantServiceJpaEntityTest {

    private static final Long ID = 1L;
    private static final String TENANT_ID = "01941234-5678-7000-8000-123456789abc";
    private static final Long SERVICE_ID = 1L;
    private static final TenantServiceStatus STATUS = TenantServiceStatus.ACTIVE;
    private static final Instant SUBSCRIBED_AT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T00:00:00Z");

    @Nested
    @DisplayName("of() 팩토리 메서드")
    class OfFactoryMethod {

        @Test
        @DisplayName("성공: 모든 필드가 올바르게 설정됨")
        void shouldSetAllFields_Correctly() {
            // when
            TenantServiceJpaEntity entity =
                    TenantServiceJpaEntity.of(
                            ID,
                            TENANT_ID,
                            SERVICE_ID,
                            STATUS,
                            SUBSCRIBED_AT,
                            CREATED_AT,
                            UPDATED_AT);

            // then
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getTenantId()).isEqualTo(TENANT_ID);
            assertThat(entity.getServiceId()).isEqualTo(SERVICE_ID);
            assertThat(entity.getStatus()).isEqualTo(STATUS);
            assertThat(entity.getSubscribedAt()).isEqualTo(SUBSCRIBED_AT);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
        }

        @Test
        @DisplayName("id가 null이면 신규 엔티티 (저장 전)")
        void shouldAllowNullId_ForNewEntity() {
            // when
            TenantServiceJpaEntity entity =
                    TenantServiceJpaEntity.of(
                            null,
                            TENANT_ID,
                            SERVICE_ID,
                            STATUS,
                            SUBSCRIBED_AT,
                            CREATED_AT,
                            UPDATED_AT);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getTenantId()).isEqualTo(TENANT_ID);
            assertThat(entity.getServiceId()).isEqualTo(SERVICE_ID);
        }
    }

    @Nested
    @DisplayName("Getter 검증")
    class Getters {

        @Test
        @DisplayName("모든 getter가 설정된 값을 반환함")
        void shouldReturnAllFieldValues() {
            // when
            TenantServiceJpaEntity entity =
                    TenantServiceJpaEntity.of(
                            ID,
                            TENANT_ID,
                            SERVICE_ID,
                            STATUS,
                            SUBSCRIBED_AT,
                            CREATED_AT,
                            UPDATED_AT);

            // then
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getTenantId()).isEqualTo(TENANT_ID);
            assertThat(entity.getServiceId()).isEqualTo(SERVICE_ID);
            assertThat(entity.getStatus()).isEqualTo(STATUS);
            assertThat(entity.getSubscribedAt()).isEqualTo(SUBSCRIBED_AT);
        }
    }

    @Nested
    @DisplayName("감사 필드 (BaseAuditEntity 상속)")
    class AuditFields {

        @Test
        @DisplayName("createdAt이 올바르게 설정됨")
        void shouldSetCreatedAt() {
            // when
            TenantServiceJpaEntity entity =
                    TenantServiceJpaEntity.of(
                            ID,
                            TENANT_ID,
                            SERVICE_ID,
                            STATUS,
                            SUBSCRIBED_AT,
                            CREATED_AT,
                            UPDATED_AT);

            // then
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
        }

        @Test
        @DisplayName("updatedAt이 올바르게 설정됨")
        void shouldSetUpdatedAt() {
            // when
            TenantServiceJpaEntity entity =
                    TenantServiceJpaEntity.of(
                            ID,
                            TENANT_ID,
                            SERVICE_ID,
                            STATUS,
                            SUBSCRIBED_AT,
                            CREATED_AT,
                            UPDATED_AT);

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
            TenantServiceJpaEntity entity =
                    TenantServiceJpaEntity.of(
                            ID, TENANT_ID, SERVICE_ID, STATUS, SUBSCRIBED_AT, createdAt, updatedAt);

            // then
            assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
            assertThat(entity.getUpdatedAt()).isEqualTo(updatedAt);
            assertThat(entity.getCreatedAt()).isNotEqualTo(entity.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("상태별 엔티티")
    class StatusVariants {

        @Test
        @DisplayName("ACTIVE 상태가 올바르게 설정됨")
        void shouldSetActiveStatus() {
            // when
            TenantServiceJpaEntity entity =
                    TenantServiceJpaEntity.of(
                            ID,
                            TENANT_ID,
                            SERVICE_ID,
                            TenantServiceStatus.ACTIVE,
                            SUBSCRIBED_AT,
                            CREATED_AT,
                            UPDATED_AT);

            // then
            assertThat(entity.getStatus()).isEqualTo(TenantServiceStatus.ACTIVE);
        }

        @Test
        @DisplayName("INACTIVE 상태가 올바르게 설정됨")
        void shouldSetInactiveStatus() {
            // when
            TenantServiceJpaEntity entity =
                    TenantServiceJpaEntity.of(
                            ID,
                            TENANT_ID,
                            SERVICE_ID,
                            TenantServiceStatus.INACTIVE,
                            SUBSCRIBED_AT,
                            CREATED_AT,
                            UPDATED_AT);

            // then
            assertThat(entity.getStatus()).isEqualTo(TenantServiceStatus.INACTIVE);
        }

        @Test
        @DisplayName("SUSPENDED 상태가 올바르게 설정됨")
        void shouldSetSuspendedStatus() {
            // when
            TenantServiceJpaEntity entity =
                    TenantServiceJpaEntity.of(
                            ID,
                            TENANT_ID,
                            SERVICE_ID,
                            TenantServiceStatus.SUSPENDED,
                            SUBSCRIBED_AT,
                            CREATED_AT,
                            UPDATED_AT);

            // then
            assertThat(entity.getStatus()).isEqualTo(TenantServiceStatus.SUSPENDED);
        }
    }
}
