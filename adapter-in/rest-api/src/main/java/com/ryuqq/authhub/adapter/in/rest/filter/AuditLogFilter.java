package com.ryuqq.authhub.adapter.in.rest.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Audit Log Filter - HTTP 요청 감사 로그 자동 기록 필터.
 *
 * <p>모든 HTTP 요청에 대한 감사 로그를 자동으로 기록하는 Spring Filter입니다.
 * OncePerRequestFilter를 상속하여 요청당 한 번만 실행됨을 보장하며,
 * 구조화된 JSON 로그를 AUDIT_LOG Logger로 출력합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>클라이언트 IP 주소 추출 (X-Forwarded-For 헤더 고려)</li>
 *   <li>JWT에서 UserId 추출 (sub 클레임)</li>
 *   <li>User-Agent 추출</li>
 *   <li>요청 정보 기록 (method, endpoint)</li>
 *   <li>응답 정보 기록 (status code)</li>
 *   <li>처리 시간 측정 (duration)</li>
 *   <li>구조화된 로그 출력 (JSON 형식)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Law of Demeter 준수 - Getter chaining 금지</li>
 *   <li>✅ @Transactional 미사용 - Filter는 트랜잭션 밖에서 실행</li>
 *   <li>✅ @Order(3) 설정 - BlacklistCheckFilter(2) 이후 실행</li>
 *   <li>✅ Phase 1 구현 - 구조화된 로깅만 수행 (DB 저장 없음)</li>
 * </ul>
 *
 * <p><strong>Phase 1 전략:</strong></p>
 * <ul>
 *   <li>MySQL 저장 부담 최소화를 위해 로그 파일로만 기록</li>
 *   <li>ELK Stack, Splunk 등 로그 수집 시스템으로 분석 가능</li>
 *   <li>향후 Phase 2에서 선별적 DB 저장 기능 추가 예정</li>
 *   <li>선별 기준: 로그인/로그아웃, 권한 변경, 중요 데이터 수정/삭제</li>
 * </ul>
 *
 * <p><strong>실행 순서 (Filter Chain):</strong></p>
 * <ol>
 *   <li>@Order(1) - SecurityHeaderFilter (보안 헤더 추가)</li>
 *   <li>@Order(2) - BlacklistCheckFilter (블랙리스트 확인)</li>
 *   <li><strong>@Order(3) - AuditLogFilter (감사 로그 기록)</strong></li>
 *   <li>@Order(4) - RateLimitFilter (Rate Limiting)</li>
 *   <li>Spring Security Filter Chain</li>
 * </ol>
 *
 * <p><strong>실행 흐름:</strong></p>
 * <ol>
 *   <li>요청 정보 추출:
 *     <ul>
 *       <li>IP 주소 (X-Forwarded-For → RemoteAddr)</li>
 *       <li>UserId (JWT의 sub 클레임, 없으면 "anonymous")</li>
 *       <li>User-Agent</li>
 *       <li>HTTP Method</li>
 *       <li>Request URI</li>
 *     </ul>
 *   </li>
 *   <li>시작 시간 기록</li>
 *   <li>필터 체인 실행 (try-finally로 보장)</li>
 *   <li>응답 정보 추출:
 *     <ul>
 *       <li>HTTP Status Code</li>
 *       <li>처리 시간 (duration)</li>
 *     </ul>
 *   </li>
 *   <li>구조화된 로그 출력 (항상 실행)</li>
 * </ol>
 *
 * <p><strong>로그 형식:</strong></p>
 * <pre>
 * INFO AUDIT_LOG - REQUEST_AUDIT: ip=192.168.1.100, userId=12345,
 *   method=POST, endpoint=/api/v1/users, status=201, duration=45ms,
 *   userAgent=Mozilla/5.0...
 * </pre>
 *
 * <p><strong>로그 수준별 출력:</strong></p>
 * <ul>
 *   <li>INFO - 정상 요청 (2xx, 3xx 응답)</li>
 *   <li>WARN - 클라이언트 오류 (4xx 응답)</li>
 *   <li>ERROR - 서버 오류 (5xx 응답)</li>
 * </ul>
 *
 * <p><strong>IP 추출 우선순위:</strong></p>
 * <ol>
 *   <li>X-Forwarded-For 헤더 (프록시/로드밸런서 뒤에서 실행 시)</li>
 *   <li>request.getRemoteAddr() (직접 접속 시)</li>
 * </ol>
 *
 * <p><strong>UserId 추출 로직:</strong></p>
 * <ul>
 *   <li>Authorization Header에서 Bearer 토큰 추출</li>
 *   <li>JWT Payload에서 "sub" 클레임 추출</li>
 *   <li>토큰이 없거나 파싱 실패 시 "anonymous" 반환</li>
 * </ul>
 *
 * <p><strong>예외 처리:</strong></p>
 * <ul>
 *   <li>JWT 파싱 실패 → userId를 "anonymous"로 처리, 로그는 정상 출력</li>
 *   <li>로그 출력 실패 → 예외를 삼키고 필터 체인 진행 (요청에 영향 없음)</li>
 *   <li>finally 블록 → 예외 발생 여부와 관계없이 항상 로그 출력</li>
 * </ul>
 *
 * <p><strong>성능 고려사항:</strong></p>
 * <ul>
 *   <li>JWT 파싱 - 서명 검증 제외, Base64 디코딩만 수행 (< 1ms)</li>
 *   <li>로그 출력 - 비동기 Logger 사용 시 성능 영향 최소화</li>
 *   <li>DB 저장 없음 - I/O 부담 없음 (Phase 1)</li>
 *   <li>finally 블록 - 한 번만 실행 보장</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // Spring Boot에서 자동으로 Bean 등록 및 적용
 *
 * // 정상 요청
 * POST /api/v1/users
 * Authorization: Bearer eyJhbGc...valid-token
 * → INFO AUDIT_LOG - REQUEST_AUDIT: ip=192.168.1.100, userId=12345,
 *      method=POST, endpoint=/api/v1/users, status=201, duration=45ms
 *
 * // 인증 없는 요청
 * GET /api/v1/health
 * → INFO AUDIT_LOG - REQUEST_AUDIT: ip=192.168.1.100, userId=anonymous,
 *      method=GET, endpoint=/api/v1/health, status=200, duration=5ms
 *
 * // 에러 요청
 * GET /api/v1/users/999
 * → WARN AUDIT_LOG - REQUEST_AUDIT: ip=192.168.1.100, userId=12345,
 *      method=GET, endpoint=/api/v1/users/999, status=404, duration=12ms
 * </pre>
 *
 * <p><strong>향후 확장 (Phase 2):</strong></p>
 * <ul>
 *   <li>선별적 DB 저장 - 중요 이벤트만 저장</li>
 *   <li>비동기 저장 - @Async로 성능 영향 최소화</li>
 *   <li>UseCase 통합 - SaveAuditLogUseCase</li>
 *   <li>저장 기준 설정 - application.yml</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
