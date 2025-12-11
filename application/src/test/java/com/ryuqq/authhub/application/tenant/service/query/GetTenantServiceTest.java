package com.ryuqq.authhub.application.tenant.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.query.GetTenantQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.manager.query.TenantReadManager;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * GetTenantService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetTenantService 단위 테스트")
class GetTenantServiceTest {

    @Mock private TenantReadManager readManager;

    @Mock private TenantAssembler assembler;

    private GetTenantService service;

    @BeforeEach
    void setUp() {
        service = new GetTenantService(readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("테넌트를 성공적으로 조회한다")
        void shouldGetTenantSuccessfully() {
            // given
            UUID tenantUuid = TenantFixture.defaultUUID();
            GetTenantQuery query = new GetTenantQuery(tenantUuid);
            Tenant tenant = TenantFixture.create();
            TenantResponse expectedResponse =
                    new TenantResponse(
                            tenant.tenantIdValue(),
                            tenant.nameValue(),
                            tenant.statusValue(),
                            tenant.createdAt(),
                            tenant.updatedAt());

            given(readManager.findById(any(TenantId.class))).willReturn(tenant);
            given(assembler.toResponse(tenant)).willReturn(expectedResponse);

            // when
            TenantResponse response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(readManager).findById(any(TenantId.class));
            verify(assembler).toResponse(tenant);
        }

        @Test
        @DisplayName("존재하지 않는 테넌트 조회 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenTenantNotFound() {
            // given
            UUID tenantUuid = UUID.randomUUID();
            GetTenantQuery query = new GetTenantQuery(tenantUuid);
            given(readManager.findById(any(TenantId.class)))
                    .willThrow(new TenantNotFoundException(tenantUuid));

            // when & then
            assertThatThrownBy(() -> service.execute(query))
                    .isInstanceOf(TenantNotFoundException.class);
        }
    }
}
