package com.ryuqq.authhub.application.userrole.dto.response;

import java.time.Instant;
import java.util.Set;

/**
 * UserPermissionsResult - Gateway용 사용자 권한 조회 결과 DTO
 *
 * <p>Gateway가 사용자 인가 검증을 위해 필요한 역할/권한 정보를 제공합니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param userId 사용자 ID
 * @param roles 역할 이름 Set
 * @param permissions 권한 키 Set
 * @param hash 권한 해시 (변경 감지용)
 * @param generatedAt 권한 생성 시점
 * @author development-team
 * @since 1.0.0
 */
public record UserPermissionsResult(
        String userId,
        Set<String> roles,
        Set<String> permissions,
        String hash,
        Instant generatedAt) {

    public static UserPermissionsResult empty(String userId) {
        return new UserPermissionsResult(userId, Set.of(), Set.of(), "", Instant.now());
    }
}
