package com.ryuqq.authhub.application.permission.dto.command;

import java.util.UUID;

/**
 * UpdatePermissionCommand - 권한 수정 Command DTO
 *
 * <p>CUSTOM 권한의 설명만 수정 가능합니다. SYSTEM 권한은 수정 불가.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param permissionId 권한 ID
 * @param description 새 권한 설명
 * @author development-team
 * @since 1.0.0
 */
public record UpdatePermissionCommand(UUID permissionId, String description) {}
