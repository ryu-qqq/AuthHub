package com.ryuqq.authhub.application.role.service.command;

import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.fixture.RoleCommandFixtures;
import com.ryuqq.authhub.application.role.internal.RoleDeleteCoordinator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * DeleteRoleService 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Service는 Coordinator 위임만 담당 → coordinator.execute 호출 검증
 *   <li>예외 전파 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteRoleService 단위 테스트")
class DeleteRoleServiceTest {

    @Mock private RoleDeleteCoordinator deleteCoordinator;

    private DeleteRoleService sut;

    @BeforeEach
    void setUp() {
        sut = new DeleteRoleService(deleteCoordinator);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Coordinator.execute에 command 전달")
        void shouldDelegateToCoordinator() {
            // given
            DeleteRoleCommand command = RoleCommandFixtures.deleteCommand();

            // when
            sut.execute(command);

            // then
            then(deleteCoordinator).should().execute(command);
        }
    }
}
