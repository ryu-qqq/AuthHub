package com.ryuqq.authhub.application.endpointpermission.dto.response;

import java.util.Set;

/**
 * EndpointPermissionSpecResponse - 엔드포인트 권한 스펙 Response DTO (인증용)
 *
 * <p>인증/인가 처리에 필요한 최소 정보만 포함합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param isPublic 공개 여부 (true면 권한 체크 스킵)
 * @param requiredPermissions 필요 권한 목록 (OR 조건)
 * @param requiredRoles 필요 역할 목록 (OR 조건)
 * @author development-team
 * @since 1.0.0
 */
public record EndpointPermissionSpecResponse(
        boolean isPublic, Set<String> requiredPermissions, Set<String> requiredRoles) {

    /**
     * 공개 엔드포인트용 응답 생성
     *
     * @return 공개 엔드포인트 스펙
     */
    public static EndpointPermissionSpecResponse publicEndpoint() {
        return new EndpointPermissionSpecResponse(true, Set.of(), Set.of());
    }
}
