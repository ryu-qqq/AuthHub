package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserRoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserRoleQueryDslRepository;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleQueryAdapter 단위 테스트")
class UserRoleQueryAdapterTest {

    @Mock private UserRoleQueryDslRepository queryDslRepository;

    @Mock private UserRoleJpaEntityMapper mapper;

    private UserRoleQueryAdapter adapter;

    private static final UUID USER_UUID = UserFixture.defaultUUID();
    private static final UUID ROLE_UUID = UUID.randomUUID();
    private static final UUID ROLE_UUID_2 = UUID.randomUUID();
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        adapter = new UserRoleQueryAdapter(queryDslRepository, mapper);
    }

    @Nested
    @DisplayName("findByUserIdAndRoleId 메서드")
    class FindByUserIdAndRoleIdTest {

        @Test
        @DisplayName("UserId와 RoleId로 UserRole을 조회한다")
        void shouldFindUserRoleByUserIdAndRoleId() {
            // given
            UserId userId = UserId.of(USER_UUID);
            RoleId roleId = RoleId.of(ROLE_UUID);
            UserRole expectedUserRole = UserRole.reconstitute(userId, roleId, FIXED_INSTANT);
            UserRoleJpaEntity entity =
                    UserRoleJpaEntity.of(UUID.randomUUID(), USER_UUID, ROLE_UUID, FIXED_INSTANT);

            given(queryDslRepository.findByUserIdAndRoleId(USER_UUID, ROLE_UUID))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedUserRole);

            // when
            Optional<UserRole> result = adapter.findByUserIdAndRoleId(userId, roleId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedUserRole);
            verify(queryDslRepository).findByUserIdAndRoleId(USER_UUID, ROLE_UUID);
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenUserRoleNotFound() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            RoleId roleId = RoleId.of(UUID.randomUUID());

            given(queryDslRepository.findByUserIdAndRoleId(userId.value(), roleId.value()))
                    .willReturn(Optional.empty());

            // when
            Optional<UserRole> result = adapter.findByUserIdAndRoleId(userId, roleId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByUserId 메서드")
    class FindAllByUserIdTest {

        @Test
        @DisplayName("UserId로 모든 UserRole을 조회한다")
        void shouldFindAllUserRolesByUserId() {
            // given
            UserId userId = UserId.of(USER_UUID);
            UserRole userRole1 = UserRole.reconstitute(userId, RoleId.of(ROLE_UUID), FIXED_INSTANT);
            UserRole userRole2 =
                    UserRole.reconstitute(userId, RoleId.of(ROLE_UUID_2), FIXED_INSTANT);
            UserRoleJpaEntity entity1 =
                    UserRoleJpaEntity.of(UUID.randomUUID(), USER_UUID, ROLE_UUID, FIXED_INSTANT);
            UserRoleJpaEntity entity2 =
                    UserRoleJpaEntity.of(UUID.randomUUID(), USER_UUID, ROLE_UUID_2, FIXED_INSTANT);

            given(queryDslRepository.findAllByUserId(USER_UUID))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(any(UserRoleJpaEntity.class))).willReturn(userRole1, userRole2);

            // when
            List<UserRole> results = adapter.findAllByUserId(userId);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("역할이 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoRolesFound() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());

            given(queryDslRepository.findAllByUserId(userId.value())).willReturn(List.of());

            // when
            List<UserRole> results = adapter.findAllByUserId(userId);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByUserIdAndRoleId 메서드")
    class ExistsByUserIdAndRoleIdTest {

        @Test
        @DisplayName("UserRole이 존재하면 true를 반환한다")
        void shouldReturnTrueWhenUserRoleExists() {
            // given
            UserId userId = UserId.of(USER_UUID);
            RoleId roleId = RoleId.of(ROLE_UUID);

            given(queryDslRepository.existsByUserIdAndRoleId(USER_UUID, ROLE_UUID))
                    .willReturn(true);

            // when
            boolean result = adapter.existsByUserIdAndRoleId(userId, roleId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("UserRole이 존재하지 않으면 false를 반환한다")
        void shouldReturnFalseWhenUserRoleNotExists() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            RoleId roleId = RoleId.of(UUID.randomUUID());

            given(queryDslRepository.existsByUserIdAndRoleId(userId.value(), roleId.value()))
                    .willReturn(false);

            // when
            boolean result = adapter.existsByUserIdAndRoleId(userId, roleId);

            // then
            assertThat(result).isFalse();
        }
    }
}
