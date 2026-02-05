package com.ryuqq.authhub.application.permission.service.command;

import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.fixture.PermissionCommandFixtures;
import com.ryuqq.authhub.application.permission.internal.PermissionDeleteCoordinator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * DeletePermissionService 단위 테스트
 *
 * <p>Service는 Coordinator에 위임만 하므로 위임 호출 검증 위주.
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("DeletePermissionService 단위 테스트")
class DeletePermissionServiceTest {

    @Mock private PermissionDeleteCoordinator deleteCoordinator;

    private DeletePermissionService sut;

    @BeforeEach
    void setUp() {
        sut = new DeletePermissionService(deleteCoordinator);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Coordinator.execute에 Command 위임")
        void shouldDelegateToCoordinator() {
            // given
            DeletePermissionCommand command = PermissionCommandFixtures.deleteCommand();

            // when
            sut.execute(command);

            // then
            then(deleteCoordinator).should().execute(command);
        }
    }
}
