package com.ryuqq.authhub.application.auth.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.auth.dto.response.LoginResponse;
import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LoginResponseAssembler 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LoginResponseAssembler 단위 테스트")
class LoginResponseAssemblerTest {

    private LoginResponseAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new LoginResponseAssembler();
    }

    @Nested
    @DisplayName("toLoginResponse 메서드")
    class ToLoginResponseTest {

        @Test
        @DisplayName("User와 TokenResponse를 LoginResponse로 변환한다")
        void shouldAssembleLoginResponse() {
            // given
            User user = UserFixture.create();
            TokenResponse tokenResponse =
                    new TokenResponse(
                            "access-token-value",
                            "refresh-token-value",
                            3600L,
                            604800L,
                            "Bearer");

            // when
            LoginResponse result = assembler.toLoginResponse(user, tokenResponse);

            // then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(user.userIdValue());
            assertThat(result.accessToken()).isEqualTo("access-token-value");
            assertThat(result.refreshToken()).isEqualTo("refresh-token-value");
            assertThat(result.expiresIn()).isEqualTo(3600L);
            assertThat(result.tokenType()).isEqualTo("Bearer");
        }

        @Test
        @DisplayName("다양한 사용자 정보로 LoginResponse를 올바르게 조립한다")
        void shouldAssembleWithVariousUserInfo() {
            // given
            User user = UserFixture.createWithIdentifier("admin@example.com");
            TokenResponse tokenResponse =
                    new TokenResponse(
                            "admin-access-token",
                            "admin-refresh-token",
                            7200L,
                            1209600L,
                            "Bearer");

            // when
            LoginResponse result = assembler.toLoginResponse(user, tokenResponse);

            // then
            assertThat(result.userId()).isEqualTo(user.userIdValue());
            assertThat(result.accessToken()).isEqualTo("admin-access-token");
            assertThat(result.refreshToken()).isEqualTo("admin-refresh-token");
            assertThat(result.expiresIn()).isEqualTo(7200L);
        }

        @Test
        @DisplayName("토큰 타입이 올바르게 전달된다")
        void shouldPreserveTokenType() {
            // given
            User user = UserFixture.create();
            String customTokenType = "CustomBearer";
            TokenResponse tokenResponse =
                    new TokenResponse(
                            "access", "refresh", 3600L, 604800L, customTokenType);

            // when
            LoginResponse result = assembler.toLoginResponse(user, tokenResponse);

            // then
            assertThat(result.tokenType()).isEqualTo(customTokenType);
        }
    }
}
