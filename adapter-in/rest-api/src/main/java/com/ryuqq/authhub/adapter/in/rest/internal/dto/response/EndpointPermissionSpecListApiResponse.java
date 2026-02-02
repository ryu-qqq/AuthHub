package com.ryuqq.authhub.adapter.in.rest.internal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

/**
 * EndpointPermissionSpecListApiResponse - Gateway용 엔드포인트-권한 스펙 목록 API 응답 DTO
 *
 * <p>Gateway가 전체 엔드포인트-권한 매핑 정보를 캐싱하기 위해 사용합니다.
 *
 * @param version 스펙 버전 (ETag용)
 * @param updatedAt 마지막 수정 시간
 * @param endpoints 엔드포인트-권한 매핑 목록
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "엔드포인트-권한 스펙 목록")
public record EndpointPermissionSpecListApiResponse(
        @Schema(description = "스펙 버전 (ETag용)", example = "1704067200000") String version,
        @Schema(description = "마지막 수정 시간") Instant updatedAt,
        @Schema(description = "엔드포인트-권한 매핑 목록")
                List<EndpointPermissionSpecApiResponse> endpoints) {}
