package com.ryuqq.authhub.application.organization.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.organization.dto.query.GetOrganizationQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationDetailResponse;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationAdminQueryPort;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
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
 * GetOrganizationDetailService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetOrganizationDetailService 단위 테스트")
class GetOrganizationDetailServiceTest {

    @Mock private OrganizationAdminQueryPort adminQueryPort;

    private GetOrganizationDetailService service;

    private static final UUID ORGANIZATION_UUID = UUID.randomUUID();
    private static final UUID TENANT_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new GetOrganizationDetailService(adminQueryPort);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("조직 상세 정보를 성공적으로 조회한다")
        void shouldGetOrganizationDetailSuccessfully() {
            // given
            GetOrganizationQuery query = new GetOrganizationQuery(ORGANIZATION_UUID);
            OrganizationDetailResponse expectedResponse =
                    new OrganizationDetailResponse(
                            ORGANIZATION_UUID,
                            TENANT_UUID,
                            "테스트 테넌트",
                            "테스트 조직",
                            "ACTIVE",
                            List.of(),
                            5,
                            Instant.now(),
                            Instant.now());

            given(adminQueryPort.findOrganizationDetail(OrganizationId.of(ORGANIZATION_UUID)))
                    .willReturn(Optional.of(expectedResponse));

            // when
            OrganizationDetailResponse response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(adminQueryPort).findOrganizationDetail(OrganizationId.of(ORGANIZATION_UUID));
        }

        @Test
        @DisplayName("조직이 존재하지 않으면 예외를 발생시킨다")
        void shouldThrowExceptionWhenOrganizationNotFound() {
            // given
            UUID nonExistingOrganizationId = UUID.randomUUID();
            GetOrganizationQuery query = new GetOrganizationQuery(nonExistingOrganizationId);

            given(
                            adminQueryPort.findOrganizationDetail(
                                    OrganizationId.of(nonExistingOrganizationId)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> service.execute(query))
                    .isInstanceOf(OrganizationNotFoundException.class);
        }
    }
}
