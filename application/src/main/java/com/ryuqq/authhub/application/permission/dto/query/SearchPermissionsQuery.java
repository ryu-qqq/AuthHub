package com.ryuqq.authhub.application.permission.dto.query;

import com.ryuqq.authhub.domain.permission.vo.PermissionType;

/**
 * SearchPermissionsQuery - 권한 검색 Query DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param resource 리소스 필터 (선택)
 * @param action 액션 필터 (선택)
 * @param type 권한 타입 필터 (선택, SYSTEM/CUSTOM)
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
public record SearchPermissionsQuery(
        String resource, String action, PermissionType type, int page, int size) {}
