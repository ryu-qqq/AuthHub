package com.ryuqq.authhub.adapter.in.rest.service.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.service.exception.DuplicateServiceIdException;
import com.ryuqq.authhub.domain.service.exception.ServiceInUseException;
import com.ryuqq.authhub.domain.service.exception.ServiceNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * ServiceErrorMapper - 서비스 도메인 예외 → HTTP 응답 변환기
 *
 * <p>서비스 도메인 예외를 RFC 7807 형식의 HTTP 응답으로 변환합니다.
 *
 * <p><strong>예외 타입 매핑:</strong>
 *
 * <ul>
 *   <li>ServiceNotFoundException → 404 Not Found
 *   <li>DuplicateServiceIdException → 409 Conflict
 *   <li>ServiceInUseException → 409 Conflict
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class ServiceErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "https://authhub.ryuqq.com/errors/service";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ServiceNotFoundException
                || ex instanceof DuplicateServiceIdException
                || ex instanceof ServiceInUseException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        return switch (ex) {
            case ServiceNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Service Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            case DuplicateServiceIdException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Service Code Duplicate",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/duplicate-code"));

            case ServiceInUseException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Service In Use",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/in-use"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Service Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
