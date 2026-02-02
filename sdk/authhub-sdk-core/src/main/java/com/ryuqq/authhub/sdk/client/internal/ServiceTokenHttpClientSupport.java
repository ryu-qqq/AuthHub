package com.ryuqq.authhub.sdk.client.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ryuqq.authhub.sdk.config.GatewayClientConfig;
import com.ryuqq.authhub.sdk.exception.AuthHubBadRequestException;
import com.ryuqq.authhub.sdk.exception.AuthHubException;
import com.ryuqq.authhub.sdk.exception.AuthHubForbiddenException;
import com.ryuqq.authhub.sdk.exception.AuthHubNotFoundException;
import com.ryuqq.authhub.sdk.exception.AuthHubServerException;
import com.ryuqq.authhub.sdk.exception.AuthHubUnauthorizedException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 서비스 토큰 인증용 HTTP 클라이언트 지원 클래스.
 *
 * <p>X-Service-Name, X-Service-Token 헤더를 사용하여 Internal API를 호출합니다.
 */
class ServiceTokenHttpClientSupport {

    private static final Logger log = LoggerFactory.getLogger(ServiceTokenHttpClientSupport.class);
    private static final String SERVICE_NAME_HEADER = "X-Service-Name";
    private static final String SERVICE_TOKEN_HEADER = "X-Service-Token";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private final GatewayClientConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ServiceTokenHttpClientSupport(GatewayClientConfig config) {
        this.config = config;
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
    public <T> T get(String path, TypeReference<T> typeReference) {
        String url = buildUrl(path);
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                        .header(SERVICE_NAME_HEADER, config.serviceName())
                        .header(SERVICE_TOKEN_HEADER, config.serviceToken())
                        .GET()
                        .timeout(config.readTimeout())
                        .build();

        return execute(request, typeReference);
    }

    private <T> T execute(HttpRequest request, TypeReference<T> typeReference) {
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

    private String buildUrl(String path) {
        StringBuilder url = new StringBuilder(config.baseUrl());
        if (!path.startsWith("/")) {
            url.append("/");
        }
        url.append(path);
        return url.toString();
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
