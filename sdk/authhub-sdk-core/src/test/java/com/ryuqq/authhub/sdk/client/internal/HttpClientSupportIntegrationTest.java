package com.ryuqq.authhub.sdk.client.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.sdk.auth.StaticTokenResolver;
import com.ryuqq.authhub.sdk.config.AuthHubConfig;
import com.ryuqq.authhub.sdk.exception.AuthHubBadRequestException;
import com.ryuqq.authhub.sdk.exception.AuthHubForbiddenException;
import com.ryuqq.authhub.sdk.exception.AuthHubNotFoundException;
import com.ryuqq.authhub.sdk.exception.AuthHubServerException;
import com.ryuqq.authhub.sdk.exception.AuthHubUnauthorizedException;
import java.io.IOException;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("HttpClientSupport Integration Test")
class HttpClientSupportIntegrationTest {

    private MockWebServer mockServer;
    private HttpClientSupport httpClient;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();

        String baseUrl = mockServer.url("/").toString();
        // trailing slash 제거
        baseUrl = baseUrl.substring(0, baseUrl.length() - 1);

        AuthHubConfig config = AuthHubConfig.of(baseUrl);
        httpClient = new HttpClientSupport(config, new StaticTokenResolver("test-token"));
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Nested
    @DisplayName("GET 요청")
    class GetRequests {

        @Test
        @DisplayName("성공적인 GET 요청을 수행한다")
        void shouldPerformSuccessfulGetRequest() throws InterruptedException {
            // given
            String responseBody =
                    """
                    {"id": 1, "name": "Test"}
                    """;
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(200)
                            .setHeader("Content-Type", "application/json")
                            .setBody(responseBody));

            // when
            TestResponse response = httpClient.get("/api/test", TestResponse.class);

            // then
            assertThat(response.id()).isEqualTo(1);
            assertThat(response.name()).isEqualTo("Test");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/api/test");
            assertThat(request.getHeader("Authorization")).isEqualTo("Bearer test-token");
            assertThat(request.getHeader("Content-Type")).isEqualTo("application/json");
        }

        @Test
        @DisplayName("쿼리 파라미터와 함께 GET 요청을 수행한다")
        void shouldPerformGetRequestWithQueryParams() throws InterruptedException {
            // given
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(200)
                            .setHeader("Content-Type", "application/json")
                            .setBody("{\"id\": 1, \"name\": \"Test\"}"));

            Map<String, Object> queryParams = Map.of("page", 0, "size", 10, "name", "test");

            // when
            httpClient.get("/api/test", queryParams, TestResponse.class);

