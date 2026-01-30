package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * UpdatePermissionEndpointApiRequest - PermissionEndpoint 수정 API Request
 *
 * <p>PermissionEndpoint 수정 REST API 요청 DTO입니다.
 *
 * @param urlPattern 새 URL 패턴 (null이면 변경 안 함)
 * @param httpMethod 새 HTTP 메서드 (null이면 변경 안 함)
 * @param description 새 설명 (null이면 변경 안 함)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "PermissionEndpoint 수정 요청 DTO")
public record UpdatePermissionEndpointApiRequest(
        @Schema(description = "URL 패턴", example = "/api/v1/users/{id}")
                @Size(min = 1, max = 500, message = "urlPattern은 500자 이하여야 합니다")
                @Pattern(regexp = "^/.*", message = "urlPattern은 '/'로 시작해야 합니다")
                String urlPattern,
        @Schema(description = "HTTP 메서드", example = "GET")
                @Pattern(
                        regexp = "^(GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS)$",
                        message =
                                "httpMethod는 GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS 중 하나여야"
                                        + " 합니다")
                String httpMethod,
        @Schema(description = "설명", example = "사용자 상세 조회 API")
                @Size(max = 500, message = "description은 500자 이하여야 합니다")
                String description) {}
