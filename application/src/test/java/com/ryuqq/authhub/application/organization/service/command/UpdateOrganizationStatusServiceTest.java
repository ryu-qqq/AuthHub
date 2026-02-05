package com.ryuqq.authhub.application.organization.service.command;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;
import com.ryuqq.authhub.application.organization.factory.OrganizationCommandFactory;
import com.ryuqq.authhub.application.organization.fixture.OrganizationCommandFixtures;
import com.ryuqq.authhub.application.organization.manager.OrganizationCommandManager;
import com.ryuqq.authhub.application.organization.validator.OrganizationValidator;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
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
 * UpdateOrganizationStatusService 단위 테스트
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
@DisplayName("UpdateOrganizationStatusService 단위 테스트")
class UpdateOrganizationStatusServiceTest {

    @Mock private OrganizationValidator validator;

    @Mock private OrganizationCommandFactory commandFactory;

    @Mock private OrganizationCommandManager commandManager;

    private UpdateOrganizationStatusService sut;

    private static final Instant FIXED_TIME = OrganizationFixture.fixedTime();

    @BeforeEach
    void setUp() {
        sut = new UpdateOrganizationStatusService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → Validator → Domain → Manager 순서로 호출")
        void shouldOrchestrate_FactoryThenValidatorThenDomainThenManager() {
            // given
            UpdateOrganizationStatusCommand command =
                    OrganizationCommandFixtures.deactivateCommand();
            OrganizationId id = OrganizationId.of(command.organizationId());
            StatusChangeContext<OrganizationId> context = new StatusChangeContext<>(id, FIXED_TIME);
            Organization organization = OrganizationFixture.create();

            given(commandFactory.createStatusChangeContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(organization);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createStatusChangeContext(command);
            then(validator).should().findExistingOrThrow(context.id());
            then(commandManager).should().persist(organization);
        }

        @Test
        @DisplayName("실패: Organization이 존재하지 않으면 OrganizationNotFoundException 발생")
        void shouldThrowException_WhenOrganizationNotExists() {
            // given
            UpdateOrganizationStatusCommand command =
                    OrganizationCommandFixtures.deactivateCommand();
            OrganizationId id = OrganizationId.of(command.organizationId());
            StatusChangeContext<OrganizationId> context = new StatusChangeContext<>(id, FIXED_TIME);

            given(commandFactory.createStatusChangeContext(command)).willReturn(context);
            willThrow(new OrganizationNotFoundException(id))
                    .given(validator)
                    .findExistingOrThrow(any(OrganizationId.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(OrganizationNotFoundException.class);

            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("실패: 잘못된 status 문자열이면 IllegalArgumentException 발생")
        void shouldThrowException_WhenStatusIsInvalid() {
            // given
            UpdateOrganizationStatusCommand command =
                    OrganizationCommandFixtures.statusCommand(
                            OrganizationCommandFixtures.defaultOrganizationId(), "INVALID");
            OrganizationId id = OrganizationId.of(command.organizationId());
            StatusChangeContext<OrganizationId> context = new StatusChangeContext<>(id, FIXED_TIME);

            given(commandFactory.createStatusChangeContext(command)).willReturn(context);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(IllegalArgumentException.class);

            then(validator).should(never()).findExistingOrThrow(any());
            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("Domain에서 상태 변경 메서드 호출")
        void shouldInvokeDomainChangeStatusMethod() {
            // given
            UpdateOrganizationStatusCommand command =
                    OrganizationCommandFixtures.deactivateCommand();
            OrganizationId id = OrganizationId.of(command.organizationId());
            StatusChangeContext<OrganizationId> context = new StatusChangeContext<>(id, FIXED_TIME);
            Organization organization = OrganizationFixture.create();

            given(commandFactory.createStatusChangeContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(organization);

            // when
            sut.execute(command);

            // then
            assertThatCode(
                            () ->
                                    organization.changeStatus(
                                            com.ryuqq.authhub.domain.organization.vo
                                                    .OrganizationStatus.INACTIVE,
                                            context.changedAt()))
                    .doesNotThrowAnyException();
        }
    }
}
