package com.ryuqq.authhub.adapter.out.persistence.userrole.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.userrole.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.userrole.fixture.UserRoleJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.userrole.mapper.UserRoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.userrole.repository.UserRoleQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import com.ryuqq.authhub.domain.userrole.fixture.UserRoleFixture;
import com.ryuqq.authhub.domain.userrole.query.criteria.UserRoleSearchCriteria;
import com.ryuqq.authhub.domain.userrole.vo.UserRoleSortKey;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserRoleQueryAdapter 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Adapter는 Repository 위임 + Mapper 변환 담당
 *   <li>QueryDslRepository/Mapper를 Mock으로 대체
 *   <li>Entity → Domain 변환 흐름 검증
 *   <li>String userId (UUID) 변환 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleQueryAdapter 단위 테스트")
class UserRoleQueryAdapterTest {

    private static final String USER_ID = UserRoleFixture.defaultUserIdString();
    private static final Long ROLE_ID = UserRoleFixture.defaultRoleIdValue();

    @Mock private UserRoleQueryDslRepository repository;

    @Mock private UserRoleJpaEntityMapper mapper;

    private UserRoleQueryAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new UserRoleQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("exists 메서드")
    class Exists {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();
            RoleId roleId = UserRoleFixture.defaultRoleId();
            given(repository.exists(USER_ID, ROLE_ID)).willReturn(true);

            // when
            boolean result = sut.exists(userId, roleId);

            // then
            assertThat(result).isTrue();
            then(repository).should().exists(USER_ID, ROLE_ID);
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();
            RoleId roleId = UserRoleFixture.defaultRoleId();
            given(repository.exists(USER_ID, ROLE_ID)).willReturn(false);

            // when
            boolean result = sut.exists(userId, roleId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByUserIdAndRoleId 메서드")
    class FindByUserIdAndRoleId {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();
            RoleId roleId = UserRoleFixture.defaultRoleId();
            UserRoleJpaEntity entity = UserRoleJpaEntityFixture.create();
            UserRole expectedDomain = UserRoleFixture.create();

            given(repository.findByUserIdAndRoleId(USER_ID, ROLE_ID))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<UserRole> result = sut.findByUserIdAndRoleId(userId, roleId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();
            RoleId roleId = UserRoleFixture.defaultRoleId();
            given(repository.findByUserIdAndRoleId(USER_ID, ROLE_ID)).willReturn(Optional.empty());

            // when
            Optional<UserRole> result = sut.findByUserIdAndRoleId(userId, roleId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByUserId 메서드")
    class FindAllByUserId {

        @Test
        @DisplayName("성공: Entity 목록을 Domain 목록으로 변환하여 반환")
        void shouldReturnDomainList() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();
            UserRoleJpaEntity entity = UserRoleJpaEntityFixture.create();
            UserRole domain = UserRoleFixture.create();
            given(repository.findAllByUserId(USER_ID)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<UserRole> result = sut.findAllByUserId(userId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }
    }

    @Nested
    @DisplayName("findAssignedRoleIds 메서드")
    class FindAssignedRoleIds {

        @Test
        @DisplayName("성공: 이미 할당된 역할 ID 목록 반환")
        void shouldReturnAssignedRoleIds() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();
            List<RoleId> roleIds = List.of(RoleId.of(1L), RoleId.of(2L));
            given(repository.findAssignedRoleIds(USER_ID, List.of(1L, 2L))).willReturn(List.of(1L));

            // when
            List<RoleId> result = sut.findAssignedRoleIds(userId, roleIds);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("빈 roleIds 목록이면 빈 목록 반환")
        void shouldReturnEmptyList_WhenRoleIdsEmpty() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();

            // when
            List<RoleId> result = sut.findAssignedRoleIds(userId, List.of());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllBySearchCriteria 메서드")
    class FindAllBySearchCriteria {

        @Test
        @DisplayName("성공: 조건에 맞는 Domain 목록 반환")
        void shouldReturnDomainList() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));
            UserRoleJpaEntity entity = UserRoleJpaEntityFixture.create();
            UserRole domain = UserRoleFixture.create();
            given(repository.findAllByCriteria(criteria)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<UserRole> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }
    }

    @Nested
    @DisplayName("countBySearchCriteria 메서드")
    class CountBySearchCriteria {

        @Test
        @DisplayName("성공: 조건에 맞는 개수 반환")
        void shouldReturnCount() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));
            given(repository.countByCriteria(criteria)).willReturn(3L);

            // when
            long result = sut.countBySearchCriteria(criteria);

            // then
            assertThat(result).isEqualTo(3L);
        }
    }

    @Nested
    @DisplayName("existsByRoleId 메서드")
    class ExistsByRoleId {

        @Test
        @DisplayName("역할이 사용 중이면 true 반환")
        void shouldReturnTrue_WhenRoleInUse() {
            // given
            RoleId roleId = UserRoleFixture.defaultRoleId();
            given(repository.existsByRoleId(ROLE_ID)).willReturn(true);

            // when
            boolean result = sut.existsByRoleId(roleId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("역할이 사용 중이 아니면 false 반환")
        void shouldReturnFalse_WhenRoleNotInUse() {
            // given
            RoleId roleId = UserRoleFixture.defaultRoleId();
            given(repository.existsByRoleId(ROLE_ID)).willReturn(false);

            // when
            boolean result = sut.existsByRoleId(roleId);

            // then
            assertThat(result).isFalse();
        }
    }
}
