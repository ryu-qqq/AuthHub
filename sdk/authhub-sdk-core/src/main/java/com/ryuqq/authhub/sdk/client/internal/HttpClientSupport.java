package com.ryuqq.authhub.sdk.client.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ryuqq.authhub.sdk.auth.TokenResolver;
import com.ryuqq.authhub.sdk.config.AuthHubConfig;
import com.ryuqq.authhub.sdk.exception.AuthHubBadRequestException;
import com.ryuqq.authhub.sdk.exception.AuthHubException;
import com.ryuqq.authhub.sdk.exception.AuthHubForbiddenException;
import com.ryuqq.authhub.sdk.exception.AuthHubNotFoundException;
import com.ryuqq.authhub.sdk.exception.AuthHubServerException;
import com.ryuqq.authhub.sdk.exception.AuthHubUnauthorizedException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** HTTP 클라이언트 지원 클래스. REST API 호출에 필요한 공통 기능을 제공합니다. */
class HttpClientSupport {

    private static final Logger log = LoggerFactory.getLogger(HttpClientSupport.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthHubConfig config;
    private final TokenResolver tokenResolver;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpClientSupport(AuthHubConfig config, TokenResolver tokenResolver) {
        this.config = config;
        this.tokenResolver = tokenResolver;
        this.httpClient = HttpClient.newBuilder().connectTimeout(config.connectTimeout()).build();
        this.objectMapper = createObjectMapper();
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /** GET 요청을 수행합니다. */
    public <T> T get(String path, Class<T> responseType) {
        return get(path, Map.of(), responseType);
    }

    /** GET 요청을 수행합니다 (쿼리 파라미터 포함). */
    public <T> T get(String path, Map<String, Object> queryParams, Class<T> responseType) {
        String url = buildUrl(path, queryParams);
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getAuthorizationHeader())
                        .GET()
                        .timeout(config.readTimeout())
                        .build();

        return execute(request, responseType);
    }

    /** GET 요청을 수행합니다 (제네릭 타입 지원). */
    public <T> T get(String path, Map<String, Object> queryParams, TypeReference<T> typeReference) {
        String url = buildUrl(path, queryParams);
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getAuthorizationHeader())
                        .GET()
                        .timeout(config.readTimeout())
                        .build();

        return executeWithTypeReference(request, typeReference);
    }

    /** POST 요청을 수행합니다. */
    public <T> T post(String path, Object body, Class<T> responseType) {
        String url = buildUrl(path, Map.of());
        String jsonBody = toJson(body);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getAuthorizationHeader())
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(config.readTimeout())
                        .build();

