package com.ryuqq.authhub.application.user.dto.response;

import java.time.Instant;

/**
 * UserResult - 사용자 조회 결과 DTO
 *
 * <p>Application Layer에서 사용하는 User 응답 DTO입니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-007: Response DTO는 createdAt, updatedAt 시간 필드 필수 포함.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param userId 사용자 ID
 * @param organizationId 소속 조직 ID
 * @param identifier 로그인 식별자
 * @param phoneNumber 전화번호 (null 가능)
 * @param status 사용자 상태 (ACTIVE, INACTIVE, SUSPENDED)
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author development-team
 * @since 1.0.0
 */
public record UserResult(
        String userId,
        String organizationId,
        String identifier,
        String phoneNumber,
        String status,
        Instant createdAt,
        Instant updatedAt) {}
