package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.PermissionEndpointJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.fixture.PermissionEndpointJpaEntityFixture;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.fixture.PermissionEndpointFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionEndpointJpaEntityMapper 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Mapper는 순수 변환 로직 → Mock 불필요
 *   <li>Domain ↔ Entity 양방향 변환 검증
 *   <li>DeletionStatus 변환 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionEndpointJpaEntityMapper 단위 테스트")
class PermissionEndpointJpaEntityMapperTest {

    private PermissionEndpointJpaEntityMapper sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionEndpointJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드 (Domain → Entity)")
    class ToEntity {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Entity로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToEntity() {
            // given
            PermissionEndpoint domain = PermissionEndpointFixture.create();

            // when
            PermissionEndpointJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getPermissionEndpointId())
                    .isEqualTo(domain.permissionEndpointIdValue());
            assertThat(entity.getPermissionId()).isEqualTo(domain.permissionIdValue());
            assertThat(entity.getServiceName()).isEqualTo(domain.serviceNameValue());
            assertThat(entity.getUrlPattern()).isEqualTo(domain.urlPatternValue());
            assertThat(entity.getHttpMethod()).isEqualTo(domain.getHttpMethod());
            assertThat(entity.getDescription()).isEqualTo(domain.getDescription());
            assertThat(entity.isPublic()).isEqualTo(domain.isPublicEndpoint());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(domain.updatedAt());
        }

        @Test
        @DisplayName("활성 Domain은 deletedAt이 null로 매핑됨")
        void shouldMapDeletedAtToNull_WhenDomainIsActive() {
            // given
            PermissionEndpoint activeDomain = PermissionEndpointFixture.create();

            // when
            PermissionEndpointJpaEntity entity = sut.toEntity(activeDomain);

            // then
            assertThat(entity.getDeletedAt()).isNull();
            assertThat(entity.isDeleted()).isFalse();
            assertThat(entity.isActive()).isTrue();
        }

        @Test
        @DisplayName("삭제된 Domain은 deletedAt이 설정됨")
        void shouldMapDeletedAt_WhenDomainIsDeleted() {
            // given
            PermissionEndpoint deletedDomain = PermissionEndpointFixture.createDeleted();

            // when
            PermissionEndpointJpaEntity entity = sut.toEntity(deletedDomain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
            assertThat(entity.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("toDomain 메서드 (Entity → Domain)")
    class ToDomain {

        @Test
        @DisplayName("성공: Entity의 모든 필드가 Domain으로 올바르게 매핑됨")
        void shouldMapAllFields_FromEntityToDomain() {
            // given
            PermissionEndpointJpaEntity entity = PermissionEndpointJpaEntityFixture.create();

            // when
            PermissionEndpoint domain = sut.toDomain(entity);

            // then
            assertThat(domain.permissionEndpointIdValue())
                    .isEqualTo(entity.getPermissionEndpointId());
            assertThat(domain.permissionIdValue()).isEqualTo(entity.getPermissionId());
            assertThat(domain.serviceNameValue()).isEqualTo(entity.getServiceName());
            assertThat(domain.urlPatternValue()).isEqualTo(entity.getUrlPattern());
            assertThat(domain.getHttpMethod()).isEqualTo(entity.getHttpMethod());
            assertThat(domain.getDescription()).isEqualTo(entity.getDescription());
            assertThat(domain.isPublicEndpoint()).isEqualTo(entity.isPublic());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.updatedAt()).isEqualTo(entity.getUpdatedAt());
        }

        @Test
        @DisplayName("삭제된 Entity는 DeletionStatus.deleted로 변환됨")
        void shouldMapToDeletedStatus_WhenEntityIsDeleted() {
            // given
            PermissionEndpointJpaEntity deletedEntity =
                    PermissionEndpointJpaEntityFixture.createDeleted();

            // when
            PermissionEndpoint domain = sut.toDomain(deletedEntity);

            // then
            assertThat(domain.getDeletionStatus().isDeleted()).isTrue();
            assertThat(domain.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("양방향 변환 (Round-trip)")
    class RoundTrip {

        @Test
        @DisplayName("Entity → Domain → Entity 시 데이터 보존")
        void shouldPreserveData_WhenEntityToDomainToEntity() {
            // given
            PermissionEndpointJpaEntity original = PermissionEndpointJpaEntityFixture.create();

            // when
            PermissionEndpoint domain = sut.toDomain(original);
            PermissionEndpointJpaEntity roundTripped = sut.toEntity(domain);

            // then
            assertThat(roundTripped.getPermissionEndpointId())
                    .isEqualTo(original.getPermissionEndpointId());
            assertThat(roundTripped.getPermissionId()).isEqualTo(original.getPermissionId());
            assertThat(roundTripped.getServiceName()).isEqualTo(original.getServiceName());
            assertThat(roundTripped.getUrlPattern()).isEqualTo(original.getUrlPattern());
            assertThat(roundTripped.getHttpMethod()).isEqualTo(original.getHttpMethod());
            assertThat(roundTripped.getDescription()).isEqualTo(original.getDescription());
            assertThat(roundTripped.isPublic()).isEqualTo(original.isPublic());
        }

        @Test
        @DisplayName("삭제된 Entity Round-trip 시 삭제 상태 보존")
        void shouldPreserveDeletedStatus_WhenRoundTrip() {
            // given
            PermissionEndpointJpaEntity deleted =
                    PermissionEndpointJpaEntityFixture.createDeleted();

            // when
            PermissionEndpoint domain = sut.toDomain(deleted);
            PermissionEndpointJpaEntity roundTripped = sut.toEntity(domain);

            // then
            assertThat(roundTripped.getDeletedAt()).isNotNull();
            assertThat(roundTripped.isDeleted()).isTrue();
        }
    }
}
