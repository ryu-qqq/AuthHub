package com.ryuqq.authhub.sdk.sync;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("EndpointSyncRunner")
class EndpointSyncRunnerTest {

    @Nested
    @DisplayName("run")
    class Run {

        @Test
        @DisplayName("엔드포인트 스캔 후 동기화 요청 전송")
        void syncsEndpointsOnRun() throws Exception {
            List<EndpointInfo> endpoints =
                    List.of(
                            EndpointInfo.of("GET", "/api/v1/users/{id}", "user:read", ""),
                            EndpointInfo.of("POST", "/api/v1/users", "user:create", ""));

            Map<RequestMappingInfo, HandlerMethod> handlerMethods =
                    createHandlerMethodsWithEndpoints(endpoints);

            RequestMappingHandlerMapping handlerMapping = mock(RequestMappingHandlerMapping.class);
            when(handlerMapping.getHandlerMethods()).thenReturn(handlerMethods);

            EndpointSyncClient syncClient = mock(EndpointSyncClient.class);

            EndpointSyncRunner runner =
                    new EndpointSyncRunner(handlerMapping, syncClient, "authhub");

            runner.run(mock(ApplicationArguments.class));

            ArgumentCaptor<EndpointSyncRequest> requestCaptor =
                    ArgumentCaptor.forClass(EndpointSyncRequest.class);
            verify(syncClient).sync(requestCaptor.capture());

            EndpointSyncRequest captured = requestCaptor.getValue();
            assertThat(captured.serviceName()).isEqualTo("authhub");
            assertThat(captured.endpoints()).hasSize(2);
        }

        @Test
        @DisplayName("엔드포인트 없으면 동기화 스킵")
        void skipsSyncWhenNoEndpoints() {
            RequestMappingHandlerMapping handlerMapping = mock(RequestMappingHandlerMapping.class);
            when(handlerMapping.getHandlerMethods()).thenReturn(Collections.emptyMap());

            EndpointSyncClient syncClient = mock(EndpointSyncClient.class);

            EndpointSyncRunner runner =
                    new EndpointSyncRunner(handlerMapping, syncClient, "authhub");

            runner.run(mock(ApplicationArguments.class));

            verify(syncClient, never()).sync(any());
        }

        @Test
        @DisplayName("enabled=false면 동기화 스킵")
        void skipsSyncWhenDisabled() {
            // enabled=false인 경우 handlerMapping.getHandlerMethods()가 호출되지 않으므로
            // stubbing 없이 mock만 생성
            RequestMappingHandlerMapping handlerMapping = mock(RequestMappingHandlerMapping.class);
            EndpointSyncClient syncClient = mock(EndpointSyncClient.class);

            EndpointSyncRunner runner =
                    new EndpointSyncRunner(handlerMapping, syncClient, "authhub", false);

            runner.run(mock(ApplicationArguments.class));

            verify(syncClient, never()).sync(any());
        }

        @Test
        @DisplayName("동기화 실패해도 예외 전파하지 않음")
        void doesNotPropagateExceptionOnSyncFailure() throws Exception {
            List<EndpointInfo> endpoints =
                    List.of(EndpointInfo.of("GET", "/api/v1/users", "user:read", ""));
            Map<RequestMappingInfo, HandlerMethod> handlerMethods =
                    createHandlerMethodsWithEndpoints(endpoints);

            RequestMappingHandlerMapping handlerMapping = mock(RequestMappingHandlerMapping.class);
            when(handlerMapping.getHandlerMethods()).thenReturn(handlerMethods);

            EndpointSyncClient syncClient = mock(EndpointSyncClient.class);
            org.mockito.Mockito.doThrow(new EndpointSyncException("Sync failed"))
                    .when(syncClient)
                    .sync(any());

            EndpointSyncRunner runner =
                    new EndpointSyncRunner(handlerMapping, syncClient, "authhub");

            runner.run(mock(ApplicationArguments.class));
        }
    }

    private Map<RequestMappingInfo, HandlerMethod> createHandlerMethodsWithEndpoints(
            List<EndpointInfo> endpoints) throws Exception {
        Map<RequestMappingInfo, HandlerMethod> map = new HashMap<>();
        EndpointScannerTest.TestController controller = new EndpointScannerTest.TestController();

        for (EndpointInfo info : endpoints) {
            org.springframework.web.bind.annotation.RequestMethod method =
                    org.springframework.web.bind.annotation.RequestMethod.valueOf(
                            info.httpMethod());
            RequestMappingInfo mappingInfo =
                    RequestMappingInfo.paths(info.pathPattern()).methods(method).build();

            java.lang.reflect.Method handlerMethod;
            if ("user:read".equals(info.permissionKey())) {
                handlerMethod = EndpointScannerTest.TestController.class.getMethod("getUser");
            } else if ("user:create".equals(info.permissionKey())) {
                handlerMethod = EndpointScannerTest.TestController.class.getMethod("createUser");
            } else {
                continue;
            }

            map.put(mappingInfo, new HandlerMethod(controller, handlerMethod));
        }
        return map;
    }
}
