package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * CreatePermissionEndpointApiRequest - PermissionEndpoint 생성 API Request
 *
 * <p>PermissionEndpoint 생성 REST API 요청 DTO입니다.
 *
 * @param permissionId 연결할 권한 ID
 * @param urlPattern URL 패턴 (예: /api/v1/users/{id})
 * @param httpMethod HTTP 메서드 (GET, POST, PUT, DELETE 등)
 * @param description 설명
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "PermissionEndpoint 생성 요청 DTO")
public record CreatePermissionEndpointApiRequest(
        @Schema(description = "연결할 권한 ID", example = "1")
                @NotNull(message = "permissionId는 필수입니다")
                @Positive(message = "permissionId는 양수여야 합니다")
                Long permissionId,
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
                String description) {}