@Order(3)
public class AuditLogFilter extends OncePerRequestFilter {

    /**
     * Audit Log 전용 Logger.
     * <p>일반 애플리케이션 로그와 분리하여 감사 로그만 추출 가능하도록 별도 Logger 사용.</p>
     */
    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("AUDIT_LOG");

    /**
     * X-Forwarded-For 헤더 이름.
     * 프록시/로드밸런서를 통한 요청 시 실제 클라이언트 IP 추출에 사용됩니다.
     */
    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

    /**
     * Authorization Header 이름.
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Bearer 토큰 Prefix.
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Bearer Prefix 길이 (7글자).
     */
    private static final int BEARER_PREFIX_LENGTH = 7;

    /**
     * User-Agent Header 이름.
     */
    private static final String USER_AGENT_HEADER = "User-Agent";

    /**
     * 익명 사용자 식별자.
     */
    private static final String ANONYMOUS_USER = "anonymous";

    /**
     * ObjectMapper 인스턴스 (Thread-Safe, JWT 파싱용).
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 요청 필터링 로직을 수행합니다.
     *
     * <p>OncePerRequestFilter에 의해 요청당 한 번만 실행됩니다.
     * 모든 HTTP 요청에 대한 감사 로그를 자동으로 기록합니다.</p>
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 다음 필터 체인
     * @throws ServletException 서블릿 처리 중 예외 발생 시
     * @throws IOException I/O 처리 중 예외 발생 시
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. 요청 정보 추출
        final String clientIp = this.extractClientIp(request);
        final String userId = this.extractUserId(request);
        final String userAgent = request.getHeader(USER_AGENT_HEADER);
        final String method = request.getMethod();
        final String endpoint = request.getRequestURI();

        // 2. 시작 시간 기록
        final long startTime = System.currentTimeMillis();

        try {
            // 3. 필터 체인 실행
            filterChain.doFilter(request, response);

        } finally {
            // 4. 응답 정보 추출 및 로그 출력 (항상 실행)
            final long duration = System.currentTimeMillis() - startTime;
            final int status = response.getStatus();

            // 5. 구조화된 로그 출력
            this.logAuditRecord(clientIp, userId, method, endpoint, status, duration, userAgent);
        }
    }

    /**
     * 클라이언트 IP 주소를 추출합니다.
     *
     * <p>X-Forwarded-For 헤더를 우선적으로 확인하여
     * 프록시/로드밸런서 뒤에서 실행되는 경우에도 실제 클라이언트 IP를 정확히 추출합니다.</p>
     *
     * <p><strong>추출 우선순위:</strong></p>
     * <ol>
     *   <li>X-Forwarded-For 헤더의 첫 번째 IP (프록시 체인의 원본 클라이언트)</li>
     *   <li>request.getRemoteAddr() (직접 접속)</li>
     * </ol>
     *
     * <p><strong>X-Forwarded-For 예시:</strong></p>
     * <ul>
     *   <li>"192.168.1.100" → "192.168.1.100"</li>
     *   <li>"192.168.1.100, 10.0.0.1" → "192.168.1.100" (첫 번째 IP만 사용)</li>
     *   <li>null 또는 빈 문자열 → request.getRemoteAddr() 사용</li>
     * </ul>
     *
     * @param request HTTP 요청 객체
     * @return 클라이언트 IP 주소 (null이 아님)
     * @author AuthHub Team
     * @since 1.0.0
     */
    private String extractClientIp(final HttpServletRequest request) {
        String ip = request.getHeader(X_FORWARDED_FOR_HEADER);

        // X-Forwarded-For가 없거나 빈 문자열이면 RemoteAddr 사용
        if (ip != null && !ip.isBlank()) {
            // X-Forwarded-For에 여러 IP가 있는 경우 첫 번째 IP 사용
            // 형식: "client, proxy1, proxy2"
            final String clientIp = ip.split(",")[0].trim();
            if (!clientIp.isBlank()) {
                return clientIp;
            }
        }

        // Fallback: RemoteAddr 사용
        return request.getRemoteAddr();
    }

