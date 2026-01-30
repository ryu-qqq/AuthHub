package com.ryuqq.authhub.application.token.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.token.dto.composite.MyContextComposite;
import com.ryuqq.authhub.application.token.dto.composite.UserContextComposite;
import com.ryuqq.authhub.application.token.manager.query.UserContextCompositeReadManager;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import com.ryuqq.authhub.application.userrole.facade.UserRoleReadFacade;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
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
 * MyContextReadFacade 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("MyContextReadFacade 단위 테스트")
class MyContextReadFacadeTest {

    @Mock private UserContextCompositeReadManager userContextCompositeReadManager;
    @Mock private UserRoleReadFacade userRoleReadFacade;

    private MyContextReadFacade sut;

    @BeforeEach
    void setUp() {
        sut = new MyContextReadFacade(userContextCompositeReadManager, userRoleReadFacade);
    }

    @Nested
    @DisplayName("findMyContext 메서드")
    class FindMyContext {

        @Test
        @DisplayName("성공: UserContextCompositeReadManager와 UserRoleReadFacade를 조합하여 Composite 반환")
        void shouldCombine_UserContextAndRolesPermissions_AndReturnComposite() {
            // given
            UserId userId = UserId.of("019450eb-4f1e-7000-8000-000000000001");

            UserContextComposite userContext =
                    UserContextComposite.builder()
                            .userId(userId.value())
                            .email("test@example.com")
                            .name("Test User")
                            .tenantId("tenant-123")
                            .tenantName("Test Tenant")
                            .organizationId("org-456")
                            .organizationName("Test Organization")
                            .build();

            RolesAndPermissionsComposite rolesAndPermissions =
                    new RolesAndPermissionsComposite(
                            Set.of("ADMIN", "USER"), Set.of("user:read", "user:write"));

            given(userContextCompositeReadManager.getUserContextByUserId(userId))
                    .willReturn(userContext);
            given(userRoleReadFacade.findRolesAndPermissionsByUserId(userId))
                    .willReturn(rolesAndPermissions);

            // when
            MyContextComposite result = sut.findMyContext(userId);

            // then
            assertThat(result.userContext()).isEqualTo(userContext);
            assertThat(result.rolesAndPermissions()).isEqualTo(rolesAndPermissions);
            then(userContextCompositeReadManager).should().getUserContextByUserId(userId);
            then(userRoleReadFacade).should().findRolesAndPermissionsByUserId(userId);
        }

        @Test
        @DisplayName("실패: 사용자가 존재하지 않으면 UserNotFoundException 발생")
        void shouldThrowException_WhenUserNotExists() {
            // given
            UserId userId = UserId.of("019450eb-4f1e-7000-8000-000000000001");

            given(userContextCompositeReadManager.getUserContextByUserId(userId))
                    .willThrow(new UserNotFoundException(userId));

            // when & then
            assertThatThrownBy(() -> sut.findMyContext(userId))
                    .isInstanceOf(UserNotFoundException.class);
            then(userRoleReadFacade).should(never()).findRolesAndPermissionsByUserId(userId);
        }

        @Test
        @DisplayName("성공: 역할/권한이 없어도 빈 Composite로 정상 반환")
        void shouldReturnComposite_WithEmptyRolesAndPermissions() {
            // given
            UserId userId = UserId.of("019450eb-4f1e-7000-8000-000000000001");

            UserContextComposite userContext =
                    UserContextComposite.builder()
                            .userId(userId.value())
                            .email("test@example.com")
                            .name("Test User")
                            .tenantId("tenant-123")
                            .tenantName("Test Tenant")
                            .organizationId("org-456")
                            .organizationName("Test Organization")
                            .build();

            RolesAndPermissionsComposite emptyRolesAndPermissions =
                    RolesAndPermissionsComposite.empty();

            given(userContextCompositeReadManager.getUserContextByUserId(userId))
                    .willReturn(userContext);
            given(userRoleReadFacade.findRolesAndPermissionsByUserId(userId))
                    .willReturn(emptyRolesAndPermissions);

            // when
            MyContextComposite result = sut.findMyContext(userId);

            // then
            assertThat(result.userContext()).isEqualTo(userContext);
            assertThat(result.rolesAndPermissions().roleNames()).isEmpty();
            assertThat(result.rolesAndPermissions().permissionKeys()).isEmpty();
        }
    }
}
