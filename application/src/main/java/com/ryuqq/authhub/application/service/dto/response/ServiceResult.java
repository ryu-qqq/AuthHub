package com.ryuqq.authhub.application.service.dto.response;

import java.time.Instant;

/**
 * ServiceResult - 서비스 조회 결과 DTO
 *
 * <p>Application Layer에서 사용하는 Service 응답 DTO입니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-007: Response DTO는 createdAt, updatedAt 시간 필드 필수 포함.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param serviceId 서비스 ID
 * @param serviceCode 서비스 코드
 * @param name 서비스 이름
 * @param description 서비스 설명
 * @param status 서비스 상태 (ACTIVE, INACTIVE)
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author development-team
 * @since 1.0.0
 */
public record ServiceResult(
        Long serviceId,
        String serviceCode,
        String name,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt) {}
