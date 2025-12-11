package com.ryuqq.authhub.application.organization.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.query.GetOrganizationQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * GetOrganizationService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetOrganizationService 단위 테스트")
class GetOrganizationServiceTest {

    @Mock private OrganizationReadManager readManager;

    @Mock private OrganizationAssembler assembler;

    private GetOrganizationService service;

    @BeforeEach
    void setUp() {
        service = new GetOrganizationService(readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("조직을 성공적으로 조회한다")
        void shouldGetOrganizationSuccessfully() {
            // given
            Organization organization = OrganizationFixture.create();
            GetOrganizationQuery query =
                    new GetOrganizationQuery(OrganizationFixture.defaultUUID());
            OrganizationResponse expectedResponse =
                    new OrganizationResponse(
                            organization.organizationIdValue(),
                            organization.tenantIdValue(),
                            organization.nameValue(),
                            organization.statusValue(),
                            organization.createdAt(),
                            organization.updatedAt());

            given(readManager.findById(any(OrganizationId.class))).willReturn(organization);
            given(assembler.toResponse(organization)).willReturn(expectedResponse);

            // when
            OrganizationResponse response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.organizationId()).isEqualTo(organization.organizationIdValue());
            assertThat(response.name()).isEqualTo(organization.nameValue());
            verify(readManager).findById(any(OrganizationId.class));
            verify(assembler).toResponse(organization);
        }

        @Test
        @DisplayName("존재하지 않는 조직 조회 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenOrganizationNotFound() {
            // given
            GetOrganizationQuery query =
                    new GetOrganizationQuery(OrganizationFixture.defaultUUID());
            given(readManager.findById(any(OrganizationId.class)))
                    .willThrow(new OrganizationNotFoundException(OrganizationFixture.defaultId()));

            // when & then
            assertThatThrownBy(() -> service.execute(query))
                    .isInstanceOf(OrganizationNotFoundException.class);
        }

        @Test
        @DisplayName("삭제된 조직도 조회할 수 있다")
        void shouldGetDeletedOrganization() {
            // given
            Organization deletedOrganization = OrganizationFixture.createDeleted();
            GetOrganizationQuery query =
                    new GetOrganizationQuery(OrganizationFixture.defaultUUID());
            OrganizationResponse expectedResponse =
                    new OrganizationResponse(
                            deletedOrganization.organizationIdValue(),
                            deletedOrganization.tenantIdValue(),
                            deletedOrganization.nameValue(),
                            deletedOrganization.statusValue(),
                            deletedOrganization.createdAt(),
                            deletedOrganization.updatedAt());

            given(readManager.findById(any(OrganizationId.class))).willReturn(deletedOrganization);
            given(assembler.toResponse(deletedOrganization)).willReturn(expectedResponse);

            // when
            OrganizationResponse response = service.execute(query);

            // then
            assertThat(response.status()).isEqualTo("DELETED");
        }
    }
}
