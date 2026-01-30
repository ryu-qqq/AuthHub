package com.ryuqq.authhub.application.permissionendpoint.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.permissionendpoint.port.out.command.PermissionEndpointCommandPort;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.fixture.PermissionEndpointFixture;
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
 * PermissionEndpointCommandManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionEndpointCommandManager 단위 테스트")
class PermissionEndpointCommandManagerTest {

    @Mock private PermissionEndpointCommandPort permissionEndpointCommandPort;

    private PermissionEndpointCommandManager sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionEndpointCommandManager(permissionEndpointCommandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: PermissionEndpoint 영속화 후 ID 반환")
        void shouldPersistAndReturnId() {
            // given
            PermissionEndpoint permissionEndpoint = PermissionEndpointFixture.createNew();
            Long expectedId = PermissionEndpointFixture.defaultIdValue();

            given(permissionEndpointCommandPort.persist(permissionEndpoint)).willReturn(expectedId);

            // when
            Long result = sut.persist(permissionEndpoint);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(permissionEndpointCommandPort).should().persist(permissionEndpoint);
        }
    }

    @Nested
    @DisplayName("persistAll 메서드")
    class PersistAll {

        @Test
        @DisplayName("성공: PermissionEndpoint 목록 영속화")
        void shouldPersistAll() {
            // given
            List<PermissionEndpoint> permissionEndpoints =
                    List.of(PermissionEndpointFixture.createNew());

            // when
            sut.persistAll(permissionEndpoints);

            // then
            then(permissionEndpointCommandPort).should().persist(permissionEndpoints.get(0));
        }

        @Test
        @DisplayName("빈 목록 입력 시 Port 호출하지 않음")
        void shouldNotCallPort_WhenInputIsEmpty() {
            // when
            sut.persistAll(List.of());

            // then
            then(permissionEndpointCommandPort).shouldHaveNoInteractions();
        }
    }
}
