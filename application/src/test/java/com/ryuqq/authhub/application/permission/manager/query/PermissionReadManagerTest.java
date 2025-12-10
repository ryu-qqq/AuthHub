package com.ryuqq.authhub.application.permission.manager.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.application.permission.port.out.query.PermissionQueryPort;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
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
 * PermissionReadManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionReadManager 단위 테스트")
class PermissionReadManagerTest {

    @Mock private PermissionQueryPort queryPort;

    private PermissionReadManager readManager;

    @BeforeEach
    void setUp() {
        readManager = new PermissionReadManager(queryPort);
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetByIdTest {

        @Test
        @DisplayName("ID로 권한을 조회한다")
        void shouldGetPermissionById() {
            // given
            PermissionId permissionId = PermissionFixture.createPermissionId();
            Permission expectedPermission = PermissionFixture.createReconstituted(permissionId.value());
            given(queryPort.findById(permissionId)).willReturn(Optional.of(expectedPermission));

            // when
            Permission result = readManager.getById(permissionId);

            // then
            assertThat(result).isEqualTo(expectedPermission);
        }

        @Test
        @DisplayName("권한이 없으면 PermissionNotFoundException을 발생시킨다")
        void shouldThrowWhenPermissionNotFound() {
            // given
            PermissionId permissionId = PermissionId.of(UUID.randomUUID());
            given(queryPort.findById(permissionId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> readManager.getById(permissionId))
                    .isInstanceOf(PermissionNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getByKey 메서드")
    class GetByKeyTest {

        @Test
        @DisplayName("권한 키로 권한을 조회한다")
        void shouldGetPermissionByKey() {
            // given
            PermissionKey key = PermissionKey.of("user:read");
            Permission expectedPermission = PermissionFixture.createReconstituted();
            given(queryPort.findByKey(key)).willReturn(Optional.of(expectedPermission));

            // when
            Permission result = readManager.getByKey(key);

            // then
            assertThat(result).isEqualTo(expectedPermission);
        }

        @Test
        @DisplayName("권한이 없으면 PermissionNotFoundException을 발생시킨다")
        void shouldThrowWhenPermissionNotFoundByKey() {
            // given
            PermissionKey key = PermissionKey.of("nonexistent:action");
            given(queryPort.findByKey(key)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> readManager.getByKey(key))
                    .isInstanceOf(PermissionNotFoundException.class);
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
            given(queryPort.existsByKey(key)).willReturn(true);

            // when
            boolean result = readManager.existsByKey(key);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("권한 키가 존재하지 않으면 false를 반환한다")
        void shouldReturnFalseWhenKeyNotExists() {
            // given
            PermissionKey key = PermissionKey.of("nonexistent:action");
            given(queryPort.existsByKey(key)).willReturn(false);

            // when
            boolean result = readManager.existsByKey(key);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("search 메서드")
    class SearchTest {

        @Test
        @DisplayName("검색 조건에 맞는 권한 목록을 반환한다")
        void shouldSearchPermissions() {
            // given
            SearchPermissionsQuery query = new SearchPermissionsQuery("user", null, null, 0, 10);
            List<Permission> expectedPermissions = List.of(PermissionFixture.createReconstituted());
            given(queryPort.search(query)).willReturn(expectedPermissions);

            // when
            List<Permission> result = readManager.search(query);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).isEqualTo(expectedPermissions);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoResults() {
            // given
            SearchPermissionsQuery query = new SearchPermissionsQuery("nonexistent", null, null, 0, 10);
            given(queryPort.search(query)).willReturn(List.of());

            // when
            List<Permission> result = readManager.search(query);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByIds 메서드")
    class FindAllByIdsTest {

        @Test
        @DisplayName("여러 ID로 권한 목록을 조회한다")
        void shouldFindAllByIds() {
            // given
            PermissionId id1 = PermissionFixture.createPermissionId();
            Set<PermissionId> permissionIds = Set.of(id1);
            List<Permission> expectedPermissions =
                    List.of(PermissionFixture.createReconstituted(id1.value()));
            given(queryPort.findAllByIds(permissionIds)).willReturn(expectedPermissions);

            // when
            List<Permission> result = readManager.findAllByIds(permissionIds);

            // then
            assertThat(result).isEqualTo(expectedPermissions);
        }

        @Test
        @DisplayName("빈 ID Set은 빈 목록을 반환한다")
        void shouldReturnEmptyListForEmptyIds() {
            // when
            List<Permission> result = readManager.findAllByIds(Set.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null ID Set은 빈 목록을 반환한다")
        void shouldReturnEmptyListForNullIds() {
            // when
            List<Permission> result = readManager.findAllByIds(null);

            // then
            assertThat(result).isEmpty();
        }
    }
}
