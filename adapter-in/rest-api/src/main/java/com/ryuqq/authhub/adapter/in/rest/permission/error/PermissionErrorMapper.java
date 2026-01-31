package com.ryuqq.authhub.adapter.in.rest.permission.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.permission.exception.DuplicatePermissionKeyException;
import com.ryuqq.authhub.domain.permission.exception.PermissionInUseException;
import com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException;
import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotDeletableException;
import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotModifiableException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * PermissionErrorMapper - 권한 도메인 예외 → HTTP 응답 변환기
 *
 * <p>권한 도메인 예외를 RFC 7807 형식의 HTTP 응답으로 변환합니다.
 *
 * <p><strong>예외 타입 매핑:</strong>
 *
 * <ul>
 *   <li>PermissionNotFoundException → 404 Not Found
 *   <li>DuplicatePermissionKeyException → 409 Conflict
 *   <li>SystemPermissionNotModifiableException → 403 Forbidden
 *   <li>SystemPermissionNotDeletableException → 403 Forbidden
 *   <li>PermissionInUseException → 409 Conflict
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "https://authhub.ryuqq.com/errors/permission";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof PermissionNotFoundException
                || ex instanceof DuplicatePermissionKeyException
                || ex instanceof SystemPermissionNotModifiableException
                || ex instanceof SystemPermissionNotDeletableException
                || ex instanceof PermissionInUseException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        return switch (ex) {
            case PermissionNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Permission Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            case DuplicatePermissionKeyException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Permission Key Duplicate",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/duplicate-key"));

            case SystemPermissionNotModifiableException e ->
                    new MappedError(
                            HttpStatus.FORBIDDEN,
                            "System Permission Not Modifiable",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/system-permission-not-modifiable"));

            case SystemPermissionNotDeletableException e ->
                    new MappedError(
                            HttpStatus.FORBIDDEN,
                            "System Permission Not Deletable",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/system-permission-not-deletable"));

            case PermissionInUseException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Permission In Use",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/in-use"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Permission Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
