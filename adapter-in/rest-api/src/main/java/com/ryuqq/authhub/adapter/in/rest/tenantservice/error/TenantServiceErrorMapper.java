package com.ryuqq.authhub.adapter.in.rest.tenantservice.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.tenantservice.exception.DuplicateTenantServiceException;
import com.ryuqq.authhub.domain.tenantservice.exception.TenantServiceNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * TenantServiceErrorMapper - 테넌트-서비스 도메인 예외 → HTTP 응답 변환기
 *
 * <p>테넌트-서비스 도메인 예외를 RFC 7807 형식의 HTTP 응답으로 변환합니다.
 *
 * <p><strong>예외 타입 매핑:</strong>
 *
 * <ul>
 *   <li>TenantServiceNotFoundException → 404 Not Found
 *   <li>DuplicateTenantServiceException → 409 Conflict
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantServiceErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "https://authhub.ryuqq.com/errors/tenant-service";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof TenantServiceNotFoundException
                || ex instanceof DuplicateTenantServiceException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        return switch (ex) {
            case TenantServiceNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Tenant Service Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            case DuplicateTenantServiceException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Tenant Service Already Subscribed",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/duplicate"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Tenant Service Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
