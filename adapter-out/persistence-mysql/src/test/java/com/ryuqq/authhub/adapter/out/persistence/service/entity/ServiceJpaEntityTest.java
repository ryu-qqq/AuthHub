package com.ryuqq.authhub.adapter.out.persistence.service.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.service.fixture.ServiceJpaEntityFixture;
import com.ryuqq.authhub.domain.service.vo.ServiceStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ServiceJpaEntity 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>of() 스태틱 팩토리 메서드 검증
 *   <li>Getter 메서드 반환값 검증
 *   <li>BaseAuditEntity 상속(createdAt, updatedAt) 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ServiceJpaEntity 단위 테스트")
class ServiceJpaEntityTest {

    private static final Instant CREATED = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant UPDATED = Instant.parse("2025-01-02T00:00:00Z");

    @Nested
    @DisplayName("of() 스태틱 팩토리 메서드")
    class Of {

        @Test
        @DisplayName("정상: 모든 필드가 주어지면 Entity 생성")
        void shouldCreateEntity_WhenAllFieldsProvided() {
            // when
            ServiceJpaEntity entity =
                    ServiceJpaEntity.of(
                            1L,
                            "SVC_STORE",
                            "자사몰",
                            "자사몰 서비스",
                            ServiceStatus.ACTIVE,
                            CREATED,
                            UPDATED);

            // then
            assertThat(entity.getServiceId()).isEqualTo(1L);
            assertThat(entity.getServiceCode()).isEqualTo("SVC_STORE");
            assertThat(entity.getName()).isEqualTo("자사몰");
            assertThat(entity.getDescription()).isEqualTo("자사몰 서비스");
            assertThat(entity.getStatus()).isEqualTo(ServiceStatus.ACTIVE);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED);
        }

        @Test
        @DisplayName("신규 생성: serviceId가 null이어도 생성됨")
        void shouldCreateEntity_WhenServiceIdIsNull() {
            // when
            ServiceJpaEntity entity =
                    ServiceJpaEntity.of(
                            null,
                            "SVC_NEW",
                            "New Service",
                            "Description",
                            ServiceStatus.ACTIVE,
                            CREATED,
                            UPDATED);

            // then
            assertThat(entity.getServiceId()).isNull();
            assertThat(entity.getServiceCode()).isEqualTo("SVC_NEW");
            assertThat(entity.getName()).isEqualTo("New Service");
            assertThat(entity.getDescription()).isEqualTo("Description");
        }

        @Test
        @DisplayName("description이 null이어도 생성됨")
        void shouldCreateEntity_WhenDescriptionIsNull() {
            // when
            ServiceJpaEntity entity =
                    ServiceJpaEntity.of(
                            1L,
                            "SVC_NO_DESC",
                            "Service Without Description",
                            null,
                            ServiceStatus.ACTIVE,
                            CREATED,
                            UPDATED);

            // then
            assertThat(entity.getServiceId()).isEqualTo(1L);
            assertThat(entity.getDescription()).isNull();
        }

        @Test
        @DisplayName("Fixture로 생성한 Entity는 일관된 값 반환")
        void shouldCreateConsistentEntity_FromFixture() {
            // when
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createWithId(1L);

            // then
            assertThat(entity.getServiceId()).isEqualTo(1L);
            assertThat(entity.getServiceCode())
                    .isEqualTo(ServiceJpaEntityFixture.defaultServiceCode());
            assertThat(entity.getStatus()).isEqualTo(ServiceStatus.ACTIVE);
            assertThat(entity.getCreatedAt()).isEqualTo(ServiceJpaEntityFixture.fixedTime());
            assertThat(entity.getUpdatedAt()).isEqualTo(ServiceJpaEntityFixture.fixedTime());
        }
    }

    @Nested
    @DisplayName("Getter 메서드")
    class Getters {

        @Test
        @DisplayName("getServiceId는 PK 반환")
        void getServiceId_ReturnsPrimaryKey() {
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createWithId(99L);
            assertThat(entity.getServiceId()).isEqualTo(99L);
        }

        @Test
        @DisplayName("getServiceCode는 비즈니스 식별자 반환")
        void getServiceCode_ReturnsBusinessCode() {
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createWithCode("SVC_CUSTOM");
            assertThat(entity.getServiceCode()).isEqualTo("SVC_CUSTOM");
        }

        @Test
        @DisplayName("getName은 서비스 이름 반환")
        void getName_ReturnsName() {
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createWithName("Custom Name");
            assertThat(entity.getName()).isEqualTo("Custom Name");
        }

        @Test
        @DisplayName("getDescription은 설명 반환 (null 허용)")
        void getDescription_ReturnsDescriptionOrNull() {
            assertThat(ServiceJpaEntityFixture.create().getDescription()).isNotNull();
            assertThat(ServiceJpaEntityFixture.createWithoutDescription().getDescription())
                    .isNull();
        }

        @Test
        @DisplayName("getStatus는 상태 반환")
        void getStatus_ReturnsStatus() {
            assertThat(ServiceJpaEntityFixture.create().getStatus())
                    .isEqualTo(ServiceStatus.ACTIVE);
            assertThat(ServiceJpaEntityFixture.createInactive().getStatus())
                    .isEqualTo(ServiceStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("BaseAuditEntity 상속")
    class BaseAuditEntityInheritance {

        @Test
        @DisplayName("getCreatedAt은 생성 일시 반환")
        void getCreatedAt_ReturnsCreatedInstant() {
            ServiceJpaEntity entity = ServiceJpaEntityFixture.create();
            assertThat(entity.getCreatedAt()).isEqualTo(ServiceJpaEntityFixture.fixedTime());
        }

        @Test
        @DisplayName("getUpdatedAt은 수정 일시 반환")
        void getUpdatedAt_ReturnsUpdatedInstant() {
            ServiceJpaEntity entity = ServiceJpaEntityFixture.create();
            assertThat(entity.getUpdatedAt()).isEqualTo(ServiceJpaEntityFixture.fixedTime());
        }
    }
}
