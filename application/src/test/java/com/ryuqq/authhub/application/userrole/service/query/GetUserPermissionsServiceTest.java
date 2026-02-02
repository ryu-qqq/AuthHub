package com.ryuqq.authhub.application.userrole.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import com.ryuqq.authhub.application.userrole.dto.response.UserPermissionsResult;
import com.ryuqq.authhub.application.userrole.facade.UserRoleReadFacade;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * GetUserPermissionsService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserPermissionsService 단위 테스트")
class GetUserPermissionsServiceTest {

    @Mock private UserRoleReadFacade userRoleReadFacade;

    private GetUserPermissionsService sut;

    @BeforeEach
    void setUp() {
        sut = new GetUserPermissionsService(userRoleReadFacade);
    }

    @Nested
    @DisplayName("getByUserId 메서드")
    class GetByUserId {

        @Test
        @DisplayName("성공: Facade를 호출하고 역할/권한이 있는 결과를 반환한다")
        void shouldReturnUserPermissions_WhenUserHasRolesAndPermissions() {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000001";
            Set<String> roleNames = Set.of("ADMIN", "USER");
            Set<String> permissionKeys = Set.of("user:read", "user:write");

            RolesAndPermissionsComposite composite =
                    new RolesAndPermissionsComposite(roleNames, permissionKeys);

            given(userRoleReadFacade.findRolesAndPermissionsByUserId(any(UserId.class)))
                    .willReturn(composite);

            // when
            UserPermissionsResult result = sut.getByUserId(userId);

            // then
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.roles()).containsExactlyInAnyOrderElementsOf(roleNames);
            assertThat(result.permissions()).containsExactlyInAnyOrderElementsOf(permissionKeys);
            then(userRoleReadFacade).should().findRolesAndPermissionsByUserId(any(UserId.class));
        }

        @Test
        @DisplayName("성공: 역할/권한이 없는 사용자는 빈 Set을 반환한다")
        void shouldReturnEmptySets_WhenUserHasNoRolesOrPermissions() {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000002";
            RolesAndPermissionsComposite emptyComposite = RolesAndPermissionsComposite.empty();

            given(userRoleReadFacade.findRolesAndPermissionsByUserId(any(UserId.class)))
                    .willReturn(emptyComposite);

            // when
            UserPermissionsResult result = sut.getByUserId(userId);

            // then
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.roles()).isEmpty();
            assertThat(result.permissions()).isEmpty();
            then(userRoleReadFacade).should().findRolesAndPermissionsByUserId(any(UserId.class));
        }

        @Test
        @DisplayName("성공: 역할만 있고 권한이 없는 경우 권한은 빈 Set을 반환한다")
        void shouldReturnEmptyPermissions_WhenUserHasOnlyRoles() {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000003";
            Set<String> roleNames = Set.of("VIEWER");
            Set<String> permissionKeys = Set.of();

            RolesAndPermissionsComposite composite =
                    new RolesAndPermissionsComposite(roleNames, permissionKeys);

            given(userRoleReadFacade.findRolesAndPermissionsByUserId(any(UserId.class)))
                    .willReturn(composite);

            // when
            UserPermissionsResult result = sut.getByUserId(userId);

            // then
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.roles()).containsExactly("VIEWER");
            assertThat(result.permissions()).isEmpty();
        }
    }
}
