package com.ryuqq.authhub.adapter.in.rest.filter;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RateLimitFilter 테스트용 컨트롤러.
 *
 * <p>통합 테스트에서 실제 HTTP 요청을 받을 수 있도록 제공하는 테스트 전용 컨트롤러입니다.
 * 실제 프로덕션 코드에는 포함되지 않으며, 테스트 환경에서만 로딩됩니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Test-only Controller - src/test/java에만 존재</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1")
public class TestController {

    /**
     * 테스트용 엔드포인트.
     *
     * @return 200 OK 응답
     */
    @GetMapping("/test-endpoint")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("OK");
    }

    /**
     * Rate Limit 테스트용 엔드포인트.
     *
     * @return 200 OK 응답
     */
    @GetMapping("/rate-limit-test")
    public ResponseEntity<String> rateLimitTest() {
        return ResponseEntity.ok("OK");
    }

    /**
     * 독립성 테스트용 엔드포인트.
     *
     * @return 200 OK 응답
     */
    @GetMapping("/independent-test")
    public ResponseEntity<String> independentTest() {
        return ResponseEntity.ok("OK");
    }

    /**
     * Header 테스트용 엔드포인트.
     *
     * @return 200 OK 응답
     */
    @GetMapping("/header-test")
    public ResponseEntity<String> headerTest() {
        return ResponseEntity.ok("OK");
    }
}
