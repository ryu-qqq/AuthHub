package com.ryuqq.authhub.application.organization.service.command;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationNameCommand;
import com.ryuqq.authhub.application.organization.factory.OrganizationCommandFactory;
import com.ryuqq.authhub.application.organization.fixture.OrganizationCommandFixtures;
import com.ryuqq.authhub.application.organization.manager.OrganizationCommandManager;
import com.ryuqq.authhub.application.organization.validator.OrganizationValidator;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.DuplicateOrganizationNameException;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UpdateOrganizationNameService 단위 테스트
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
@DisplayName("UpdateOrganizationNameService 단위 테스트")
class UpdateOrganizationNameServiceTest {

    @Mock private OrganizationValidator validator;

    @Mock private OrganizationCommandFactory commandFactory;

    @Mock private OrganizationCommandManager commandManager;

    private UpdateOrganizationNameService sut;

    private static final Instant FIXED_TIME = OrganizationFixture.fixedTime();

    @BeforeEach
    void setUp() {
        sut = new UpdateOrganizationNameService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → Validator → Domain → Manager 순서로 호출")
        void shouldOrchestrate_FactoryThenValidatorThenDomainThenManager() {
            // given
            UpdateOrganizationNameCommand command = OrganizationCommandFixtures.updateNameCommand();
            OrganizationId id = OrganizationId.of(command.organizationId());
            OrganizationName newName = OrganizationName.of(command.name());
            UpdateContext<OrganizationId, OrganizationName> context =
                    new UpdateContext<>(id, newName, FIXED_TIME);
            Organization organization = OrganizationFixture.create();

            given(commandFactory.createNameUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(organization);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createNameUpdateContext(command);
            then(validator).should().findExistingOrThrow(context.id());
            then(validator)
                    .should()
                    .validateNameNotDuplicatedExcluding(context.updateData(), organization);
            then(commandManager).should().persist(organization);
        }

        @Test
        @DisplayName("실패: Organization이 존재하지 않으면 OrganizationNotFoundException 발생")
        void shouldThrowException_WhenOrganizationNotExists() {
            // given
            UpdateOrganizationNameCommand command = OrganizationCommandFixtures.updateNameCommand();
            OrganizationId id = OrganizationId.of(command.organizationId());
            OrganizationName newName = OrganizationName.of(command.name());
            UpdateContext<OrganizationId, OrganizationName> context =
                    new UpdateContext<>(id, newName, FIXED_TIME);

            given(commandFactory.createNameUpdateContext(command)).willReturn(context);
            willThrow(new OrganizationNotFoundException(id))
                    .given(validator)
                    .findExistingOrThrow(any(OrganizationId.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(OrganizationNotFoundException.class);

            then(validator).should(never()).validateNameNotDuplicatedExcluding(any(), any());
            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("실패: 중복 이름이면 DuplicateOrganizationNameException 발생")
        void shouldThrowException_WhenNameIsDuplicated() {
            // given
            UpdateOrganizationNameCommand command = OrganizationCommandFixtures.updateNameCommand();
            OrganizationId id = OrganizationId.of(command.organizationId());
            OrganizationName newName = OrganizationName.of(command.name());
            UpdateContext<OrganizationId, OrganizationName> context =
                    new UpdateContext<>(id, newName, FIXED_TIME);
            Organization organization = OrganizationFixture.create();

            given(commandFactory.createNameUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(organization);
            willThrow(new DuplicateOrganizationNameException(organization.getTenantId(), newName))
                    .given(validator)
                    .validateNameNotDuplicatedExcluding(
                            any(OrganizationName.class), any(Organization.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(DuplicateOrganizationNameException.class);

            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("Domain에서 이름 변경 메서드 호출")
        void shouldInvokeDomainChangeNameMethod() {
            // given
            UpdateOrganizationNameCommand command = OrganizationCommandFixtures.updateNameCommand();
            OrganizationId id = OrganizationId.of(command.organizationId());
            OrganizationName newName = OrganizationName.of("Updated Name");
            UpdateContext<OrganizationId, OrganizationName> context =
                    new UpdateContext<>(id, newName, FIXED_TIME);
            Organization organization = OrganizationFixture.create();

            given(commandFactory.createNameUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(organization);

            // when
            sut.execute(command);

            // then
            assertThatCode(() -> organization.changeName(newName, FIXED_TIME))
                    .doesNotThrowAnyException();
        }
    }
}
