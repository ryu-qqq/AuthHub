package com.ryuqq.authhub.adapter.in.rest.role.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * CreateRoleApiRequest - Role 생성 API Request
 *
 * <p>Role 생성 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param tenantId 테넌트 ID (null이면 Global 역할)
 * @param serviceId 서비스 ID (null이면 서비스 무관)
 * @param name 역할 이름 (UPPER_SNAKE_CASE)
 * @param displayName 표시 이름
 * @param description 역할 설명
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Role 생성 요청 DTO")
public record CreateRoleApiRequest(
        @Schema(
                        description = "테넌트 ID (null이면 Global 역할)",
                        example = "550e8400-e29b-41d4-a716-446655440000")
                String tenantId,
        @Schema(description = "서비스 ID (null이면 서비스 무관)", example = "1") Long serviceId,
        @Schema(description = "역할 이름 (UPPER_SNAKE_CASE)", example = "USER_MANAGER")
                @NotBlank(message = "name은 필수입니다")
                @Size(min = 2, max = 50, message = "name은 2자 이상 50자 이하여야 합니다")
                @Pattern(
                        regexp = "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$",
                        message = "name은 UPPER_SNAKE_CASE 형식이어야 합니다")
                String name,
        @Schema(description = "표시 이름", example = "사용자 관리자")
                @Size(max = 100, message = "displayName은 100자 이하여야 합니다")
                String displayName,
        @Schema(description = "역할 설명", example = "사용자 관리 권한을 가진 역할")
                @Size(max = 500, message = "description은 500자 이하여야 합니다")
                String description) {}
