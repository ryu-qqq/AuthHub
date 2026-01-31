package com.ryuqq.authhub.sdk.sync;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("EndpointScanner")
class EndpointScannerTest {

    @Nested
    @DisplayName("scan")
    class Scan {

        @Test
        @DisplayName("@RequirePermission 엔드포인트 스캔")
        void scansEndpointsWithRequirePermission() throws Exception {
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = new HashMap<>();
            RequestMappingInfo getMapping =
                    RequestMappingInfo.paths("/api/v1/users/{id}")
                            .methods(org.springframework.web.bind.annotation.RequestMethod.GET)
                            .build();
            HandlerMethod handlerMethod =
                    new HandlerMethod(
                            new TestController(), TestController.class.getMethod("getUser"));

            handlerMethods.put(getMapping, handlerMethod);

            RequestMappingHandlerMapping handlerMapping = mock(RequestMappingHandlerMapping.class);
            when(handlerMapping.getHandlerMethods()).thenReturn(handlerMethods);

            EndpointScanner scanner = new EndpointScanner(handlerMapping);
            List<EndpointInfo> result = scanner.scan();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).httpMethod()).isEqualTo("GET");
            assertThat(result.get(0).pathPattern()).isEqualTo("/api/v1/users/{id}");
            assertThat(result.get(0).permissionKey()).isEqualTo("user:read");
            assertThat(result.get(0).description()).isEqualTo("사용자 조회");
        }

        @Test
        @DisplayName("@RequirePermission 없는 엔드포인트는 제외")
        void excludesEndpointsWithoutRequirePermission() throws Exception {
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = new HashMap<>();
            RequestMappingInfo getMapping =
                    RequestMappingInfo.paths("/api/v1/users")
                            .methods(org.springframework.web.bind.annotation.RequestMethod.GET)
                            .build();
            HandlerMethod handlerMethod =
                    new HandlerMethod(
                            new TestController(),
                            TestController.class.getMethod("listUsersWithoutPermission"));

            handlerMethods.put(getMapping, handlerMethod);

            RequestMappingHandlerMapping handlerMapping = mock(RequestMappingHandlerMapping.class);
            when(handlerMapping.getHandlerMethods()).thenReturn(handlerMethods);

            EndpointScanner scanner = new EndpointScanner(handlerMapping);
            List<EndpointInfo> result = scanner.scan();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("여러 엔드포인트 스캔")
        void scansMultipleEndpoints() throws Exception {
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = new HashMap<>();

            RequestMappingInfo getMapping =
                    RequestMappingInfo.paths("/api/v1/users/{id}")
                            .methods(org.springframework.web.bind.annotation.RequestMethod.GET)
                            .build();
            handlerMethods.put(
                    getMapping,
                    new HandlerMethod(
                            new TestController(), TestController.class.getMethod("getUser")));

            RequestMappingInfo postMapping =
                    RequestMappingInfo.paths("/api/v1/users")
                            .methods(org.springframework.web.bind.annotation.RequestMethod.POST)
                            .build();
            handlerMethods.put(
                    postMapping,
                    new HandlerMethod(
                            new TestController(), TestController.class.getMethod("createUser")));

            RequestMappingHandlerMapping handlerMapping = mock(RequestMappingHandlerMapping.class);
            when(handlerMapping.getHandlerMethods()).thenReturn(handlerMethods);

            EndpointScanner scanner = new EndpointScanner(handlerMapping);
            List<EndpointInfo> result = scanner.scan();

            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(EndpointInfo::permissionKey)
                    .containsExactlyInAnyOrder("user:read", "user:create");
        }

        @Test
        @DisplayName("빈 핸들러 매핑이면 빈 목록 반환")
        void returnsEmptyWhenNoHandlerMethods() {
            RequestMappingHandlerMapping handlerMapping = mock(RequestMappingHandlerMapping.class);
            when(handlerMapping.getHandlerMethods()).thenReturn(new HashMap<>());

            EndpointScanner scanner = new EndpointScanner(handlerMapping);
            List<EndpointInfo> result = scanner.scan();

            assertThat(result).isEmpty();
        }
    }

    @RequestMapping("/api/v1/users")
    static class TestController {

        @GetMapping("/{id}")
        @RequirePermission(value = "user:read", description = "사용자 조회")
        public void getUser() {}

        @PostMapping
        @RequirePermission(value = "user:create", description = "사용자 생성")
        public void createUser() {}

        @GetMapping
        public void listUsersWithoutPermission() {}
    }
}
