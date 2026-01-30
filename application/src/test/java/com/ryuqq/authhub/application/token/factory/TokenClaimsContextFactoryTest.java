package com.ryuqq.authhub.application.token.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.token.dto.composite.TokenClaimsComposite;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TokenClaimsContextFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TokenClaimsContextFactory 단위 테스트")
class TokenClaimsContextFactoryTest {

    private TokenClaimsContextFactory sut;

    @BeforeEach
    void setUp() {
        sut = new TokenClaimsContextFactory();
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공: User, Tenant, Organization으로 TokenClaimsComposite 생성")
        void shouldCreateTokenClaimsComposite_FromUserTenantOrganization() {
            // given
            User user = UserFixture.create();
            Tenant tenant = TenantFixture.create();
            Organization organization = OrganizationFixture.create();

            // when
            TokenClaimsComposite result = sut.create(user, tenant, organization);

            // then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(user.getUserId());
            assertThat(result.tenantId()).isEqualTo(tenant.tenantIdValue());
            assertThat(result.tenantName()).isEqualTo(tenant.nameValue());
            assertThat(result.organizationId()).isEqualTo(organization.organizationIdValue());
            assertThat(result.organizationName()).isEqualTo(organization.nameValue());
            assertThat(result.email()).isEqualTo(user.identifierValue());
        }
    }
}
