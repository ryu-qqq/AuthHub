package com.ryuqq.authhub.application.permission.dto.command;

/**
 * UpdatePermissionCommand - 권한 수정 Command DTO
 *
 * <p>CUSTOM 권한의 설명만 수정 가능합니다. SYSTEM 권한은 수정 불가합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param permissionId 수정할 권한 ID (필수)
 * @param description 새로운 설명 (필수)
 * @author development-team
 * @since 1.0.0
 */
public record UpdatePermissionCommand(Long permissionId, String description) {}
