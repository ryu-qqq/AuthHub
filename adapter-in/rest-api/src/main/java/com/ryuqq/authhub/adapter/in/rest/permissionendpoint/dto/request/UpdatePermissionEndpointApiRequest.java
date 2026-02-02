package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * UpdatePermissionEndpointApiRequest - PermissionEndpoint 수정 API Request
 *
 * <p>PermissionEndpoint 수정 REST API 요청 DTO입니다.
 *
 * @param serviceName 서비스 이름
 * @param urlPattern URL 패턴
 * @param httpMethod HTTP 메서드
 * @param description 설명
 * @param isPublic 공개 엔드포인트 여부
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "PermissionEndpoint 수정 요청 DTO")
public record UpdatePermissionEndpointApiRequest(
        @Schema(description = "서비스 이름", example = "product-service")
                @NotBlank(message = "serviceName은 필수입니다")
                @Size(min = 1, max = 100, message = "serviceName은 100자 이하여야 합니다")
                @Pattern(
                        regexp = "^[a-z0-9][a-z0-9-]*[a-z0-9]$|^[a-z0-9]$",
                        message = "serviceName은 영문 소문자, 숫자, 하이픈만 허용됩니다")
                String serviceName,
        @Schema(description = "URL 패턴", example = "/api/v1/users/{id}")
                @NotBlank(message = "urlPattern은 필수입니다")
                @Size(min = 1, max = 500, message = "urlPattern은 500자 이하여야 합니다")
                @Pattern(regexp = "^/.*", message = "urlPattern은 '/'로 시작해야 합니다")
                String urlPattern,
        @Schema(description = "HTTP 메서드", example = "GET")
                @NotBlank(message = "httpMethod는 필수입니다")
                @Pattern(
                        regexp = "^(GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS)$",
                        message =
                                "httpMethod는 GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS 중 하나여야"
                                        + " 합니다")
                String httpMethod,
        @Schema(description = "설명", example = "사용자 상세 조회 API")
                @Size(max = 500, message = "description은 500자 이하여야 합니다")
                String description,
        @Schema(description = "공개 엔드포인트 여부", example = "false")
                @NotNull(message = "isPublic은 필수입니다")
                Boolean isPublic) {}
