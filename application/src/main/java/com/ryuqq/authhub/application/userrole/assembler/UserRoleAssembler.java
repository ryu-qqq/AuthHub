package com.ryuqq.authhub.application.userrole.assembler;

import com.ryuqq.authhub.application.userrole.dto.response.UserRolesResponse;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * UserRoleAssembler - 사용자 역할/권한 조합기
 *
 * <p>역할 이름과 권한 키를 UserRolesResponse로 조립합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>조회된 데이터를 응답 DTO로 변환
 *   <li>조회 로직 없음 (순수 조립만 담당)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleAssembler {

    /**
     * 역할 이름과 권한 키를 UserRolesResponse로 조립
     *
     * @param roleNames 역할 이름 Set
     * @param permissionKeys 권한 키 Set
     * @return 역할/권한 응답
     */
    public UserRolesResponse assemble(Set<String> roleNames, Set<String> permissionKeys) {
        return new UserRolesResponse(roleNames, permissionKeys);
    }
}
