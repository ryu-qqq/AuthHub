package com.ryuqq.authhub.sdk.sync;

/**
 * EndpointSyncClient - 엔드포인트 동기화 클라이언트 인터페이스
 *
 * <p>AuthHub에 엔드포인트 동기화 요청을 보내는 클라이언트 인터페이스입니다.
 *
 * <p><strong>구현 예시:</strong>
 *
 * <pre>{@code
 * @Component
 * public class HttpEndpointSyncClient implements EndpointSyncClient {
 *
 *     private final RestTemplate restTemplate;
 *     private final String authHubUrl;
 *
 *     @Override
 *     public void sync(EndpointSyncRequest request) {
 *         restTemplate.postForEntity(
 *             authHubUrl + "/api/v1/internal/endpoints/sync",
 *             request,
 *             Void.class
 *         );
 *     }
 * }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface EndpointSyncClient {

    /**
     * AuthHub에 엔드포인트 동기화 요청
     *
     * @param request 동기화 요청
     * @throws EndpointSyncException 동기화 실패 시
     */
    void sync(EndpointSyncRequest request);
}
