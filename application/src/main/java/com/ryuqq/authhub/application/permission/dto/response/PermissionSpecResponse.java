package com.ryuqq.authhub.application.permission.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * PermissionSpecResponse - 권한 명세 응답 DTO
 *
 * <p>Gateway가 AuthHub에서 조회하는 전체 엔드포인트별 권한 명세입니다.
 *
 * <p><strong>포함 정보:</strong>
 *
 * <ul>
 *   <li>version - Gateway 캐시 갱신 판단용 버전
 *   <li>updatedAt - 명세 생성 시간
 *   <li>permissions - 엔드포인트별 권한 목록
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record PermissionSpecResponse(
        int version, Instant updatedAt, List<EndpointPermissionResponse> permissions) {

    public static PermissionSpecResponse of(
            int version, Instant updatedAt, List<EndpointPermissionResponse> permissions) {
        return new PermissionSpecResponse(version, updatedAt, permissions);
    }
}
