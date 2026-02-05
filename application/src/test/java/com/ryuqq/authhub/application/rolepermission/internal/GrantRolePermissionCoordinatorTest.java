package com.ryuqq.authhub.application.rolepermission.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.permission.validator.PermissionValidator;
import com.ryuqq.authhub.application.role.validator.RoleValidator;
import com.ryuqq.authhub.application.rolepermission.factory.RolePermissionCommandFactory;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionReadManager;
import com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.fixture.RolePermissionFixture;
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
 * GrantRolePermissionCoordinator 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GrantRolePermissionCoordinator 단위 테스트")
class GrantRolePermissionCoordinatorTest {

    @Mock private RoleValidator roleValidator;

    @Mock private PermissionValidator permissionValidator;

    @Mock private RolePermissionReadManager rolePermissionReadManager;

    @Mock private RolePermissionCommandFactory commandFactory;

    private GrantRolePermissionCoordinator sut;

    @BeforeEach
    void setUp() {
        sut =
                new GrantRolePermissionCoordinator(
                        roleValidator,
                        permissionValidator,
                        rolePermissionReadManager,
                        commandFactory);
    }

    @Nested
    @DisplayName("coordinate 메서드")
    class Coordinate {

        @Test
        @DisplayName("성공: 검증 → 필터링 → Factory 생성 순서로 호출하고 RolePermission 목록 반환")
        void shouldOrchestrate_ValidationThenFilterThenFactory() {
            // given
            Long roleId = RolePermissionFixture.defaultRoleIdValue();
            List<Long> permissionIds = List.of(RolePermissionFixture.defaultPermissionIdValue());
            RoleId roleIdVo = RolePermissionFixture.defaultRoleId();
            List<PermissionId> permissionIdVos =
                    List.of(RolePermissionFixture.defaultPermissionId());
            List<RolePermission> created = List.of(RolePermissionFixture.createNew());

            given(rolePermissionReadManager.findGrantedPermissionIds(roleIdVo, permissionIdVos))
                    .willReturn(List.of());
            given(commandFactory.createAll(roleIdVo, permissionIdVos)).willReturn(created);

            // when
            List<RolePermission> result = sut.coordinate(roleId, permissionIds);

            // then
            assertThat(result).hasSize(1);
            then(roleValidator).should().findExistingOrThrow(roleIdVo);
            then(permissionValidator).should().validateAllExist(permissionIdVos);
            then(rolePermissionReadManager)
                    .should()
                    .findGrantedPermissionIds(roleIdVo, permissionIdVos);
            then(commandFactory).should().createAll(roleIdVo, permissionIdVos);
        }

        @Test
        @DisplayName("permissionIds가 null이면 빈 목록 반환하고 validator/readManager/createAll 미호출")
        void shouldReturnEmpty_WhenPermissionIdsIsNull() {
            // given
            Long roleId = RolePermissionFixture.defaultRoleIdValue();

            // when
            List<RolePermission> result = sut.coordinate(roleId, null);

            // then
            assertThat(result).isEmpty();
            then(roleValidator).shouldHaveNoInteractions();
            then(permissionValidator).shouldHaveNoInteractions();
            then(rolePermissionReadManager).shouldHaveNoInteractions();
            then(commandFactory).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("permissionIds가 빈 목록이면 빈 목록 반환하고 validator/readManager/createAll 미호출")
        void shouldReturnEmpty_WhenPermissionIdsIsEmpty() {
            // given
            Long roleId = RolePermissionFixture.defaultRoleIdValue();

            // when
            List<RolePermission> result = sut.coordinate(roleId, List.of());

            // then
            assertThat(result).isEmpty();
            then(roleValidator).shouldHaveNoInteractions();
            then(permissionValidator).shouldHaveNoInteractions();
            then(rolePermissionReadManager).shouldHaveNoInteractions();
            then(commandFactory).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패: Role이 없으면 RoleNotFoundException 발생")
        void shouldThrowException_WhenRoleNotFound() {
            // given
            Long roleId = RolePermissionFixture.defaultRoleIdValue();
            List<Long> permissionIds = List.of(RolePermissionFixture.defaultPermissionIdValue());
            RoleId roleIdVo = RoleId.of(roleId);

            given(roleValidator.findExistingOrThrow(roleIdVo))
                    .willThrow(new RoleNotFoundException(roleIdVo));

            // when & then
            assertThatThrownBy(() -> sut.coordinate(roleId, permissionIds))
                    .isInstanceOf(RoleNotFoundException.class);
            then(permissionValidator).shouldHaveNoInteractions();
            then(commandFactory).should(never()).createAll(any(), any());
        }

        @Test
        @DisplayName("실패: Permission이 없으면 PermissionNotFoundException 발생")
        void shouldThrowException_WhenPermissionNotFound() {
            // given
            Long roleId = RolePermissionFixture.defaultRoleIdValue();
            List<Long> permissionIds = List.of(RolePermissionFixture.defaultPermissionIdValue());
            RoleId roleIdVo = RolePermissionFixture.defaultRoleId();
            List<PermissionId> permissionIdVos =
                    List.of(RolePermissionFixture.defaultPermissionId());

            given(permissionValidator.validateAllExist(permissionIdVos))
                    .willThrow(
                            new PermissionNotFoundException(
                                    RolePermissionFixture.defaultPermissionIdValue()));

            // when & then
            assertThatThrownBy(() -> sut.coordinate(roleId, permissionIds))
                    .isInstanceOf(PermissionNotFoundException.class);
            then(rolePermissionReadManager).shouldHaveNoInteractions();
            then(commandFactory).should(never()).createAll(any(), any());
        }

        @Test
        @DisplayName("이미 모두 부여된 경우 빈 목록 반환하고 createAll 미호출")
        void shouldReturnEmpty_WhenAllAlreadyGranted() {
            // given
            Long roleId = RolePermissionFixture.defaultRoleIdValue();
            List<Long> permissionIds = List.of(RolePermissionFixture.defaultPermissionIdValue());
            RoleId roleIdVo = RolePermissionFixture.defaultRoleId();
            List<PermissionId> permissionIdVos =
                    List.of(RolePermissionFixture.defaultPermissionId());

            given(rolePermissionReadManager.findGrantedPermissionIds(roleIdVo, permissionIdVos))
                    .willReturn(permissionIdVos);

            // when
            List<RolePermission> result = sut.coordinate(roleId, permissionIds);

            // then
            assertThat(result).isEmpty();
            then(roleValidator).should().findExistingOrThrow(roleIdVo);
            then(permissionValidator).should().validateAllExist(permissionIdVos);
            then(commandFactory).should(never()).createAll(any(), any());
        }
    }
}
