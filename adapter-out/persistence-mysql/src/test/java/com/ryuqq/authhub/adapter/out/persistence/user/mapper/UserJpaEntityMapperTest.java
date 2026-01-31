package com.ryuqq.authhub.adapter.out.persistence.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.fixture.UserJpaEntityFixture;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserJpaEntityMapper 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Mapper는 순수 변환 로직 → Mock 불필요
 *   <li>Domain ↔ Entity 양방향 변환 검증
 *   <li>모든 필드가 올바르게 매핑되는지 검증
 *   <li>Edge case (null phoneNumber, deletedAt, 다양한 상태) 처리 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserJpaEntityMapper 단위 테스트")
class UserJpaEntityMapperTest {

    private UserJpaEntityMapper sut;

    @BeforeEach
    void setUp() {
        sut = new UserJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드 (Domain → Entity)")
    class ToEntity {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Entity로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToEntity() {
            // given
            User domain = UserFixture.create();

            // when
            UserJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getUserId()).isEqualTo(domain.userIdValue());
            assertThat(entity.getOrganizationId()).isEqualTo(domain.organizationIdValue());
            assertThat(entity.getIdentifier()).isEqualTo(domain.identifierValue());
            assertThat(entity.getPhoneNumber()).isEqualTo(domain.phoneNumberValue());
            assertThat(entity.getHashedPassword()).isEqualTo(domain.hashedPasswordValue());
            assertThat(entity.getStatus()).isEqualTo(domain.getStatus());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(domain.updatedAt());
        }

        @Test
        @DisplayName("활성 Domain은 deletedAt이 null로 매핑됨")
        void shouldMapDeletedAtToNull_WhenDomainIsActive() {
            // given
            User activeDomain = UserFixture.create();

            // when
            UserJpaEntity entity = sut.toEntity(activeDomain);

            // then
            assertThat(entity.getDeletedAt()).isNull();
            assertThat(entity.isDeleted()).isFalse();
            assertThat(entity.isActive()).isTrue();
        }

        @Test
        @DisplayName("삭제된 Domain은 deletedAt이 설정됨")
        void shouldMapDeletedAt_WhenDomainIsDeleted() {
            // given
            User deletedDomain = UserFixture.createDeleted();

            // when
            UserJpaEntity entity = sut.toEntity(deletedDomain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
            assertThat(entity.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("전화번호 없는 Domain이 올바르게 매핑됨")
        void shouldMapNullPhoneNumber_WhenDomainHasNoPhone() {
            // given
            User domainWithoutPhone = UserFixture.createWithoutPhone();

            // when
            UserJpaEntity entity = sut.toEntity(domainWithoutPhone);

            // then
            assertThat(entity.getPhoneNumber()).isNull();
        }

        @Test
        @DisplayName("비활성 Domain이 올바르게 매핑됨")
        void shouldMapInactiveStatus_Correctly() {
            // given
            User inactiveDomain = UserFixture.createInactive();

            // when
            UserJpaEntity entity = sut.toEntity(inactiveDomain);

            // then
            assertThat(entity.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("toDomain 메서드 (Entity → Domain)")
    class ToDomain {

        @Test
        @DisplayName("성공: Entity의 모든 필드가 Domain으로 올바르게 매핑됨")
        void shouldMapAllFields_FromEntityToDomain() {
            // given
            UserJpaEntity entity = UserJpaEntityFixture.create();

            // when
            User domain = sut.toDomain(entity);

            // then
            assertThat(domain.userIdValue()).isEqualTo(entity.getUserId());
            assertThat(domain.organizationIdValue()).isEqualTo(entity.getOrganizationId());
            assertThat(domain.identifierValue()).isEqualTo(entity.getIdentifier());
            assertThat(domain.phoneNumberValue()).isEqualTo(entity.getPhoneNumber());
            assertThat(domain.hashedPasswordValue()).isEqualTo(entity.getHashedPassword());
            assertThat(domain.getStatus()).isEqualTo(entity.getStatus());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.updatedAt()).isEqualTo(entity.getUpdatedAt());
        }

        @Test
        @DisplayName("활성 Entity는 DeletionStatus가 active로 매핑됨")
        void shouldMapToDeletionStatusActive_WhenEntityIsActive() {
            // given
            UserJpaEntity activeEntity = UserJpaEntityFixture.create();

            // when
            User domain = sut.toDomain(activeEntity);

            // then
            assertThat(domain.isDeleted()).isFalse();
            assertThat(domain.getDeletionStatus().isDeleted()).isFalse();
        }

        @Test
        @DisplayName("삭제된 Entity는 DeletionStatus가 deleted로 매핑됨")
        void shouldMapToDeletionStatusDeleted_WhenEntityIsDeleted() {
            // given
            UserJpaEntity deletedEntity = UserJpaEntityFixture.createDeleted();

            // when
            User domain = sut.toDomain(deletedEntity);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.getDeletionStatus().isDeleted()).isTrue();
            assertThat(domain.getDeletionStatus().deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("전화번호 없는 Entity가 올바르게 매핑됨")
        void shouldMapNullPhoneNumber_WhenEntityHasNoPhone() {
            // given
            UserJpaEntity entityWithoutPhone = UserJpaEntityFixture.createWithoutPhone();

            // when
            User domain = sut.toDomain(entityWithoutPhone);

            // then
            assertThat(domain.phoneNumberValue()).isNull();
        }

        @Test
        @DisplayName("비활성 Entity가 올바르게 매핑됨")
        void shouldMapInactiveStatus_Correctly() {
            // given
            UserJpaEntity inactiveEntity = UserJpaEntityFixture.createInactive();

            // when
            User domain = sut.toDomain(inactiveEntity);

            // then
            assertThat(domain.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }

        @Test
        @DisplayName("재구성된 Domain은 isNew()가 false")
        void shouldSetIsNewToFalse_WhenReconstituted() {
            // given
            UserJpaEntity entity = UserJpaEntityFixture.create();

            // when
            User domain = sut.toDomain(entity);

            // then
            assertThat(domain.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("양방향 변환 테스트")
    class RoundTrip {

        @Test
        @DisplayName("Domain → Entity → Domain 변환 시 데이터 보존")
        void shouldPreserveData_OnRoundTrip() {
            // given
            User originalDomain = UserFixture.create();

            // when
            UserJpaEntity entity = sut.toEntity(originalDomain);
            User reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.userIdValue()).isEqualTo(originalDomain.userIdValue());
            assertThat(reconstitutedDomain.organizationIdValue())
                    .isEqualTo(originalDomain.organizationIdValue());
            assertThat(reconstitutedDomain.identifierValue())
                    .isEqualTo(originalDomain.identifierValue());
            assertThat(reconstitutedDomain.phoneNumberValue())
                    .isEqualTo(originalDomain.phoneNumberValue());
            assertThat(reconstitutedDomain.hashedPasswordValue())
                    .isEqualTo(originalDomain.hashedPasswordValue());
            assertThat(reconstitutedDomain.getStatus()).isEqualTo(originalDomain.getStatus());
            assertThat(reconstitutedDomain.createdAt()).isEqualTo(originalDomain.createdAt());
            assertThat(reconstitutedDomain.updatedAt()).isEqualTo(originalDomain.updatedAt());
        }

        @Test
        @DisplayName("삭제된 Domain도 양방향 변환 시 데이터 보존")
        void shouldPreserveDeletedData_OnRoundTrip() {
            // given
            User deletedDomain = UserFixture.createDeleted();

            // when
            UserJpaEntity entity = sut.toEntity(deletedDomain);
            User reconstitutedDomain = sut.toDomain(entity);

            // then
            assertThat(reconstitutedDomain.isDeleted()).isEqualTo(deletedDomain.isDeleted());
            assertThat(reconstitutedDomain.getDeletionStatus().deletedAt())
                    .isEqualTo(deletedDomain.getDeletionStatus().deletedAt());
        }
    }
}
