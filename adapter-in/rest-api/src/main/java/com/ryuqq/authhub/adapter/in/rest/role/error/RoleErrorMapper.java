package com.ryuqq.authhub.adapter.in.rest.role.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.exception.DuplicateRoleNameException;
import com.ryuqq.authhub.domain.role.exception.RoleInUseException;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.exception.SystemRoleNotDeletableException;
import com.ryuqq.authhub.domain.role.exception.SystemRoleNotModifiableException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * RoleErrorMapper - 역할 도메인 예외 → HTTP 응답 변환기
 *
 * <p>역할 도메인 예외를 RFC 7807 형식의 HTTP 응답으로 변환합니다.
 *
 * <p><strong>예외 타입 매핑:</strong>
 *
 * <ul>
 *   <li>RoleNotFoundException → 404 Not Found
 *   <li>DuplicateRoleNameException → 409 Conflict
 *   <li>SystemRoleNotModifiableException → 403 Forbidden
 *   <li>SystemRoleNotDeletableException → 403 Forbidden
 *   <li>RoleInUseException → 409 Conflict
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "https://authhub.ryuqq.com/errors/role";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof RoleNotFoundException
                || ex instanceof DuplicateRoleNameException
                || ex instanceof SystemRoleNotModifiableException
                || ex instanceof SystemRoleNotDeletableException
                || ex instanceof RoleInUseException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        return switch (ex) {
            case RoleNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Role Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            case DuplicateRoleNameException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Role Name Duplicate",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/duplicate-name"));

            case SystemRoleNotModifiableException e ->
                    new MappedError(
                            HttpStatus.FORBIDDEN,
                            "System Role Not Modifiable",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/system-role-not-modifiable"));

            case SystemRoleNotDeletableException e ->
                    new MappedError(
                            HttpStatus.FORBIDDEN,
                            "System Role Not Deletable",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/system-role-not-deletable"));

            case RoleInUseException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Role In Use",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/in-use"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Role Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
