package com.ryuqq.authhub.domain.permission.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;

import java.util.Map;

/**
 * DuplicatePermissionKeyException - 권한 키가 이미 존재할 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicatePermissionKeyException extends DomainException {

    public DuplicatePermissionKeyException(String permissionKey) {
        super(PermissionErrorCode.DUPLICATE_PERMISSION_KEY, Map.of("permissionKey", permissionKey));
    }
}
