package com.ryuqq.authhub.adapter.in.rest.userrole.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * UserRoleErrorMapper - 사용자-역할 도메인 예외 → HTTP 응답 변환기
 *
 * <p>사용자-역할 도메인 예외를 RFC 7807 형식의 HTTP 응답으로 변환합니다.
 *
 * <p><strong>에러 코드 매핑:</strong>
 *
 * <ul>
 *   <li>USER_ROLE-001: 사용자-역할 관계 찾을 수 없음 → 404 Not Found
 *   <li>USER_ROLE-002: 사용자-역할 관계 중복 → 409 Conflict
 *   <li>USER_ROLE-003: 역할이 사용 중 → 409 Conflict
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleErrorMapper implements ErrorMapper {

    private static final Set<String> SUPPORTED_CODES =
            Set.of("USER_ROLE-001", "USER_ROLE-002", "USER_ROLE-003");

    @Override
    public boolean supports(DomainException ex) {
        String code = ex.code();
        if (code == null) {
            return false;
        }
        return SUPPORTED_CODES.contains(code);
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        String errorCode = ex.code();

        return switch (errorCode) {
            case "USER_ROLE-001" ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "User Role Not Found",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/user-role-not-found"));
            case "USER_ROLE-002" ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Duplicate User Role",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/user-role-duplicate"));
            case "USER_ROLE-003" ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Role In Use",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/role-in-use"));
            default ->
                    new MappedError(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Internal Server Error",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/internal-error"));
        };
    }
}
