package com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * CreateEndpointPermissionApiResponse - 엔드포인트 권한 생성 API 응답 DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지
 *   <li>Record 사용
 * </ul>
 *
 * @param id 생성된 엔드포인트 권한 ID
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "엔드포인트 권한 생성 응답")
public record CreateEndpointPermissionApiResponse(
        @Schema(description = "생성된 엔드포인트 권한 ID", example = "550e8400-e29b-41d4-a716-446655440000")
                String id) {}
