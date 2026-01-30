package com.ryuqq.authhub.adapter.in.rest.rolepermission.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.rolepermission.exception.DuplicateRolePermissionException;
import com.ryuqq.authhub.domain.rolepermission.exception.RolePermissionNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * RolePermissionErrorMapper - 역할-권한 관계 도메인 예외 → HTTP 응답 변환기
 *
 * <p>역할-권한 관계 도메인 예외를 RFC 7807 형식의 HTTP 응답으로 변환합니다.
 *
 * <p><strong>예외 타입 매핑:</strong>
 *
 * <ul>
 *   <li>RolePermissionNotFoundException → 404 Not Found
 *   <li>DuplicateRolePermissionException → 409 Conflict
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE =
            "https://authhub.ryuqq.com/errors/role-permission";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof RolePermissionNotFoundException
                || ex instanceof DuplicateRolePermissionException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        return switch (ex) {
            case RolePermissionNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Role Permission Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            case DuplicateRolePermissionException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Role Permission Duplicate",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/duplicate"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Role Permission Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
