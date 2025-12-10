package com.ryuqq.authhub.application.organization.service.command;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.organization.dto.command.DeleteOrganizationCommand;
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
 * DeleteOrganizationService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteOrganizationService 단위 테스트")
class DeleteOrganizationServiceTest {

    @Mock private OrganizationTransactionManager transactionManager;

    @Mock private OrganizationReadManager readManager;

    private Clock clock;

    private DeleteOrganizationService service;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-02T00:00:00Z"), ZoneOffset.UTC);
        service = new DeleteOrganizationService(transactionManager, readManager, clock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("ACTIVE 조직을 성공적으로 삭제한다")
        void shouldDeleteActiveOrganization() {
            // given
            Organization activeOrganization = OrganizationFixture.create();
            DeleteOrganizationCommand command =
                    new DeleteOrganizationCommand(OrganizationFixture.defaultUUID());

            Organization deletedOrganization = activeOrganization.delete(clock);

            given(readManager.findById(any(OrganizationId.class))).willReturn(activeOrganization);
            given(transactionManager.persist(any(Organization.class)))
                    .willReturn(deletedOrganization);

            // when & then
            assertThatCode(() -> service.execute(command)).doesNotThrowAnyException();
            verify(readManager).findById(any(OrganizationId.class));
            verify(transactionManager).persist(any(Organization.class));
        }

        @Test
        @DisplayName("INACTIVE 조직을 성공적으로 삭제한다")
        void shouldDeleteInactiveOrganization() {
            // given
            Organization inactiveOrganization = OrganizationFixture.createInactive();
            DeleteOrganizationCommand command =
                    new DeleteOrganizationCommand(OrganizationFixture.defaultUUID());

            Organization deletedOrganization = inactiveOrganization.delete(clock);

            given(readManager.findById(any(OrganizationId.class))).willReturn(inactiveOrganization);
            given(transactionManager.persist(any(Organization.class)))
                    .willReturn(deletedOrganization);

            // when & then
            assertThatCode(() -> service.execute(command)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("존재하지 않는 조직 삭제 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenOrganizationNotFound() {
            // given
            DeleteOrganizationCommand command =
                    new DeleteOrganizationCommand(OrganizationFixture.defaultUUID());
            given(readManager.findById(any(OrganizationId.class)))
                    .willThrow(new OrganizationNotFoundException(OrganizationFixture.defaultId()));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(OrganizationNotFoundException.class);
        }

        @Test
        @DisplayName("이미 삭제된 조직 삭제 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenAlreadyDeleted() {
            // given
            Organization deletedOrganization = OrganizationFixture.createDeleted();
            DeleteOrganizationCommand command =
                    new DeleteOrganizationCommand(OrganizationFixture.defaultUUID());

            given(readManager.findById(any(OrganizationId.class))).willReturn(deletedOrganization);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidOrganizationStateException.class);
        }
    }
}
