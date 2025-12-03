package com.ryuqq.authhub.adapter.in.rest.organization.error;

import java.net.URI;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;

/**
 * Organization Bounded Context ErrorMapper
 *
 * <p>ORGANIZATION- 접두사를 가진 Domain Exception을 HTTP Problem Details로 변환합니다.
 *
 * <p>지원하는 에러 코드:
 * <ul>
 *   <li>ORGANIZATION-001 (ORGANIZATION_NOT_FOUND) → 404 Not Found</li>
 *   <li>ORGANIZATION-005 (ACTIVE_USERS_EXIST) → 409 Conflict</li>
 *   <li>기타 → 400 Bad Request</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationErrorMapper implements ErrorMapper {

    private static final String PREFIX = "ORGANIZATION-";
    private static final String TYPE_BASE = "https://api.authhub.com/problems/organization/";

    private final MessageSource messageSource;

    public OrganizationErrorMapper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(String code) {
        return code != null && code.startsWith(PREFIX);
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        String code = ex.code();

        HttpStatus status = mapHttpStatus(code);
        String title = resolveTitle(code, status, locale);
        String detail = resolveDetail(code, ex, locale);
        URI type = createTypeUri(code);

        return new MappedError(status, title, detail, type);
    }

    private HttpStatus mapHttpStatus(String code) {
        return switch (code) {
            // 404 - 리소스 없음
            case "ORGANIZATION-001" -> HttpStatus.NOT_FOUND;

            // 409 - 충돌
            case "ORGANIZATION-005" -> HttpStatus.CONFLICT;

            // 400 - 잘못된 요청 (기본값)
            default -> HttpStatus.BAD_REQUEST;
        };
    }

    private String resolveTitle(String code, HttpStatus status, Locale locale) {
        String key = "problem.title." + code.toLowerCase();
        return messageSource.getMessage(
                key,
                new Object[0],
                status.getReasonPhrase(),
                locale
        );
    }

    private String resolveDetail(String code, DomainException ex, Locale locale) {
        String key = "problem.detail." + code.toLowerCase();
        return messageSource.getMessage(
                key,
                ex.args().values().toArray(),
                ex.getMessage(),
                locale
        );
    }

    private URI createTypeUri(String code) {
        String path = code.toLowerCase();
        return URI.create(TYPE_BASE + path);
    }
}
