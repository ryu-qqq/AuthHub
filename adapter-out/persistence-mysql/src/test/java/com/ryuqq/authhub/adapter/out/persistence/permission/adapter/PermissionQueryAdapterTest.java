package com.ryuqq.authhub.adapter.out.persistence.permission.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.mapper.PermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionQueryDslRepository;
import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
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
 * PermissionQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionQueryAdapter 단위 테스트")
class PermissionQueryAdapterTest {

    @Mock private PermissionQueryDslRepository repository;

    @Mock private PermissionJpaEntityMapper mapper;

    private PermissionQueryAdapter adapter;

    private static final UUID PERMISSION_UUID = UUID.randomUUID();
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @BeforeEach
    void setUp() {
        adapter = new PermissionQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 권한을 성공적으로 조회한다")
        void shouldFindPermissionByIdSuccessfully() {
            // given
            PermissionId permissionId = PermissionId.of(PERMISSION_UUID);
            Permission expectedPermission = PermissionFixture.createReconstituted(PERMISSION_UUID);
            PermissionJpaEntity entity = createPermissionEntity();

            given(repository.findByPermissionId(PERMISSION_UUID)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedPermission);

            // when
            Optional<Permission> result = adapter.findById(permissionId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedPermission);
            verify(repository).findByPermissionId(PERMISSION_UUID);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenPermissionNotFound() {
            // given
            PermissionId permissionId = PermissionId.of(UUID.randomUUID());

            given(repository.findByPermissionId(permissionId.value())).willReturn(Optional.empty());

            // when
            Optional<Permission> result = adapter.findById(permissionId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByKey 메서드")
    class FindByKeyTest {

        @Test
        @DisplayName("권한 키로 권한을 조회한다")
        void shouldFindPermissionByKey() {
            // given
            PermissionKey key = PermissionKey.of("user:read");
            Permission expectedPermission = PermissionFixture.createReconstituted(PERMISSION_UUID);
            PermissionJpaEntity entity = createPermissionEntity();

            given(repository.findByKey("user:read")).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedPermission);

            // when
            Optional<Permission> result = adapter.findByKey(key);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedPermission);
        }

        @Test
        @DisplayName("존재하지 않는 키로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenKeyNotFound() {
            // given
            PermissionKey key = PermissionKey.of("nonexistent:key");

            given(repository.findByKey("nonexistent:key")).willReturn(Optional.empty());

            // when
            Optional<Permission> result = adapter.findByKey(key);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByKey 메서드")
    class ExistsByKeyTest {

        @Test
        @DisplayName("권한 키가 존재하면 true를 반환한다")
        void shouldReturnTrueWhenKeyExists() {
            // given
            PermissionKey key = PermissionKey.of("user:read");

            given(repository.existsByKey("user:read")).willReturn(true);

            // when
            boolean result = adapter.existsByKey(key);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("권한 키가 존재하지 않으면 false를 반환한다")
        void shouldReturnFalseWhenKeyNotExists() {
            // given
            PermissionKey key = PermissionKey.of("nonexistent:key");

            given(repository.existsByKey("nonexistent:key")).willReturn(false);

            // when
            boolean result = adapter.existsByKey(key);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("search 메서드")
    class SearchTest {

        @Test
        @DisplayName("검색 조건으로 권한 목록을 조회한다")
        void shouldSearchPermissionsSuccessfully() {
            // given
            SearchPermissionsQuery query = new SearchPermissionsQuery(null, null, null, 0, 20);
            Permission permission1 =
                    PermissionFixture.createReconstituted(UUID.randomUUID(), "user:read");
            Permission permission2 =
                    PermissionFixture.createReconstituted(UUID.randomUUID(), "user:write");
            PermissionJpaEntity entity1 = createPermissionEntity();
            PermissionJpaEntity entity2 = createPermissionEntity();

            given(repository.search(any(), any(), any(), anyInt(), anyInt()))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(any(PermissionJpaEntity.class)))
                    .willReturn(permission1, permission2);

            // when
            List<Permission> results = adapter.search(query);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoResults() {
            // given
            SearchPermissionsQuery query =
                    new SearchPermissionsQuery("nonexistent", null, null, 0, 20);

            given(repository.search(any(), any(), any(), anyInt(), anyInt())).willReturn(List.of());

            // when
            List<Permission> results = adapter.search(query);

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("타입 필터로 권한을 검색한다")
        void shouldSearchPermissionsWithTypeFilter() {
            // given
            SearchPermissionsQuery query =
                    new SearchPermissionsQuery(null, null, PermissionType.SYSTEM, 0, 20);
            Permission systemPermission =
                    PermissionFixture.createReconstitutedSystem(PERMISSION_UUID, "user:admin");
            PermissionJpaEntity entity = createSystemPermissionEntity();

            given(repository.search(any(), any(), eq("SYSTEM"), anyInt(), anyInt()))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(any(PermissionJpaEntity.class))).willReturn(systemPermission);

            // when
            List<Permission> results = adapter.search(query);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).isSystem()).isTrue();
        }
    }

    @Nested
    @DisplayName("count 메서드")
    class CountTest {

        @Test
        @DisplayName("검색 조건으로 권한 개수를 조회한다")
        void shouldCountPermissionsSuccessfully() {
            // given
            SearchPermissionsQuery query = new SearchPermissionsQuery(null, null, null, 0, 20);

            given(repository.count(any(), any(), any())).willReturn(10L);

            // when
            long result = adapter.count(query);

            // then
            assertThat(result).isEqualTo(10L);
        }
    }

    @Nested
    @DisplayName("findAllByIds 메서드")
    class FindAllByIdsTest {

        @Test
        @DisplayName("여러 ID로 권한 목록을 조회한다")
        void shouldFindAllByIds() {
            // given
            UUID permissionId1 = UUID.randomUUID();
            UUID permissionId2 = UUID.randomUUID();
            Set<PermissionId> permissionIds =
                    Set.of(PermissionId.of(permissionId1), PermissionId.of(permissionId2));
            Permission permission1 = PermissionFixture.createReconstituted(permissionId1);
            Permission permission2 = PermissionFixture.createReconstituted(permissionId2);
            PermissionJpaEntity entity1 = createPermissionEntity();
            PermissionJpaEntity entity2 = createPermissionEntity();

            given(repository.findAllByIds(Set.of(permissionId1, permissionId2)))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(any(PermissionJpaEntity.class)))
                    .willReturn(permission1, permission2);

            // when
            List<Permission> results = adapter.findAllByIds(permissionIds);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("빈 ID Set이면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenIdsEmpty() {
            // given
            Set<PermissionId> emptyIds = Set.of();

            // when
            List<Permission> results = adapter.findAllByIds(emptyIds);

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("null ID Set이면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenIdsNull() {
            // when
            List<Permission> results = adapter.findAllByIds(null);

            // then
            assertThat(results).isEmpty();
        }
    }

    private PermissionJpaEntity createPermissionEntity() {
        return PermissionJpaEntity.of(
                1L,
                PERMISSION_UUID,
                "user:read",
                "user",
                "read",
                "사용자 조회 권한",
                PermissionType.CUSTOM,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }

    private PermissionJpaEntity createSystemPermissionEntity() {
        return PermissionJpaEntity.of(
                1L,
                PERMISSION_UUID,
                "user:admin",
                "user",
                "admin",
                "사용자 관리자 권한",
                PermissionType.SYSTEM,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }
}
