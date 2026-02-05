package com.ryuqq.authhub.application.rolepermission.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.rolepermission.port.out.query.RolePermissionQueryPort;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.fixture.RolePermissionFixture;
import com.ryuqq.authhub.domain.rolepermission.query.criteria.RolePermissionSearchCriteria;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RolePermissionReadManager 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>ReadManager는 QueryPort 위임 담당
 *   <li>Port 호출이 올바르게 위임되는지 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RolePermissionReadManager 단위 테스트")
class RolePermissionReadManagerTest {

    @Mock private RolePermissionQueryPort queryPort;

    private RolePermissionReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new RolePermissionReadManager(queryPort);
    }

    @Nested
    @DisplayName("exists 메서드")
    class Exists {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            RoleId roleId = RolePermissionFixture.defaultRoleId();
            PermissionId permissionId = RolePermissionFixture.defaultPermissionId();

            given(queryPort.exists(roleId, permissionId)).willReturn(true);

            // when
            boolean result = sut.exists(roleId, permissionId);

            // then
            assertThat(result).isTrue();
            then(queryPort).should().exists(roleId, permissionId);
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            RoleId roleId = RolePermissionFixture.defaultRoleId();
            PermissionId permissionId = RolePermissionFixture.defaultPermissionId();

            given(queryPort.exists(roleId, permissionId)).willReturn(false);

            // when
            boolean result = sut.exists(roleId, permissionId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByRoleId 메서드")
    class FindAllByRoleId {

        @Test
        @DisplayName("성공: 역할 ID에 해당하는 RolePermission 목록 반환")
        void shouldReturnRolePermissions_ForRoleId() {
            // given
            RoleId roleId = RolePermissionFixture.defaultRoleId();
            List<RolePermission> expected = List.of(RolePermissionFixture.create());

            given(queryPort.findAllByRoleId(roleId)).willReturn(expected);

            // when
            List<RolePermission> result = sut.findAllByRoleId(roleId);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllByRoleId(roleId);
        }
    }

    @Nested
    @DisplayName("existsByPermissionId 메서드")
    class ExistsByPermissionId {

        @Test
        @DisplayName("권한이 사용 중이면 true 반환")
        void shouldReturnTrue_WhenPermissionInUse() {
            // given
            PermissionId permissionId = RolePermissionFixture.defaultPermissionId();

            given(queryPort.existsByPermissionId(permissionId)).willReturn(true);

            // when
            boolean result = sut.existsByPermissionId(permissionId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("권한이 사용 중이 아니면 false 반환")
        void shouldReturnFalse_WhenPermissionNotInUse() {
            // given
            PermissionId permissionId = RolePermissionFixture.defaultPermissionId();

            given(queryPort.existsByPermissionId(permissionId)).willReturn(false);

            // when
            boolean result = sut.existsByPermissionId(permissionId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllBySearchCriteria 메서드")
    class FindAllBySearchCriteria {

        @Test
        @DisplayName("성공: Criteria에 맞는 RolePermission 목록 반환")
        void shouldReturnRolePermissions_MatchingCriteria() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.ofRoleId(
                            RolePermissionFixture.defaultRoleId(), 0, 10);
            List<RolePermission> expected = List.of(RolePermissionFixture.create());

            given(queryPort.findAllBySearchCriteria(criteria)).willReturn(expected);

            // when
            List<RolePermission> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllBySearchCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("countBySearchCriteria 메서드")
    class CountBySearchCriteria {

        @Test
        @DisplayName("성공: Criteria에 맞는 개수 반환")
        void shouldReturnCount_MatchingCriteria() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.ofRoleId(
                            RolePermissionFixture.defaultRoleId(), 0, 10);
            given(queryPort.countBySearchCriteria(criteria)).willReturn(5L);

            // when
            long result = sut.countBySearchCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
            then(queryPort).should().countBySearchCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("findGrantedPermissionIds 메서드")
    class FindGrantedPermissionIds {

        @Test
        @DisplayName("성공: 역할에 이미 부여된 권한 ID 목록 반환")
        void shouldReturnGrantedPermissionIds_ForRoleAndPermissionIds() {
            // given
            RoleId roleId = RolePermissionFixture.defaultRoleId();
            List<PermissionId> permissionIds = List.of(RolePermissionFixture.defaultPermissionId());
            List<PermissionId> expected = List.of(RolePermissionFixture.defaultPermissionId());

            given(queryPort.findGrantedPermissionIds(roleId, permissionIds)).willReturn(expected);

            // when
            List<PermissionId> result = sut.findGrantedPermissionIds(roleId, permissionIds);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).containsExactly(RolePermissionFixture.defaultPermissionId());
            then(queryPort).should().findGrantedPermissionIds(roleId, permissionIds);
        }

        @Test
        @DisplayName("부여된 권한이 없으면 빈 목록 반환")
        void shouldReturnEmpty_WhenNoneGranted() {
            // given
            RoleId roleId = RolePermissionFixture.defaultRoleId();
            List<PermissionId> permissionIds = List.of(RolePermissionFixture.defaultPermissionId());
            given(queryPort.findGrantedPermissionIds(roleId, permissionIds)).willReturn(List.of());

            // when
            List<PermissionId> result = sut.findGrantedPermissionIds(roleId, permissionIds);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByRoleIds 메서드")
    class FindAllByRoleIds {

        @Test
        @DisplayName("성공: 역할 ID 목록에 해당하는 RolePermission 목록 반환")
        void shouldReturnRolePermissions_ForRoleIds() {
            // given
            List<RoleId> roleIds = List.of(RolePermissionFixture.defaultRoleId());
            List<RolePermission> expected = List.of(RolePermissionFixture.create());

            given(queryPort.findAllByRoleIds(roleIds)).willReturn(expected);

            // when
            List<RolePermission> result = sut.findAllByRoleIds(roleIds);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllByRoleIds(roleIds);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmpty_WhenNoRolePermissions() {
            // given
            List<RoleId> roleIds = List.of(RolePermissionFixture.defaultRoleId());
            given(queryPort.findAllByRoleIds(roleIds)).willReturn(List.of());

            // when
            List<RolePermission> result = sut.findAllByRoleIds(roleIds);

            // then
            assertThat(result).isEmpty();
        }
    }
}
