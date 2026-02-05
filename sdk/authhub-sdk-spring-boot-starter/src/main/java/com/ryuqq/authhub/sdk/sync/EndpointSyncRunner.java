package com.ryuqq.authhub.sdk.sync;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * EndpointSyncRunner - 애플리케이션 시작 시 엔드포인트 동기화 실행기
 *
 * <p>애플리케이션 시작 시 @RequirePermission 어노테이션이 붙은 모든 엔드포인트를 스캔하고, AuthHub에 동기화 요청을 보냅니다.
 *
 * <p><strong>동작 흐름:</strong>
 *
 * <ol>
 *   <li>애플리케이션 시작
 *   <li>EndpointScanner로 모든 @RequirePermission 엔드포인트 스캔
 *   <li>EndpointSyncClient를 통해 AuthHub에 동기화 요청
 * </ol>
 *
 * <p><strong>사용 방법:</strong>
 *
 * <pre>{@code
 * @Configuration
 * public class EndpointSyncConfig {
 *
 *     @Bean
 *     public EndpointSyncRunner endpointSyncRunner(
 *             RequestMappingHandlerMapping handlerMapping,
 *             EndpointSyncClient syncClient) {
 *         return new EndpointSyncRunner(
 *             handlerMapping,
 *             syncClient,
 *             "my-service"
 *         );
 *     }
 * }
 * }</pre>
 *
 * <p><strong>비활성화:</strong>
 *
 * <p>동기화를 비활성화하려면 빈을 등록하지 않거나, enabled 플래그를 사용하세요.
 *
 * @author development-team
 * @since 1.0.0
 */
public class EndpointSyncRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(EndpointSyncRunner.class);

    private final RequestMappingHandlerMapping handlerMapping;
    private final EndpointSyncClient syncClient;
    private final String serviceName;
    private final String serviceCode;
    private final boolean enabled;

    /**
     * EndpointSyncRunner 생성자
     *
     * @param handlerMapping Spring MVC 핸들러 매핑
     * @param syncClient 동기화 클라이언트
     * @param serviceName 서비스 이름
     */
    public EndpointSyncRunner(
            RequestMappingHandlerMapping handlerMapping,
            EndpointSyncClient syncClient,
            String serviceName) {
        this(handlerMapping, syncClient, serviceName, null, true);
    }

    /**
     * EndpointSyncRunner 생성자 (serviceCode 포함)
     *
     * @param handlerMapping Spring MVC 핸들러 매핑
     * @param syncClient 동기화 클라이언트
     * @param serviceName 서비스 이름
     * @param serviceCode 서비스 코드 (nullable, Role-Permission 자동 매핑용)
     */
    public EndpointSyncRunner(
            RequestMappingHandlerMapping handlerMapping,
            EndpointSyncClient syncClient,
            String serviceName,
            String serviceCode) {
        this(handlerMapping, syncClient, serviceName, serviceCode, true);
    }

    /**
     * EndpointSyncRunner 생성자 (활성화 플래그 포함)
     *
     * @param handlerMapping Spring MVC 핸들러 매핑
     * @param syncClient 동기화 클라이언트
     * @param serviceName 서비스 이름
     * @param serviceCode 서비스 코드 (nullable)
     * @param enabled 동기화 활성화 여부
     */
    public EndpointSyncRunner(
            RequestMappingHandlerMapping handlerMapping,
            EndpointSyncClient syncClient,
            String serviceName,
            String serviceCode,
            boolean enabled) {
        this.handlerMapping = handlerMapping;
        this.syncClient = syncClient;
        this.serviceName = serviceName;
        this.serviceCode = serviceCode;
        this.enabled = enabled;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("Endpoint sync is disabled");
            return;
        }

        try {
            log.info("Starting endpoint sync for service: {}", serviceName);

            // 1. 엔드포인트 스캔
            EndpointScanner scanner = new EndpointScanner(handlerMapping);
            List<EndpointInfo> endpoints = scanner.scan();

            if (endpoints.isEmpty()) {
                log.info("No endpoints with @RequirePermission found");
                return;
            }

            // 2. 동기화 요청 생성
            EndpointSyncRequest request =
                    EndpointSyncRequest.of(serviceName, serviceCode, endpoints);

            // 3. AuthHub에 동기화 요청
            syncClient.sync(request);

            log.info(
                    "Successfully synced {} endpoints for service: {}",
                    endpoints.size(),
                    serviceName);

        } catch (Exception e) {
            log.error("Failed to sync endpoints for service: {}", serviceName, e);
            // 동기화 실패해도 애플리케이션 시작은 계속 진행
        }
    }
}
