package com.ryuqq.authhub.adapter.in.rest.filter;

import com.ryuqq.authhub.adapter.in.rest.config.RateLimitProperties;
import com.ryuqq.authhub.application.security.ratelimit.port.in.CheckRateLimitUseCase;
import com.ryuqq.authhub.application.security.ratelimit.port.in.IncrementRateLimitUseCase;
import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * Rate Limit Filter - IP 기반 요청 제한 필터.
 *
 * <p>Redis 기반 분산 Rate Limiting을 적용하여 과도한 API 요청을 차단하는 Spring Filter입니다.
 * OncePerRequestFilter를 상속하여 요청당 한 번만 실행됨을 보장하며,
 * 제한 초과 시 HTTP 429 Too Many Requests 응답을 반환합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>클라이언트 IP 주소 추출 (X-Forwarded-For 헤더 고려)</li>
 *   <li>현재 요청 횟수 확인 (CheckRateLimitUseCase)</li>
 *   <li>제한 초과 시 429 응답 반환</li>
 *   <li>제한 이내 시 카운터 증가 (IncrementRateLimitUseCase)</li>
 *   <li>Rate Limit 정보를 Response Header에 추가</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Law of Demeter 준수 - Getter chaining 금지</li>
 *   <li>✅ Port/Adapter 패턴 - UseCase를 통한 Application Layer 접근</li>
 *   <li>✅ @Transactional 미사용 - Filter는 트랜잭션 밖에서 실행</li>
 *   <li>✅ IP 기반 제한 - RateLimitType.IP_BASED (기본 전략)</li>
 * </ul>
 *
 * <p><strong>Rate Limit 정책:</strong></p>
 * <ul>
 *   <li>타입: IP_BASED (IP 주소 기반)</li>
 *   <li>제한: 100회/60초 (IP당)</li>
 *   <li>Redis TTL: 60초 (자동 만료)</li>
 * </ul>
 *
 * <p><strong>Response Headers:</strong></p>
 * <ul>
 *   <li>X-RateLimit-Limit: 시간 윈도우 내 최대 요청 횟수</li>
 *   <li>X-RateLimit-Remaining: 남은 요청 횟수</li>
 *   <li>X-RateLimit-Reset: 제한 리셋 시각 (Unix Timestamp)</li>
 * </ul>
 *
 * <p><strong>실행 흐름:</strong></p>
 * <ol>
 *   <li>IP 주소 추출 (X-Forwarded-For → RemoteAddr)</li>
 *   <li>CheckRateLimitUseCase 호출 (현재 카운트 조회 + 제한 확인)</li>
 *   <li>제한 초과 시:
 *     <ul>
 *       <li>HTTP 429 Too Many Requests 응답</li>
 *       <li>Rate Limit Headers 설정</li>
 *       <li>필터 체인 중단 (return)</li>
 *     </ul>
 *   </li>
 *   <li>제한 이내 시:
 *     <ul>
 *       <li>IncrementRateLimitUseCase 호출 (카운터 증가)</li>
 *       <li>Rate Limit Headers 설정</li>
 *       <li>필터 체인 계속 진행</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <p><strong>IP 추출 우선순위:</strong></p>
 * <ol>
 *   <li>X-Forwarded-For 헤더 (프록시/로드밸런서 뒤에서 실행 시)</li>
 *   <li>request.getRemoteAddr() (직접 접속 시)</li>
 * </ol>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // Spring Boot에서 자동으로 Bean 등록 및 적용
 * // SecurityFilterConfig에서 URL 패턴 설정
 *
 * // 정상 요청 (100회 이내)
 * GET /api/v1/users → 200 OK
 * X-RateLimit-Limit: 100
 * X-RateLimit-Remaining: 99
 *
 * // 제한 초과 (101회째 요청)
 * GET /api/v1/users → 429 Too Many Requests
 * X-RateLimit-Limit: 100
 * X-RateLimit-Remaining: 0
 * X-RateLimit-Reset: 1698765432
 * </pre>
 *
 * <p><strong>의존성:</strong></p>
 * <ul>
 *   <li>CheckRateLimitUseCase - Rate Limit 확인 (Query)</li>
 *   <li>IncrementRateLimitUseCase - 카운터 증가 (Command)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    /**
     * X-Forwarded-For 헤더 이름.
     * 프록시/로드밸런서를 통한 요청 시 실제 클라이언트 IP 추출에 사용됩니다.
     */
    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

    /**
     * Rate Limit 관련 Response Header 이름 - 최대 요청 횟수.
     */
    private static final String X_RATE_LIMIT_LIMIT_HEADER = "X-RateLimit-Limit";

    /**
     * Rate Limit 관련 Response Header 이름 - 남은 요청 횟수.
     */
    private static final String X_RATE_LIMIT_REMAINING_HEADER = "X-RateLimit-Remaining";

    /**
     * Rate Limit 관련 Response Header 이름 - 리셋 시각.
     */
    private static final String X_RATE_LIMIT_RESET_HEADER = "X-RateLimit-Reset";

    private final CheckRateLimitUseCase checkRateLimitUseCase;
    private final IncrementRateLimitUseCase incrementRateLimitUseCase;
    private final RateLimitProperties rateLimitProperties;

    /**
     * RateLimitFilter 생성자.
     *
     * @param checkRateLimitUseCase Rate Limit 확인 UseCase
     * @param incrementRateLimitUseCase 카운터 증가 UseCase
     * @param rateLimitProperties Rate Limit 설정 프로퍼티
     * @throws NullPointerException 파라미터가 null인 경우
     */
    public RateLimitFilter(
            final CheckRateLimitUseCase checkRateLimitUseCase,
            final IncrementRateLimitUseCase incrementRateLimitUseCase,
            final RateLimitProperties rateLimitProperties
    ) {
        this.checkRateLimitUseCase = Objects.requireNonNull(
                checkRateLimitUseCase,
                "CheckRateLimitUseCase cannot be null"
        );
        this.incrementRateLimitUseCase = Objects.requireNonNull(
                incrementRateLimitUseCase,
                "IncrementRateLimitUseCase cannot be null"
        );
        this.rateLimitProperties = Objects.requireNonNull(
                rateLimitProperties,
                "RateLimitProperties cannot be null"
        );
    }

    /**
     * 요청 필터링 로직을 수행합니다.
     *
     * <p>OncePerRequestFilter에 의해 요청당 한 번만 실행됩니다.
     * IP 기반 Rate Limiting을 적용하여 과도한 요청을 차단합니다.</p>
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

        // 1. 클라이언트 IP 주소 추출
        final String clientIp = this.extractClientIp(request);
        final String endpoint = request.getRequestURI();

        // 2. Rate Limit 확인
        final CheckRateLimitUseCase.Command checkCommand = new CheckRateLimitUseCase.Command(
                clientIp,
                endpoint,
                RateLimitType.IP_BASED
        );

        final CheckRateLimitUseCase.Result result = this.checkRateLimitUseCase.checkRateLimit(checkCommand);

        // 3. 제한 초과 시 429 응답
        if (result.isExceeded()) {
            this.handleRateLimitExceeded(response, result);
            return;
        }

        // 4. 제한 이내 - 카운터 증가
        final IncrementRateLimitUseCase.Command incrementCommand = new IncrementRateLimitUseCase.Command(
                clientIp,
                endpoint,
                RateLimitType.IP_BASED,
                this.rateLimitProperties.getTimeWindowSeconds()
        );
        this.incrementRateLimitUseCase.incrementRateLimit(incrementCommand);

        // 5. Response Header 추가
        this.addRateLimitHeaders(response, result);

        // 6. 다음 필터 실행
        filterChain.doFilter(request, response);
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
        if (ip == null || ip.isEmpty()) {
            return request.getRemoteAddr();
        }

        // X-Forwarded-For에 여러 IP가 있는 경우 첫 번째 IP 사용
        // 형식: "client, proxy1, proxy2"
        return ip.split(",")[0].trim();
    }

    /**
     * Rate Limit 초과 시 429 응답을 처리합니다.
     *
     * <p>HTTP 429 Too Many Requests 상태 코드를 설정하고
     * Rate Limit 정보를 Response Header에 추가합니다.</p>
     *
     * @param response HTTP 응답 객체
     * @param result Rate Limit 확인 결과
     * @author AuthHub Team
     * @since 1.0.0
     */
    private void handleRateLimitExceeded(
            final HttpServletResponse response,
            final CheckRateLimitUseCase.Result result
    ) {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        this.addRateLimitHeaders(response, result);
    }

    /**
     * Rate Limit 정보를 Response Header에 추가합니다.
     *
     * <p><strong>추가되는 Headers:</strong></p>
     * <ul>
     *   <li>X-RateLimit-Limit: 최대 요청 횟수 (예: 100)</li>
     *   <li>X-RateLimit-Remaining: 남은 요청 횟수 (예: 45)</li>
     *   <li>X-RateLimit-Reset: 리셋 시각 Unix Timestamp (예: 1698765432)</li>
     * </ul>
     *
     * @param response HTTP 응답 객체
     * @param result Rate Limit 확인 결과
     * @author AuthHub Team
     * @since 1.0.0
     */
    private void addRateLimitHeaders(
            final HttpServletResponse response,
            final CheckRateLimitUseCase.Result result
    ) {
        // 최대 요청 횟수
        response.setHeader(
                X_RATE_LIMIT_LIMIT_HEADER,
                String.valueOf(result.getLimitCount())
        );

        // 남은 요청 횟수
        response.setHeader(
                X_RATE_LIMIT_REMAINING_HEADER,
                String.valueOf(result.getRemainingCount())
        );

        // 리셋 시각 (현재 시각 + 시간 윈도우)
        final long resetTimestamp = System.currentTimeMillis() / 1000 + result.getTimeWindowSeconds();
        response.setHeader(
                X_RATE_LIMIT_RESET_HEADER,
                String.valueOf(resetTimestamp)
        );
    }
}
