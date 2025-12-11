package com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

/**
 * CreateEndpointPermissionApiRequest - 엔드포인트 권한 생성 API 요청 DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>jakarta.validation 어노테이션 필수
 *   <li>Lombok 금지
 *   <li>Record 사용
 * </ul>
 *
 * @param serviceName 서비스 이름 (필수)
 * @param path 엔드포인트 경로 (필수)
 * @param method HTTP 메서드 (필수)
 * @param description 설명 (선택)
 * @param isPublic 공개 여부 (필수)
 * @param requiredPermissions 필요 권한 목록 (OR 조건, 선택)
 * @param requiredRoles 필요 역할 목록 (OR 조건, 선택)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "엔드포인트 권한 생성 요청")
public record CreateEndpointPermissionApiRequest(
        @Schema(
                        description = "서비스 이름",
                        example = "auth-hub",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "서비스 이름은 필수입니다")
                @Size(max = 100, message = "서비스 이름은 100자 이하여야 합니다")
                String serviceName,
        @Schema(
                        description = "엔드포인트 경로",
                        example = "/api/v1/users/{userId}",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "경로는 필수입니다")
                @Size(max = 500, message = "경로는 500자 이하여야 합니다")
                String path,
        @Schema(
                        description = "HTTP 메서드",
                        example = "GET",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "HTTP 메서드는 필수입니다")
                String method,
        @Schema(description = "설명", example = "사용자 상세 조회 엔드포인트")
                @Size(max = 500, message = "설명은 500자 이하여야 합니다")
                String description,
        @Schema(
                        description = "공개 여부 (true: 인증 불필요)",
                        example = "false",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "공개 여부는 필수입니다")
                Boolean isPublic,
        @Schema(description = "필요 권한 목록 (OR 조건)", example = "[\"user:read\", \"admin:read\"]")
                Set<String> requiredPermissions,
        @Schema(description = "필요 역할 목록 (OR 조건)", example = "[\"ADMIN\", \"USER_MANAGER\"]")
                Set<String> requiredRoles) {}