        return execute(request, responseType);
    }

    /** POST 요청을 수행합니다 (제네릭 타입 지원). */
    public <T> T post(String path, Object body, TypeReference<T> typeReference) {
        String url = buildUrl(path, Map.of());
        String jsonBody = toJson(body);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getAuthorizationHeader())
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(config.readTimeout())
                        .build();

        return executeWithTypeReference(request, typeReference);
    }

    /** PUT 요청을 수행합니다. */
    public <T> T put(String path, Object body, Class<T> responseType) {
        String url = buildUrl(path, Map.of());
        String jsonBody = toJson(body);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getAuthorizationHeader())
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(config.readTimeout())
                        .build();

        return execute(request, responseType);
    }

    /** PUT 요청을 수행합니다 (제네릭 타입 지원). */
    public <T> T put(String path, Object body, TypeReference<T> typeReference) {
        String url = buildUrl(path, Map.of());
        String jsonBody = toJson(body);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getAuthorizationHeader())
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(config.readTimeout())
                        .build();

        return executeWithTypeReference(request, typeReference);
    }

    /** PATCH 요청을 수행합니다. */
    public <T> T patch(String path, Object body, Class<T> responseType) {
        String url = buildUrl(path, Map.of());
        String jsonBody = toJson(body);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getAuthorizationHeader())
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(config.readTimeout())
                        .build();

        return execute(request, responseType);
    }

    /** PATCH 요청을 수행합니다 (제네릭 타입 지원). */
    public <T> T patch(String path, Object body, TypeReference<T> typeReference) {
        String url = buildUrl(path, Map.of());
        String jsonBody = toJson(body);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getAuthorizationHeader())
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(config.readTimeout())
                        .build();

        return executeWithTypeReference(request, typeReference);
    }

    /** DELETE 요청을 수행합니다. */
    public void delete(String path) {
        String url = buildUrl(path, Map.of());

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getAuthorizationHeader())
                        .DELETE()
                        .timeout(config.readTimeout())
                        .build();

        executeVoid(request);
    }

    /** DELETE 요청을 수행합니다 (본문 포함). */
    public void delete(String path, Object body) {
        String url = buildUrl(path, Map.of());
        String jsonBody = toJson(body);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getAuthorizationHeader())
                        .method("DELETE", HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(config.readTimeout())
                        .build();

        executeVoid(request);
    }

    /** DELETE 요청을 수행합니다 (본문 포함, 제네릭 타입 지원). */
    public <T> T delete(String path, Object body, TypeReference<T> typeReference) {
        String url = buildUrl(path, Map.of());
        String jsonBody = toJson(body);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getAuthorizationHeader())
                        .method("DELETE", HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(config.readTimeout())
                        .build();

        return executeWithTypeReference(request, typeReference);
    }

    private <T> T execute(HttpRequest request, Class<T> responseType) {
        try {
            log.debug("Executing {} {}", request.method(), request.uri());
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            handleErrorResponse(response);
            return fromJson(response.body(), responseType);
        } catch (AuthHubException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AuthHubServerException(
                    500, "REQUEST_INTERRUPTED", "Request was interrupted", e);
        } catch (IOException e) {
            throw new AuthHubServerException(
                    500, "CONNECTION_ERROR", "Failed to connect to AuthHub server", e);
        }
    }

    private <T> T executeWithTypeReference(HttpRequest request, TypeReference<T> typeReference) {
        try {
            log.debug("Executing {} {}", request.method(), request.uri());
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            handleErrorResponse(response);
            return fromJson(response.body(), typeReference);
        } catch (AuthHubException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AuthHubServerException(
                    500, "REQUEST_INTERRUPTED", "Request was interrupted", e);
        } catch (IOException e) {
            throw new AuthHubServerException(
                    500, "CONNECTION_ERROR", "Failed to connect to AuthHub server", e);
        }
    }

    private void executeVoid(HttpRequest request) {
        try {
            log.debug("Executing {} {}", request.method(), request.uri());
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            handleErrorResponse(response);
        } catch (AuthHubException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AuthHubServerException(
                    500, "REQUEST_INTERRUPTED", "Request was interrupted", e);
        } catch (IOException e) {
            throw new AuthHubServerException(
                    500, "CONNECTION_ERROR", "Failed to connect to AuthHub server", e);
        }
    }

    private void handleErrorResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            return;
        }

        ErrorResponse error = parseErrorResponse(response.body());

        switch (statusCode) {
            case 400 -> throw new AuthHubBadRequestException(error.errorCode(), error.message());
            case 401 -> throw new AuthHubUnauthorizedException(error.errorCode(), error.message());
            case 403 -> throw new AuthHubForbiddenException(error.errorCode(), error.message());
            case 404 -> throw new AuthHubNotFoundException(error.errorCode(), error.message());
            default -> {
                if (statusCode >= 500) {
                    throw new AuthHubServerException(
                            statusCode, error.errorCode(), error.message());
                }
                throw new AuthHubException(statusCode, error.errorCode(), error.message());
            }
        }
    }

    private ErrorResponse parseErrorResponse(String body) {
        try {
            return objectMapper.readValue(body, ErrorResponse.class);
        } catch (JsonProcessingException e) {
            return new ErrorResponse("UNKNOWN_ERROR", body);
        }
    }

    private String buildUrl(String path, Map<String, Object> queryParams) {
        StringBuilder url = new StringBuilder(config.baseUrl());
        if (!path.startsWith("/")) {
            url.append("/");
        }
        url.append(path);

        if (!queryParams.isEmpty()) {
            url.append("?");
            queryParams.forEach(
                    (key, value) -> {
                        if (value != null) {
                            String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
                            String encodedValue =
                                    URLEncoder.encode(
                                            String.valueOf(value), StandardCharsets.UTF_8);
                            url.append(encodedKey).append("=").append(encodedValue).append("&");
                        }
                    });
            url.setLength(url.length() - 1);
        }

        return url.toString();
    }

    private String getAuthorizationHeader() {
        Optional<String> token = tokenResolver.resolve();
        if (token.isEmpty()) {
            throw new AuthHubUnauthorizedException("TOKEN_NOT_FOUND", "No token available");
        }
        return BEARER_PREFIX + token.get();
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new AuthHubBadRequestException(
                    "SERIALIZATION_ERROR", "Failed to serialize request body", e);
        }
    }

    private <T> T fromJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new AuthHubServerException(
                    500, "DESERIALIZATION_ERROR", "Failed to parse response", e);
        }
    }

    private <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new AuthHubServerException(
                    500, "DESERIALIZATION_ERROR", "Failed to parse response", e);
        }
    }

    private record ErrorResponse(String errorCode, String message) {}
}
