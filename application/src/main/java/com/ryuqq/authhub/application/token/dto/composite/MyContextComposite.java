package com.ryuqq.authhub.application.token.dto.composite;

import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;

/**
 * MyContextComposite - 내 컨텍스트 Composite
 *
 * <p>사용자 기본 정보(User, Organization, Tenant)와 역할/권한 정보를 함께 담는 Composite입니다.
 *
 * <p><strong>구성 요소:</strong>
 *
 * <ul>
 *   <li>UserContextComposite: 사용자 + 조직 + 테넌트 조인 정보
 *   <li>RolesAndPermissionsComposite: 역할 이름 + 권한 키
 * </ul>
 *
 * @param userContext 사용자 컨텍스트 (User + Organization + Tenant)
 * @param rolesAndPermissions 역할 및 권한 정보
 * @author development-team
 * @since 1.0.0
 */
public record MyContextComposite(
        UserContextComposite userContext, RolesAndPermissionsComposite rolesAndPermissions) {}
