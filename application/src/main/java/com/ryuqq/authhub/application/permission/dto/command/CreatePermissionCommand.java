package com.ryuqq.authhub.application.permission.dto.command;

/**
 * CreatePermissionCommand - 권한 생성 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param resource 리소스 식별자 (예: user, organization)
 * @param action 액션 식별자 (예: read, write, manage)
 * @param description 권한 설명 (선택)
 * @param isSystem 시스템 권한 여부 (true: 수정/삭제 불가)
 * @author development-team
 * @since 1.0.0
 */
public record CreatePermissionCommand(
        String resource, String action, String description, boolean isSystem) {}
