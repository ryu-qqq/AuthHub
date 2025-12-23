package com.ryuqq.authhub.application.permission.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * PermissionUsageResponse - 권한 사용 이력 Response DTO
 *
 * <p>권한 사용 이력 조회/등록 결과를 반환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param usageId 사용 이력 ID
 * @param permissionKey 권한 키
 * @param serviceName 서비스명
 * @param locations 코드 위치 목록
 * @param lastScannedAt 마지막 스캔 시간
 * @param createdAt 생성 시간
 * @author development-team
 * @since 1.0.0
 */
public record PermissionUsageResponse(
        UUID usageId,
        String permissionKey,
        String serviceName,
        List<String> locations,
        Instant lastScannedAt,
        Instant createdAt) {}
