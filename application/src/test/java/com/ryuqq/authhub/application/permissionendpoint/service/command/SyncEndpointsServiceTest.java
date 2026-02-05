package com.ryuqq.authhub.application.permissionendpoint.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.SyncEndpointsResult;
import com.ryuqq.authhub.application.permissionendpoint.internal.EndpointSyncCoordinator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SyncEndpointsService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SyncEndpointsService 단위 테스트")
class SyncEndpointsServiceTest {

    @Mock private EndpointSyncCoordinator coordinator;

    private SyncEndpointsService sut;

    @BeforeEach
    void setUp() {
        sut = new SyncEndpointsService(coordinator);
    }

    @Nested
    @DisplayName("sync 메서드")
    class Sync {

        @Test
        @DisplayName("성공: Coordinator coordinate 위임 후 결과 반환")
        void shouldDelegateToCoordinator_AndReturnResult() {
            SyncEndpointsCommand command = new SyncEndpointsCommand("authhub", null, List.of());
            SyncEndpointsResult expected = SyncEndpointsResult.of("authhub", 0, 0, 0, 0, 0);

            given(coordinator.coordinate(command)).willReturn(expected);

            SyncEndpointsResult result = sut.sync(command);

            assertThat(result).isEqualTo(expected);
            then(coordinator).should().coordinate(command);
        }
    }
}
