package com.ryuqq.authhub.application.tenant.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.factory.TenantCommandFactory;
import com.ryuqq.authhub.application.tenant.fixture.TenantCommandFixtures;
import com.ryuqq.authhub.application.tenant.manager.TenantCommandManager;
import com.ryuqq.authhub.application.tenant.validator.TenantValidator;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateTenantService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateTenantService 단위 테스트")
class CreateTenantServiceTest {

    @Mock private TenantValidator validator;

    @Mock private TenantCommandFactory commandFactory;

    @Mock private TenantCommandManager commandManager;

    private CreateTenantService sut;

    @BeforeEach
    void setUp() {
        sut = new CreateTenantService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Validator → Factory → Manager 순서로 호출하고 ID 반환")
        void shouldOrchestrate_ValidatorThenFactoryThenManager_AndReturnId() {
            // given
            CreateTenantCommand command = TenantCommandFixtures.createCommand();
            Tenant tenant = TenantFixture.createNew();
            String expectedId = TenantFixture.defaultIdString();

            given(commandFactory.create(command)).willReturn(tenant);
            given(commandManager.persist(tenant)).willReturn(expectedId);

            // when
            String result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(validator).should().validateNameNotDuplicated(any(TenantName.class));
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(tenant);
        }

        @Test
        @DisplayName("실패: 중복 이름일 경우 DuplicateTenantNameException 발생")
        void shouldThrowException_WhenNameIsDuplicated() {
            // given
            CreateTenantCommand command = TenantCommandFixtures.createCommand();
            TenantName name = TenantName.of(command.name());

            willThrow(new DuplicateTenantNameException(name))
                    .given(validator)
                    .validateNameNotDuplicated(any(TenantName.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(DuplicateTenantNameException.class);
            then(commandFactory).should(never()).create(any());
            then(commandManager).should(never()).persist(any());
        }
    }
}
