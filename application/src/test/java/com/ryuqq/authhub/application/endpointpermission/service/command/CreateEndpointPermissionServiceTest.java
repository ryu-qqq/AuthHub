package com.ryuqq.authhub.application.endpointpermission.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.endpointpermission.assembler.EndpointPermissionAssembler;
import com.ryuqq.authhub.application.endpointpermission.dto.command.CreateEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.factory.command.EndpointPermissionCommandFactory;
import com.ryuqq.authhub.application.endpointpermission.manager.command.EndpointPermissionTransactionManager;
import com.ryuqq.authhub.application.endpointpermission.manager.query.EndpointPermissionReadManager;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.exception.DuplicateEndpointPermissionException;
import com.ryuqq.authhub.domain.endpointpermission.fixture.EndpointPermissionFixture;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointPath;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateEndpointPermissionService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateEndpointPermissionService 단위 테스트")
class CreateEndpointPermissionServiceTest {

    @Mock private EndpointPermissionCommandFactory commandFactory;
    @Mock private EndpointPermissionTransactionManager transactionManager;
    @Mock private EndpointPermissionReadManager readManager;
    @Mock private EndpointPermissionAssembler assembler;

    private CreateEndpointPermissionService service;

    @BeforeEach
    void setUp() {
        service =
                new CreateEndpointPermissionService(
                        commandFactory, transactionManager, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("Public 엔드포인트 권한을 성공적으로 생성한다")
        void shouldCreatePublicEndpointPermissionSuccessfully() {
            // given
            EndpointPermission endpointPermission = EndpointPermissionFixture.createPublic();
            CreateEndpointPermissionCommand command =
                    new CreateEndpointPermissionCommand(
                            "auth-hub", "/api/v1/health", "GET", "헬스체크", true, null, null);
            EndpointPermissionResponse expectedResponse =
                    new EndpointPermissionResponse(
                            endpointPermission.endpointPermissionIdValue().toString(),
                            endpointPermission.serviceNameValue(),
                            endpointPermission.pathValue(),
                            endpointPermission.methodValue(),
                            endpointPermission.descriptionValue(),
                            endpointPermission.isPublic(),
                            endpointPermission.requiredPermissionValues(),
                            endpointPermission.requiredRoleValues(),
                            endpointPermission.getVersion(),
                            endpointPermission.createdAt(),
                            endpointPermission.updatedAt());

            given(
                            readManager.existsByServiceNameAndPathAndMethod(
                                    any(ServiceName.class),
                                    any(EndpointPath.class),
                                    any(HttpMethod.class)))
                    .willReturn(false);
            given(commandFactory.create(command)).willReturn(endpointPermission);
            given(transactionManager.persist(endpointPermission)).willReturn(endpointPermission);
            given(assembler.toResponse(endpointPermission)).willReturn(expectedResponse);

            // when
            EndpointPermissionResponse response = service.execute(command);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.isPublic()).isTrue();
            verify(commandFactory).create(command);
            verify(transactionManager).persist(endpointPermission);
            verify(assembler).toResponse(endpointPermission);
        }

        @Test
        @DisplayName("Protected 엔드포인트 권한을 성공적으로 생성한다")
        void shouldCreateProtectedEndpointPermissionSuccessfully() {
            // given
            EndpointPermission endpointPermission = EndpointPermissionFixture.createProtected();
            CreateEndpointPermissionCommand command =
                    new CreateEndpointPermissionCommand(
                            "auth-hub",
                            "/api/v1/users",
                            "GET",
                            "사용자 조회",
                            false,
                            Set.of("user:read"),
                            null);
            EndpointPermissionResponse expectedResponse =
                    new EndpointPermissionResponse(
                            endpointPermission.endpointPermissionIdValue().toString(),
                            endpointPermission.serviceNameValue(),
                            endpointPermission.pathValue(),
                            endpointPermission.methodValue(),
                            endpointPermission.descriptionValue(),
                            endpointPermission.isPublic(),
                            endpointPermission.requiredPermissionValues(),
                            endpointPermission.requiredRoleValues(),
                            endpointPermission.getVersion(),
                            endpointPermission.createdAt(),
                            endpointPermission.updatedAt());

            given(
                            readManager.existsByServiceNameAndPathAndMethod(
                                    any(ServiceName.class),
                                    any(EndpointPath.class),
                                    any(HttpMethod.class)))
                    .willReturn(false);
            given(commandFactory.create(command)).willReturn(endpointPermission);
            given(transactionManager.persist(endpointPermission)).willReturn(endpointPermission);
            given(assembler.toResponse(endpointPermission)).willReturn(expectedResponse);

            // when
            EndpointPermissionResponse response = service.execute(command);

            // then
            assertThat(response.isPublic()).isFalse();
            verify(transactionManager).persist(endpointPermission);
        }

        @Test
        @DisplayName("중복 엔드포인트 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDuplicateEndpoint() {
            // given
            CreateEndpointPermissionCommand command =
                    new CreateEndpointPermissionCommand(
                            "auth-hub",
                            "/api/v1/users",
                            "GET",
                            "설명",
                            false,
                            Set.of("user:read"),
                            null);

            given(
                            readManager.existsByServiceNameAndPathAndMethod(
                                    any(ServiceName.class),
                                    any(EndpointPath.class),
                                    any(HttpMethod.class)))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(DuplicateEndpointPermissionException.class);

            verify(commandFactory, never()).create(any());
            verify(transactionManager, never()).persist(any());
        }
    }
}
