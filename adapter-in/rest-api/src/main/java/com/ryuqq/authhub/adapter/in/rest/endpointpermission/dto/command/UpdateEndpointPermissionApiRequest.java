package com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.Set;

/**
 * UpdateEndpointPermissionApiRequest - 엔드포인트 권한 수정 API 요청 DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>jakarta.validation 어노테이션 필수
 *   <li>Lombok 금지
 *   <li>Record 사용
 * </ul>
 *
 * @param description 설명 (선택)
 * @param isPublic 공개 여부 (선택)
 * @param requiredPermissions 필요 권한 목록 (OR 조건, 선택)
 * @param requiredRoles 필요 역할 목록 (OR 조건, 선택)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "엔드포인트 권한 수정 요청")
public record UpdateEndpointPermissionApiRequest(
        @Schema(description = "설명", example = "사용자 상세 조회 엔드포인트 (수정됨)")
                @Size(max = 500, message = "설명은 500자 이하여야 합니다")
                String description,
        @Schema(description = "공개 여부 (true: 인증 불필요)", example = "false") Boolean isPublic,
        @Schema(description = "필요 권한 목록 (OR 조건)", example = "[\"user:read\", \"admin:read\"]")
                Set<String> requiredPermissions,
        @Schema(description = "필요 역할 목록 (OR 조건)", example = "[\"ADMIN\", \"USER_MANAGER\"]")
                Set<String> requiredRoles) {}
