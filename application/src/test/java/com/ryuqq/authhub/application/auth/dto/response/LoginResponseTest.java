package com.ryuqq.authhub.application.auth.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LoginResponse DTO 설계 검증 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("dto")
@DisplayName("LoginResponse DTO 설계 테스트")
class LoginResponseTest {

    @Nested
    @DisplayName("Record 구조 검증")
    class RecordStructureTest {

        @Test
        @DisplayName("[필수] LoginResponse는 Record 타입이어야 한다")
        void shouldBeRecord() {
            assertThat(LoginResponse.class.isRecord())
                    .as("LoginResponse는 Record 타입으로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] LoginResponse는 public이어야 한다")
        void shouldBePublic() {
            assertThat(Modifier.isPublic(LoginResponse.class.getModifiers()))
                    .as("LoginResponse는 public으로 선언되어야 합니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("필드 검증")
    class FieldsTest {

        @Test
        @DisplayName("[필수] userId 필드가 존재해야 한다")
        void shouldHaveUserIdField() {
            RecordComponent[] components = LoginResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("userId");

            RecordComponent component = findComponent(components, "userId");
            assertThat(component.getType())
                    .as("userId는 UUID 타입이어야 합니다")
                    .isEqualTo(UUID.class);
        }

        @Test
        @DisplayName("[필수] accessToken 필드가 존재해야 한다")
        void shouldHaveAccessTokenField() {
            RecordComponent[] components = LoginResponse.class.getRecordComponents();

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
            RecordComponent[] components = LoginResponse.class.getRecordComponents();

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
            RecordComponent[] components = LoginResponse.class.getRecordComponents();

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
            RecordComponent[] components = LoginResponse.class.getRecordComponents();

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
            UUID userId = UUID.randomUUID();
            String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.access...";
            String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh...";
            Long expiresIn = 3600L;
            String tokenType = "Bearer";

            // When
            LoginResponse response = new LoginResponse(
                    userId, accessToken, refreshToken, expiresIn, tokenType
            );

            // Then
            assertThat(response.userId()).isEqualTo(userId);
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
