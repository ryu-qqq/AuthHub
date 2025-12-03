package com.ryuqq.authhub.application.user.dto.command;

/**
 * CreateUserCommand - 사용자 생성 요청 데이터
 *
 * <p>사용자 생성에 필요한 데이터를 전달하는 불변 Command DTO입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (Lombok 금지)
 *   <li>jakarta.validation 금지
 *   <li>비즈니스 로직/검증 금지 (데이터 전달만)
 * </ul>
 *
 * @param tenantId 테넌트 ID (필수)
 * @param organizationId 조직 ID (필수)
 * @param identifier 로그인 식별자 (이메일 또는 아이디) (필수)
 * @param rawPassword 평문 비밀번호 (필수)
 * @param userType 사용자 유형 (PUBLIC, ADMIN 등) (선택, 기본값 PUBLIC)
 * @param name 이름 (선택)
 * @param phoneNumber 전화번호 (선택, Tenant별 유니크)
 * @author development-team
 * @since 1.0.0
 */
public record CreateUserCommand(
        Long tenantId,
        Long organizationId,
        String identifier,
        String rawPassword,
        String userType,
        String name,
        String phoneNumber) {}
