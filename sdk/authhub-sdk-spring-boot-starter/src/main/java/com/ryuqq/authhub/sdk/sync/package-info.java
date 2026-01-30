/**
 * 엔드포인트 자동 동기화 패키지
 *
 * <p>이 패키지는 @RequirePermission 어노테이션이 붙은 엔드포인트를 자동으로 스캔하고 AuthHub에 동기화하는 기능을 제공합니다.
 *
 * <p><strong>주요 컴포넌트:</strong>
 *
 * <ul>
 *   <li>{@link com.ryuqq.authhub.sdk.sync.EndpointScanner} - 엔드포인트 스캐너
 *   <li>{@link com.ryuqq.authhub.sdk.sync.EndpointSyncRunner} - 애플리케이션 시작 시 동기화 실행
 *   <li>{@link com.ryuqq.authhub.sdk.sync.EndpointSyncClient} - 동기화 클라이언트 인터페이스
 *   <li>{@link com.ryuqq.authhub.sdk.sync.EndpointInfo} - 엔드포인트 정보 DTO
 *   <li>{@link com.ryuqq.authhub.sdk.sync.EndpointSyncRequest} - 동기화 요청 DTO
 * </ul>
 *
 * <p><strong>사용 방법:</strong>
 *
 * <pre>{@code
 * // 1. @RequirePermission 어노테이션 사용
 * @GetMapping("/users/{id}")
 * @RequirePermission("user:read")
 * public UserResponse getUser(@PathVariable Long id) { ... }
 *
 * // 2. EndpointSyncClient 구현
 * @Component
 * public class HttpEndpointSyncClient implements EndpointSyncClient {
 *     // AuthHub API 호출 구현
 * }
 *
 * // 3. EndpointSyncRunner 빈 등록
 * @Bean
 * public EndpointSyncRunner endpointSyncRunner(
 *         RequestMappingHandlerMapping handlerMapping,
 *         EndpointSyncClient syncClient) {
 *     return new EndpointSyncRunner(handlerMapping, syncClient, "my-service");
 * }
 * }</pre>
 *
 * <p><strong>의존성:</strong>
 *
 * <p>이 패키지는 Spring Web MVC에 의존합니다. common-auth 모듈에서는 compileOnly로 선언되어 있으므로, 사용하는 서비스에서 해당 의존성을
 * 제공해야 합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
package com.ryuqq.authhub.sdk.sync;
