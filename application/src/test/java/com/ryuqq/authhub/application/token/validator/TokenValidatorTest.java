package com.ryuqq.authhub.application.token.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.organization.validator.OrganizationValidator;
import com.ryuqq.authhub.application.tenant.validator.TenantValidator;
import com.ryuqq.authhub.application.token.dto.composite.TokenClaimsComposite;
import com.ryuqq.authhub.application.token.factory.TokenClaimsContextFactory;
import com.ryuqq.authhub.application.token.manager.RefreshTokenReader;
import com.ryuqq.authhub.application.user.validator.UserValidator;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.token.exception.InvalidRefreshTokenException;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TokenValidator 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TokenValidator 단위 테스트")
class TokenValidatorTest {

    @Mock private RefreshTokenReader refreshTokenReader;

    @Mock private UserValidator userValidator;

    @Mock private OrganizationValidator organizationValidator;

    @Mock private TenantValidator tenantValidator;

    @Mock private TokenClaimsContextFactory tokenClaimsContextFactory;

    private TokenValidator sut;

    @BeforeEach
    void setUp() {
        sut =
                new TokenValidator(
                        refreshTokenReader,
                        userValidator,
                        organizationValidator,
                        tenantValidator,
                        tokenClaimsContextFactory);
    }

    @Nested
    @DisplayName("validateAndBuildContext 메서드")
    class ValidateAndBuildContext {

        @Test
        @DisplayName("성공: Refresh Token 검증 후 TokenClaimsComposite 반환")
        void shouldValidateAndReturnTokenClaimsComposite() {
            // given
            String refreshToken = "valid-refresh-token";
            UserId userId = UserFixture.defaultId();
            User user = UserFixture.create();
            Organization organization = OrganizationFixture.create();
            Tenant tenant = TenantFixture.create();
            TokenClaimsComposite expected =
                    TokenClaimsComposite.builder()
                            .userId(userId)
                            .tenantId(tenant.tenantIdValue())
                            .tenantName(tenant.nameValue())
                            .organizationId(organization.organizationIdValue())
                            .organizationName(organization.nameValue())
                            .email(user.identifierValue())
                            .build();

            given(refreshTokenReader.findUserIdByToken(refreshToken))
                    .willReturn(Optional.of(userId));
            given(userValidator.findExistingOrThrow(userId)).willReturn(user);
            given(organizationValidator.findExistingOrThrow(user.getOrganizationId()))
                    .willReturn(organization);
            given(tenantValidator.findExistingOrThrow(organization.getTenantId()))
                    .willReturn(tenant);
            given(tokenClaimsContextFactory.create(user, tenant, organization))
                    .willReturn(expected);

            // when
            TokenClaimsComposite result = sut.validateAndBuildContext(refreshToken);

            // then
            assertThat(result).isEqualTo(expected);
            then(refreshTokenReader).should().findUserIdByToken(refreshToken);
            then(userValidator).should().findExistingOrThrow(userId);
            then(organizationValidator).should().findExistingOrThrow(user.getOrganizationId());
            then(tenantValidator).should().findExistingOrThrow(organization.getTenantId());
            then(tokenClaimsContextFactory).should().create(user, tenant, organization);
        }

        @Test
        @DisplayName("실패: 유효하지 않은 Refresh Token 시 InvalidRefreshTokenException 발생")
        void shouldThrowException_WhenRefreshTokenInvalid() {
            // given
            String refreshToken = "invalid-refresh-token";

            given(refreshTokenReader.findUserIdByToken(refreshToken)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.validateAndBuildContext(refreshToken))
                    .isInstanceOf(InvalidRefreshTokenException.class);
            then(userValidator).shouldHaveNoInteractions();
        }
    }
}
