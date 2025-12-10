package com.ryuqq.authhub.application.endpointpermission.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.endpointpermission.dto.command.DeleteEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.manager.command.EndpointPermissionTransactionManager;
import com.ryuqq.authhub.application.endpointpermission.manager.query.EndpointPermissionReadManager;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.exception.EndpointPermissionNotFoundException;
import com.ryuqq.authhub.domain.endpointpermission.fixture.EndpointPermissionFixture;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import java.time.Clock;
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
 * DeleteEndpointPermissionService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteEndpointPermissionService 단위 테스트")
class DeleteEndpointPermissionServiceTest {

    @Mock private EndpointPermissionTransactionManager transactionManager;
    @Mock private EndpointPermissionReadManager readManager;

    private DeleteEndpointPermissionService service;
    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = EndpointPermissionFixture.fixedClock();
        service = new DeleteEndpointPermissionService(transactionManager, readManager, clock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("엔드포인트 권한을 성공적으로 삭제한다")
        void shouldDeleteEndpointPermissionSuccessfully() {
            // given
            UUID id = UUID.randomUUID();
            EndpointPermission existing = EndpointPermissionFixture.createReconstituted(id);
            DeleteEndpointPermissionCommand command =
                    new DeleteEndpointPermissionCommand(id.toString());

            given(readManager.findById(any(EndpointPermissionId.class)))
                    .willReturn(Optional.of(existing));
            given(transactionManager.persist(any(EndpointPermission.class))).willReturn(existing);

            // when
            service.execute(command);

            // then
            verify(transactionManager).persist(any(EndpointPermission.class));
        }

        @Test
        @DisplayName("존재하지 않는 엔드포인트 권한 삭제 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenEndpointPermissionNotFound() {
            // given
            DeleteEndpointPermissionCommand command =
                    new DeleteEndpointPermissionCommand(UUID.randomUUID().toString());

            given(readManager.findById(any(EndpointPermissionId.class)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(EndpointPermissionNotFoundException.class);

            verify(transactionManager, never()).persist(any());
        }
    }
}
