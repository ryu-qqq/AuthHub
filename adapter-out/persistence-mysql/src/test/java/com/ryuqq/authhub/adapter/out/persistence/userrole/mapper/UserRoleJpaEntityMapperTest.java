package com.ryuqq.authhub.adapter.out.persistence.userrole.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.userrole.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.userrole.fixture.UserRoleJpaEntityFixture;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import com.ryuqq.authhub.domain.userrole.fixture.UserRoleFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserRoleJpaEntityMapper 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Mapper는 순수 변환 로직 → Mock 불필요
 *   <li>Domain ↔ Entity 양방향 변환 검증
 *   <li>신규(isNew) vs 기존 구분 toEntity 검증
 *   <li>String userId (UUID) 변환 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserRoleJpaEntityMapper 단위 테스트")
class UserRoleJpaEntityMapperTest {

    private UserRoleJpaEntityMapper sut;

    @BeforeEach
    void setUp() {
        sut = new UserRoleJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드 (Domain → Entity)")
    class ToEntity {

        @Test
        @DisplayName("성공: 신규 Domain은 forNew() 사용")
        void shouldUseForNew_WhenDomainIsNew() {
            // given
            UserRole newDomain = UserRoleFixture.createNew();

            // when
            UserRoleJpaEntity entity = sut.toEntity(newDomain);

            // then
            assertThat(entity.getUserRoleId()).isNull();
            assertThat(entity.getUserId()).isEqualTo(newDomain.userIdValue());
            assertThat(entity.getRoleId()).isEqualTo(newDomain.roleIdValue());
            assertThat(entity.getCreatedAt()).isEqualTo(newDomain.createdAt());
        }

        @Test
        @DisplayName("성공: 기존 Domain은 of() 사용")
        void shouldUseOf_WhenDomainIsNotNew() {
            // given
            UserRole domain = UserRoleFixture.create();

            // when
            UserRoleJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getUserRoleId()).isEqualTo(domain.userRoleIdValue());
            assertThat(entity.getUserId()).isEqualTo(domain.userIdValue());
            assertThat(entity.getRoleId()).isEqualTo(domain.roleIdValue());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
        }

        @Test
        @DisplayName("String userId (UUID)가 올바르게 변환됨")
        void shouldConvertUserId_Correctly() {
            // given
            String userIdStr = "01941234-5678-7000-8000-123456789002";
            UserRole domain = UserRoleFixture.createWithUser(userIdStr);

            // when
            UserRoleJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getUserId()).isEqualTo(userIdStr);
        }

        @Test
        @DisplayName("모든 필드가 올바르게 매핑됨")
        void shouldMapAllFields_Correctly() {
            // given
            UserRole domain = UserRoleFixture.create();

            // when
            UserRoleJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getUserRoleId()).isEqualTo(domain.userRoleIdValue());
            assertThat(entity.getUserId()).isEqualTo(domain.userIdValue());
            assertThat(entity.getRoleId()).isEqualTo(domain.roleIdValue());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
        }
    }

    @Nested
    @DisplayName("toDomain 메서드 (Entity → Domain)")
    class ToDomain {

        @Test
        @DisplayName("성공: Entity의 모든 필드가 Domain으로 올바르게 매핑됨")
        void shouldMapAllFields_FromEntityToDomain() {
            // given
            UserRoleJpaEntity entity = UserRoleJpaEntityFixture.create();

            // when
            UserRole domain = sut.toDomain(entity);

            // then
            assertThat(domain.userRoleIdValue()).isEqualTo(entity.getUserRoleId());
            assertThat(domain.userIdValue()).isEqualTo(entity.getUserId());
            assertThat(domain.roleIdValue()).isEqualTo(entity.getRoleId());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
        }

        @Test
        @DisplayName("String userId (UUID)가 올바르게 변환됨")
        void shouldConvertUserId_Correctly() {
            // given
            String userIdStr = "01941234-5678-7000-8000-123456789002";
            UserRoleJpaEntity entity = UserRoleJpaEntityFixture.createWith(userIdStr, 2L);

            // when
            UserRole domain = sut.toDomain(entity);

            // then
            assertThat(domain.userIdValue()).isEqualTo(userIdStr);
        }

        @Test
        @DisplayName("재구성된 Domain은 isNew()가 false")
        void shouldSetIsNewToFalse_WhenReconstituted() {
            // given
            UserRoleJpaEntity entity = UserRoleJpaEntityFixture.create();

            // when
            UserRole domain = sut.toDomain(entity);

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
            UserRole original = UserRoleFixture.create();

            // when
            UserRoleJpaEntity entity = sut.toEntity(original);
            UserRole reconstituted = sut.toDomain(entity);

            // then
            assertThat(reconstituted.userRoleIdValue()).isEqualTo(original.userRoleIdValue());
            assertThat(reconstituted.userIdValue()).isEqualTo(original.userIdValue());
            assertThat(reconstituted.roleIdValue()).isEqualTo(original.roleIdValue());
            assertThat(reconstituted.createdAt()).isEqualTo(original.createdAt());
        }
    }
}
