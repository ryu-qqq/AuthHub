package com.ryuqq.authhub.integration.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.bootstrap.AuthHubWebApiApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

/**
 * 통합 테스트 기본 클래스
 *
 * <p>모든 통합 테스트에서 상속받아 사용합니다.
 *
 * <p><strong>제공 기능:</strong>
 *
 * <ul>
 *   <li>Spring Boot 전체 컨텍스트 로딩
 *   <li>TestRestTemplate 주입
 *   <li>ObjectMapper 주입
 *   <li>외부 서비스 Mock 설정 (IntegrationTestConfig)
 *   <li>테스트 프로파일 활성화
 * </ul>
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * class TenantIntegrationTest extends BaseIntegrationTest {
 *
 *     @Test
 *     void createTenant_success() {
 *         var request = TenantIntegrationTestFixture.createTenantRequest();
 *         var response = restTemplate.postForEntity(
 *             baseUrl() + "/api/v1/tenants",
 *             request,
 *             ApiResponse.class
 *         );
 *         assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
 *     }
 * }
 * }</pre>
 *
 * @author Development Team
 * @since 1.0.0
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = AuthHubWebApiApplication.class)
@Import({IntegrationTestConfig.class, TestSecurityConfig.class})
@ActiveProfiles("test")
@Sql(scripts = "/sql/cleanup-all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public abstract class BaseIntegrationTest {

    @LocalServerPort protected int port;

    @Autowired protected TestRestTemplate restTemplate;

    @Autowired protected ObjectMapper objectMapper;

    /**
     * 테스트 서버 기본 URL
     *
     * @return base URL (예: http://localhost:8080)
     */
    protected String baseUrl() {
        return "http://localhost:" + port;
    }

    /**
     * API 기본 경로
     *
     * @return API base path (예: http://localhost:8080/api/v1)
     */
    protected String apiV1() {
        return baseUrl() + "/api/v1";
    }

    /**
     * JSON Content-Type 헤더를 포함한 HttpEntity 생성
     *
     * @param body 요청 본문
     * @param <T> 요청 본문 타입
     * @return HttpEntity 인스턴스
     */
    protected <T> HttpEntity<T> createHttpEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    /**
     * 인증 헤더를 포함한 HttpEntity 생성
     *
     * @param body 요청 본문
     * @param accessToken JWT 액세스 토큰
     * @param <T> 요청 본문 타입
     * @return HttpEntity 인스턴스
     */
    protected <T> HttpEntity<T> createHttpEntityWithAuth(T body, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        return new HttpEntity<>(body, headers);
    }

    /**
     * 인증 헤더만 포함한 HttpEntity 생성 (본문 없음)
     *
     * @param accessToken JWT 액세스 토큰
     * @return HttpEntity 인스턴스
     */
    protected HttpEntity<Void> createHttpEntityWithAuth(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        return new HttpEntity<>(headers);
    }

    /**
     * POST 요청 후 ApiResponse 래퍼에서 데이터 추출
     *
     * <p>API 응답이 {@code ApiResponse<T>} 형식으로 래핑되어 있으므로 이 헬퍼 메서드를 사용합니다.
     *
     * @param url 요청 URL
     * @param request 요청 본문
     * @param typeReference ApiResponse 래퍼 포함 타입 참조
     * @param <T> 응답 데이터 타입
     * @return ApiResponse 래핑된 응답
     */
    protected <T> ResponseEntity<ApiResponse<T>> postForApiResponse(
            String url, Object request, ParameterizedTypeReference<ApiResponse<T>> typeReference) {
        return restTemplate.exchange(
                url, HttpMethod.POST, createHttpEntity(request), typeReference);
    }

    /**
     * GET 요청 후 ApiResponse 래퍼에서 데이터 추출
     *
     * @param url 요청 URL
     * @param typeReference ApiResponse 래퍼 포함 타입 참조
     * @param <T> 응답 데이터 타입
     * @return ApiResponse 래핑된 응답
     */
    protected <T> ResponseEntity<ApiResponse<T>> getForApiResponse(
            String url, ParameterizedTypeReference<ApiResponse<T>> typeReference) {
        return restTemplate.exchange(url, HttpMethod.GET, null, typeReference);
    }
}
