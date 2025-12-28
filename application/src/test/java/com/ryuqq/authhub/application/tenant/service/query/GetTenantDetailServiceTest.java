package com.ryuqq.authhub.application.tenant.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.tenant.dto.query.GetTenantQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantDetailResponse;
import com.ryuqq.authhub.application.tenant.port.out.query.TenantAdminQueryPort;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
 * GetTenantDetailService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetTenantDetailService 단위 테스트")
class GetTenantDetailServiceTest {

    @Mock private TenantAdminQueryPort adminQueryPort;

    private GetTenantDetailService service;

    private static final UUID TENANT_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new GetTenantDetailService(adminQueryPort);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("테넌트 상세 정보를 성공적으로 조회한다")
        void shouldGetTenantDetailSuccessfully() {
            // given
            GetTenantQuery query = GetTenantQuery.of(TENANT_UUID);
            TenantDetailResponse expectedResponse =
                    new TenantDetailResponse(
                            TENANT_UUID,
                            "테스트 테넌트",
                            "ACTIVE",
                            List.of(),
                            3,
                            Instant.now(),
                            Instant.now());

            given(adminQueryPort.findTenantDetail(TenantId.of(TENANT_UUID)))
                    .willReturn(Optional.of(expectedResponse));

            // when
            TenantDetailResponse response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(adminQueryPort).findTenantDetail(TenantId.of(TENANT_UUID));
        }

        @Test
        @DisplayName("테넌트가 존재하지 않으면 예외를 발생시킨다")
        void shouldThrowExceptionWhenTenantNotFound() {
            // given
            UUID nonExistingTenantId = UUID.randomUUID();
            GetTenantQuery query = GetTenantQuery.of(nonExistingTenantId);

            given(adminQueryPort.findTenantDetail(TenantId.of(nonExistingTenantId)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> service.execute(query))
                    .isInstanceOf(TenantNotFoundException.class);
        }
    }
}
