package com.ryuqq.authhub.adapter.in.rest.auth.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.auth.dto.response.LoginResponse;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LoginApiResponse 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("dto")
@DisplayName("LoginApiResponse 테스트")
class LoginApiResponseTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 데이터로 생성 성공")
        void givenValidData_whenCreate_thenSuccess() {
            // given
            UUID userId = UUID.randomUUID();
            String accessToken = "accessToken123";
            String refreshToken = "refreshToken456";
            Long expiresIn = 3600L;
            String tokenType = "Bearer";

            // when
            LoginApiResponse response =
                    new LoginApiResponse(userId, accessToken, refreshToken, expiresIn, tokenType);

            // then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.accessToken()).isEqualTo(accessToken);
            assertThat(response.refreshToken()).isEqualTo(refreshToken);
            assertThat(response.expiresIn()).isEqualTo(expiresIn);
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
            UUID userId = UUID.randomUUID();
            String accessToken = "accessToken123";
            String refreshToken = "refreshToken456";
            Long expiresIn = 3600L;
            String tokenType = "Bearer";
            LoginResponse appResponse =
                    new LoginResponse(userId, accessToken, refreshToken, expiresIn, tokenType);

            // when
            LoginApiResponse apiResponse = LoginApiResponse.from(appResponse);

            // then
            assertThat(apiResponse.userId()).isEqualTo(userId);
            assertThat(apiResponse.accessToken()).isEqualTo(accessToken);
            assertThat(apiResponse.refreshToken()).isEqualTo(refreshToken);
            assertThat(apiResponse.expiresIn()).isEqualTo(expiresIn);
            assertThat(apiResponse.tokenType()).isEqualTo(tokenType);
        }
    }
}
