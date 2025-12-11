package com.ryuqq.authhub.adapter.in.rest.role.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * RoleErrorMapper - 역할 도메인 예외 → HTTP 응답 변환기
 *
 * <p>역할 도메인 예외를 RFC 7807 형식의 HTTP 응답으로 변환합니다.
 *
 * <p><strong>에러 코드 매핑:</strong>
 *
 * <ul>
 *   <li>ROLE-001: 역할 찾을 수 없음 → 404 Not Found
 *   <li>ROLE-002: 역할 이름 중복 → 409 Conflict
 *   <li>ROLE-003: 시스템 역할 수정 불가 → 400 Bad Request
 *   <li>ROLE-004: 시스템 역할 삭제 불가 → 400 Bad Request
 *   <li>ROLE-005: 잘못된 역할 범위 → 400 Bad Request
 *   <li>ROLE-006: 역할 권한 찾을 수 없음 → 404 Not Found
 *   <li>ROLE-007: 역할 권한 중복 → 409 Conflict
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleErrorMapper implements ErrorMapper {

    private static final Set<String> SUPPORTED_CODES =
            Set.of(
                    "ROLE-001",
                    "ROLE-002",
                    "ROLE-003",
                    "ROLE-004",
                    "ROLE-005",
                    "ROLE-006",
                    "ROLE-007");

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
            case "ROLE-001" ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Role Not Found",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/role-not-found"));
            case "ROLE-002" ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Duplicate Role Name",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/role-duplicate"));
            case "ROLE-003" ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "System Role Not Modifiable",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/role-not-modifiable"));
            case "ROLE-004" ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "System Role Not Deletable",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/role-not-deletable"));
            case "ROLE-005" ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Invalid Role Scope",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/role-invalid-scope"));
            case "ROLE-006" ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Role Permission Not Found",
                            ex.getMessage(),
                            URI.create(
                                    "https://authhub.ryuqq.com/errors/role-permission-not-found"));
            case "ROLE-007" ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Duplicate Role Permission",
                            ex.getMessage(),
                            URI.create(
                                    "https://authhub.ryuqq.com/errors/role-permission-duplicate"));
            default ->
                    new MappedError(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Internal Server Error",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/internal-error"));
        };
    }
}
