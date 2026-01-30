package com.ryuqq.authhub.application.user.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.user.port.out.command.UserCommandPort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserCommandManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandManager 단위 테스트")
class UserCommandManagerTest {

    @Mock private UserCommandPort persistencePort;

    private UserCommandManager sut;

    @BeforeEach
    void setUp() {
        sut = new UserCommandManager(persistencePort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: User 영속화 후 ID 반환")
        void shouldPersistAndReturnId() {
            // given
            User user = UserFixture.createNew();
            String expectedId = UserFixture.defaultIdString();

            given(persistencePort.persist(user)).willReturn(expectedId);

            // when
            String result = sut.persist(user);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(persistencePort).should().persist(user);
        }
    }
}
