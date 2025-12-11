package com.ryuqq.authhub.adapter.in.rest.auth.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TokenApiResponse 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("dto")
@DisplayName("TokenApiResponse 테스트")
class TokenApiResponseTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 데이터로 생성 성공")
        void givenValidData_whenCreate_thenSuccess() {
            // given
            String accessToken = "accessToken123";
            String refreshToken = "refreshToken456";
            long accessTokenExpiresIn = 3_600L;
            long refreshTokenExpiresIn = 86_400L;
            String tokenType = "Bearer";

            // when
            TokenApiResponse response =
                    new TokenApiResponse(
                            accessToken,
                            refreshToken,
                            accessTokenExpiresIn,
                            refreshTokenExpiresIn,
                            tokenType);

            // then
            assertThat(response.accessToken()).isEqualTo(accessToken);
            assertThat(response.refreshToken()).isEqualTo(refreshToken);
            assertThat(response.accessTokenExpiresIn()).isEqualTo(accessTokenExpiresIn);
            assertThat(response.refreshTokenExpiresIn()).isEqualTo(refreshTokenExpiresIn);
            assertThat(response.tokenType()).isEqualTo(tokenType);
        }
    }

    @Nested
    @DisplayName("from() 변환 테스트")
    class FromTest {

        @Test
        @DisplayName("Application Response에서 API Response로 변환 성공")
        void givenApplicationResponse_whenFrom_thenConvertSuccess() {
            // given
            String accessToken = "accessToken123";
            String refreshToken = "refreshToken456";
            long accessTokenExpiresIn = 3_600L;
            long refreshTokenExpiresIn = 86_400L;
            String tokenType = "Bearer";
            TokenResponse appResponse =
                    new TokenResponse(
                            accessToken,
                            refreshToken,
                            accessTokenExpiresIn,
                            refreshTokenExpiresIn,
                            tokenType);

            // when
            TokenApiResponse apiResponse = TokenApiResponse.from(appResponse);

            // then
            assertThat(apiResponse.accessToken()).isEqualTo(accessToken);
            assertThat(apiResponse.refreshToken()).isEqualTo(refreshToken);
            assertThat(apiResponse.accessTokenExpiresIn()).isEqualTo(accessTokenExpiresIn);
            assertThat(apiResponse.refreshTokenExpiresIn()).isEqualTo(refreshTokenExpiresIn);
            assertThat(apiResponse.tokenType()).isEqualTo(tokenType);
        }
    }
}
