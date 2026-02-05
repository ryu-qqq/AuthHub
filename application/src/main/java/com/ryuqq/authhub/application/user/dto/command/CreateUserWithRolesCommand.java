package com.ryuqq.authhub.application.user.dto.command;

import java.util.List;

/**
 * CreateUserWithRolesCommand - 사용자 생성 + 역할 할당 Command DTO
 *
 * <p>사용자 생성과 SERVICE scope Role 자동 할당을 한 번에 처리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param organizationId 소속 조직 ID (필수)
 * @param identifier 로그인 식별자 (필수, 이메일 또는 사용자명)
 * @param phoneNumber 전화번호 (선택)
 * @param rawPassword 평문 비밀번호 (필수, 해싱 전)
 * @param serviceCode 서비스 코드 (선택, 예: SVC_STORE)
 * @param roleNames 역할 이름 목록 (선택, 예: ["ADMIN"])
 * @author development-team
 * @since 1.0.0
 */
public record CreateUserWithRolesCommand(
        String organizationId,
        String identifier,
        String phoneNumber,
        String rawPassword,
        String serviceCode,
        List<String> roleNames) {}
