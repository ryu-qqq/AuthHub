package com.ryuqq.authhub.application.auth.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TokenResponse DTO 설계 검증 테스트
 *
 * <p>토큰 갱신 응답용 DTO (LoginResponse보다 간소화)
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("dto")
@DisplayName("TokenResponse DTO 설계 테스트")
class TokenResponseTest {

    @Nested
    @DisplayName("Record 구조 검증")
    class RecordStructureTest {

        @Test
        @DisplayName("[필수] TokenResponse는 Record 타입이어야 한다")
        void shouldBeRecord() {
            assertThat(TokenResponse.class.isRecord())
                    .as("TokenResponse는 Record 타입으로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] TokenResponse는 public이어야 한다")
        void shouldBePublic() {
            assertThat(Modifier.isPublic(TokenResponse.class.getModifiers()))
                    .as("TokenResponse는 public으로 선언되어야 합니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("필드 검증")
    class FieldsTest {

        @Test
        @DisplayName("[필수] accessToken 필드가 존재해야 한다")
        void shouldHaveAccessTokenField() {
            RecordComponent[] components = TokenResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("accessToken");

            RecordComponent component = findComponent(components, "accessToken");
            assertThat(component.getType())
                    .as("accessToken은 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }

        @Test
        @DisplayName("[필수] refreshToken 필드가 존재해야 한다")
        void shouldHaveRefreshTokenField() {
            RecordComponent[] components = TokenResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("refreshToken");

            RecordComponent component = findComponent(components, "refreshToken");
            assertThat(component.getType())
                    .as("refreshToken은 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }

        @Test
        @DisplayName("[필수] expiresIn 필드가 존재해야 한다")
        void shouldHaveExpiresInField() {
            RecordComponent[] components = TokenResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("expiresIn");

            RecordComponent component = findComponent(components, "expiresIn");
            assertThat(component.getType())
                    .as("expiresIn은 Long 타입이어야 합니다")
                    .isEqualTo(Long.class);
        }

        @Test
        @DisplayName("[필수] tokenType 필드가 존재해야 한다")
        void shouldHaveTokenTypeField() {
            RecordComponent[] components = TokenResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("tokenType");

            RecordComponent component = findComponent(components, "tokenType");
            assertThat(component.getType())
                    .as("tokenType은 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }
    }

    @Nested
    @DisplayName("불변성 검증")
    class ImmutabilityTest {

        @Test
        @DisplayName("[필수] Record 인스턴스 생성 후 필드 값이 유지되어야 한다")
        void shouldMaintainFieldValues() {
            // Given
            String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.new_access...";
            String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.new_refresh...";
            Long expiresIn = 3600L;
            String tokenType = "Bearer";

            // When
            TokenResponse response = new TokenResponse(
                    accessToken, refreshToken, expiresIn, tokenType
            );

            // Then
            assertThat(response.accessToken()).isEqualTo(accessToken);
            assertThat(response.refreshToken()).isEqualTo(refreshToken);
            assertThat(response.expiresIn()).isEqualTo(expiresIn);
            assertThat(response.tokenType()).isEqualTo(tokenType);
        }
    }

    private RecordComponent findComponent(RecordComponent[] components, String name) {
        return java.util.Arrays.stream(components)
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Component not found: " + name));
    }
}
