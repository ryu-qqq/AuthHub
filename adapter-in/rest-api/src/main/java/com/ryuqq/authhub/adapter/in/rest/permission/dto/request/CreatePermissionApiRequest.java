package com.ryuqq.authhub.adapter.in.rest.permission.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * CreatePermissionApiRequest - Permission 생성 API Request (Global Only)
 *
 * <p>Permission 생성 REST API 요청 DTO입니다.
 *
 * <p><strong>Global Only 설계:</strong>
 *
 * <ul>
 *   <li>모든 Permission은 전체 시스템에서 공유됩니다
 *   <li>테넌트 관련 필드가 제거되었습니다
 * </ul>
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param resource 리소스명 (예: user, role)
 * @param action 행위명 (예: read, create, update, delete)
 * @param description 권한 설명
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Permission 생성 요청 DTO")
public record CreatePermissionApiRequest(
        @Schema(description = "리소스명", example = "user")
                @NotBlank(message = "resource는 필수입니다")
                @Size(min = 2, max = 50, message = "resource는 2자 이상 50자 이하여야 합니다")
                @Pattern(
                        regexp = "^[a-z][a-z0-9]*(-[a-z0-9]+)*$",
                        message = "resource는 소문자와 하이픈으로 구성되어야 합니다")
                String resource,
        @Schema(description = "행위명", example = "read")
                @NotBlank(message = "action은 필수입니다")
                @Size(min = 2, max = 50, message = "action은 2자 이상 50자 이하여야 합니다")
                @Pattern(
                        regexp = "^[a-z][a-z0-9]*(-[a-z0-9]+)*$",
                        message = "action은 소문자와 하이픈으로 구성되어야 합니다")
                String action,
        @Schema(description = "권한 설명", example = "사용자 조회 권한")
                @Size(max = 500, message = "description은 500자 이하여야 합니다")
                String description) {}
