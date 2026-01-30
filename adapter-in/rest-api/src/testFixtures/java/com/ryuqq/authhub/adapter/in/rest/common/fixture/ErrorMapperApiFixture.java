package com.ryuqq.authhub.adapter.in.rest.common.fixture;

import com.ryuqq.authhub.domain.auth.exception.InvalidCredentialsException;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.organization.exception.DuplicateOrganizationNameException;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.permission.exception.DuplicatePermissionKeyException;
import com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException;
import com.ryuqq.authhub.domain.permissionendpoint.exception.DuplicatePermissionEndpointException;
import com.ryuqq.authhub.domain.permissionendpoint.exception.PermissionEndpointNotFoundException;
import com.ryuqq.authhub.domain.role.exception.DuplicateRoleNameException;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.exception.DuplicateRolePermissionException;
import com.ryuqq.authhub.domain.rolepermission.exception.RolePermissionNotFoundException;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.exception.DuplicateUserRoleException;
import com.ryuqq.authhub.domain.userrole.exception.UserRoleNotFoundException;

/**
 * ErrorMapper 테스트용 DomainException 픽스처
 *
 * <p>각 ErrorMapper 단위 테스트에서 도메인 예외를 생성할 때 사용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ErrorMapperApiFixture {

    private static final String TEST_TENANT_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String TEST_TENANT_NAME = "테스트테넌트";
    private static final String TEST_ROLE_NAME = "ROLE_ADMIN";
    private static final String TEST_ORG_NAME = "테스트조직";
    private static final String TEST_PERMISSION_KEY = "user:read";
    private static final String TEST_USER_ID = "550e8400-e29b-41d4-a716-446655440001";

    private ErrorMapperApiFixture() {}

    // ========== Tenant ==========

    public static TenantNotFoundException tenantNotFoundException() {
        return new TenantNotFoundException(TEST_TENANT_ID);
    }

    public static DuplicateTenantNameException duplicateTenantNameException() {
        return new DuplicateTenantNameException(TEST_TENANT_NAME);
    }

    // ========== Role ==========

    public static RoleNotFoundException roleNotFoundException() {
        return new RoleNotFoundException(TEST_ROLE_NAME);
    }

    public static DuplicateRoleNameException duplicateRoleNameException() {
        return new DuplicateRoleNameException(TEST_ROLE_NAME);
    }

    // ========== Organization ==========

    public static OrganizationNotFoundException organizationNotFoundException() {
        return new OrganizationNotFoundException(TEST_TENANT_ID);
    }

    public static DuplicateOrganizationNameException duplicateOrganizationNameException() {
        return new DuplicateOrganizationNameException(TEST_TENANT_ID, TEST_ORG_NAME);
    }

    // ========== Permission ==========

    public static PermissionNotFoundException permissionNotFoundException() {
        return new PermissionNotFoundException(1L);
    }

    public static DuplicatePermissionKeyException duplicatePermissionKeyException() {
        return new DuplicatePermissionKeyException(TEST_PERMISSION_KEY);
    }

    // ========== PermissionEndpoint ==========

    public static PermissionEndpointNotFoundException permissionEndpointNotFoundException() {
        return new PermissionEndpointNotFoundException(1L);
    }

    public static DuplicatePermissionEndpointException duplicatePermissionEndpointException() {
        return new DuplicatePermissionEndpointException("/api/v1/users", "GET");
    }

    // ========== RolePermission ==========

    public static RolePermissionNotFoundException rolePermissionNotFoundException() {
        return new RolePermissionNotFoundException(1L, 1L);
    }

    public static DuplicateRolePermissionException duplicateRolePermissionException() {
        return new DuplicateRolePermissionException(1L, 1L);
    }

    // ========== User ==========

    public static UserNotFoundException userNotFoundException() {
        return new UserNotFoundException(TEST_USER_ID);
    }

    // ========== UserRole ==========

    public static UserRoleNotFoundException userRoleNotFoundException() {
        return new UserRoleNotFoundException(UserId.of(TEST_USER_ID), RoleId.of(1L));
    }

    public static DuplicateUserRoleException duplicateUserRoleException() {
        return new DuplicateUserRoleException(UserId.of(TEST_USER_ID), RoleId.of(1L));
    }

    // ========== Auth (AUTH- prefix) ==========

    public static InvalidCredentialsException invalidCredentialsException() {
        return new InvalidCredentialsException();
    }

    // ========== Generic (DomainExceptionFixture 호환) ==========

    /** supports() false 반환 테스트용 - 다른 도메인 예외 */
    public static DomainException unsupportedDomainException() {
        return tenantNotFoundException();
    }
}
