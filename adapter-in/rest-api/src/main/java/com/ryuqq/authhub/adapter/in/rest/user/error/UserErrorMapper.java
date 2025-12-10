package com.ryuqq.authhub.adapter.in.rest.user.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * UserErrorMapper - 사용자 도메인 예외 → HTTP 응답 변환기
 *
 * <p>사용자 도메인 예외를 RFC 7807 형식의 HTTP 응답으로 변환합니다.
 *
 * <p><strong>에러 코드 매핑:</strong>
 *
 * <ul>
 *   <li>USER-001: 사용자 찾을 수 없음 → 404 Not Found
 *   <li>USER-002: 잘못된 사용자 상태 → 400 Bad Request
 *   <li>USER-003: 사용자 식별자 중복 → 409 Conflict
 *   <li>USER-004: 사용자 역할 찾을 수 없음 → 404 Not Found
 *   <li>USER-005: 사용자 역할 중복 → 409 Conflict
 *   <li>USER-006: 잘못된 비밀번호 → 400 Bad Request
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserErrorMapper implements ErrorMapper {

    private static final Set<String> SUPPORTED_CODES =
            Set.of("USER-001", "USER-002", "USER-003", "USER-004", "USER-005", "USER-006");

    @Override
    public boolean supports(String code) {
        if (code == null) {
            return false;
        }
        return SUPPORTED_CODES.contains(code);
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        String errorCode = ex.code();

        return switch (errorCode) {
            case "USER-001" ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "User Not Found",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/user-not-found"));
            case "USER-002" ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Invalid User State",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/user-invalid-state"));
            case "USER-003" ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Duplicate User Identifier",
                            ex.getMessage(),
                            URI.create(
                                    "https://authhub.ryuqq.com/errors/user-duplicate-identifier"));
            case "USER-004" ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "User Role Not Found",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/user-role-not-found"));
            case "USER-005" ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Duplicate User Role",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/user-role-duplicate"));
            case "USER-006" ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Invalid Password",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/user-invalid-password"));
            default ->
                    new MappedError(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Internal Server Error",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/internal-error"));
        };
    }
}
