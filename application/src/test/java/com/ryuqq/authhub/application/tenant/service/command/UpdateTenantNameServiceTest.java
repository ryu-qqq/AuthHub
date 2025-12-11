package com.ryuqq.authhub.application.tenant.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.manager.command.TenantTransactionManager;
import com.ryuqq.authhub.application.tenant.manager.query.TenantReadManager;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.exception.InvalidTenantStateException;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
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
 * UpdateTenantNameService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateTenantNameService 단위 테스트")
class UpdateTenantNameServiceTest {

    @Mock private TenantReadManager readManager;

    @Mock private TenantTransactionManager transactionManager;

    @Mock private TenantAssembler assembler;

    private Clock clock;

    private UpdateTenantNameService service;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-02T00:00:00Z"), ZoneOffset.UTC);
        service = new UpdateTenantNameService(readManager, transactionManager, assembler, clock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("테넌트 이름을 성공적으로 변경한다")
        void shouldUpdateTenantNameSuccessfully() {
            // given
            Tenant existingTenant = TenantFixture.create();
            UpdateTenantNameCommand command =
                    new UpdateTenantNameCommand(TenantFixture.defaultUUID(), "New Name");

            Tenant updatedTenant = existingTenant.changeName(TenantName.of("New Name"), clock);
            TenantResponse expectedResponse =
                    new TenantResponse(
                            updatedTenant.tenantIdValue(),
                            updatedTenant.nameValue(),
                            updatedTenant.statusValue(),
                            updatedTenant.createdAt(),
                            updatedTenant.updatedAt());

            given(readManager.findById(any(TenantId.class))).willReturn(existingTenant);
            given(readManager.existsByName(any(TenantName.class))).willReturn(false);
            given(transactionManager.persist(any(Tenant.class))).willReturn(updatedTenant);
            given(assembler.toResponse(updatedTenant)).willReturn(expectedResponse);

            // when
            TenantResponse response = service.execute(command);

            // then
            assertThat(response.name()).isEqualTo("New Name");
            verify(readManager).findById(any(TenantId.class));
            verify(transactionManager).persist(any(Tenant.class));
            verify(assembler).toResponse(any(Tenant.class));
        }

        @Test
        @DisplayName("같은 이름으로 변경 시 중복 검사를 건너뛴다")
        void shouldSkipDuplicateCheckWhenSameName() {
            // given
            Tenant existingTenant = TenantFixture.createWithName("Same Name");
            UpdateTenantNameCommand command =
                    new UpdateTenantNameCommand(TenantFixture.defaultUUID(), "Same Name");

            Tenant updatedTenant = existingTenant.changeName(TenantName.of("Same Name"), clock);
            TenantResponse expectedResponse =
                    new TenantResponse(
                            updatedTenant.tenantIdValue(),
                            updatedTenant.nameValue(),
                            updatedTenant.statusValue(),
                            updatedTenant.createdAt(),
                            updatedTenant.updatedAt());

            given(readManager.findById(any(TenantId.class))).willReturn(existingTenant);
            given(transactionManager.persist(any(Tenant.class))).willReturn(updatedTenant);
            given(assembler.toResponse(updatedTenant)).willReturn(expectedResponse);

            // when
            TenantResponse response = service.execute(command);

            // then
            assertThat(response.name()).isEqualTo("Same Name");
            verify(readManager, never()).existsByName(any(TenantName.class));
        }

        @Test
        @DisplayName("존재하지 않는 테넌트 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenTenantNotFound() {
            // given
            UpdateTenantNameCommand command =
                    new UpdateTenantNameCommand(TenantFixture.defaultUUID(), "New Name");
            given(readManager.findById(any(TenantId.class)))
                    .willThrow(new TenantNotFoundException(TenantFixture.defaultId()));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(TenantNotFoundException.class);
        }

        @Test
        @DisplayName("중복 이름 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDuplicateName() {
            // given
            Tenant existingTenant = TenantFixture.create();
            UpdateTenantNameCommand command =
                    new UpdateTenantNameCommand(TenantFixture.defaultUUID(), "Duplicate Name");

            given(readManager.findById(any(TenantId.class))).willReturn(existingTenant);
            given(readManager.existsByName(any(TenantName.class))).willReturn(true);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(DuplicateTenantNameException.class);
        }

        @Test
        @DisplayName("삭제된 테넌트 이름 변경 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenTenantIsDeleted() {
            // given
            Tenant deletedTenant = TenantFixture.createDeleted();
            UpdateTenantNameCommand command =
                    new UpdateTenantNameCommand(TenantFixture.defaultUUID(), "New Name");

            given(readManager.findById(any(TenantId.class))).willReturn(deletedTenant);
            given(readManager.existsByName(any(TenantName.class))).willReturn(false);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidTenantStateException.class);
        }
    }
}
