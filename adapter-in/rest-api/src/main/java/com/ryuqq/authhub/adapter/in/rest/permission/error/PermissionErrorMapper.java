package com.ryuqq.authhub.adapter.in.rest.permission.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * PermissionErrorMapper - 권한 도메인 예외 → HTTP 응답 변환기
 *
 * <p>권한 도메인 예외를 RFC 7807 형식의 HTTP 응답으로 변환합니다.
 *
 * <p><strong>에러 코드 매핑:</strong>
 *
 * <ul>
 *   <li>PERMISSION-001: 권한 찾을 수 없음 → 404 Not Found
 *   <li>PERMISSION-002: 권한 키 중복 → 409 Conflict
 *   <li>PERMISSION-003: 시스템 권한 수정 불가 → 400 Bad Request
 *   <li>PERMISSION-004: 시스템 권한 삭제 불가 → 400 Bad Request
 *   <li>PERMISSION-005: 잘못된 권한 키 형식 → 400 Bad Request
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionErrorMapper implements ErrorMapper {

    private static final Set<String> SUPPORTED_CODES =
            Set.of(
                    "PERMISSION-001",
                    "PERMISSION-002",
                    "PERMISSION-003",
                    "PERMISSION-004",
                    "PERMISSION-005");

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
            case "PERMISSION-001" ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Permission Not Found",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/permission-not-found"));
            case "PERMISSION-002" ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Duplicate Permission Key",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/permission-duplicate"));
            case "PERMISSION-003" ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "System Permission Not Modifiable",
                            ex.getMessage(),
                            URI.create(
                                    "https://authhub.ryuqq.com/errors/permission-not-modifiable"));
            case "PERMISSION-004" ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "System Permission Not Deletable",
                            ex.getMessage(),
                            URI.create(
                                    "https://authhub.ryuqq.com/errors/permission-not-deletable"));
            case "PERMISSION-005" ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Invalid Permission Key Format",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/permission-invalid-key"));
            default ->
                    new MappedError(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Internal Server Error",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/internal-error"));
        };
    }
}
