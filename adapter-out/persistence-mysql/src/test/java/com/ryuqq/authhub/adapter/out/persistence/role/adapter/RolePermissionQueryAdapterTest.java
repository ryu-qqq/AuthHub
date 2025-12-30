package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.mapper.RolePermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RolePermissionQueryDslRepository;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
 * RolePermissionQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RolePermissionQueryAdapter 단위 테스트")
class RolePermissionQueryAdapterTest {

    @Mock private RolePermissionQueryDslRepository queryDslRepository;

    @Mock private RolePermissionJpaEntityMapper mapper;

    private RolePermissionQueryAdapter adapter;

    private static final UUID ROLE_UUID = RoleFixture.defaultUUID();
    private static final UUID ROLE_UUID_2 = UUID.randomUUID();
    private static final UUID PERMISSION_UUID = UUID.randomUUID();
    private static final UUID PERMISSION_UUID_2 = UUID.randomUUID();
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        adapter = new RolePermissionQueryAdapter(queryDslRepository, mapper);
    }

    @Nested
    @DisplayName("findByRoleIdAndPermissionId 메서드")
    class FindByRoleIdAndPermissionIdTest {

        @Test
        @DisplayName("RoleId와 PermissionId로 RolePermission을 조회한다")
        void shouldFindRolePermissionByRoleIdAndPermissionId() {
            // given
            RoleId roleId = RoleId.of(ROLE_UUID);
            PermissionId permissionId = PermissionId.of(PERMISSION_UUID);
            RolePermission expectedRolePermission =
                    RolePermission.reconstitute(roleId, permissionId, FIXED_INSTANT);
            RolePermissionJpaEntity entity =
                    RolePermissionJpaEntity.of(
                            UUID.randomUUID(), ROLE_UUID, PERMISSION_UUID, FIXED_INSTANT);

            given(queryDslRepository.findByRoleIdAndPermissionId(ROLE_UUID, PERMISSION_UUID))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedRolePermission);

            // when
            Optional<RolePermission> result =
                    adapter.findByRoleIdAndPermissionId(roleId, permissionId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedRolePermission);
            verify(queryDslRepository).findByRoleIdAndPermissionId(ROLE_UUID, PERMISSION_UUID);
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenRolePermissionNotFound() {
            // given
            RoleId roleId = RoleId.of(UUID.randomUUID());
            PermissionId permissionId = PermissionId.of(UUID.randomUUID());

            given(
                            queryDslRepository.findByRoleIdAndPermissionId(
                                    roleId.value(), permissionId.value()))
                    .willReturn(Optional.empty());

            // when
            Optional<RolePermission> result =
                    adapter.findByRoleIdAndPermissionId(roleId, permissionId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByRoleId 메서드")
    class FindAllByRoleIdTest {

        @Test
        @DisplayName("RoleId로 모든 RolePermission을 조회한다")
        void shouldFindAllRolePermissionsByRoleId() {
            // given
            RoleId roleId = RoleId.of(ROLE_UUID);
            RolePermission rolePermission1 =
                    RolePermission.reconstitute(
                            roleId, PermissionId.of(PERMISSION_UUID), FIXED_INSTANT);
            RolePermission rolePermission2 =
                    RolePermission.reconstitute(
                            roleId, PermissionId.of(PERMISSION_UUID_2), FIXED_INSTANT);
            RolePermissionJpaEntity entity1 =
                    RolePermissionJpaEntity.of(
                            UUID.randomUUID(), ROLE_UUID, PERMISSION_UUID, FIXED_INSTANT);
            RolePermissionJpaEntity entity2 =
                    RolePermissionJpaEntity.of(
                            UUID.randomUUID(), ROLE_UUID, PERMISSION_UUID_2, FIXED_INSTANT);

            given(queryDslRepository.findAllByRoleId(ROLE_UUID))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(any(RolePermissionJpaEntity.class)))
                    .willReturn(rolePermission1, rolePermission2);

            // when
            List<RolePermission> results = adapter.findAllByRoleId(roleId);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("권한이 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoPermissionsFound() {
            // given
            RoleId roleId = RoleId.of(UUID.randomUUID());

            given(queryDslRepository.findAllByRoleId(roleId.value())).willReturn(List.of());

            // when
            List<RolePermission> results = adapter.findAllByRoleId(roleId);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByRoleIdAndPermissionId 메서드")
    class ExistsByRoleIdAndPermissionIdTest {

        @Test
        @DisplayName("RolePermission이 존재하면 true를 반환한다")
        void shouldReturnTrueWhenRolePermissionExists() {
            // given
            RoleId roleId = RoleId.of(ROLE_UUID);
            PermissionId permissionId = PermissionId.of(PERMISSION_UUID);

            given(queryDslRepository.existsByRoleIdAndPermissionId(ROLE_UUID, PERMISSION_UUID))
                    .willReturn(true);

            // when
            boolean result = adapter.existsByRoleIdAndPermissionId(roleId, permissionId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("RolePermission이 존재하지 않으면 false를 반환한다")
        void shouldReturnFalseWhenRolePermissionNotExists() {
            // given
            RoleId roleId = RoleId.of(UUID.randomUUID());
            PermissionId permissionId = PermissionId.of(UUID.randomUUID());

            given(
                            queryDslRepository.existsByRoleIdAndPermissionId(
                                    roleId.value(), permissionId.value()))
                    .willReturn(false);

            // when
            boolean result = adapter.existsByRoleIdAndPermissionId(roleId, permissionId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByRoleIds 메서드")
    class FindAllByRoleIdsTest {

        @Test
        @DisplayName("여러 RoleId로 RolePermission 목록을 조회한다")
        void shouldFindAllByRoleIds() {
            // given
            Set<RoleId> roleIds = Set.of(RoleId.of(ROLE_UUID), RoleId.of(ROLE_UUID_2));
            RolePermission rolePermission1 =
                    RolePermission.reconstitute(
                            RoleId.of(ROLE_UUID), PermissionId.of(PERMISSION_UUID), FIXED_INSTANT);
            RolePermission rolePermission2 =
                    RolePermission.reconstitute(
                            RoleId.of(ROLE_UUID_2),
                            PermissionId.of(PERMISSION_UUID_2),
                            FIXED_INSTANT);
            RolePermissionJpaEntity entity1 =
                    RolePermissionJpaEntity.of(
                            UUID.randomUUID(), ROLE_UUID, PERMISSION_UUID, FIXED_INSTANT);
            RolePermissionJpaEntity entity2 =
                    RolePermissionJpaEntity.of(
                            UUID.randomUUID(), ROLE_UUID_2, PERMISSION_UUID_2, FIXED_INSTANT);

            given(queryDslRepository.findAllByRoleIds(anySet()))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(any(RolePermissionJpaEntity.class)))
                    .willReturn(rolePermission1, rolePermission2);

            // when
            List<RolePermission> results = adapter.findAllByRoleIds(roleIds);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("빈 RoleId Set이면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenRoleIdsEmpty() {
            // given
            Set<RoleId> emptyRoleIds = Set.of();

            // when
            List<RolePermission> results = adapter.findAllByRoleIds(emptyRoleIds);

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("null RoleId Set이면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenRoleIdsNull() {
            // when
            List<RolePermission> results = adapter.findAllByRoleIds(null);

            // then
            assertThat(results).isEmpty();
        }
    }
}
