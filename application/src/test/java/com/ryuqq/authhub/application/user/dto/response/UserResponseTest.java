package com.ryuqq.authhub.application.user.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserResponse DTO 설계 검증 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("dto")
@DisplayName("UserResponse DTO 설계 테스트")
class UserResponseTest {

    @Nested
    @DisplayName("Record 구조 검증")
    class RecordStructureTest {

        @Test
        @DisplayName("[필수] UserResponse는 Record 타입이어야 한다")
        void shouldBeRecord() {
            assertThat(UserResponse.class.isRecord())
                    .as("UserResponse는 Record 타입으로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] UserResponse는 public이어야 한다")
        void shouldBePublic() {
            assertThat(Modifier.isPublic(UserResponse.class.getModifiers()))
                    .as("UserResponse는 public으로 선언되어야 합니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("필드 검증")
    class FieldsTest {

        @Test
        @DisplayName("[필수] userId 필드가 존재해야 한다")
        void shouldHaveUserIdField() {
            RecordComponent[] components = UserResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("userId");

            RecordComponent component = findComponent(components, "userId");
            assertThat(component.getType())
                    .as("userId는 UUID 타입이어야 합니다")
                    .isEqualTo(UUID.class);
        }

        @Test
        @DisplayName("[필수] tenantId 필드가 존재해야 한다")
        void shouldHaveTenantIdField() {
            RecordComponent[] components = UserResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("tenantId");

            RecordComponent component = findComponent(components, "tenantId");
            assertThat(component.getType())
                    .as("tenantId는 Long 타입이어야 합니다")
                    .isEqualTo(Long.class);
        }

        @Test
        @DisplayName("[선택] organizationId 필드가 존재해야 한다")
        void shouldHaveOrganizationIdField() {
            RecordComponent[] components = UserResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("organizationId");

            RecordComponent component = findComponent(components, "organizationId");
            assertThat(component.getType())
                    .as("organizationId는 Long 타입이어야 합니다")
                    .isEqualTo(Long.class);
        }

        @Test
        @DisplayName("[필수] userType 필드가 존재해야 한다")
        void shouldHaveUserTypeField() {
            RecordComponent[] components = UserResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("userType");

            RecordComponent component = findComponent(components, "userType");
            assertThat(component.getType())
                    .as("userType은 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }

        @Test
        @DisplayName("[필수] status 필드가 존재해야 한다")
        void shouldHaveStatusField() {
            RecordComponent[] components = UserResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("status");

            RecordComponent component = findComponent(components, "status");
            assertThat(component.getType())
                    .as("status는 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }

        @Test
        @DisplayName("[필수] name 필드가 존재해야 한다")
        void shouldHaveNameField() {
            RecordComponent[] components = UserResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("name");

            RecordComponent component = findComponent(components, "name");
            assertThat(component.getType())
                    .as("name은 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }

        @Test
        @DisplayName("[필수] nickname 필드가 존재해야 한다")
        void shouldHaveNicknameField() {
            RecordComponent[] components = UserResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("nickname");

            RecordComponent component = findComponent(components, "nickname");
            assertThat(component.getType())
                    .as("nickname은 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }

        @Test
        @DisplayName("[필수] profileImageUrl 필드가 존재해야 한다")
        void shouldHaveProfileImageUrlField() {
            RecordComponent[] components = UserResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("profileImageUrl");

            RecordComponent component = findComponent(components, "profileImageUrl");
            assertThat(component.getType())
                    .as("profileImageUrl은 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }

        @Test
        @DisplayName("[필수] createdAt 필드가 존재해야 한다")
        void shouldHaveCreatedAtField() {
            RecordComponent[] components = UserResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("createdAt");

            RecordComponent component = findComponent(components, "createdAt");
            assertThat(component.getType())
                    .as("createdAt은 Instant 타입이어야 합니다")
                    .isEqualTo(Instant.class);
        }

        @Test
        @DisplayName("[필수] updatedAt 필드가 존재해야 한다")
        void shouldHaveUpdatedAtField() {
            RecordComponent[] components = UserResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .contains("updatedAt");

            RecordComponent component = findComponent(components, "updatedAt");
            assertThat(component.getType())
                    .as("updatedAt은 Instant 타입이어야 합니다")
                    .isEqualTo(Instant.class);
        }
    }

    @Nested
    @DisplayName("보안 검증")
    class SecurityTest {

        @Test
        @DisplayName("[금지] 민감 정보 필드가 없어야 한다 (password)")
        void shouldNotHavePasswordField() {
            RecordComponent[] components = UserResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .as("UserResponse는 비밀번호 필드를 포함하지 않아야 합니다")
                    .doesNotContain("password", "rawPassword", "hashedPassword", "encryptedPassword");
        }

        @Test
        @DisplayName("[금지] 민감 정보 필드가 없어야 한다 (credential)")
        void shouldNotHaveCredentialField() {
            RecordComponent[] components = UserResponse.class.getRecordComponents();

            assertThat(components)
                    .extracting(RecordComponent::getName)
                    .as("UserResponse는 credential 필드를 포함하지 않아야 합니다")
                    .doesNotContain("credential", "credentials");
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
            Long tenantId = 1L;
            Long organizationId = 10L;
            String userType = "PUBLIC";
            String status = "ACTIVE";
            String name = "홍길동";
            String nickname = "길동이";
            String profileImageUrl = "https://example.com/profile.jpg";
            Instant createdAt = Instant.now();
            Instant updatedAt = Instant.now();

            // When
            UserResponse response = new UserResponse(
                    userId, tenantId, organizationId, userType, status,
                    name, nickname, profileImageUrl, createdAt, updatedAt
            );

            // Then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.tenantId()).isEqualTo(tenantId);
            assertThat(response.organizationId()).isEqualTo(organizationId);
            assertThat(response.userType()).isEqualTo(userType);
            assertThat(response.status()).isEqualTo(status);
            assertThat(response.name()).isEqualTo(name);
            assertThat(response.nickname()).isEqualTo(nickname);
            assertThat(response.profileImageUrl()).isEqualTo(profileImageUrl);
            assertThat(response.createdAt()).isEqualTo(createdAt);
            assertThat(response.updatedAt()).isEqualTo(updatedAt);
        }
    }

    private RecordComponent findComponent(RecordComponent[] components, String name) {
        return java.util.Arrays.stream(components)
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Component not found: " + name));
    }
}
