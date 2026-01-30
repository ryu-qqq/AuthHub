package com.ryuqq.authhub.application.organization.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.factory.OrganizationCommandFactory;
import com.ryuqq.authhub.application.organization.fixture.OrganizationCommandFixtures;
import com.ryuqq.authhub.application.organization.manager.OrganizationCommandManager;
import com.ryuqq.authhub.application.organization.validator.OrganizationValidator;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.DuplicateOrganizationNameException;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
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
 * CreateOrganizationService 단위 테스트
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
@DisplayName("CreateOrganizationService 단위 테스트")
class CreateOrganizationServiceTest {

    @Mock private OrganizationValidator validator;

    @Mock private OrganizationCommandFactory commandFactory;

    @Mock private OrganizationCommandManager commandManager;

    private CreateOrganizationService sut;

    @BeforeEach
    void setUp() {
        sut = new CreateOrganizationService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Validator → Factory → Manager 순서로 호출하고 ID 반환")
        void shouldOrchestrate_ValidatorThenFactoryThenManager_AndReturnId() {
            // given
            CreateOrganizationCommand command = OrganizationCommandFixtures.createCommand();
            Organization organization = OrganizationFixture.createNew();
            String expectedId = OrganizationFixture.defaultIdString();

            given(commandFactory.create(command)).willReturn(organization);
            given(commandManager.persist(organization)).willReturn(expectedId);

            // when
            String result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);

            // 호출 순서 검증
            then(validator)
                    .should()
                    .validateNameNotDuplicated(any(TenantId.class), any(OrganizationName.class));
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(organization);
        }

        @Test
        @DisplayName("실패: 중복 이름일 경우 DuplicateOrganizationNameException 발생")
        void shouldThrowException_WhenNameIsDuplicated() {
            // given
            CreateOrganizationCommand command = OrganizationCommandFixtures.createCommand();
            TenantId tenantId = TenantId.of(command.tenantId());
            OrganizationName name = OrganizationName.of(command.name());

            willThrow(new DuplicateOrganizationNameException(tenantId, name))
                    .given(validator)
                    .validateNameNotDuplicated(any(TenantId.class), any(OrganizationName.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(DuplicateOrganizationNameException.class);

            // Factory와 Manager는 호출되지 않아야 함
            then(commandFactory).should(never()).create(any());
            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("검증 통과 후 Factory가 Domain 객체 생성")
        void shouldCreateDomainObject_WhenValidationPasses() {
            // given
            CreateOrganizationCommand command = OrganizationCommandFixtures.createCommand();
            Organization organization = OrganizationFixture.createNew();

            given(commandFactory.create(command)).willReturn(organization);
            given(commandManager.persist(organization)).willReturn("generated-id");

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().create(command);
        }

        @Test
        @DisplayName("Manager를 통해 영속화 수행")
        void shouldPersistOrganization_ThroughManager() {
            // given
            CreateOrganizationCommand command = OrganizationCommandFixtures.createCommand();
            Organization organization = OrganizationFixture.createNew();

            given(commandFactory.create(command)).willReturn(organization);
            given(commandManager.persist(organization)).willReturn("persisted-id");

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persist(organization);
        }
    }
}
