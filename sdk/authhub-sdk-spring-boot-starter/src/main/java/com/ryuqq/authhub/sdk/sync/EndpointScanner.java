package com.ryuqq.authhub.sdk.sync;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * EndpointScanner - @RequirePermission 어노테이션 스캐너
 *
 * <p>Spring MVC의 RequestMappingHandlerMapping을 사용하여 모든 엔드포인트를 스캔하고, @RequirePermission 어노테이션이 붙은
 * 엔드포인트 정보를 수집합니다.
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * @Autowired
 * private RequestMappingHandlerMapping handlerMapping;
 *
 * public void scan() {
 *     EndpointScanner scanner = new EndpointScanner(handlerMapping);
 *     List<EndpointInfo> endpoints = scanner.scan();
 * }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public class EndpointScanner {

    private static final Logger log = LoggerFactory.getLogger(EndpointScanner.class);

    private final RequestMappingHandlerMapping handlerMapping;

    public EndpointScanner(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    /**
     * 모든 @RequirePermission 어노테이션이 붙은 엔드포인트 스캔
     *
     * @return 스캔된 엔드포인트 정보 목록
     */
    public List<EndpointInfo> scan() {
        List<EndpointInfo> endpoints = new ArrayList<>();

        handlerMapping
                .getHandlerMethods()
                .forEach(
                        (mappingInfo, handlerMethod) -> {
                            EndpointInfo endpoint = extractEndpointInfo(mappingInfo, handlerMethod);
                            if (endpoint != null) {
                                endpoints.add(endpoint);
                            }
                        });

        log.info("Scanned {} endpoints with @RequirePermission", endpoints.size());
        return endpoints;
    }

    /**
     * 핸들러 메서드에서 엔드포인트 정보 추출
     *
     * @param mappingInfo 요청 매핑 정보
     * @param handlerMethod 핸들러 메서드
     * @return 엔드포인트 정보 (어노테이션이 없으면 null)
     */
    private EndpointInfo extractEndpointInfo(
            RequestMappingInfo mappingInfo, HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);

        if (annotation == null) {
            return null;
        }

        String httpMethod = extractHttpMethod(mappingInfo);
        String pathPattern = extractPathPattern(mappingInfo);
        String permissionKey = annotation.value();
        String description = annotation.description();

        if (httpMethod == null || pathPattern == null) {
            log.warn(
                    "Could not extract HTTP method or path pattern for method: {}",
                    method.getName());
            return null;
        }

        log.debug("Found endpoint: {} {} -> {}", httpMethod, pathPattern, permissionKey);

        return EndpointInfo.of(httpMethod, pathPattern, permissionKey, description);
    }

    /**
     * RequestMappingInfo에서 HTTP 메서드 추출
     *
     * @param mappingInfo 요청 매핑 정보
     * @return HTTP 메서드 (여러 개면 첫 번째, 없으면 null)
     */
    private String extractHttpMethod(RequestMappingInfo mappingInfo) {
        Set<org.springframework.web.bind.annotation.RequestMethod> methods =
                mappingInfo.getMethodsCondition().getMethods();

        if (methods.isEmpty()) {
            return null;
        }

        return methods.iterator().next().name();
    }

    /**
     * RequestMappingInfo에서 경로 패턴 추출
     *
     * @param mappingInfo 요청 매핑 정보
     * @return 경로 패턴 (여러 개면 첫 번째, 없으면 null)
     */
    private String extractPathPattern(RequestMappingInfo mappingInfo) {
        Set<String> patterns = mappingInfo.getPatternValues();

        if (patterns.isEmpty()) {
            return null;
        }

        return patterns.iterator().next();
    }
}
