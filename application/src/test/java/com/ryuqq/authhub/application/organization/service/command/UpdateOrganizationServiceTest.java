package com.ryuqq.authhub.application.organization.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.manager.command.OrganizationTransactionManager;
import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.DuplicateOrganizationNameException;
import com.ryuqq.authhub.domain.organization.exception.InvalidOrganizationStateException;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
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
 * UpdateOrganizationService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateOrganizationService 단위 테스트")
class UpdateOrganizationServiceTest {

    @Mock private OrganizationTransactionManager transactionManager;

    @Mock private OrganizationReadManager readManager;

    @Mock private OrganizationAssembler assembler;

    private Clock clock;

    private UpdateOrganizationService service;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-02T00:00:00Z"), ZoneOffset.UTC);
        service = new UpdateOrganizationService(transactionManager, readManager, assembler, clock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("조직 이름을 성공적으로 변경한다")
        void shouldUpdateOrganizationNameSuccessfully() {
            // given
            Organization organization = OrganizationFixture.create();
            UpdateOrganizationCommand command =
                    new UpdateOrganizationCommand(
                            OrganizationFixture.defaultUUID(), "Updated Name");

            Organization updatedOrganization =
                    organization.changeName(OrganizationName.of("Updated Name"), clock);
            OrganizationResponse expectedResponse =
                    new OrganizationResponse(
                            updatedOrganization.organizationIdValue(),
                            updatedOrganization.tenantIdValue(),
                            updatedOrganization.nameValue(),
                            updatedOrganization.statusValue(),
                            updatedOrganization.createdAt(),
                            updatedOrganization.updatedAt());

            given(readManager.findById(any(OrganizationId.class))).willReturn(organization);
            given(
                            readManager.existsByTenantIdAndName(
                                    any(TenantId.class), any(OrganizationName.class)))
                    .willReturn(false);
            given(transactionManager.persist(any(Organization.class)))
                    .willReturn(updatedOrganization);
            given(assembler.toResponse(updatedOrganization)).willReturn(expectedResponse);

            // when
            OrganizationResponse response = service.execute(command);

            // then
            assertThat(response.name()).isEqualTo("Updated Name");
            verify(readManager).findById(any(OrganizationId.class));
            verify(transactionManager).persist(any(Organization.class));
            verify(assembler).toResponse(any(Organization.class));
        }

        @Test
        @DisplayName("동일한 이름으로 변경 시 중복 검사를 하지 않는다")
        void shouldSkipDuplicateCheckWhenSameName() {
            // given
            Organization organization = OrganizationFixture.createWithName("Same Name");
            UpdateOrganizationCommand command =
                    new UpdateOrganizationCommand(OrganizationFixture.defaultUUID(), "Same Name");

            OrganizationResponse expectedResponse =
                    new OrganizationResponse(
                            organization.organizationIdValue(),
                            organization.tenantIdValue(),
                            organization.nameValue(),
                            organization.statusValue(),
                            organization.createdAt(),
                            organization.updatedAt());

            given(readManager.findById(any(OrganizationId.class))).willReturn(organization);
            given(transactionManager.persist(any(Organization.class))).willReturn(organization);
            given(assembler.toResponse(organization)).willReturn(expectedResponse);

            // when
            OrganizationResponse response = service.execute(command);

            // then
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 조직 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenOrganizationNotFound() {
            // given
            UpdateOrganizationCommand command =
                    new UpdateOrganizationCommand(OrganizationFixture.defaultUUID(), "New Name");
            given(readManager.findById(any(OrganizationId.class)))
                    .willThrow(new OrganizationNotFoundException(OrganizationFixture.defaultId()));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(OrganizationNotFoundException.class);
        }

        @Test
        @DisplayName("테넌트 내 중복 이름 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDuplicateName() {
            // given
            Organization organization = OrganizationFixture.create();
            UpdateOrganizationCommand command =
                    new UpdateOrganizationCommand(
                            OrganizationFixture.defaultUUID(), "Duplicate Name");

            given(readManager.findById(any(OrganizationId.class))).willReturn(organization);
            given(
                            readManager.existsByTenantIdAndName(
                                    any(TenantId.class), any(OrganizationName.class)))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(DuplicateOrganizationNameException.class);
        }

        @Test
        @DisplayName("삭제된 조직 수정 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenOrganizationIsDeleted() {
            // given
            Organization deletedOrganization = OrganizationFixture.createDeleted();
            UpdateOrganizationCommand command =
                    new UpdateOrganizationCommand(OrganizationFixture.defaultUUID(), "New Name");

            given(readManager.findById(any(OrganizationId.class))).willReturn(deletedOrganization);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidOrganizationStateException.class);
        }
    }
}
