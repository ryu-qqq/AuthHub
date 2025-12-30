package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.mapper.RoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleQueryDslRepository;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Instant;
import java.time.LocalDateTime;
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
 * RoleQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleQueryAdapter 단위 테스트")
class RoleQueryAdapterTest {

    @Mock private RoleQueryDslRepository repository;

    @Mock private RoleJpaEntityMapper mapper;

    private RoleQueryAdapter adapter;

    private static final UUID ROLE_UUID = RoleFixture.defaultUUID();
    private static final UUID TENANT_UUID = RoleFixture.defaultTenantUUID();
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
    private static final Instant CREATED_FROM = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant CREATED_TO = Instant.parse("2025-12-31T23:59:59Z");

    @BeforeEach
    void setUp() {
        adapter = new RoleQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 역할을 성공적으로 조회한다")
        void shouldFindRoleByIdSuccessfully() {
            // given
            RoleId roleId = RoleId.of(ROLE_UUID);
            Role expectedRole = RoleFixture.create();
            RoleJpaEntity entity = createRoleEntity();

            given(repository.findByRoleId(ROLE_UUID)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedRole);

            // when
            Optional<Role> result = adapter.findById(roleId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedRole);
            verify(repository).findByRoleId(ROLE_UUID);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenRoleNotFound() {
            // given
            RoleId roleId = RoleId.of(UUID.randomUUID());

            given(repository.findByRoleId(roleId.value())).willReturn(Optional.empty());

            // when
            Optional<Role> result = adapter.findById(roleId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByTenantIdAndName 메서드")
    class FindByTenantIdAndNameTest {

        @Test
        @DisplayName("테넌트 ID와 이름으로 역할을 조회한다")
        void shouldFindRoleByTenantIdAndName() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            RoleName name = RoleName.of("TEST_ROLE");
            Role expectedRole = RoleFixture.create();
            RoleJpaEntity entity = createRoleEntity();

            given(repository.findByTenantIdAndName(TENANT_UUID, "TEST_ROLE"))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedRole);

            // when
            Optional<Role> result = adapter.findByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedRole);
        }

        @Test
        @DisplayName("GLOBAL 역할 조회 시 tenantId를 null로 전달한다")
        void shouldFindGlobalRoleWithNullTenantId() {
            // given
            RoleName name = RoleName.of("SUPER_ADMIN");
            Role expectedRole = RoleFixture.createSystemGlobal();
            RoleJpaEntity entity = createGlobalRoleEntity();

            given(repository.findByTenantIdAndName(null, "SUPER_ADMIN"))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedRole);

            // when
            Optional<Role> result = adapter.findByTenantIdAndName(null, name);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getScope()).isEqualTo(RoleScope.GLOBAL);
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndName 메서드")
    class ExistsByTenantIdAndNameTest {

        @Test
        @DisplayName("역할이 존재하면 true를 반환한다")
        void shouldReturnTrueWhenRoleExists() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            RoleName name = RoleName.of("TEST_ROLE");

            given(repository.existsByTenantIdAndName(TENANT_UUID, "TEST_ROLE")).willReturn(true);

            // when
            boolean result = adapter.existsByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("역할이 존재하지 않으면 false를 반환한다")
        void shouldReturnFalseWhenRoleNotExists() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            RoleName name = RoleName.of("NONEXISTENT_ROLE");

            given(repository.existsByTenantIdAndName(TENANT_UUID, "NONEXISTENT_ROLE"))
                    .willReturn(false);

            // when
            boolean result = adapter.existsByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("search 메서드")
    class SearchTest {

        @Test
        @DisplayName("검색 조건으로 역할 목록을 조회한다")
        void shouldSearchRolesSuccessfully() {
            // given
            SearchRolesQuery query =
                    SearchRolesQuery.of(
                            TENANT_UUID, null, null, null, CREATED_FROM, CREATED_TO, 0, 20);
            Role role1 = RoleFixture.create();
            Role role2 = RoleFixture.createWithName("ANOTHER_ROLE");
            RoleJpaEntity entity1 = createRoleEntity();
            RoleJpaEntity entity2 = createRoleEntity();

            given(repository.searchByQuery(query)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(any(RoleJpaEntity.class))).willReturn(role1, role2);

            // when
            List<Role> results = adapter.search(query);

            // then
            assertThat(results).hasSize(2);
            verify(repository).searchByQuery(query);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoResults() {
            // given
            SearchRolesQuery query =
                    SearchRolesQuery.of(
                            TENANT_UUID,
                            "nonexistent",
                            null,
                            null,
                            CREATED_FROM,
                            CREATED_TO,
                            0,
                            20);

            given(repository.searchByQuery(query)).willReturn(List.of());

            // when
            List<Role> results = adapter.search(query);

            // then
            assertThat(results).isEmpty();
            verify(repository).searchByQuery(query);
        }
    }

    @Nested
    @DisplayName("count 메서드")
    class CountTest {

        @Test
        @DisplayName("검색 조건으로 역할 개수를 조회한다")
        void shouldCountRolesSuccessfully() {
            // given
            SearchRolesQuery query =
                    SearchRolesQuery.of(
                            TENANT_UUID, null, null, null, CREATED_FROM, CREATED_TO, 0, 20);

            given(repository.countByQuery(query)).willReturn(5L);

            // when
            long result = adapter.count(query);

            // then
            assertThat(result).isEqualTo(5L);
            verify(repository).countByQuery(query);
        }
    }

    @Nested
    @DisplayName("findAllByIds 메서드")
    class FindAllByIdsTest {

        @Test
        @DisplayName("여러 ID로 역할 목록을 조회한다")
        void shouldFindAllByIds() {
            // given
            UUID roleId1 = UUID.randomUUID();
            UUID roleId2 = UUID.randomUUID();
            Set<RoleId> roleIds = Set.of(RoleId.of(roleId1), RoleId.of(roleId2));
            Role role1 = RoleFixture.create();
            Role role2 = RoleFixture.createWithName("ROLE2");
            RoleJpaEntity entity1 = createRoleEntity();
            RoleJpaEntity entity2 = createRoleEntity();

            given(repository.findAllByIds(Set.of(roleId1, roleId2)))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(any(RoleJpaEntity.class))).willReturn(role1, role2);

            // when
            List<Role> results = adapter.findAllByIds(roleIds);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("빈 ID Set이면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenIdsEmpty() {
            // given
            Set<RoleId> emptyIds = Set.of();

            // when
            List<Role> results = adapter.findAllByIds(emptyIds);

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("null ID Set이면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenIdsNull() {
            // when
            List<Role> results = adapter.findAllByIds(null);

            // then
            assertThat(results).isEmpty();
        }
    }

    private RoleJpaEntity createRoleEntity() {
        return RoleJpaEntity.of(
                ROLE_UUID,
                TENANT_UUID,
                "TEST_ROLE",
                "Test role description",
                RoleScope.ORGANIZATION,
                RoleType.CUSTOM,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }

    private RoleJpaEntity createGlobalRoleEntity() {
        return RoleJpaEntity.of(
                ROLE_UUID,
                null,
                "SUPER_ADMIN",
                "System super admin role",
                RoleScope.GLOBAL,
                RoleType.SYSTEM,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }
}
