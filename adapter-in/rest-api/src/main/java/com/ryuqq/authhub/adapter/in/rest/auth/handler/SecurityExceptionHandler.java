package com.ryuqq.authhub.adapter.in.rest.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Security 예외 통합 핸들러
 *
 * <p>Spring Security의 인증/인가 실패를 처리합니다. GlobalExceptionHandler와 동일한 RFC 7807 Problem Details 형식으로
 * 응답합니다.
 *
 * <p>처리하는 예외:
 *
 * <ul>
 *   <li>AuthenticationException: 401 Unauthorized (인증 실패)
 *   <li>AccessDeniedException: 403 Forbidden (인가 실패)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(SecurityExceptionHandler.class);

    /** 에러 코드 상수 - GlobalExceptionHandler와 일관성 유지 */
    private static final String UNAUTHORIZED = "UNAUTHORIZED";

    private static final String FORBIDDEN = "FORBIDDEN";

    /** x-error-code 헤더명 - 클라이언트 에러 분기 용이 */
    private static final String ERROR_CODE_HEADER = "x-error-code";

    private final ObjectMapper objectMapper;

    public SecurityExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 인증 실패 처리 (401 Unauthorized)
     *
     * <p>인증이 필요한 엔드포인트에 인증 없이 접근할 때 호출됩니다.
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {

        log.warn(
                "Authentication failed: uri={}, message={}",
                request.getRequestURI(),
                authException.getMessage());

        writeResponse(
                response,
                request,
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "인증이 필요합니다. 유효한 토큰을 제공해주세요.",
                UNAUTHORIZED);
    }

    /**
     * 접근 거부 처리 (403 Forbidden)
     *
     * <p>인증은 되었지만 권한이 없는 리소스에 접근할 때 호출됩니다.
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        log.warn(
                "Access denied: uri={}, message={}",
                request.getRequestURI(),
                accessDeniedException.getMessage());

        writeResponse(
                response,
                request,
                HttpStatus.FORBIDDEN,
                "Forbidden",
                "이 리소스에 대한 접근 권한이 없습니다.",
                FORBIDDEN);
    }

    /**
     * RFC 7807 Problem Details 형식으로 응답 작성
     *
     * <p>GlobalExceptionHandler와 동일한 형식을 사용합니다.
     *
     * @param response HTTP 응답
     * @param request HTTP 요청
     * @param status HTTP 상태 코드
     * @param title 에러 제목
     * @param detail 에러 상세 메시지
     * @param errorCode 에러 코드 (x-error-code 헤더 및 code 필드에 사용)
     */
    private void writeResponse(
            HttpServletResponse response,
            HttpServletRequest request,
            HttpStatus status,
            String title,
            String detail,
            String errorCode)
            throws IOException {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create("about:blank"));

        // RFC 7807 확장 필드 - GlobalExceptionHandler와 동일
        problemDetail.setProperty("timestamp", Instant.now().toString());
        problemDetail.setProperty("code", errorCode);

        // 요청 경로를 instance로
        String uri = request.getRequestURI();
        if (request.getQueryString() != null && !request.getQueryString().isBlank()) {
            uri = uri + "?" + request.getQueryString();
        }
        problemDetail.setInstance(URI.create(uri));

        // tracing(id 존재 시) — Micrometer/Logback MDC 키 관례
        String traceId = MDC.get("traceId");
        String spanId = MDC.get("spanId");
        if (traceId != null) {
            problemDetail.setProperty("traceId", traceId);
        }
        if (spanId != null) {
            problemDetail.setProperty("spanId", spanId);
        }

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader(ERROR_CODE_HEADER, errorCode);

        objectMapper.writeValue(response.getWriter(), problemDetail);
    }
}
