package com.ryuqq.authhub.application.role.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.role.port.out.command.RoleCommandPort;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RoleCommandManager 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>CommandManager는 CommandPort 위임 + @Transactional 관리 담당
 *   <li>Port 호출이 올바르게 위임되는지 검증
 *   <li>반환값이 올바르게 전달되는지 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleCommandManager 단위 테스트")
class RoleCommandManagerTest {

    @Mock private RoleCommandPort persistencePort;

    private RoleCommandManager sut;

    @BeforeEach
    void setUp() {
        sut = new RoleCommandManager(persistencePort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: Role 영속화 후 ID 반환")
        void shouldPersistAndReturnId() {
            // given
            Role role = RoleFixture.createNewCustomRole();
            Long expectedId = RoleFixture.defaultIdValue();

            given(persistencePort.persist(role)).willReturn(expectedId);

            // when
            Long result = sut.persist(role);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(persistencePort).should().persist(role);
        }

        @Test
        @DisplayName("CommandPort에 Role을 위임하여 영속화")
        void shouldDelegateToPersistencePort() {
            // given
            Role role = RoleFixture.create();
            Long persistedId = 1L;

            given(persistencePort.persist(role)).willReturn(persistedId);

            // when
            sut.persist(role);

            // then
            then(persistencePort).should().persist(role);
        }
    }
}
