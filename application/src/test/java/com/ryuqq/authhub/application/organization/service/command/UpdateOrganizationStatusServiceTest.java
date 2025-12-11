package com.ryuqq.authhub.application.organization.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.manager.command.OrganizationTransactionManager;
import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.InvalidOrganizationStateException;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
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
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateOrganizationStatusService 단위 테스트")
class UpdateOrganizationStatusServiceTest {

    @Mock private OrganizationReadManager readManager;

    @Mock private OrganizationTransactionManager transactionManager;

    @Mock private OrganizationAssembler assembler;

    private Clock clock;

    private UpdateOrganizationStatusService service;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-02T00:00:00Z"), ZoneOffset.UTC);
        service =
                new UpdateOrganizationStatusService(
                        readManager, transactionManager, assembler, clock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("ACTIVE 조직을 INACTIVE로 변경한다")
        void shouldDeactivateOrganization() {
            // given
            Organization activeOrganization = OrganizationFixture.create();
            UpdateOrganizationStatusCommand command =
                    new UpdateOrganizationStatusCommand(
                            OrganizationFixture.defaultUUID(), "INACTIVE");

            Organization deactivatedOrganization = activeOrganization.deactivate(clock);
            OrganizationResponse expectedResponse =
                    new OrganizationResponse(
                            deactivatedOrganization.organizationIdValue(),
                            deactivatedOrganization.tenantIdValue(),
                            deactivatedOrganization.nameValue(),
                            deactivatedOrganization.statusValue(),
                            deactivatedOrganization.createdAt(),
                            deactivatedOrganization.updatedAt());

            given(readManager.findById(any(OrganizationId.class))).willReturn(activeOrganization);
            given(transactionManager.persist(any(Organization.class)))
                    .willReturn(deactivatedOrganization);
            given(assembler.toResponse(deactivatedOrganization)).willReturn(expectedResponse);

            // when
            OrganizationResponse response = service.execute(command);

            // then
            assertThat(response.status()).isEqualTo("INACTIVE");
            verify(readManager).findById(any(OrganizationId.class));
            verify(transactionManager).persist(any(Organization.class));
            verify(assembler).toResponse(any(Organization.class));
        }

        @Test
        @DisplayName("INACTIVE 조직을 ACTIVE로 변경한다")
        void shouldActivateOrganization() {
            // given
            Organization inactiveOrganization = OrganizationFixture.createInactive();
            UpdateOrganizationStatusCommand command =
                    new UpdateOrganizationStatusCommand(
                            OrganizationFixture.defaultUUID(), "ACTIVE");

            Organization activatedOrganization = inactiveOrganization.activate(clock);
            OrganizationResponse expectedResponse =
                    new OrganizationResponse(
                            activatedOrganization.organizationIdValue(),
                            activatedOrganization.tenantIdValue(),
                            activatedOrganization.nameValue(),
                            activatedOrganization.statusValue(),
                            activatedOrganization.createdAt(),
                            activatedOrganization.updatedAt());

            given(readManager.findById(any(OrganizationId.class))).willReturn(inactiveOrganization);
            given(transactionManager.persist(any(Organization.class)))
                    .willReturn(activatedOrganization);
            given(assembler.toResponse(activatedOrganization)).willReturn(expectedResponse);

            // when
            OrganizationResponse response = service.execute(command);

            // then
            assertThat(response.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("존재하지 않는 조직 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenOrganizationNotFound() {
            // given
            UpdateOrganizationStatusCommand command =
                    new UpdateOrganizationStatusCommand(
                            OrganizationFixture.defaultUUID(), "INACTIVE");
            given(readManager.findById(any(OrganizationId.class)))
                    .willThrow(new OrganizationNotFoundException(OrganizationFixture.defaultId()));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(OrganizationNotFoundException.class);
        }

        @Test
        @DisplayName("삭제된 조직 상태 변경 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenOrganizationIsDeleted() {
            // given
            Organization deletedOrganization = OrganizationFixture.createDeleted();
            UpdateOrganizationStatusCommand command =
                    new UpdateOrganizationStatusCommand(
                            OrganizationFixture.defaultUUID(), "ACTIVE");

            given(readManager.findById(any(OrganizationId.class))).willReturn(deletedOrganization);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidOrganizationStateException.class);
        }

        @Test
        @DisplayName("DELETED 상태로 변경 시도 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenTargetStatusIsDeleted() {
            // given
            Organization activeOrganization = OrganizationFixture.create();
            UpdateOrganizationStatusCommand command =
                    new UpdateOrganizationStatusCommand(
                            OrganizationFixture.defaultUUID(), "DELETED");

            given(readManager.findById(any(OrganizationId.class))).willReturn(activeOrganization);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("DeleteOrganizationUseCase를 사용하세요");
        }

        @Test
        @DisplayName("이미 ACTIVE 상태인 조직을 ACTIVE로 변경 시도 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenAlreadyActive() {
            // given
            Organization activeOrganization = OrganizationFixture.create();
            UpdateOrganizationStatusCommand command =
                    new UpdateOrganizationStatusCommand(
                            OrganizationFixture.defaultUUID(), "ACTIVE");

            given(readManager.findById(any(OrganizationId.class))).willReturn(activeOrganization);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidOrganizationStateException.class);
        }
    }
}
