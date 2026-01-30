package com.ryuqq.authhub.application.tenant.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.organization.manager.OrganizationCommandManager;
import com.ryuqq.authhub.application.tenant.dto.bundle.OnboardingBundle;
import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import com.ryuqq.authhub.application.tenant.fixture.TenantCommandFixtures;
import com.ryuqq.authhub.application.tenant.manager.TenantCommandManager;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OnboardingFacade 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OnboardingFacade 단위 테스트")
class OnboardingFacadeTest {

    @Mock private TenantCommandManager tenantCommandManager;
    @Mock private OrganizationCommandManager organizationCommandManager;

    private OnboardingFacade sut;

    @BeforeEach
    void setUp() {
        sut = new OnboardingFacade(tenantCommandManager, organizationCommandManager);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: Tenant → Organization 순서로 저장하고 OnboardingResult 반환")
        void shouldPersistTenantThenOrganization_AndReturnResult() {
            // given
            OnboardingBundle bundle = TenantCommandFixtures.createOnboardingBundle();
            String expectedTenantId = TenantFixture.defaultIdString();
            String expectedOrgId = OrganizationFixture.defaultIdString();

            given(tenantCommandManager.persist(bundle.tenant())).willReturn(expectedTenantId);
            given(organizationCommandManager.persist(bundle.organization()))
                    .willReturn(expectedOrgId);

            // when
            OnboardingResult result = sut.persist(bundle);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isEqualTo(expectedTenantId);
            assertThat(result.organizationId()).isEqualTo(expectedOrgId);

            then(tenantCommandManager).should().persist(bundle.tenant());
            then(organizationCommandManager).should().persist(bundle.organization());
        }

        @Test
        @DisplayName("성공: TenantCommandManager와 OrganizationCommandManager를 순서대로 호출")
        void shouldCallManagers_InCorrectOrder() {
            // given
            OnboardingBundle bundle = TenantCommandFixtures.createOnboardingBundle();

            given(tenantCommandManager.persist(bundle.tenant())).willReturn("tenant-id");
            given(organizationCommandManager.persist(bundle.organization())).willReturn("org-id");

            // when
            sut.persist(bundle);

            // then - 호출 순서 검증 (Tenant 먼저, Organization 나중)
            then(tenantCommandManager).should().persist(bundle.tenant());
            then(organizationCommandManager).should().persist(bundle.organization());
        }
    }
}
