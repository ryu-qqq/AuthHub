package com.ryuqq.authhub.application.tenant.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.response.TenantConfigResult;
import com.ryuqq.authhub.application.tenant.manager.TenantReadManager;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
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
 * GetTenantConfigService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetTenantConfigService 단위 테스트")
class GetTenantConfigServiceTest {

    @Mock private TenantReadManager readManager;

    @Mock private TenantAssembler assembler;

    private GetTenantConfigService sut;

    @BeforeEach
    void setUp() {
        sut = new GetTenantConfigService(readManager, assembler);
    }

    @Nested
    @DisplayName("getByTenantId 메서드")
    class GetByTenantId {

        @Test
        @DisplayName("성공: ReadManager → Assembler 순서로 호출하고 TenantConfigResult 반환")
        void shouldReturnConfig_WhenTenantExists() {
            // given
            String tenantId = TenantFixture.defaultIdString();
            Tenant tenant = TenantFixture.create();
            TenantConfigResult expected =
                    new TenantConfigResult(
                            tenant.tenantIdValue(),
                            tenant.nameValue(),
                            tenant.statusValue(),
                            tenant.isActive());

            given(readManager.findById(TenantId.of(tenantId))).willReturn(tenant);
            given(assembler.toConfigResult(tenant)).willReturn(expected);

            // when
            TenantConfigResult result = sut.getByTenantId(tenantId);

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().findById(TenantId.of(tenantId));
            then(assembler).should().toConfigResult(tenant);
        }

        @Test
        @DisplayName("실패: 테넌트가 없으면 TenantNotFoundException 발생")
        void shouldThrowException_WhenTenantNotFound() {
            // given
            String tenantId = "non-existent-tenant-id";
            given(readManager.findById(TenantId.of(tenantId)))
                    .willThrow(new TenantNotFoundException(TenantId.of(tenantId)));

            // when & then
            assertThatThrownBy(() -> sut.getByTenantId(tenantId))
                    .isInstanceOf(TenantNotFoundException.class);
            then(assembler).shouldHaveNoInteractions();
        }
    }
}
