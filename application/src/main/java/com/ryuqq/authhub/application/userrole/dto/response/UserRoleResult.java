package com.ryuqq.authhub.application.userrole.dto.response;

import java.time.Instant;

/**
 * UserRoleResult - 사용자-역할 관계 조회 결과 DTO
 *
 * <p>사용자-역할 관계 조회 시 반환하는 결과 DTO입니다.
 *
 * @param userRoleId 관계 ID
 * @param userId 사용자 ID
 * @param roleId 역할 ID
 * @param roleName 역할 이름
 * @param createdAt 생성 시간
 * @author development-team
 * @since 1.0.0
 */
public record UserRoleResult(
        Long userRoleId, String userId, Long roleId, String roleName, Instant createdAt) {}
