package com.ryuqq.authhub.application.user.manager.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.port.out.persistence.UserPersistencePort;
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
 * UserTransactionManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserTransactionManager 단위 테스트")
class UserTransactionManagerTest {

    @Mock private UserPersistencePort persistencePort;

    private UserTransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        transactionManager = new UserTransactionManager(persistencePort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("사용자를 영속화하고 반환한다")
        void shouldPersistUser() {
            // given
            User newUser = UserFixture.createNew();
            User savedUser = UserFixture.create();
            given(persistencePort.persist(newUser)).willReturn(savedUser);

            // when
            User result = transactionManager.persist(newUser);

            // then
            assertThat(result).isEqualTo(savedUser);
            verify(persistencePort).persist(newUser);
        }

        @Test
        @DisplayName("기존 사용자를 업데이트한다")
        void shouldUpdateExistingUser() {
            // given
            User existingUser = UserFixture.create();
            given(persistencePort.persist(existingUser)).willReturn(existingUser);

            // when
            User result = transactionManager.persist(existingUser);

            // then
            assertThat(result).isEqualTo(existingUser);
            verify(persistencePort).persist(existingUser);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class DeleteTest {

        @Test
        @DisplayName("사용자를 삭제한다")
        void shouldDeleteUser() {
            // given
            User user = UserFixture.create();

            // when
            transactionManager.delete(user);

            // then
            verify(persistencePort).delete(user);
        }
    }
}
