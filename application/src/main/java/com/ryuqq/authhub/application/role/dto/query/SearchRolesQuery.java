package com.ryuqq.authhub.application.role.dto.query;

import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import java.util.UUID;

/**
 * SearchRolesQuery - 역할 검색 Query DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param tenantId 테넌트 ID 필터 (선택)
 * @param name 역할 이름 필터 (선택)
 * @param scope 역할 범위 필터 (선택, GLOBAL/TENANT/ORGANIZATION)
 * @param type 역할 타입 필터 (선택, SYSTEM/CUSTOM)
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
public record SearchRolesQuery(
        UUID tenantId, String name, RoleScope scope, RoleType type, int page, int size) {}
