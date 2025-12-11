package com.ryuqq.authhub.application.endpointpermission.dto.response;

import java.util.Set;

/**
 * EndpointPermissionSpecItemResponse - 엔드포인트 권한 스펙 항목 Response DTO (Gateway용)
 *
 * <p>Gateway에서 캐싱하여 권한 검사에 사용할 개별 엔드포인트 스펙입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param method HTTP 메서드 (GET, POST, PUT, DELETE 등)
 * @param pattern 경로 패턴 (예: /api/v1/users/*)
 * @param service 서비스 이름 (예: authhub)
 * @param requiredRoles 필요 역할 목록 (OR 조건)
 * @param requiredPermissions 필요 권한 목록 (OR 조건)
 * @param isPublic 공개 여부 (true면 권한 체크 스킵)
 * @param requireMfa MFA 필수 여부 (향후 확장)
 * @author development-team
 * @since 1.0.0
 */
public record EndpointPermissionSpecItemResponse(
        String method,
        String pattern,
        String service,
        Set<String> requiredRoles,
        Set<String> requiredPermissions,
        boolean isPublic,
        boolean requireMfa) {

    /**
     * 정적 팩토리 메서드
     *
     * @param method HTTP 메서드
     * @param pattern 경로 패턴
     * @param service 서비스 이름
     * @param requiredRoles 필요 역할
     * @param requiredPermissions 필요 권한
     * @param isPublic 공개 여부
     * @return EndpointPermissionSpecItemResponse 인스턴스
     */
    public static EndpointPermissionSpecItemResponse of(
            String method,
            String pattern,
            String service,
            Set<String> requiredRoles,
            Set<String> requiredPermissions,
            boolean isPublic) {
        return new EndpointPermissionSpecItemResponse(
                method, pattern, service, requiredRoles, requiredPermissions, isPublic, false);
    }
}
