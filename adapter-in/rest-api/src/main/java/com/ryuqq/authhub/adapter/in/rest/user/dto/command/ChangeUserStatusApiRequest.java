package com.ryuqq.authhub.adapter.in.rest.user.dto.command;

import jakarta.validation.constraints.NotBlank;

/**
 * 사용자 상태 변경 API 요청 DTO
 *
 * <p>사용자 상태 변경 요청 데이터를 전달합니다.
 *
 * <p><strong>지원 상태:</strong>
 * <ul>
 *   <li>ACTIVE - 활성 상태</li>
 *   <li>INACTIVE - 비활성 상태</li>
 *   <li>SUSPENDED - 정지 상태</li>
 *   <li>DELETED - 삭제 상태</li>
 * </ul>
 *
 * @param targetStatus 변경할 상태 (필수)
 * @param reason 상태 변경 사유 (선택)
 * @author development-team
 * @since 1.0.0
 */
public record ChangeUserStatusApiRequest(
        @NotBlank(message = "대상 상태는 필수입니다")
        String targetStatus,

        String reason
) {}
