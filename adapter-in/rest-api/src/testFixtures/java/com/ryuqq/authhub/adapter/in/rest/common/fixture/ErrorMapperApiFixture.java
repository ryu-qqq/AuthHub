package com.ryuqq.authhub.adapter.in.rest.common.fixture;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.organization.exception.DuplicateOrganizationNameException;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.permission.exception.DuplicatePermissionKeyException;
import com.ryuqq.authhub.domain.permission.exception.PermissionInUseException;
import com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException;
import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotDeletableException;
import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotModifiableException;
import com.ryuqq.authhub.domain.permissionendpoint.exception.DuplicatePermissionEndpointException;
import com.ryuqq.authhub.domain.permissionendpoint.exception.PermissionEndpointNotFoundException;
import com.ryuqq.authhub.domain.role.exception.DuplicateRoleNameException;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.exception.SystemRoleNotDeletableException;
import com.ryuqq.authhub.domain.role.exception.SystemRoleNotModifiableException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.exception.DuplicateRolePermissionException;
import com.ryuqq.authhub.domain.rolepermission.exception.RolePermissionNotFoundException;
import com.ryuqq.authhub.domain.service.exception.DuplicateServiceIdException;
import com.ryuqq.authhub.domain.service.exception.ServiceInUseException;
import com.ryuqq.authhub.domain.service.exception.ServiceNotFoundException;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenantservice.exception.DuplicateTenantServiceException;
import com.ryuqq.authhub.domain.tenantservice.exception.TenantServiceNotFoundException;
import com.ryuqq.authhub.domain.token.exception.AccessForbiddenException;
import com.ryuqq.authhub.domain.token.exception.InvalidCredentialsException;
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
    private static final Long TEST_SERVICE_ID = 1L;

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

    public static SystemRoleNotModifiableException systemRoleNotModifiableException() {
        return new SystemRoleNotModifiableException(TEST_ROLE_NAME);
    }

    public static SystemRoleNotDeletableException systemRoleNotDeletableException() {
        return new SystemRoleNotDeletableException(TEST_ROLE_NAME);
    }

    public static com.ryuqq.authhub.domain.role.exception.RoleInUseException roleInUseException() {
        return new com.ryuqq.authhub.domain.role.exception.RoleInUseException(RoleId.of(1L));
    }

    public static com.ryuqq.authhub.domain.userrole.exception.RoleInUseException
            userRoleInUseException() {
        return new com.ryuqq.authhub.domain.userrole.exception.RoleInUseException(RoleId.of(1L));
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

    public static SystemPermissionNotModifiableException systemPermissionNotModifiableException() {
        return new SystemPermissionNotModifiableException(TEST_PERMISSION_KEY);
    }

    public static SystemPermissionNotDeletableException systemPermissionNotDeletableException() {
        return new SystemPermissionNotDeletableException(TEST_PERMISSION_KEY);
    }

    public static PermissionInUseException permissionInUseException() {
        return new PermissionInUseException(1L);
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

    // ========== TenantService ==========

    public static TenantServiceNotFoundException tenantServiceNotFoundException() {
        return new TenantServiceNotFoundException(TEST_TENANT_ID, TEST_SERVICE_ID);
    }

    public static DuplicateTenantServiceException duplicateTenantServiceException() {
        return new DuplicateTenantServiceException(TEST_TENANT_ID, TEST_SERVICE_ID);
    }

    // ========== Service ==========

    public static ServiceNotFoundException serviceNotFoundException() {
        return new ServiceNotFoundException(TEST_SERVICE_ID.toString());
    }

    public static DuplicateServiceIdException duplicateServiceIdException() {
        return new DuplicateServiceIdException("SVC_STORE");
    }

    public static ServiceInUseException serviceInUseException() {
        return new ServiceInUseException(TEST_SERVICE_ID.toString());
    }

    // ========== Auth (AUTH- prefix) ==========

    public static InvalidCredentialsException invalidCredentialsException() {
        return new InvalidCredentialsException();
    }

    /** AUTH-007 (403 Forbidden) 테스트용 */
    public static AccessForbiddenException accessForbiddenException() {
        return new AccessForbiddenException();
    }

    // ========== Generic (DomainExceptionFixture 호환) ==========

    /** supports() false 반환 테스트용 - 다른 도메인 예외 */
    public static DomainException unsupportedDomainException() {
        return tenantNotFoundException();
    }
}
