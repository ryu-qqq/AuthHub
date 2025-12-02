package com.ryuqq.authhub.application.user.dto.command;

import java.util.UUID;

/**
 * UpdateUserCommand - 사용자 정보 수정 요청 데이터
 *
 * <p>사용자 정보 수정에 필요한 데이터를 전달하는 불변 Command DTO입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 * <ul>
 *   <li>순수 Java Record (Lombok 금지)</li>
 *   <li>jakarta.validation 금지</li>
 *   <li>비즈니스 로직/검증 금지 (데이터 전달만)</li>
 * </ul>
 *
 * @param userId 사용자 ID (필수)
 * @param name 이름 (선택)
 * @param nickname 닉네임 (선택)
 * @param profileImageUrl 프로필 이미지 URL (선택)
 *
 * @author development-team
 * @since 1.0.0
 */
public record UpdateUserCommand(
        UUID userId,
        String name,
        String nickname,
        String profileImageUrl
) {
}
