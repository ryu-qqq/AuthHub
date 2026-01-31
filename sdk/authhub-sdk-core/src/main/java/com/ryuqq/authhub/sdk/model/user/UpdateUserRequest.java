package com.ryuqq.authhub.sdk.model.user;

/**
 * 사용자 정보 수정 요청 DTO.
 *
 * @param phoneNumber 전화번호 (null이면 변경 안 함)
 */
public record UpdateUserRequest(String phoneNumber) {}