    /**
     * JWT에서 UserId를 추출합니다.
     *
     * <p>Authorization Header에서 Bearer 토큰을 추출하고,
     * JWT Payload의 "sub" 클레임에서 UserId를 반환합니다.
     * 토큰이 없거나 파싱 실패 시 "anonymous"를 반환합니다.</p>
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>Authorization Header에서 Bearer 토큰 추출</li>
     *   <li>JWT를 "."으로 분리 (header.payload.signature)</li>
     *   <li>Payload 부분 Base64 디코딩</li>
     *   <li>JSON 파싱하여 "sub" 클레임 추출</li>
     *   <li>sub가 없거나 파싱 실패 시 "anonymous" 반환</li>
     * </ol>
     *
     * <p><strong>서명 검증 제외 이유:</strong></p>
     * <ul>
     *   <li>Audit Log는 참고용 정보</li>
     *   <li>서명 검증은 Spring Security에서 처리</li>
     *   <li>성능 최적화 - 불필요한 검증 중복 제거</li>
     * </ul>
     *
     * @param request HTTP 요청 객체
     * @return UserId (sub 클레임), 없으면 "anonymous"
     * @author AuthHub Team
     * @since 1.0.0
     */
    private String extractUserId(final HttpServletRequest request) {
        try {
            // 1. Authorization Header에서 Bearer 토큰 추출
            final String token = this.extractToken(request);
            if (token == null) {
                return ANONYMOUS_USER;
            }

            // 2. JWT를 "."으로 분리 (header.payload.signature)
            final String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return ANONYMOUS_USER;
            }

            // 3. Payload 부분 (index 1) Base64 디코딩
            final String payload = parts[1];
            final byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
            final String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);

            // 4. JSON 파싱하여 "sub" 클레임 추출
            final JsonNode jsonNode = OBJECT_MAPPER.readTree(decodedPayload);
            final JsonNode subNode = jsonNode.get("sub");

