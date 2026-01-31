package com.ryuqq.authhub.application.userrole.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.userrole.port.out.command.UserRoleCommandPort;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import com.ryuqq.authhub.domain.userrole.fixture.UserRoleFixture;
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
 * UserRoleCommandManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleCommandManager 단위 테스트")
class UserRoleCommandManagerTest {

    @Mock private UserRoleCommandPort persistencePort;

    private UserRoleCommandManager sut;

    @BeforeEach
    void setUp() {
        sut = new UserRoleCommandManager(persistencePort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: UserRole 영속화 후 반환")
        void shouldPersistAndReturn() {
            // given
            UserRole userRole = UserRoleFixture.createNew();
            UserRole persisted = UserRoleFixture.create();

            given(persistencePort.persist(userRole)).willReturn(persisted);

            // when
            UserRole result = sut.persist(userRole);

            // then
            assertThat(result).isEqualTo(persisted);
            then(persistencePort).should().persist(userRole);
        }
    }

    @Nested
    @DisplayName("deleteAll 메서드")
    class DeleteAll {

        @Test
        @DisplayName("성공: 사용자-역할 관계 다건 삭제 위임")
        void shouldDelegateDeleteAll() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();
            List<RoleId> roleIds = List.of(UserRoleFixture.defaultRoleId());

            // when
            sut.deleteAll(userId, roleIds);

            // then
            then(persistencePort).should().deleteAll(userId, roleIds);
        }
    }
}
