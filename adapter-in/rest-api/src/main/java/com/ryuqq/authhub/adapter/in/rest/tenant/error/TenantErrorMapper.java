package com.ryuqq.authhub.adapter.in.rest.tenant.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * TenantErrorMapper - 테넌트 도메인 예외 → HTTP 응답 변환기
 *
 * <p>테넌트 도메인 예외를 RFC 7807 형식의 HTTP 응답으로 변환합니다.
 *
 * <p><strong>에러 코드 매핑:</strong>
 *
 * <ul>
 *   <li>TENANT-001: 테넌트 찾을 수 없음 → 404 Not Found
 *   <li>TENANT-002: 테넌트 이름 중복 → 409 Conflict
 *   <li>TENANT-003: 테넌트 이름 검증 실패 → 400 Bad Request
 *   <li>TENANT-004: 테넌트 상태 전환 불가 → 422 Unprocessable Entity
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantErrorMapper implements ErrorMapper {

    private static final Set<String> SUPPORTED_CODES =
            Set.of("TENANT-001", "TENANT-002", "TENANT-003", "TENANT-004");

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
            case "TENANT-001" ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Tenant Not Found",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/tenant-not-found"));
            case "TENANT-002" ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Tenant Name Duplicate",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/tenant-duplicate"));
            case "TENANT-003" ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Invalid Tenant Name",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/tenant-invalid-name"));
            case "TENANT-004" ->
                    new MappedError(
                            HttpStatus.UNPROCESSABLE_ENTITY,
                            "Invalid Tenant Status Transition",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/tenant-invalid-status"));
            default ->
                    new MappedError(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Internal Server Error",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/internal-error"));
        };
    }
}
