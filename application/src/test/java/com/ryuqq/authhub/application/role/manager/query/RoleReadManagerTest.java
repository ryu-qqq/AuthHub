package com.ryuqq.authhub.application.role.manager.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.port.out.query.RoleQueryPort;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
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
 * RoleReadManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleReadManager 단위 테스트")
class RoleReadManagerTest {

    @Mock private RoleQueryPort queryPort;

    private RoleReadManager readManager;

    @BeforeEach
    void setUp() {
        readManager = new RoleReadManager(queryPort);
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetByIdTest {

        @Test
        @DisplayName("ID로 역할을 조회한다")
        void shouldGetRoleById() {
            // given
            RoleId roleId = RoleFixture.defaultId();
            Role expectedRole = RoleFixture.create();
            given(queryPort.findById(roleId)).willReturn(Optional.of(expectedRole));

            // when
            Role result = readManager.getById(roleId);

            // then
            assertThat(result).isEqualTo(expectedRole);
        }

        @Test
        @DisplayName("역할이 없으면 RoleNotFoundException을 발생시킨다")
        void shouldThrowWhenRoleNotFound() {
            // given
            RoleId roleId = RoleId.of(UUID.randomUUID());
            given(queryPort.findById(roleId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> readManager.getById(roleId))
                    .isInstanceOf(RoleNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getByTenantIdAndName 메서드")
    class GetByTenantIdAndNameTest {

        @Test
        @DisplayName("테넌트 ID와 이름으로 역할을 조회한다")
        void shouldGetRoleByTenantIdAndName() {
            // given
            TenantId tenantId = RoleFixture.defaultTenantId();
            RoleName name = RoleName.of("TEST_ROLE");
            Role expectedRole = RoleFixture.create();
            given(queryPort.findByTenantIdAndName(tenantId, name))
                    .willReturn(Optional.of(expectedRole));

            // when
            Role result = readManager.getByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isEqualTo(expectedRole);
        }

        @Test
        @DisplayName("역할이 없으면 RoleNotFoundException을 발생시킨다")
        void shouldThrowWhenRoleNotFoundByName() {
            // given
            TenantId tenantId = RoleFixture.defaultTenantId();
            RoleName name = RoleName.of("NON_EXISTENT_ROLE");
            given(queryPort.findByTenantIdAndName(tenantId, name)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> readManager.getByTenantIdAndName(tenantId, name))
                    .isInstanceOf(RoleNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndName 메서드")
    class ExistsByTenantIdAndNameTest {

        @Test
        @DisplayName("역할 존재 여부를 반환한다 - 존재함")
        void shouldReturnTrueWhenExists() {
            // given
            TenantId tenantId = RoleFixture.defaultTenantId();
            RoleName name = RoleName.of("EXISTING_ROLE");
            given(queryPort.existsByTenantIdAndName(tenantId, name)).willReturn(true);

            // when
            boolean result = readManager.existsByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("역할 존재 여부를 반환한다 - 존재하지 않음")
        void shouldReturnFalseWhenNotExists() {
            // given
            TenantId tenantId = RoleFixture.defaultTenantId();
            RoleName name = RoleName.of("NON_EXISTENT_ROLE");
            given(queryPort.existsByTenantIdAndName(tenantId, name)).willReturn(false);

            // when
            boolean result = readManager.existsByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("search 메서드")
    class SearchTest {

        @Test
        @DisplayName("검색 조건에 맞는 역할 목록을 반환한다")
        void shouldSearchRoles() {
            // given
            SearchRolesQuery query =
                    SearchRolesQuery.of(RoleFixture.defaultTenantUUID(), null, null, null, 0, 10);
            List<Role> expectedRoles = List.of(RoleFixture.create());
            given(queryPort.search(query)).willReturn(expectedRoles);

            // when
            List<Role> result = readManager.search(query);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).isEqualTo(expectedRoles);
        }
    }

    @Nested
    @DisplayName("count 메서드")
    class CountTest {

        @Test
        @DisplayName("검색 조건에 맞는 역할 개수를 반환한다")
        void shouldCountRoles() {
            // given
            SearchRolesQuery query =
                    SearchRolesQuery.of(RoleFixture.defaultTenantUUID(), null, null, null, 0, 10);
            given(queryPort.count(query)).willReturn(5L);

            // when
            long result = readManager.count(query);

            // then
            assertThat(result).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("findAllByIds 메서드")
    class FindAllByIdsTest {

        @Test
        @DisplayName("여러 ID로 역할 목록을 조회한다")
        void shouldFindAllByIds() {
            // given
            Set<RoleId> roleIds = Set.of(RoleFixture.defaultId());
            List<Role> expectedRoles = List.of(RoleFixture.create());
            given(queryPort.findAllByIds(roleIds)).willReturn(expectedRoles);

            // when
            List<Role> result = readManager.findAllByIds(roleIds);

            // then
            assertThat(result).isEqualTo(expectedRoles);
        }

        @Test
        @DisplayName("빈 ID Set은 빈 목록을 반환한다")
        void shouldReturnEmptyListForEmptyIds() {
            // when
            List<Role> result = readManager.findAllByIds(Set.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null ID Set은 빈 목록을 반환한다")
        void shouldReturnEmptyListForNullIds() {
            // when
            List<Role> result = readManager.findAllByIds(null);

            // then
            assertThat(result).isEmpty();
        }
    }
}
