package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.permissionendpoint.exception.DuplicatePermissionEndpointException;
import com.ryuqq.authhub.domain.permissionendpoint.exception.PermissionEndpointNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * PermissionEndpointErrorMapper - PermissionEndpoint 도메인 예외 → HTTP 응답 변환기
 *
 * <p>PermissionEndpoint 도메인 예외를 RFC 7807 형식의 HTTP 응답으로 변환합니다.
 *
 * <p><strong>예외 타입 매핑:</strong>
 *
 * <ul>
 *   <li>PermissionEndpointNotFoundException → 404 Not Found
 *   <li>DuplicatePermissionEndpointException → 409 Conflict
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionEndpointErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE =
            "https://authhub.ryuqq.com/errors/permission-endpoint";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof PermissionEndpointNotFoundException
                || ex instanceof DuplicatePermissionEndpointException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        return switch (ex) {
            case PermissionEndpointNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Permission Endpoint Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            case DuplicatePermissionEndpointException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Permission Endpoint Duplicate",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/duplicate"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Permission Endpoint Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
