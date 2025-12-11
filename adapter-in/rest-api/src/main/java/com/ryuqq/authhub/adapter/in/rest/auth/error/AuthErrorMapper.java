package com.ryuqq.authhub.adapter.in.rest.auth.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Auth Bounded Context ErrorMapper
 *
 * <p>AUTH- 접두사를 가진 Domain Exception을 HTTP Problem Details로 변환합니다.
 *
 * <p>지원하는 에러 코드:
 *
 * <ul>
 *   <li>AUTH-001 ~ AUTH-006 → 401 Unauthorized
 *   <li>AUTH-007 (FORBIDDEN) → 403 Forbidden
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class AuthErrorMapper implements ErrorMapper {

    private static final String PREFIX = "AUTH-";
    private static final String TYPE_BASE = "https://api.authhub.com/problems/auth/";

    private final MessageSource messageSource;

    public AuthErrorMapper(MessageSource messageSource) {
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
                // 403 - 접근 금지
            case "AUTH-007" -> HttpStatus.FORBIDDEN;

                // 401 - 인증 실패 (기본값)
            default -> HttpStatus.UNAUTHORIZED;
        };
    }

    private String resolveTitle(String code, HttpStatus status, Locale locale) {
        String key = "problem.title." + code.toLowerCase(Locale.ROOT);
        return messageSource.getMessage(key, new Object[0], status.getReasonPhrase(), locale);
    }

    private String resolveDetail(String code, DomainException ex, Locale locale) {
        String key = "problem.detail." + code.toLowerCase(Locale.ROOT);
        return messageSource.getMessage(key, ex.args().values().toArray(), ex.getMessage(), locale);
    }

    private URI createTypeUri(String code) {
        String path = code.toLowerCase(Locale.ROOT);
        return URI.create(TYPE_BASE + path);
    }
}
