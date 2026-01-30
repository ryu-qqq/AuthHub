package com.ryuqq.authhub.application.userrole.dto.composite;

import java.util.Set;

/**
 * RolesAndPermissionsComposite - 역할 이름과 권한 키 Composite
 *
 * <p>사용자의 역할 이름과 권한 키를 함께 담는 Composite DTO입니다.
 *
 * @param roleNames 역할 이름 Set
 * @param permissionKeys 권한 키 Set
 * @author development-team
 * @since 1.0.0
 */
public record RolesAndPermissionsComposite(Set<String> roleNames, Set<String> permissionKeys) {

    public static RolesAndPermissionsComposite empty() {
        return new RolesAndPermissionsComposite(Set.of(), Set.of());
    }
}
