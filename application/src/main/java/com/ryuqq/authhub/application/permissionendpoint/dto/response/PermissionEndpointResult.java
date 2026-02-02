package com.ryuqq.authhub.application.permissionendpoint.dto.response;

import java.time.Instant;

/**
 * PermissionEndpointResult - 엔드포인트 조회 결과 DTO
 *
 * <p>Application Layer에서 사용하는 PermissionEndpoint 응답 DTO입니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-007: Response DTO는 createdAt, updatedAt 시간 필드 필수 포함.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param permissionEndpointId 엔드포인트 ID
 * @param permissionId 연결된 권한 ID
 * @param serviceName 서비스 이름
 * @param urlPattern URL 패턴
 * @param httpMethod HTTP 메서드
 * @param description 설명
 * @param isPublic 공개 엔드포인트 여부
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author development-team
 * @since 1.0.0
 */
public record PermissionEndpointResult(
        Long permissionEndpointId,
        Long permissionId,
        String serviceName,
        String urlPattern,
        String httpMethod,
        String description,
        boolean isPublic,
        Instant createdAt,
        Instant updatedAt) {}
