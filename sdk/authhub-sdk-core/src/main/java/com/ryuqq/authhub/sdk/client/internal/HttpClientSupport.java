package com.ryuqq.authhub.sdk.client.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

/**
 * HTTP 클라이언트 지원 클래스. REST API 호출에 필요한 공통 기능을 제공합니다.
 *
 * <p>이 클래스는 HTTP 요청/응답 처리의 모든 공통 로직을 담당합니다. GodClass 경고가 발생하지만, HTTP 클라이언트의 특성상
 * GET/POST/PUT/PATCH/DELETE 메서드들이 함께 있어야 응집도가 높습니다.
 */
@SuppressWarnings("PMD.GodClass")
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

    /** POST 요청을 수행합니다 (인증 없이 - Public API용). */
    public <T> T postPublic(String path, Object body, Class<T> responseType) {
        String url = buildUrl(path, Map.of());
        String jsonBody = toJson(body);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(config.readTimeout())
                        .build();

        return execute(request, responseType);
    }

    /** POST 요청을 수행합니다 (인증 없이 - Public API용, 제네릭 타입 지원). */
    public <T> T postPublic(String path, Object body, TypeReference<T> typeReference) {
        String url = buildUrl(path, Map.of());
        String jsonBody = toJson(body);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(config.readTimeout())
                        .build();

        return executeWithTypeReference(request, typeReference);
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

    /**
     * POST 요청을 수행합니다 (제네릭 타입 지원, 커스텀 헤더 포함).
     *
     * @param path API 경로
     * @param body 요청 본문
     * @param typeReference 응답 타입 참조
     * @param extraHeaders 추가 헤더 (key-value)
     * @return 응답 객체
     */
    public <T> T post(
            String path,
            Object body,
            TypeReference<T> typeReference,
            Map<String, String> extraHeaders) {
        String url = buildUrl(path, Map.of());
        String jsonBody = toJson(body);

        HttpRequest.Builder builder =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getAuthorizationHeader())
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .timeout(config.readTimeout());

        // 추가 헤더 설정
        if (extraHeaders != null) {
            extraHeaders.forEach(
                    (key, value) -> {
                        if (value != null && !value.isBlank()) {
                            builder.header(key, value);
                        }
                    });
        }

        return executeWithTypeReference(builder.build(), typeReference);
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
            ProblemDetailResponse problemDetail =
                    objectMapper.readValue(body, ProblemDetailResponse.class);
            return problemDetail.toErrorResponse();
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

    /**
     * RFC 7807 ProblemDetail 및 레거시 에러 응답을 파싱하기 위한 레코드.
     *
     * <p>지원 형식:
     *
     * <ul>
     *   <li>RFC 7807 ProblemDetail: adapter-in GlobalExceptionHandler 형식 (type, title, status,
     *       detail, code)
     *   <li>레거시 형식: 기존 SDK 호환 형식 (errorCode, message)
     * </ul>
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ProblemDetailResponse(
            String type,
            String title,
            Integer status,
            String detail,
            String instance,
            String timestamp,
            String code,
            Object errors,
            String errorCode,
            String message) {

        /**
         * ProblemDetail 또는 레거시 형식을 ErrorResponse로 변환합니다.
         *
         * @return RFC 7807이면 code→errorCode, detail→message. 레거시면 errorCode, message 그대로 사용.
         */
        ErrorResponse toErrorResponse() {
            String resolvedErrorCode = resolveErrorCode();
            String resolvedMessage = resolveMessage();
            return new ErrorResponse(resolvedErrorCode, resolvedMessage);
        }

        private String resolveErrorCode() {
            if (errorCode != null && !errorCode.isBlank()) {
                return errorCode;
            }
            if (code != null && !code.isBlank()) {
                return code;
            }
            if (title != null && !title.isBlank()) {
                return title.toUpperCase().replace(" ", "_");
            }
            return "UNKNOWN_ERROR";
        }

        private String resolveMessage() {
            if (message != null && !message.isBlank()) {
                return message;
            }
            if (detail != null && !detail.isBlank()) {
                return detail;
            }
            if (title != null && !title.isBlank()) {
                return title;
            }
            return "An unknown error occurred";
        }
    }
}
