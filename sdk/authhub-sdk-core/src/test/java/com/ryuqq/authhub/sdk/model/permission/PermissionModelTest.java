package com.ryuqq.authhub.sdk.model.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Permission Model Tests")
class PermissionModelTest {

    @Nested
    @DisplayName("CreatePermissionRequest")
    class CreatePermissionRequestTest {

        @Test
        @DisplayName("유효한 입력으로 생성 성공")
        void shouldCreateWithValidInput() {
            CreatePermissionRequest request =
                    new CreatePermissionRequest("/users", "READ", "사용자 조회 권한", true);

            assertThat(request.resource()).isEqualTo("/users");
            assertThat(request.action()).isEqualTo("READ");
            assertThat(request.description()).isEqualTo("사용자 조회 권한");
            assertThat(request.isSystem()).isTrue();
        }

        @Test
        @DisplayName("resource가 null이면 예외 발생")
        void shouldThrowWhenResourceIsNull() {
            assertThatThrownBy(() -> new CreatePermissionRequest(null, "READ", "사용자 조회 권한", false))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("resource");
        }

        @Test
        @DisplayName("resource가 빈 문자열이면 예외 발생")
        void shouldThrowWhenResourceIsBlank() {
            assertThatThrownBy(() -> new CreatePermissionRequest("   ", "READ", "사용자 조회 권한", false))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("resource must not be blank");
        }

        @Test
        @DisplayName("action이 null이면 예외 발생")
        void shouldThrowWhenActionIsNull() {
            assertThatThrownBy(
                            () -> new CreatePermissionRequest("/users", null, "사용자 조회 권한", false))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("action");
        }

        @Test
        @DisplayName("action이 빈 문자열이면 예외 발생")
        void shouldThrowWhenActionIsBlank() {
            assertThatThrownBy(
                            () -> new CreatePermissionRequest("/users", "   ", "사용자 조회 권한", false))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("action must not be blank");
        }

        @Test
        @DisplayName("description과 isSystem이 null이어도 생성 가능")
        void shouldCreateWithNullOptionalFields() {
            CreatePermissionRequest request =
                    new CreatePermissionRequest("/users", "READ", null, null);

            assertThat(request.description()).isNull();
            assertThat(request.isSystem()).isNull();
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            CreatePermissionRequest request1 =
                    new CreatePermissionRequest("/users", "READ", "사용자 조회 권한", true);
            CreatePermissionRequest request2 =
                    new CreatePermissionRequest("/users", "READ", "사용자 조회 권한", true);

            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }
    }

    @Nested
    @DisplayName("CreatePermissionResponse")
    class CreatePermissionResponseTest {

        @Test
        @DisplayName("permissionId로 생성 성공")
        void shouldCreateWithPermissionId() {
            CreatePermissionResponse response = new CreatePermissionResponse("123");

            assertThat(response.permissionId()).isEqualTo("123");
        }
    }

    @Nested
    @DisplayName("PermissionResponse")
    class PermissionResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            PermissionResponse response =
                    new PermissionResponse("1", "/users", "READ", "사용자 조회 권한", true, now, now);

            assertThat(response.permissionId()).isEqualTo("1");
            assertThat(response.resource()).isEqualTo("/users");
            assertThat(response.action()).isEqualTo("READ");
            assertThat(response.description()).isEqualTo("사용자 조회 권한");
            assertThat(response.isSystem()).isTrue();
            assertThat(response.createdAt()).isEqualTo(now);
            assertThat(response.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            Instant now = Instant.now();
            PermissionResponse response1 =
                    new PermissionResponse("1", "/users", "READ", "사용자 조회 권한", true, now, now);
            PermissionResponse response2 =
                    new PermissionResponse("1", "/users", "READ", "사용자 조회 권한", true, now, now);

            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("toString에 필드가 포함됨")
        void shouldIncludeFieldsInToString() {
            Instant now = Instant.now();
            PermissionResponse response =
                    new PermissionResponse("1", "/users", "READ", "사용자 조회 권한", true, now, now);

            String str = response.toString();
            assertThat(str).contains("/users");
            assertThat(str).contains("READ");
        }
    }

    @Nested
    @DisplayName("UpdatePermissionRequest")
    class UpdatePermissionRequestTest {

        @Test
        @DisplayName("description으로 생성 성공")
        void shouldCreateWithDescription() {
            UpdatePermissionRequest request = new UpdatePermissionRequest("주문 쓰기 권한");

            assertThat(request.description()).isEqualTo("주문 쓰기 권한");
        }

        @Test
        @DisplayName("description이 null이면 예외 발생")
        void shouldThrowWhenDescriptionIsNull() {
            assertThatThrownBy(() -> new UpdatePermissionRequest(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("description must not be null");
        }

        @Test
        @DisplayName("description이 빈 문자열이면 예외 발생")
        void shouldThrowWhenDescriptionIsBlank() {
            assertThatThrownBy(() -> new UpdatePermissionRequest("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("description must not be blank");
        }
    }
}
