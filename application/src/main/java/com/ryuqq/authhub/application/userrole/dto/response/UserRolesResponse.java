package com.ryuqq.authhub.application.userrole.dto.response;

import java.util.Set;

/**
 * UserRolesResponse - 사용자 역할/권한 조회 결과 DTO
 *
 * <p>사용자의 역할 및 권한 목록을 반환하는 DTO입니다. JWT 토큰 발급 시 TokenManager에서 사용합니다.
 *
 * <p><strong>포함 정보:</strong>
 *
 * <ul>
 *   <li>roles - 사용자에게 할당된 역할 이름 목록
 *   <li>permissions - 사용자 역할을 통해 부여된 권한 코드 목록
 * </ul>
 *
 * @param roles 역할 이름 목록
 * @param permissions 권한 코드 목록
 * @author development-team
 * @since 1.0.0
 */
public record UserRolesResponse(Set<String> roles, Set<String> permissions) {

    /**
     * 빈 응답 생성
     *
     * @return 빈 역할/권한 응답
     */
    public static UserRolesResponse empty() {
        return new UserRolesResponse(Set.of(), Set.of());
    }
}
