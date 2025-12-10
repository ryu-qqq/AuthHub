package com.ryuqq.authhub.adapter.in.rest.organization.error;

import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * OrganizationErrorMapper - 조직 도메인 예외 → HTTP 응답 변환기
 *
 * <p>조직 도메인 예외를 RFC 7807 형식의 HTTP 응답으로 변환합니다.
 *
 * <p><strong>에러 코드 매핑:</strong>
 *
 * <ul>
 *   <li>ORG-001: 조직 찾을 수 없음 → 404 Not Found
 *   <li>ORG-002: 조직 상태 전환 불가 → 400 Bad Request
 *   <li>ORG-003: 조직 이름 중복 → 409 Conflict
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationErrorMapper implements ErrorMapper {

    private static final Set<String> SUPPORTED_CODES = Set.of("ORG-001", "ORG-002", "ORG-003");

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
            case "ORG-001" ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Organization Not Found",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/organization-not-found"));
            case "ORG-002" ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Invalid Organization State Transition",
                            ex.getMessage(),
                            URI.create(
                                    "https://authhub.ryuqq.com/errors/organization-invalid-state"));
            case "ORG-003" ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Organization Name Duplicate",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/organization-duplicate"));
            default ->
                    new MappedError(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Internal Server Error",
                            ex.getMessage(),
                            URI.create("https://authhub.ryuqq.com/errors/internal-error"));
        };
    }
}
