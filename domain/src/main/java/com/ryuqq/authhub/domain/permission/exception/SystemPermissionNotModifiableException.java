package com.ryuqq.authhub.domain.permission.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * SystemPermissionNotModifiableException - 시스템 권한 수정 시도 시 발생하는 예외
 *
 * <p>시스템(SYSTEM) 타입의 권한은 수정할 수 없습니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class SystemPermissionNotModifiableException extends DomainException {

    public SystemPermissionNotModifiableException(String permissionKey) {
        super(
                PermissionErrorCode.SYSTEM_PERMISSION_NOT_MODIFIABLE,
                Map.of("permissionKey", permissionKey));
    }
}
