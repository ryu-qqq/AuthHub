package com.ryuqq.authhub.application.rolepermission.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.rolepermission.dto.query.RolePermissionSearchParams;
import com.ryuqq.authhub.application.rolepermission.fixture.RolePermissionQueryFixtures;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.query.criteria.RolePermissionSearchCriteria;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RolePermissionQueryFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RolePermissionQueryFactory 단위 테스트")
class RolePermissionQueryFactoryTest {

    private RolePermissionQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new RolePermissionQueryFactory();
    }

    @Nested
    @DisplayName("toCriteria 메서드")
    class ToCriteria {

        @Test
        @DisplayName("성공: 기본 SearchParams로 Criteria 생성 (모든 필드 null)")
        void shouldCreateCriteria_FromDefaultSearchParams() {
            // given
            RolePermissionSearchParams params = RolePermissionQueryFixtures.searchParams();

            // when
            RolePermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.roleId()).isNull();
            assertThat(result.roleIds()).isNull();
            assertThat(result.permissionId()).isNull();
            assertThat(result.permissionIds()).isNull();
            assertThat(result.queryContext()).isNotNull();
            assertThat(result.queryContext().sortKey()).isNotNull();
        }

        @Test
        @DisplayName("성공: roleId가 있으면 Criteria에 반영됨")
        void shouldCreateCriteria_WithRoleId() {
            // given
            Long roleId = RolePermissionQueryFixtures.defaultRoleId();
            RolePermissionSearchParams params =
                    RolePermissionQueryFixtures.searchParamsWithRoleId(roleId);

            // when
            RolePermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.roleId()).isEqualTo(RoleId.of(roleId));
            assertThat(result.roleIds()).isNull();
        }

        @Test
        @DisplayName("성공: roleIds가 있으면 Criteria에 반영됨")
        void shouldCreateCriteria_WithRoleIds() {
            // given
            List<Long> roleIds = List.of(1L, 2L);
            RolePermissionSearchParams params =
                    RolePermissionQueryFixtures.searchParamsWithRoleIds(roleIds);

            // when
            RolePermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.roleIds()).hasSize(2);
            assertThat(result.roleIds()).contains(RoleId.of(1L), RoleId.of(2L));
        }

        @Test
        @DisplayName("roleIds가 null이면 criteria.roleIds()는 null")
        void shouldCreateCriteriaWithNullRoleIds_WhenRoleIdsIsNull() {
            // given
            RolePermissionSearchParams params = RolePermissionQueryFixtures.searchParams();

            // when
            RolePermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.roleIds()).isNull();
        }

        @Test
        @DisplayName("roleIds가 빈 목록이면 criteria.roleIds()는 null")
        void shouldCreateCriteriaWithNullRoleIds_WhenRoleIdsIsEmpty() {
            // given
            RolePermissionSearchParams params =
                    RolePermissionQueryFixtures.searchParamsWithRoleIds(Collections.emptyList());

            // when
            RolePermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.roleIds()).isNull();
        }

        @Test
        @DisplayName("성공: permissionId가 있으면 Criteria에 반영됨")
        void shouldCreateCriteria_WithPermissionId() {
            // given
            Long permissionId = RolePermissionQueryFixtures.defaultPermissionId();
            RolePermissionSearchParams params =
                    RolePermissionQueryFixtures.searchParamsWithPermissionId(permissionId);

            // when
            RolePermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.permissionId()).isEqualTo(PermissionId.of(permissionId));
            assertThat(result.permissionIds()).isNull();
        }

        @Test
        @DisplayName("성공: permissionIds가 있으면 Criteria에 반영됨")
        void shouldCreateCriteria_WithPermissionIds() {
            // given
            List<Long> permissionIds = List.of(1L, 2L);
            RolePermissionSearchParams params =
                    RolePermissionQueryFixtures.searchParamsWithPermissionIds(permissionIds);

            // when
            RolePermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.permissionIds()).hasSize(2);
            assertThat(result.permissionIds()).contains(PermissionId.of(1L), PermissionId.of(2L));
        }

        @Test
        @DisplayName("permissionIds가 null이면 criteria.permissionIds()는 null")
        void shouldCreateCriteriaWithNullPermissionIds_WhenPermissionIdsIsNull() {
            // given
            RolePermissionSearchParams params = RolePermissionQueryFixtures.searchParams();

            // when
            RolePermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.permissionIds()).isNull();
        }

        @Test
        @DisplayName("permissionIds가 빈 목록이면 criteria.permissionIds()는 null")
        void shouldCreateCriteriaWithNullPermissionIds_WhenPermissionIdsIsEmpty() {
            // given
            RolePermissionSearchParams params =
                    RolePermissionQueryFixtures.searchParamsWithPermissionIds(
                            Collections.emptyList());

            // when
            RolePermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.permissionIds()).isNull();
        }
    }
}
