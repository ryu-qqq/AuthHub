package com.ryuqq.authhub.application.user.dto.query;

import java.util.UUID;

/**
 * SearchUsersQuery - 사용자 목록 검색 Query DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param tenantId 테넌트 ID (필터)
 * @param organizationId 조직 ID (필터)
 * @param identifier 식별자 검색 (부분 일치)
 * @param status 상태 필터 (ACTIVE, INACTIVE, SUSPENDED)
 * @param page 페이지 번호
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
public record SearchUsersQuery(
        UUID tenantId, UUID organizationId, String identifier, String status, int page, int size) {}
