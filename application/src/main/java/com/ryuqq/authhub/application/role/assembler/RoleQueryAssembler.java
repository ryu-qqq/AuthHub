package com.ryuqq.authhub.application.role.assembler;

import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.vo.PermissionCode;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * RoleQueryAssembler - Role 조회 응답 조립기
 *
 * <p>Role Domain 객체를 Response DTO로 변환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Domain → DTO 변환만 담당
 *   <li>비즈니스 로직 포함 금지
 *   <li>외부 호출 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleQueryAssembler {

    /**
     * 사용자의 Role 목록을 UserRolesResponse로 변환
     *
     * @param userId 사용자 ID
     * @param roles 사용자에게 할당된 Role 목록
     * @return UserRolesResponse
     */
    public UserRolesResponse toUserRolesResponse(UserId userId, List<Role> roles) {
        Set<String> roleNames = new HashSet<>();
        Set<String> permissions = new HashSet<>();

        for (Role role : roles) {
            roleNames.add(role.nameValue());
            for (PermissionCode permissionCode : role.getPermissions()) {
                permissions.add(permissionCode.value());
            }
        }

        return new UserRolesResponse(userId.value(), roleNames, permissions);
    }
}
