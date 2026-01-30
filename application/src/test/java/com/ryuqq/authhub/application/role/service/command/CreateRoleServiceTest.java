package com.ryuqq.authhub.application.role.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.factory.RoleCommandFactory;
import com.ryuqq.authhub.application.role.fixture.RoleCommandFixtures;
import com.ryuqq.authhub.application.role.manager.RoleCommandManager;
import com.ryuqq.authhub.application.role.validator.RoleValidator;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.exception.DuplicateRoleNameException;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateRoleService 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Service는 오케스트레이션만 담당 → 협력 객체 호출 순서/조건 검증
 *   <li>비즈니스 로직은 Domain/Validator에서 테스트
 *   <li>BDDMockito 스타일로 Given-When-Then 구조 명확화
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateRoleService 단위 테스트")
class CreateRoleServiceTest {

    @Mock private RoleValidator validator;

    @Mock private RoleCommandFactory commandFactory;

    @Mock private RoleCommandManager commandManager;

    private CreateRoleService sut;

    @BeforeEach
    void setUp() {
        sut = new CreateRoleService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Validator → Factory → Manager 순서로 호출하고 ID 반환")
        void shouldOrchestrate_ValidatorThenFactoryThenManager_AndReturnId() {
            // given
            CreateRoleCommand command = RoleCommandFixtures.createCommand();
            Role role = RoleFixture.createNewCustomRole();
            Long expectedId = RoleFixture.defaultIdValue();

            given(commandFactory.create(command)).willReturn(role);
            given(commandManager.persist(role)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);

            then(validator)
                    .should()
                    .validateNameNotDuplicated(any(TenantId.class), any(RoleName.class));
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(role);
        }

        @Test
        @DisplayName("실패: 중복 이름일 경우 DuplicateRoleNameException 발생")
        void shouldThrowException_WhenNameIsDuplicated() {
            // given
            CreateRoleCommand command = RoleCommandFixtures.createCommand();
            TenantId tenantId = TenantId.fromNullable(command.tenantId());
            RoleName name = RoleName.of(command.name());

            willThrow(new DuplicateRoleNameException(name))
                    .given(validator)
                    .validateNameNotDuplicated(any(TenantId.class), any(RoleName.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(DuplicateRoleNameException.class);

            then(commandFactory).should(never()).create(any());
            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("검증 통과 후 Manager를 통해 영속화 수행")
        void shouldPersistRole_ThroughManager() {
            // given
            CreateRoleCommand command = RoleCommandFixtures.createCommand();
            Role role = RoleFixture.createNewCustomRole();

            given(commandFactory.create(command)).willReturn(role);
            given(commandManager.persist(role)).willReturn(1L);

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persist(role);
        }
    }
}
