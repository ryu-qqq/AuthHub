package com.ryuqq.authhub.adapter.in.rest.token.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.token.dto.command.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.dto.command.LogoutApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.dto.command.RefreshTokenApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.dto.response.MyContextApiResponse;
import com.ryuqq.authhub.adapter.in.rest.token.dto.response.PublicKeysApiResponse;
import com.ryuqq.authhub.adapter.in.rest.token.fixture.TokenApiFixture;
import com.ryuqq.authhub.application.token.dto.command.LoginCommand;
import com.ryuqq.authhub.application.token.dto.command.LogoutCommand;
import com.ryuqq.authhub.application.token.dto.command.RefreshTokenCommand;
import com.ryuqq.authhub.application.token.dto.response.JwkResponse;
import com.ryuqq.authhub.application.token.dto.response.MyContextResponse;
import com.ryuqq.authhub.application.token.dto.response.PublicKeysResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TokenApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TokenApiMapper 단위 테스트")
class TokenApiMapperTest {

    private TokenApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TokenApiMapper();
    }

    @Nested
    @DisplayName("toLoginCommand(LoginApiRequest) 메서드는")
    class ToLoginCommand {

        @Test
        @DisplayName("LoginApiRequest를 LoginCommand로 변환한다")
        void shouldConvertToLoginCommand() {
            // Given
            LoginApiRequest request = TokenApiFixture.loginRequest();

            // When
            LoginCommand result = mapper.toLoginCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.identifier()).isEqualTo(TokenApiFixture.defaultIdentifier());
            assertThat(result.password()).isEqualTo(TokenApiFixture.defaultPassword());
        }

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toLoginCommand(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toRefreshTokenCommand(RefreshTokenApiRequest) 메서드는")
    class ToRefreshTokenCommand {

        @Test
        @DisplayName("RefreshTokenApiRequest를 RefreshTokenCommand로 변환한다")
        void shouldConvertToRefreshTokenCommand() {
            // Given
            RefreshTokenApiRequest request = TokenApiFixture.refreshTokenRequest();

            // When
            RefreshTokenCommand result = mapper.toRefreshTokenCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.refreshToken()).isEqualTo(TokenApiFixture.defaultRefreshToken());
        }

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toRefreshTokenCommand(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toLogoutCommand(LogoutApiRequest) 메서드는")
    class ToLogoutCommand {

        @Test
        @DisplayName("LogoutApiRequest를 LogoutCommand로 변환한다")
        void shouldConvertToLogoutCommand() {
            // Given
            LogoutApiRequest request = TokenApiFixture.logoutRequest();

            // When
            LogoutCommand result = mapper.toLogoutCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(TokenApiFixture.defaultUserId());
        }

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toLogoutCommand(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toPublicKeysApiResponse(PublicKeysResponse) 메서드는")
    class ToPublicKeysApiResponse {

        @Test
        @DisplayName("PublicKeysResponse를 PublicKeysApiResponse로 변환한다")
        void shouldConvertToPublicKeysApiResponse() {
            // Given
            JwkResponse jwk =
                    new JwkResponse("key-id-1", "RSA", "sig", "RS256", "modulus", "exponent");
            PublicKeysResponse response = new PublicKeysResponse(List.of(jwk));

            // When
            PublicKeysApiResponse result = mapper.toPublicKeysApiResponse(response);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.keys()).hasSize(1);
            assertThat(result.keys().get(0).kid()).isEqualTo("key-id-1");
            assertThat(result.keys().get(0).kty()).isEqualTo("RSA");
            assertThat(result.keys().get(0).use()).isEqualTo("sig");
            assertThat(result.keys().get(0).alg()).isEqualTo("RS256");
            assertThat(result.keys().get(0).n()).isEqualTo("modulus");
            assertThat(result.keys().get(0).e()).isEqualTo("exponent");
        }

        @Test
        @DisplayName("빈 키 목록을 변환한다")
        void shouldConvertEmptyKeysList() {
            // Given
            PublicKeysResponse response = new PublicKeysResponse(List.of());

            // When
            PublicKeysApiResponse result = mapper.toPublicKeysApiResponse(response);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.keys()).isEmpty();
        }

        @Test
        @DisplayName("다중 키를 변환한다")
        void shouldConvertMultipleKeys() {
            // Given
            JwkResponse jwk1 =
                    new JwkResponse("key-id-1", "RSA", "sig", "RS256", "modulus1", "exponent1");
            JwkResponse jwk2 =
                    new JwkResponse("key-id-2", "RSA", "sig", "RS256", "modulus2", "exponent2");
            PublicKeysResponse response = new PublicKeysResponse(List.of(jwk1, jwk2));

            // When
            PublicKeysApiResponse result = mapper.toPublicKeysApiResponse(response);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.keys()).hasSize(2);
            assertThat(result.keys().get(0).kid()).isEqualTo("key-id-1");
            assertThat(result.keys().get(1).kid()).isEqualTo("key-id-2");
        }

        @Test
        @DisplayName("response가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenResponseIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toPublicKeysApiResponse(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toMyContextApiResponse(MyContextResponse) 메서드는")
    class ToMyContextApiResponse {

        @Test
        @DisplayName("MyContextResponse를 MyContextApiResponse로 변환한다")
        void shouldConvertToMyContextApiResponse() {
            // Given
            MyContextResponse.RoleInfo role1 = new MyContextResponse.RoleInfo("role-1", "ADMIN");
            MyContextResponse.RoleInfo role2 = new MyContextResponse.RoleInfo("role-2", "USER");
            MyContextResponse response =
                    new MyContextResponse(
                            "user-123",
                            "test@example.com",
                            "Test User",
                            "tenant-123",
                            "Test Tenant",
                            "org-456",
                            "Test Organization",
                            "010-1234-5678",
                            List.of(role1, role2),
                            List.of("user:read", "user:write"));

            // When
            MyContextApiResponse result = mapper.toMyContextApiResponse(response);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo("user-123");
            assertThat(result.email()).isEqualTo("test@example.com");
            assertThat(result.name()).isEqualTo("Test User");
            assertThat(result.tenant().id()).isEqualTo("tenant-123");
            assertThat(result.tenant().name()).isEqualTo("Test Tenant");
            assertThat(result.organization().id()).isEqualTo("org-456");
            assertThat(result.organization().name()).isEqualTo("Test Organization");
            assertThat(result.roles()).hasSize(2);
            assertThat(result.roles().get(0).id()).isEqualTo("role-1");
            assertThat(result.roles().get(0).name()).isEqualTo("ADMIN");
            assertThat(result.roles().get(1).id()).isEqualTo("role-2");
            assertThat(result.roles().get(1).name()).isEqualTo("USER");
            assertThat(result.permissions()).containsExactly("user:read", "user:write");
        }

        @Test
        @DisplayName("빈 역할과 권한 목록을 변환한다")
        void shouldConvertEmptyRolesAndPermissions() {
            // Given
            MyContextResponse response =
                    new MyContextResponse(
                            "user-123",
                            "test@example.com",
                            "Test User",
                            "tenant-123",
                            "Test Tenant",
                            "org-456",
                            "Test Organization",
                            null,
                            List.of(),
                            List.of());

            // When
            MyContextApiResponse result = mapper.toMyContextApiResponse(response);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roles()).isEmpty();
            assertThat(result.permissions()).isEmpty();
        }

        @Test
        @DisplayName("다중 역할과 권한을 변환한다")
        void shouldConvertMultipleRolesAndPermissions() {
            // Given
            List<MyContextResponse.RoleInfo> roles =
                    List.of(
                            new MyContextResponse.RoleInfo("role-1", "ADMIN"),
                            new MyContextResponse.RoleInfo("role-2", "USER"),
                            new MyContextResponse.RoleInfo("role-3", "GUEST"));
            List<String> permissions =
                    List.of("user:read", "user:write", "user:delete", "admin:read", "admin:write");

            MyContextResponse response =
                    new MyContextResponse(
                            "user-123",
                            "test@example.com",
                            "Test User",
                            "tenant-123",
                            "Test Tenant",
                            "org-456",
                            "Test Organization",
                            "010-1234-5678",
                            roles,
                            permissions);

            // When
            MyContextApiResponse result = mapper.toMyContextApiResponse(response);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.roles()).hasSize(3);
            assertThat(result.permissions()).hasSize(5);
        }

        @Test
        @DisplayName("response가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenResponseIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toMyContextApiResponse(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