            // 5. sub가 없으면 "anonymous" 반환
            if (subNode == null || subNode.isNull() || subNode.asText().isBlank()) {
                return ANONYMOUS_USER;
            }

            return subNode.asText();

        } catch (final Exception e) {
            // JWT 파싱 실패 - "anonymous" 반환
            // 로그는 생략 (성능 고려, Spring Security에서 처리)
            return ANONYMOUS_USER;
        }
    }

    /**
     * Authorization Header에서 Bearer 토큰을 추출합니다.
     *
     * <p>Authorization Header에서 "Bearer " Prefix를 제거하고
     * 실제 JWT 토큰 문자열만 추출합니다.</p>
     *
     * <p><strong>추출 로직:</strong></p>
     * <ol>
     *   <li>Authorization Header 확인</li>
     *   <li>"Bearer " Prefix 확인</li>
     *   <li>Prefix 제거 후 토큰 문자열 반환</li>
     * </ol>
     *
     * <p><strong>Authorization Header 예시:</strong></p>
     * <ul>
     *   <li>"Bearer eyJhbGc..." → "eyJhbGc..."</li>
     *   <li>"eyJhbGc..." (Bearer 없음) → null</li>
     *   <li>null 또는 빈 문자열 → null</li>
     * </ul>
     *
     * @param request HTTP 요청 객체
     * @return JWT 토큰 문자열 (Bearer Prefix 제거), 토큰이 없으면 null
     * @author AuthHub Team
     * @since 1.0.0
     */
    private String extractToken(final HttpServletRequest request) {
        final String header = request.getHeader(AUTHORIZATION_HEADER);

        // Authorization Header가 없거나 빈 문자열이면 null 반환
        if (header == null || header.isBlank()) {
            return null;
        }

        // Bearer Prefix가 있는 경우에만 토큰 추출
        if (header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX_LENGTH);
        }

        // Bearer Prefix가 없으면 null 반환
        return null;
    }

    /**
     * 감사 로그를 구조화된 형식으로 출력합니다.
     *
     * <p>HTTP 상태 코드에 따라 로그 레벨을 자동으로 조정합니다:
     * INFO (2xx, 3xx), WARN (4xx), ERROR (5xx).</p>
     *
     * <p><strong>로그 형식:</strong></p>
     * <pre>
     * REQUEST_AUDIT: ip=192.168.1.100, userId=12345, method=POST,
     *   endpoint=/api/v1/users, status=201, duration=45ms, userAgent=Mozilla/5.0...
     * </pre>
     *
     * @param clientIp 클라이언트 IP 주소
     * @param userId 사용자 ID (인증된 사용자) 또는 "anonymous"
     * @param method HTTP Method (GET, POST, PUT, DELETE 등)
     * @param endpoint Request URI (/api/v1/users 등)
     * @param status HTTP 상태 코드 (200, 404, 500 등)
     * @param duration 처리 시간 (밀리초)
     * @param userAgent User-Agent 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    private void logAuditRecord(
            final String clientIp,
            final String userId,
            final String method,
            final String endpoint,
            final int status,
            final long duration,
            final String userAgent
    ) {
        try {
            // HTTP 상태 코드에 따라 로그 레벨 결정
            if (status >= 500) {
                // 5xx - 서버 오류
                AUDIT_LOGGER.error(
                        "REQUEST_AUDIT: ip={}, userId={}, method={}, endpoint={}, status={}, duration={}ms, userAgent={}",
                        clientIp, userId, method, endpoint, status, duration, userAgent
                );
            } else if (status >= 400) {
                // 4xx - 클라이언트 오류
                AUDIT_LOGGER.warn(
                        "REQUEST_AUDIT: ip={}, userId={}, method={}, endpoint={}, status={}, duration={}ms, userAgent={}",
                        clientIp, userId, method, endpoint, status, duration, userAgent
                );
            } else {
                // 2xx, 3xx - 정상 요청
                AUDIT_LOGGER.info(
                        "REQUEST_AUDIT: ip={}, userId={}, method={}, endpoint={}, status={}, duration={}ms, userAgent={}",
                        clientIp, userId, method, endpoint, status, duration, userAgent
                );
            }

        } catch (final Exception e) {
            // 로그 출력 실패 - 예외를 삼킴 (요청 처리에 영향 없음)
            // 로그 시스템 자체 장애는 별도 모니터링으로 감지
        }
    }
}
