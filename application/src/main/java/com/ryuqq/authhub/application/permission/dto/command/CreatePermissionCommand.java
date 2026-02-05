package com.ryuqq.authhub.application.permission.dto.command;

/**
 * CreatePermissionCommand - 권한 생성 Command DTO (Global Only)
 *
 * <p>권한 생성에 필요한 데이터를 전달하는 Command DTO입니다.
 *
 * <p><strong>Global Only 설계:</strong>
 *
 * <ul>
 *   <li>모든 Permission은 전체 시스템에서 공유됩니다
 *   <li>테넌트 관련 필드가 제거되었습니다
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param serviceId 서비스 ID (필수)
 * @param resource 리소스명 (필수, 예: user, role)
 * @param action 행위명 (필수, 예: read, create, update, delete)
 * @param description 권한 설명 (선택)
 * @param isSystem 시스템 권한 여부 (true면 SYSTEM, false면 CUSTOM)
 * @author development-team
 * @since 1.0.0
 */
public record CreatePermissionCommand(
        Long serviceId, String resource, String action, String description, boolean isSystem) {}
