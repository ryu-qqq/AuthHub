package com.ryuqq.authhub.application.permission.dto.command;

/**
 * DeletePermissionCommand - 권한 삭제 Command DTO
 *
 * <p>CUSTOM 권한만 삭제 가능합니다. SYSTEM 권한은 삭제 불가합니다. 삭제 전 해당 권한이 Role에 할당되어 있는지 검증합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param permissionId 삭제할 권한 ID (필수)
 * @author development-team
 * @since 1.0.0
 */
public record DeletePermissionCommand(Long permissionId) {}
