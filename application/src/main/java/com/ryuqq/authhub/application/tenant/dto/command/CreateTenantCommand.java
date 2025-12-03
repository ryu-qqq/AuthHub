package com.ryuqq.authhub.application.tenant.dto.command;

/**
 * Create Tenant Command DTO
 *
 * <p>테넌트 생성 요청 데이터를 전달하는 명령 객체입니다.
 *
 * <p><strong>최소 필드 원칙:</strong> name만 포함
 *
 * @author development-team
 * @since 1.0.0
 */
public record CreateTenantCommand(String name) {}
