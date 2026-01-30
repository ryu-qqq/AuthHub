package com.ryuqq.authhub.adapter.in.rest.rolepermission.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * RevokeRolePermissionApiRequest - 역할에서 권한 제거 API Request
 *
 * <p>역할에서 하나 이상의 권한을 제거하는 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param permissionIds 제거할 권한 ID 목록
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "역할에서 권한 제거 요청 DTO")
public record RevokeRolePermissionApiRequest(
        @Schema(description = "제거할 권한 ID 목록", example = "[1, 2, 3]")
                @NotEmpty(message = "permissionIds는 1개 이상 필요합니다")
                @Size(max = 100, message = "permissionIds는 최대 100개까지 가능합니다")
                List<Long> permissionIds) {}
