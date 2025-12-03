package com.ryuqq.authhub.adapter.in.rest.user.dto.command;

/**
 * 사용자 정보 수정 API 요청 DTO
 *
 * <p>사용자 프로필 정보(이름, 전화번호) 수정 요청 데이터를 전달합니다.
 * 모든 필드가 선택적이며, null인 필드는 기존 값을 유지합니다.
 *
 * @param name 이름 (선택)
 * @param phoneNumber 전화번호 (선택)
 * @author development-team
 * @since 1.0.0
 */
public record UpdateUserApiRequest(
        String name,
        String phoneNumber
) {}
