package com.ryuqq.authhub.application.endpointpermission.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.endpointpermission.assembler.EndpointPermissionAssembler;
import com.ryuqq.authhub.application.endpointpermission.dto.query.GetEndpointPermissionQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.manager.query.EndpointPermissionReadManager;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.exception.EndpointPermissionNotFoundException;
import com.ryuqq.authhub.domain.endpointpermission.fixture.EndpointPermissionFixture;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import java.util.Optional;
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
 * GetEndpointPermissionService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetEndpointPermissionService 단위 테스트")
class GetEndpointPermissionServiceTest {

    @Mock private EndpointPermissionReadManager readManager;
    @Mock private EndpointPermissionAssembler assembler;

    private GetEndpointPermissionService service;

    @BeforeEach
    void setUp() {
        service = new GetEndpointPermissionService(readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("엔드포인트 권한을 성공적으로 조회한다")
        void shouldGetEndpointPermissionSuccessfully() {
            // given
            UUID id = UUID.randomUUID();
            EndpointPermission endpointPermission =
                    EndpointPermissionFixture.createReconstituted(id);
            GetEndpointPermissionQuery query = new GetEndpointPermissionQuery(id.toString());
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

            given(readManager.findById(any(EndpointPermissionId.class)))
                    .willReturn(Optional.of(endpointPermission));
            given(assembler.toResponse(endpointPermission)).willReturn(expectedResponse);

            // when
            EndpointPermissionResponse response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.id()).isEqualTo(id.toString());
        }

        @Test
        @DisplayName("존재하지 않는 엔드포인트 권한 조회 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenEndpointPermissionNotFound() {
            // given
            GetEndpointPermissionQuery query =
                    new GetEndpointPermissionQuery(UUID.randomUUID().toString());

            given(readManager.findById(any(EndpointPermissionId.class)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> service.execute(query))
                    .isInstanceOf(EndpointPermissionNotFoundException.class);
        }
    }
}
