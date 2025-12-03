package com.ryuqq.authhub.adapter.in.rest.role.error;

import java.net.URI;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;

/**
 * Role Bounded Context ErrorMapper
 *
 * <p>ROLE- 접두사를 가진 Domain Exception을 HTTP Problem Details로 변환합니다.
 *
 * <p>지원하는 에러 코드:
 * <ul>
 *   <li>ROLE-001 (ROLE_NOT_FOUND) → 404 Not Found</li>
 *   <li>ROLE-002 (PERMISSION_NOT_FOUND) → 404 Not Found</li>
 *   <li>ROLE-003 (DUPLICATE_ROLE_NAME) → 409 Conflict</li>
 *   <li>ROLE-004 (DUPLICATE_PERMISSION_CODE) → 409 Conflict</li>
 *   <li>ROLE-005 (SYSTEM_ROLE_MODIFICATION_NOT_ALLOWED) → 403 Forbidden</li>
 *   <li>ROLE-006 (ROLE_ALREADY_ASSIGNED) → 409 Conflict</li>
 *   <li>ROLE-007 (ROLE_NOT_ASSIGNED) → 404 Not Found</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleErrorMapper implements ErrorMapper {

    private static final String PREFIX = "ROLE-";
    private static final String TYPE_BASE = "https://api.authhub.com/problems/role/";

    private final MessageSource messageSource;

    public RoleErrorMapper(MessageSource messageSource) {
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
            case "ROLE-001", "ROLE-002", "ROLE-007" -> HttpStatus.NOT_FOUND;

            // 403 - 접근 금지
            case "ROLE-005" -> HttpStatus.FORBIDDEN;

            // 409 - 충돌 (중복)
            case "ROLE-003", "ROLE-004", "ROLE-006" -> HttpStatus.CONFLICT;

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
