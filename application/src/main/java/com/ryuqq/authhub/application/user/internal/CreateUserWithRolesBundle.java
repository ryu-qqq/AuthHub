package com.ryuqq.authhub.application.user.internal;

import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import java.util.List;

/**
 * CreateUserWithRolesBundle - User + UserRole 영속화 번들
 *
 * <p>불변 컨테이너입니다. {@code withUserRoles()}로 UserRole을 추가한 새 인스턴스를 반환합니다.
 *
 * @param user User 도메인 객체
 * @param userRoles UserRole 도메인 객체 목록
 * @param serviceCode 서비스 코드 (Role resolve용, nullable)
 * @param roleNames 역할 이름 목록 (Role resolve용, nullable)
 * @author development-team
 * @since 1.0.0
 */
public record CreateUserWithRolesBundle(
        User user, List<UserRole> userRoles, String serviceCode, List<String> roleNames) {

    public static CreateUserWithRolesBundle of(
            User user, String serviceCode, List<String> roleNames) {
        return new CreateUserWithRolesBundle(user, List.of(), serviceCode, roleNames);
    }

    CreateUserWithRolesBundle withUserRoles(List<UserRole> userRoles) {
        return new CreateUserWithRolesBundle(
                this.user, userRoles, this.serviceCode, this.roleNames);
    }

    int assignedRoleCount() {
        return userRoles.size();
    }
}
