package com.ryuqq.authhub.adapter.in.rest.internal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * EndpointPermissionSpecListApiResponse - Gateway용 엔드포인트-권한 스펙 목록 API 응답 DTO
 *
 * <p>Gateway가 전체 엔드포인트-권한 매핑 정보를 캐싱하기 위해 사용합니다.
 *
 * @param endpoints 엔드포인트-권한 매핑 목록
 * @param totalCount 전체 개수
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "엔드포인트-권한 스펙 목록")
public record EndpointPermissionSpecListApiResponse(
        @Schema(description = "엔드포인트-권한 매핑 목록") List<EndpointPermissionSpecApiResponse> endpoints,
        @Schema(description = "전체 개수", example = "25") int totalCount) {}
