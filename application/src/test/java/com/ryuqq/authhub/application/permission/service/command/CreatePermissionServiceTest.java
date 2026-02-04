package com.ryuqq.authhub.application.permission.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.factory.PermissionCommandFactory;
import com.ryuqq.authhub.application.permission.fixture.PermissionCommandFixtures;
import com.ryuqq.authhub.application.permission.manager.PermissionCommandManager;
import com.ryuqq.authhub.application.permission.validator.PermissionValidator;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.exception.DuplicatePermissionKeyException;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreatePermissionService 단위 테스트
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
@DisplayName("CreatePermissionService 단위 테스트")
class CreatePermissionServiceTest {

    @Mock private PermissionValidator validator;

    @Mock private PermissionCommandFactory commandFactory;

    @Mock private PermissionCommandManager commandManager;

    private CreatePermissionService sut;

    @BeforeEach
    void setUp() {
        sut = new CreatePermissionService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → Validator → Manager 순서로 호출하고 ID 반환")
        void shouldOrchestrate_FactoryThenValidatorThenManager_AndReturnId() {
            // given
            CreatePermissionCommand command = PermissionCommandFixtures.createCommand();
            Permission permission = PermissionFixture.createNewCustomPermission();
            Long expectedId = PermissionFixture.defaultIdValue();

            given(commandFactory.create(command)).willReturn(permission);
            given(commandManager.persist(permission)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);

            then(commandFactory).should().create(command);
            then(validator)
                    .should()
                    .validateKeyNotDuplicated(
                            permission.getServiceId(), permission.permissionKeyValue());
            then(commandManager).should().persist(permission);
        }

        @Test
        @DisplayName("실패: 중복 권한 키일 경우 DuplicatePermissionKeyException 발생")
        void shouldThrowException_WhenKeyIsDuplicated() {
            // given
            CreatePermissionCommand command = PermissionCommandFixtures.createCommand();
            Permission permission = PermissionFixture.createNewCustomPermission();

            given(commandFactory.create(command)).willReturn(permission);
            willThrow(new DuplicatePermissionKeyException("user:read"))
                    .given(validator)
                    .validateKeyNotDuplicated(any(), any(String.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(DuplicatePermissionKeyException.class);

            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("검증 통과 후 Manager를 통해 영속화 수행")
        void shouldPersistPermission_ThroughManager() {
            // given
            CreatePermissionCommand command = PermissionCommandFixtures.createCommand();
            Permission permission = PermissionFixture.createNewCustomPermission();

            given(commandFactory.create(command)).willReturn(permission);
            given(commandManager.persist(permission)).willReturn(1L);

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persist(permission);
        }
    }
}
