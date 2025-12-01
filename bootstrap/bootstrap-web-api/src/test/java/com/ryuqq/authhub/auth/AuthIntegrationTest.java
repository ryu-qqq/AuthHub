package com.ryuqq.authhub.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.IntegrationTestBase;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * AuthIntegrationTest - 사용자 등록 E2E 통합 테스트
 *
 * <p>AUT-001 요구사항에 따른 4개의 E2E 시나리오를 테스트합니다.
 *
 * <p><strong>테스트 시나리오:</strong>
 *
 * <ol>
 *   <li>정상 회원 가입 → 201 Created
 *   <li>전화번호 중복 → 409 Conflict
 *   <li>전화번호 형식 오류 → 400 Bad Request
 *   <li>비밀번호 강도 부족 → 400 Bad Request
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("사용자 등록 E2E 통합 테스트")
class AuthIntegrationTest extends IntegrationTestBase {

    private static final String REGISTER_URL = "/api/v1/auth/register";

    @Nested
    @DisplayName("POST /api/v1/auth/register - 정상 케이스")
    class RegisterSuccessTest {

        @Test
        @DisplayName("정상 회원 가입 - 201 Created")
        void shouldRegisterUserSuccessfully() {
            // Given
            Map<String, String> request =
                    Map.of(
                            "phoneNumber", "+82-10-1234-5678",
                            "password", "password123",
                            "name", "홍길동");

            // When
            ResponseEntity<ApiResponse<UserResponse>> response =
                    restTemplate.exchange(
                            url(REGISTER_URL),
                            HttpMethod.POST,
                            createJsonEntity(request),
                            new ParameterizedTypeReference<>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().success()).isTrue();

            UserResponse userResponse = response.getBody().data();
            assertThat(userResponse).isNotNull();
            assertThat(userResponse.userId()).isNotNull();
            assertThat(userResponse.userType()).isEqualTo("PUBLIC");
            assertThat(userResponse.phoneNumber()).isEqualTo("+82-10-1234-5678");
            assertThat(userResponse.name()).isEqualTo("홍길동");
            assertThat(userResponse.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("정상 회원 가입 (프로필 이미지 포함) - 201 Created")
        void shouldRegisterUserWithProfileImageSuccessfully() {
            // Given
            Map<String, String> request =
                    Map.of(
                            "phoneNumber", "+82-10-5555-6666",
                            "password", "password123",
                            "name", "김철수",
                            "profileImageUrl", "https://example.com/profile.jpg");

            // When
            ResponseEntity<ApiResponse<UserResponse>> response =
                    restTemplate.exchange(
                            url(REGISTER_URL),
                            HttpMethod.POST,
                            createJsonEntity(request),
                            new ParameterizedTypeReference<>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().success()).isTrue();

            UserResponse userResponse = response.getBody().data();
            assertThat(userResponse).isNotNull();
            assertThat(userResponse.phoneNumber()).isEqualTo("+82-10-5555-6666");
            assertThat(userResponse.name()).isEqualTo("김철수");
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/register - 에러 케이스")
    class RegisterErrorTest {

        @Test
        @Sql("/sql/auth-test-data.sql")
        @DisplayName("전화번호 중복 - 409 Conflict")
        void shouldReturnConflictWhenPhoneNumberDuplicated() {
            // Given - auth-test-data.sql에서 +82-10-9999-8888 이미 등록됨
            Map<String, String> request =
                    Map.of(
                            "phoneNumber", "+82-10-9999-8888",
                            "password", "password123",
                            "name", "중복사용자");

            // When
            ResponseEntity<ProblemDetail> response =
                    restTemplate.exchange(
                            url(REGISTER_URL),
                            HttpMethod.POST,
                            createJsonEntity(request),
                            ProblemDetail.class);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("전화번호 형식 오류 - 400 Bad Request (국가코드 누락)")
        void shouldReturnBadRequestWhenPhoneNumberInvalid() {
            // Given - 국가코드 없이 전화번호만
            Map<String, String> request =
                    Map.of(
                            "phoneNumber", "010-1234-5678",
                            "password", "password123",
                            "name", "형식오류");

            // When
            ResponseEntity<ProblemDetail> response =
                    restTemplate.exchange(
                            url(REGISTER_URL),
                            HttpMethod.POST,
                            createJsonEntity(request),
                            ProblemDetail.class);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("비밀번호 강도 부족 - 400 Bad Request (8자 미만)")
        void shouldReturnBadRequestWhenPasswordTooShort() {
            // Given - 비밀번호 4자
            Map<String, String> request =
                    Map.of(
                            "phoneNumber", "+82-10-1111-2222",
                            "password", "1234",
                            "name", "비밀번호오류");

            // When
            ResponseEntity<ProblemDetail> response =
                    restTemplate.exchange(
                            url(REGISTER_URL),
                            HttpMethod.POST,
                            createJsonEntity(request),
                            ProblemDetail.class);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("필수 필드 누락 (이름) - 400 Bad Request")
        void shouldReturnBadRequestWhenNameMissing() {
            // Given - 이름 필드 없음
            Map<String, String> request =
                    Map.of(
                            "phoneNumber", "+82-10-3333-4444",
                            "password", "password123");

            // When
            ResponseEntity<ProblemDetail> response =
                    restTemplate.exchange(
                            url(REGISTER_URL),
                            HttpMethod.POST,
                            createJsonEntity(request),
                            ProblemDetail.class);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("필수 필드 누락 (전화번호) - 400 Bad Request")
        void shouldReturnBadRequestWhenPhoneNumberMissing() {
            // Given - 전화번호 필드 없음
            Map<String, String> request =
                    Map.of(
                            "password", "password123",
                            "name", "전화번호없음");

            // When
            ResponseEntity<ProblemDetail> response =
                    restTemplate.exchange(
                            url(REGISTER_URL),
                            HttpMethod.POST,
                            createJsonEntity(request),
                            ProblemDetail.class);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
        }
    }

    /**
     * JSON 요청 엔티티 생성
     *
     * @param body 요청 바디
     * @return HttpEntity
     */
    private HttpEntity<Map<String, String>> createJsonEntity(Map<String, String> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
