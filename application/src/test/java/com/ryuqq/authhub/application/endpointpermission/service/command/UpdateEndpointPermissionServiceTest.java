package com.ryuqq.authhub.application.endpointpermission.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.endpointpermission.assembler.EndpointPermissionAssembler;
import com.ryuqq.authhub.application.endpointpermission.dto.command.UpdateEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.manager.command.EndpointPermissionTransactionManager;
import com.ryuqq.authhub.application.endpointpermission.manager.query.EndpointPermissionReadManager;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.exception.EndpointPermissionNotFoundException;
import com.ryuqq.authhub.domain.endpointpermission.fixture.EndpointPermissionFixture;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import java.time.Clock;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UpdateEndpointPermissionService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateEndpointPermissionService 단위 테스트")
class UpdateEndpointPermissionServiceTest {

    @Mock private EndpointPermissionTransactionManager transactionManager;
    @Mock private EndpointPermissionReadManager readManager;
    @Mock private EndpointPermissionAssembler assembler;

    private UpdateEndpointPermissionService service;
    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = EndpointPermissionFixture.fixedClock();
        service =
                new UpdateEndpointPermissionService(
                        transactionManager, readManager, assembler, clock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("설명을 성공적으로 변경한다")
        void shouldUpdateDescriptionSuccessfully() {
            // given
            UUID id = UUID.randomUUID();
            EndpointPermission existing = EndpointPermissionFixture.createReconstituted(id);
            EndpointPermission updated =
                    existing.changeDescription(
                            EndpointPermissionFixture.createDescription("변경된 설명"), clock);
            UpdateEndpointPermissionCommand command =
                    new UpdateEndpointPermissionCommand(id.toString(), "변경된 설명", null, null, null);
            EndpointPermissionResponse expectedResponse = createResponse(updated);

            given(readManager.findById(any(EndpointPermissionId.class)))
                    .willReturn(Optional.of(existing));
            given(transactionManager.persist(any(EndpointPermission.class))).willReturn(updated);
            given(assembler.toResponse(any(EndpointPermission.class))).willReturn(expectedResponse);

            // when
            EndpointPermissionResponse response = service.execute(command);

            // then
            assertThat(response.description()).isEqualTo("변경된 설명");
            verify(transactionManager).persist(any(EndpointPermission.class));
        }

        @Test
        @DisplayName("Protected를 Public으로 변경한다")
        void shouldMakePublicFromProtected() {
            // given
            UUID id = UUID.randomUUID();
            EndpointPermission existing = EndpointPermissionFixture.createReconstituted(id);
            EndpointPermission updated = existing.makePublic(clock);
            UpdateEndpointPermissionCommand command =
                    new UpdateEndpointPermissionCommand(id.toString(), null, true, null, null);
            EndpointPermissionResponse expectedResponse = createResponse(updated);

            given(readManager.findById(any(EndpointPermissionId.class)))
                    .willReturn(Optional.of(existing));
            given(transactionManager.persist(any(EndpointPermission.class))).willReturn(updated);
            given(assembler.toResponse(any(EndpointPermission.class))).willReturn(expectedResponse);

            // when
            EndpointPermissionResponse response = service.execute(command);

            // then
            assertThat(response.isPublic()).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 엔드포인트 권한 수정 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenEndpointPermissionNotFound() {
            // given
            UpdateEndpointPermissionCommand command =
                    new UpdateEndpointPermissionCommand(
                            UUID.randomUUID().toString(), "설명", null, null, null);

            given(readManager.findById(any(EndpointPermissionId.class)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(EndpointPermissionNotFoundException.class);

            verify(transactionManager, never()).persist(any());
        }

        @Test
        @DisplayName("필요 권한을 변경한다")
        void shouldUpdateRequiredPermissions() {
            // given
            UUID id = UUID.randomUUID();
            EndpointPermission existing = EndpointPermissionFixture.createReconstituted(id);
            UpdateEndpointPermissionCommand command =
                    new UpdateEndpointPermissionCommand(
                            id.toString(), null, null, Set.of("user:write"), null);
            EndpointPermissionResponse expectedResponse = createResponse(existing);

            given(readManager.findById(any(EndpointPermissionId.class)))
                    .willReturn(Optional.of(existing));
            given(transactionManager.persist(any(EndpointPermission.class))).willReturn(existing);
            given(assembler.toResponse(any(EndpointPermission.class))).willReturn(expectedResponse);

            // when
            service.execute(command);

            // then
            verify(transactionManager).persist(any(EndpointPermission.class));
        }
    }

    private EndpointPermissionResponse createResponse(EndpointPermission ep) {
        return new EndpointPermissionResponse(
                ep.endpointPermissionIdValue().toString(),
                ep.serviceNameValue(),
                ep.pathValue(),
                ep.methodValue(),
                ep.descriptionValue(),
                ep.isPublic(),
                ep.requiredPermissionValues(),
                ep.requiredRoleValues(),
                ep.getVersion(),
                ep.createdAt(),
                ep.updatedAt());
    }
}
