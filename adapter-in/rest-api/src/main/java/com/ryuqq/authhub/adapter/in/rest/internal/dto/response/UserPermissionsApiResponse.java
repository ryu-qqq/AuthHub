package com.ryuqq.authhub.adapter.in.rest.internal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

/**
 * UserPermissionsApiResponse - Gateway용 사용자 권한 API 응답 DTO
 *
 * <p>Gateway가 사용자 인가 검증을 위해 필요한 역할/권한 정보를 제공합니다.
 *
 * @param userId 사용자 ID
 * @param roles 역할 이름 Set
 * @param permissions 권한 키 Set
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "사용자 권한 정보")
public record UserPermissionsApiResponse(
        @Schema(description = "사용자 ID", example = "01234567-89ab-cdef-0123-456789abcdef")
                String userId,
        @Schema(description = "역할 이름 목록", example = "[\"ADMIN\", \"USER\"]") Set<String> roles,
        @Schema(description = "권한 키 목록", example = "[\"user:read\", \"user:write\"]")
                Set<String> permissions) {}
