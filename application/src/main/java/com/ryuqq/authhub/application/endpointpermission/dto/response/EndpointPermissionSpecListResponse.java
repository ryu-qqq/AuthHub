package com.ryuqq.authhub.application.endpointpermission.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * EndpointPermissionSpecListResponse - 엔드포인트 권한 스펙 목록 Response DTO (Gateway용)
 *
 * <p>Gateway에서 캐싱하여 권한 검사에 사용할 전체 엔드포인트 스펙 목록입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param endpoints 엔드포인트 권한 스펙 목록
 * @param version 스펙 버전 (마지막 업데이트 시간, ISO-8601)
 * @author development-team
 * @since 1.0.0
 */
public record EndpointPermissionSpecListResponse(
        List<EndpointPermissionSpecItemResponse> endpoints, String version) {

    /**
     * 정적 팩토리 메서드
     *
     * @param endpoints 엔드포인트 권한 스펙 목록
     * @return EndpointPermissionSpecListResponse 인스턴스
     */
    public static EndpointPermissionSpecListResponse of(
            List<EndpointPermissionSpecItemResponse> endpoints) {
        String version = Instant.now().toString();
        return new EndpointPermissionSpecListResponse(endpoints, version);
    }

    /**
     * 버전을 지정하는 정적 팩토리 메서드
     *
     * @param endpoints 엔드포인트 권한 스펙 목록
     * @param version 스펙 버전
     * @return EndpointPermissionSpecListResponse 인스턴스
     */
    public static EndpointPermissionSpecListResponse of(
            List<EndpointPermissionSpecItemResponse> endpoints, String version) {
        return new EndpointPermissionSpecListResponse(endpoints, version);
    }
}
