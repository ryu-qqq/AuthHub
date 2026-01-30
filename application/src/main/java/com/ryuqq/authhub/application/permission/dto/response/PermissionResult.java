package com.ryuqq.authhub.application.permission.dto.response;

import java.time.Instant;

/**
 * PermissionResult - 권한 조회 결과 DTO (Global Only)
 *
 * <p>Application Layer에서 사용하는 Permission 응답 DTO입니다.
 *
 * <p><strong>Global Only 설계:</strong>
 *
 * <ul>
 *   <li>모든 Permission은 전체 시스템에서 공유됩니다
 *   <li>테넌트 관련 필드가 제거되었습니다
 * </ul>
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-007: Response DTO는 createdAt, updatedAt 시간 필드 필수 포함.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param permissionId 권한 ID
 * @param permissionKey 권한 키 (예: "user:read")
 * @param resource 리소스명
 * @param action 행위명
 * @param description 권한 설명
 * @param type 권한 유형 (SYSTEM 또는 CUSTOM)
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author development-team
 * @since 1.0.0
 */
public record PermissionResult(
        Long permissionId,
        String permissionKey,
        String resource,
        String action,
        String description,
        String type,
        Instant createdAt,
        Instant updatedAt) {}
