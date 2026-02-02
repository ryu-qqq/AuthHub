package com.ryuqq.authhub.sdk.model.internal;

import java.util.List;

/**
 * 엔드포인트-권한 스펙 모델.
 *
 * <p>Gateway가 URL 기반 권한 검사를 위해 사용합니다.
 *
 * @param serviceName 서비스 이름 (예: "product-service")
 * @param pathPattern URL 패턴 (예: "/api/v1/users/{id}")
 * @param httpMethod HTTP 메서드 (예: "GET", "POST")
 * @param requiredPermissions 필요 권한 목록 (예: ["user:read"])
 * @param requiredRoles 필요 역할 목록 (예: ["ADMIN"])
 * @param isPublic 공개 엔드포인트 여부 (인증 불필요)
 * @param description 엔드포인트 설명
 */
public record EndpointPermissionSpec(
        String serviceName,
        String pathPattern,
        String httpMethod,
        List<String> requiredPermissions,
        List<String> requiredRoles,
        boolean isPublic,
        String description) {}
