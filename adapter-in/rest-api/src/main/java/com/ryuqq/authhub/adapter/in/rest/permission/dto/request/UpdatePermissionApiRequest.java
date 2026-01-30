package com.ryuqq.authhub.adapter.in.rest.permission.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * UpdatePermissionApiRequest - Permission 수정 API Request
 *
 * <p>Permission 수정 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
 *
 * @param description 권한 설명 (null이면 변경하지 않음)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Permission 수정 요청 DTO")
public record UpdatePermissionApiRequest(
        @Schema(description = "권한 설명", example = "사용자 조회 권한")
                @Size(max = 500, message = "description은 500자 이하여야 합니다")
                String description) {}
