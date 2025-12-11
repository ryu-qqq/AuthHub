package com.ryuqq.authhub.application.permission.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.permission.assembler.PermissionAssembler;
import com.ryuqq.authhub.application.permission.dto.query.GetPermissionQuery;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.permission.manager.query.PermissionReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
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
 * GetPermissionService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetPermissionService 단위 테스트")
class GetPermissionServiceTest {

    @Mock private PermissionReadManager readManager;

    @Mock private PermissionAssembler assembler;

    private GetPermissionService service;

    @BeforeEach
    void setUp() {
        service = new GetPermissionService(readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("권한을 성공적으로 조회한다")
        void shouldRetrievePermissionSuccessfully() {
            // given
            Permission permission = PermissionFixture.createReconstituted();
            UUID permissionId = permission.permissionIdValue();
            GetPermissionQuery query = new GetPermissionQuery(permissionId);
            PermissionResponse expectedResponse =
                    new PermissionResponse(
                            permissionId,
                            permission.keyValue(),
                            permission.resourceValue(),
                            permission.actionValue(),
                            permission.descriptionValue(),
                            permission.getType().name(),
                            permission.createdAt(),
                            permission.updatedAt());

            given(readManager.getById(PermissionId.of(permissionId))).willReturn(permission);
            given(assembler.toResponse(permission)).willReturn(expectedResponse);

            // when
            PermissionResponse response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.permissionId()).isEqualTo(permissionId);
            verify(readManager).getById(PermissionId.of(permissionId));
            verify(assembler).toResponse(permission);
        }

        @Test
        @DisplayName("존재하지 않는 권한 조회 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenPermissionNotFound() {
            // given
            UUID permissionId = UUID.randomUUID();
            GetPermissionQuery query = new GetPermissionQuery(permissionId);

            given(readManager.getById(PermissionId.of(permissionId)))
                    .willThrow(new PermissionNotFoundException(permissionId));

            // when & then
            assertThatThrownBy(() -> service.execute(query))
                    .isInstanceOf(PermissionNotFoundException.class);
        }
    }
}
