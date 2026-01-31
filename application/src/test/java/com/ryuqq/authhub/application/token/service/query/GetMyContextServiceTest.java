package com.ryuqq.authhub.application.token.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.token.assembler.MyContextCompositeAssembler;
import com.ryuqq.authhub.application.token.dto.composite.MyContextComposite;
import com.ryuqq.authhub.application.token.dto.composite.UserContextComposite;
import com.ryuqq.authhub.application.token.dto.response.MyContextResponse;
import com.ryuqq.authhub.application.token.internal.MyContextReadFacade;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.util.List;
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
 * GetMyContextService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetMyContextService 단위 테스트")
class GetMyContextServiceTest {

    @Mock private MyContextReadFacade myContextReadFacade;
    @Mock private MyContextCompositeAssembler myContextCompositeAssembler;

    private GetMyContextService sut;

    @BeforeEach
    void setUp() {
        sut = new GetMyContextService(myContextReadFacade, myContextCompositeAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Facade → Assembler 순서로 호출하고 Response 반환")
        void shouldOrchestrate_FacadeThenAssembler_AndReturnResponse() {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000001";
            UserContextComposite userContext =
                    UserContextComposite.builder()
                            .userId(userId)
                            .email("test@example.com")
                            .name("Test User")
                            .tenantId("tenant-123")
                            .tenantName("Test Tenant")
                            .organizationId("org-456")
                            .organizationName("Test Organization")
                            .build();

            RolesAndPermissionsComposite rolesAndPermissions =
                    new RolesAndPermissionsComposite(Set.of("ADMIN"), Set.of("user:read"));

            MyContextComposite composite = new MyContextComposite(userContext, rolesAndPermissions);

            MyContextResponse expectedResponse =
                    new MyContextResponse(
                            userId,
                            "test@example.com",
                            "Test User",
                            "tenant-123",
                            "Test Tenant",
                            "org-456",
                            "Test Organization",
                            List.of(new MyContextResponse.RoleInfo(null, "ADMIN")),
                            List.of("user:read"));

            given(myContextReadFacade.findMyContext(any(UserId.class))).willReturn(composite);
            given(myContextCompositeAssembler.toResponse(composite)).willReturn(expectedResponse);

            // when
            MyContextResponse result = sut.execute(userId);

            // then
            assertThat(result).isEqualTo(expectedResponse);
            then(myContextReadFacade).should().findMyContext(any(UserId.class));
            then(myContextCompositeAssembler).should().toResponse(composite);
        }

        @Test
        @DisplayName("실패: 사용자가 존재하지 않으면 UserNotFoundException 발생")
        void shouldThrowException_WhenUserNotExists() {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000001";

            given(myContextReadFacade.findMyContext(any(UserId.class)))
                    .willThrow(new UserNotFoundException(UserId.of(userId)));

            // when & then
            assertThatThrownBy(() -> sut.execute(userId)).isInstanceOf(UserNotFoundException.class);
            then(myContextCompositeAssembler).shouldHaveNoInteractions();
        }
    }
}
