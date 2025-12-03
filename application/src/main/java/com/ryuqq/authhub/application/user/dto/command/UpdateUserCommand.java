package com.ryuqq.authhub.application.user.dto.command;

import java.util.UUID;

/**
 * UpdateUserCommand - 사용자 정보 수정 요청 데이터
 *
 * <p>사용자 프로필 정보(이름, 전화번호) 수정에 필요한 데이터를 전달하는 불변 Command DTO입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (Lombok 금지)
 *   <li>jakarta.validation 금지
 *   <li>비즈니스 로직/검증 금지 (데이터 전달만)
 * </ul>
 *
 * <p><strong>Tenant별 phoneNumber 유니크 제약:</strong> 같은 Tenant 내에서 동일한 phoneNumber는 허용되지 않습니다. 이 제약은
 * Application Layer(Service)에서 검증합니다.
 *
 * @param userId 수정 대상 사용자 ID (필수)
 * @param name 이름 (선택, null이면 기존 값 유지)
 * @param phoneNumber 전화번호 (선택, null이면 기존 값 유지, Tenant별 유니크)
 * @author development-team
 * @since 1.0.0
 */
public record UpdateUserCommand(UUID userId, String name, String phoneNumber) {}
