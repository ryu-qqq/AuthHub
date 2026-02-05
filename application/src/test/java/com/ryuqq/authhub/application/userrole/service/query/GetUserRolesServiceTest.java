package com.ryuqq.authhub.application.userrole.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.userrole.assembler.UserRoleAssembler;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import com.ryuqq.authhub.application.userrole.dto.response.UserRolesResponse;
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
 * GetUserRolesService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserRolesService 단위 테스트")
class GetUserRolesServiceTest {

    @Mock private UserRoleReadFacade userRoleReadFacade;
    @Mock private UserRoleAssembler assembler;

    private GetUserRolesService sut;

    @BeforeEach
    void setUp() {
        sut = new GetUserRolesService(userRoleReadFacade, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Facade 호출 후 Assembler로 UserRolesResponse 반환")
        void shouldReturnUserRolesResponse_WhenFacadeReturnsComposite() {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000001";
            Set<String> roleNames = Set.of("ADMIN", "USER");
            Set<String> permissionKeys = Set.of("user:read", "user:write");
            RolesAndPermissionsComposite composite =
                    new RolesAndPermissionsComposite(roleNames, permissionKeys);
            UserRolesResponse expectedResponse = new UserRolesResponse(roleNames, permissionKeys);

            given(userRoleReadFacade.findRolesAndPermissionsByUserId(any(UserId.class)))
                    .willReturn(composite);
            given(assembler.assemble(roleNames, permissionKeys)).willReturn(expectedResponse);

            // when
            UserRolesResponse result = sut.execute(userId);

            // then
            assertThat(result).isEqualTo(expectedResponse);
            assertThat(result.roles()).containsExactlyInAnyOrderElementsOf(roleNames);
            assertThat(result.permissions()).containsExactlyInAnyOrderElementsOf(permissionKeys);

            then(userRoleReadFacade).should().findRolesAndPermissionsByUserId(any(UserId.class));
            then(assembler).should().assemble(roleNames, permissionKeys);
        }

        @Test
        @DisplayName("빈 역할/권한이면 Assembler에 빈 Set 전달하여 응답 반환")
        void shouldReturnResponse_WhenCompositeIsEmpty() {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000002";
            RolesAndPermissionsComposite emptyComposite = RolesAndPermissionsComposite.empty();
            UserRolesResponse emptyResponse = new UserRolesResponse(Set.of(), Set.of());

            given(userRoleReadFacade.findRolesAndPermissionsByUserId(any(UserId.class)))
                    .willReturn(emptyComposite);
            given(assembler.assemble(Set.of(), Set.of())).willReturn(emptyResponse);

            // when
            UserRolesResponse result = sut.execute(userId);

            // then
            assertThat(result.roles()).isEmpty();
            assertThat(result.permissions()).isEmpty();
            then(assembler).should().assemble(Set.of(), Set.of());
        }
    }
}
