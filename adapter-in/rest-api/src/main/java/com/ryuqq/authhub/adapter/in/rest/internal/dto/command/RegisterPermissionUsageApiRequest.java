package com.ryuqq.authhub.adapter.in.rest.internal.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

/**
 * RegisterPermissionUsageApiRequest - 권한 사용 이력 등록 API 요청 DTO
 *
 * <p>n8n 또는 CI/CD에서 권한 사용 이력을 등록할 때 사용합니다.
 *
 * @param serviceName 서비스명 (소문자, 숫자, 하이픈)
 * @param locations 코드 위치 목록 (선택)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "권한 사용 이력 등록 요청")
public record RegisterPermissionUsageApiRequest(
        @Schema(description = "서비스명", example = "product-service")
                @NotBlank(message = "serviceName은 필수입니다")
                @Pattern(
                        regexp = "^[a-z][a-z0-9-]*$",
                        message = "serviceName은 소문자로 시작하고 소문자, 숫자, 하이픈만 허용됩니다")
                String serviceName,
        @Schema(
                        description = "코드 위치 목록",
                        example = "[\"ProductController.java:45\", \"OrderService.java:123\"]")
                List<String> locations) {}