            // then
            RecordedRequest request = mockServer.takeRequest();
            String path = request.getPath();
            assertThat(path).startsWith("/api/test?");
            assertThat(path).contains("page=0");
            assertThat(path).contains("size=10");
            assertThat(path).contains("name=test");
        }
    }

    @Nested
    @DisplayName("POST 요청")
    class PostRequests {

        @Test
        @DisplayName("성공적인 POST 요청을 수행한다")
        void shouldPerformSuccessfulPostRequest() throws InterruptedException {
            // given
            String responseBody =
                    """
                    {"id": 1, "name": "Created"}
                    """;
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(201)
                            .setHeader("Content-Type", "application/json")
                            .setBody(responseBody));

            TestRequest requestBody = new TestRequest("New Item");

            // when
            TestResponse response = httpClient.post("/api/test", requestBody, TestResponse.class);

            // then
            assertThat(response.id()).isEqualTo(1);
            assertThat(response.name()).isEqualTo("Created");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("POST");
            assertThat(request.getPath()).isEqualTo("/api/test");
            assertThat(request.getBody().readUtf8()).contains("New Item");
        }
    }

    @Nested
    @DisplayName("PUT 요청")
    class PutRequests {

        @Test
        @DisplayName("성공적인 PUT 요청을 수행한다")
        void shouldPerformSuccessfulPutRequest() throws InterruptedException {
            // given
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(200)
                            .setHeader("Content-Type", "application/json")
                            .setBody("{\"id\": 1, \"name\": \"Updated\"}"));

            TestRequest requestBody = new TestRequest("Updated Item");

            // when
            TestResponse response = httpClient.put("/api/test/1", requestBody, TestResponse.class);

            // then
            assertThat(response.name()).isEqualTo("Updated");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("PUT");
            assertThat(request.getPath()).isEqualTo("/api/test/1");
        }
    }

    @Nested
    @DisplayName("PATCH 요청")
    class PatchRequests {

        @Test
        @DisplayName("성공적인 PATCH 요청을 수행한다")
        void shouldPerformSuccessfulPatchRequest() throws InterruptedException {
            // given
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(200)
                            .setHeader("Content-Type", "application/json")
                            .setBody("{\"id\": 1, \"name\": \"Patched\"}"));

            TestRequest requestBody = new TestRequest("Patched Item");

            // when
            TestResponse response =
                    httpClient.patch("/api/test/1", requestBody, TestResponse.class);

            // then
            assertThat(response.name()).isEqualTo("Patched");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("PATCH");
        }
    }

    @Nested
    @DisplayName("DELETE 요청")
    class DeleteRequests {

        @Test
        @DisplayName("성공적인 DELETE 요청을 수행한다")
        void shouldPerformSuccessfulDeleteRequest() throws InterruptedException {
            // given
            mockServer.enqueue(new MockResponse().setResponseCode(204));

            // when
            httpClient.delete("/api/test/1");

            // then
            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("DELETE");
            assertThat(request.getPath()).isEqualTo("/api/test/1");
        }
    }

    @Nested
    @DisplayName("에러 응답 처리")
    class ErrorHandling {

        @Test
        @DisplayName("400 Bad Request 응답 시 AuthHubBadRequestException을 발생시킨다")
        void shouldThrowBadRequestException() {
            // given
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(400)
                            .setHeader("Content-Type", "application/json")
                            .setBody(
                                    "{\"errorCode\": \"VALIDATION_ERROR\", \"message\": \"Invalid"
                                            + " input\"}"));

            // when & then
            assertThatThrownBy(() -> httpClient.get("/api/test", TestResponse.class))
                    .isInstanceOf(AuthHubBadRequestException.class)
                    .hasMessageContaining("Invalid input");
        }

        @Test
        @DisplayName("401 Unauthorized 응답 시 AuthHubUnauthorizedException을 발생시킨다")
        void shouldThrowUnauthorizedException() {
            // given
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(401)
                            .setHeader("Content-Type", "application/json")
                            .setBody(
                                    "{\"errorCode\": \"UNAUTHORIZED\", \"message\": \"Token"
                                            + " expired\"}"));

            // when & then
            assertThatThrownBy(() -> httpClient.get("/api/test", TestResponse.class))
                    .isInstanceOf(AuthHubUnauthorizedException.class)
                    .hasMessageContaining("Token expired");
        }

        @Test
        @DisplayName("403 Forbidden 응답 시 AuthHubForbiddenException을 발생시킨다")
        void shouldThrowForbiddenException() {
            // given
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(403)
                            .setHeader("Content-Type", "application/json")
                            .setBody(
                                    "{\"errorCode\": \"ACCESS_DENIED\", \"message\": \"Permission"
                                            + " denied\"}"));

            // when & then
            assertThatThrownBy(() -> httpClient.get("/api/test", TestResponse.class))
                    .isInstanceOf(AuthHubForbiddenException.class)
                    .hasMessageContaining("Permission denied");
        }

        @Test
        @DisplayName("404 Not Found 응답 시 AuthHubNotFoundException을 발생시킨다")
        void shouldThrowNotFoundException() {
            // given
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(404)
                            .setHeader("Content-Type", "application/json")
                            .setBody(
                                    "{\"errorCode\": \"NOT_FOUND\", \"message\": \"Resource not"
                                            + " found\"}"));

            // when & then
            assertThatThrownBy(() -> httpClient.get("/api/test", TestResponse.class))
                    .isInstanceOf(AuthHubNotFoundException.class)
                    .hasMessageContaining("Resource not found");
        }

        @Test
        @DisplayName("500 Server Error 응답 시 AuthHubServerException을 발생시킨다")
        void shouldThrowServerException() {
            // given
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(500)
                            .setHeader("Content-Type", "application/json")
                            .setBody(
                                    "{\"errorCode\": \"INTERNAL_ERROR\", \"message\": \"Server"
                                            + " error\"}"));

            // when & then
            assertThatThrownBy(() -> httpClient.get("/api/test", TestResponse.class))
                    .isInstanceOf(AuthHubServerException.class)
                    .hasMessageContaining("Server error");
        }

        @Test
        @DisplayName("잘못된 JSON 에러 응답도 처리한다")
        void shouldHandleMalformedErrorResponse() {
            // given
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(400)
                            .setHeader("Content-Type", "text/plain")
                            .setBody("Bad Request"));

            // when & then
            assertThatThrownBy(() -> httpClient.get("/api/test", TestResponse.class))
                    .isInstanceOf(AuthHubBadRequestException.class);
        }
    }

    @Nested
    @DisplayName("Authorization 헤더")
    class AuthorizationHeader {

        @Test
        @DisplayName("토큰이 없으면 AuthHubUnauthorizedException을 발생시킨다")
        void shouldThrowExceptionWhenNoToken() throws IOException {
            // given
            mockServer.shutdown();
            mockServer = new MockWebServer();
            mockServer.start();

            String baseUrl = mockServer.url("/").toString();
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);

            AuthHubConfig config = AuthHubConfig.of(baseUrl);
            HttpClientSupport noTokenClient =
                    new HttpClientSupport(config, () -> java.util.Optional.empty());

            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(200)
                            .setBody("{\"id\": 1, \"name\": \"Test\"}"));

            // when & then
            assertThatThrownBy(() -> noTokenClient.get("/api/test", TestResponse.class))
                    .isInstanceOf(AuthHubUnauthorizedException.class)
                    .hasMessageContaining("No token available");
        }
    }

    // Test DTOs
    record TestRequest(String name) {}

    record TestResponse(int id, String name) {}
}
