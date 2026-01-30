package com.ryuqq.authhub.application.permissionendpoint.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.permissionendpoint.dto.command.CreatePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.factory.PermissionEndpointCommandFactory;
import com.ryuqq.authhub.application.permissionendpoint.fixture.PermissionEndpointCommandFixtures;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointCommandManager;
import com.ryuqq.authhub.application.permissionendpoint.validator.PermissionEndpointValidator;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.exception.DuplicatePermissionEndpointException;
import com.ryuqq.authhub.domain.permissionendpoint.fixture.PermissionEndpointFixture;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreatePermissionEndpointService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CreatePermissionEndpointService 단위 테스트")
class CreatePermissionEndpointServiceTest {

    @Mock private PermissionEndpointValidator validator;

    @Mock private PermissionEndpointCommandFactory commandFactory;

    @Mock private PermissionEndpointCommandManager commandManager;

    private CreatePermissionEndpointService sut;

    @BeforeEach
    void setUp() {
        sut = new CreatePermissionEndpointService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공: Validator → Factory → Manager 순서로 호출하고 ID 반환")
        void shouldOrchestrate_ValidatorThenFactoryThenManager_AndReturnId() {
            // given
            CreatePermissionEndpointCommand command =
                    PermissionEndpointCommandFixtures.createCommand();
            PermissionEndpoint permissionEndpoint = PermissionEndpointFixture.createNew();
            Long expectedId = PermissionEndpointFixture.defaultIdValue();

            given(commandFactory.create(command)).willReturn(permissionEndpoint);
            given(commandManager.persist(permissionEndpoint)).willReturn(expectedId);

            // when
            Long result = sut.create(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(validator).should().validatePermissionExists(any());
            then(validator)
                    .should()
                    .validateNoDuplicate(eq(command.urlPattern()), any(HttpMethod.class));
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(permissionEndpoint);
        }

        @Test
        @DisplayName("실패: URL 패턴+HTTP 메서드 중복 시 DuplicatePermissionEndpointException 발생")
        void shouldThrowException_WhenUrlPatternAndMethodDuplicated() {
            // given
            CreatePermissionEndpointCommand command =
                    PermissionEndpointCommandFixtures.createCommand();

            willThrow(new DuplicatePermissionEndpointException("/api/v1/users", "GET"))
                    .given(validator)
                    .validateNoDuplicate(any(String.class), any(HttpMethod.class));

            // when & then
            assertThatThrownBy(() -> sut.create(command))
                    .isInstanceOf(DuplicatePermissionEndpointException.class);
            then(commandFactory).should(never()).create(any());
            then(commandManager).should(never()).persist(any());
        }
    }
}
