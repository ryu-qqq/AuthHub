package com.ryuqq.authhub.adapter.in.rest.common.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * ErrorMapper 레지스트리
 *
 * <p>등록된 ErrorMapper들을 관리하고 Domain Exception을 적절한 HTTP 응답으로 매핑합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class ErrorMapperRegistry {
    private final List<ErrorMapper> mappers;

    public ErrorMapperRegistry(List<ErrorMapper> mappers) {
        this.mappers = mappers;
    }

    public Optional<ErrorMapper.MappedError> map(DomainException ex, Locale locale) {
        return mappers.stream()
                .filter(m -> m.supports(ex.code()))
                .findFirst()
                .map(m -> m.map(ex, locale));
    }

    public ErrorMapper.MappedError defaultMapping(DomainException ex) {
        return new ErrorMapper.MappedError(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage() != null ? ex.getMessage() : "Invalid request",
                URI.create("about:blank"));
    }
}
