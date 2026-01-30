package com.ryuqq.authhub.domain.permission.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * DuplicatePermissionKeyException - 중복된 권한 키 예외
 *
 * <p>이미 존재하는 permissionKey로 권한 생성 시도 시 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicatePermissionKeyException extends DomainException {

    public DuplicatePermissionKeyException(String permissionKey) {
        super(PermissionErrorCode.DUPLICATE_PERMISSION_KEY, Map.of("permissionKey", permissionKey));
    }
}
