package com.ryuqq.authhub.adapter.in.rest.tenant.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * TenantErrorMapper - 테넌트 도메인 예외 → HTTP 응답 변환기
 *
 * <p>테넌트 도메인 예외를 RFC 7807 형식의 HTTP 응답으로 변환합니다.
 *
 * <p><strong>예외 타입 매핑:</strong>
 *
 * <ul>
 *   <li>TenantNotFoundException → 404 Not Found
 *   <li>DuplicateTenantNameException → 409 Conflict
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "https://authhub.ryuqq.com/errors/tenant";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof TenantNotFoundException || ex instanceof DuplicateTenantNameException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        return switch (ex) {
            case TenantNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Tenant Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            case DuplicateTenantNameException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Tenant Name Duplicate",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/duplicate-name"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Tenant Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
