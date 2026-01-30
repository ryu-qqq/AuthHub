package com.ryuqq.authhub.adapter.in.rest.role.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * UpdateRoleApiRequest - Role 수정 API Request
 *
 * <p>Role 수정 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
 *
 * @param displayName 표시 이름 (null이면 변경하지 않음)
 * @param description 역할 설명 (null이면 변경하지 않음)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Role 수정 요청 DTO")
public record UpdateRoleApiRequest(
        @Schema(description = "표시 이름", example = "사용자 관리자")
                @Size(max = 100, message = "displayName은 100자 이하여야 합니다")
                String displayName,
        @Schema(description = "역할 설명", example = "사용자 관리 권한을 가진 역할")
                @Size(max = 500, message = "description은 500자 이하여야 합니다")
                String description) {}
