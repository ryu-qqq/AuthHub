package com.ryuqq.authhub.application.user.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.user.dto.command.CreateUserWithRolesCommand;
import com.ryuqq.authhub.application.user.dto.response.CreateUserWithRolesResult;
import com.ryuqq.authhub.application.user.factory.CreateUserWithRolesCommandFactory;
import com.ryuqq.authhub.application.user.fixture.UserCommandFixtures;
import com.ryuqq.authhub.application.user.internal.CreateUserWithRolesBundle;
import com.ryuqq.authhub.application.user.internal.CreateUserWithRolesCoordinator;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
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
 * CreateUserWithRolesService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserWithRolesService 단위 테스트")
class CreateUserWithRolesServiceTest {

    @Mock private CreateUserWithRolesCommandFactory createUserWithRolesCommandFactory;
    @Mock private CreateUserWithRolesCoordinator coordinator;

    private CreateUserWithRolesService sut;

    @BeforeEach
    void setUp() {
        sut = new CreateUserWithRolesService(createUserWithRolesCommandFactory, coordinator);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory로 Bundle 생성 후 Coordinator에 위임하고 결과 반환")
        void shouldDelegateToCoordinator_AndReturnResult() {
            // given
            CreateUserWithRolesCommand command = UserCommandFixtures.createUserWithRolesCommand();
            User user = UserFixture.createNew();
            CreateUserWithRolesBundle bundle =
                    CreateUserWithRolesBundle.of(user, "SVC_DEFAULT", List.of("ADMIN"));
            String expectedUserId = UserFixture.defaultIdString();
            CreateUserWithRolesResult expectedResult =
                    CreateUserWithRolesResult.of(expectedUserId, 1);

            given(createUserWithRolesCommandFactory.create(command)).willReturn(bundle);
            given(coordinator.coordinate(bundle)).willReturn(expectedResult);

            // when
            CreateUserWithRolesResult result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedResult);
            assertThat(result.userId()).isEqualTo(expectedUserId);
            assertThat(result.assignedRoleCount()).isEqualTo(1);
            then(createUserWithRolesCommandFactory).should().create(command);
            then(coordinator).should().coordinate(bundle);
        }
    }
}
