package com.ryuqq.authhub.application.permission.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.permission.port.out.command.PermissionCommandPort;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
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
 * PermissionCommandManager 단위 테스트
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
@DisplayName("PermissionCommandManager 단위 테스트")
class PermissionCommandManagerTest {

    @Mock private PermissionCommandPort persistencePort;

    private PermissionCommandManager sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionCommandManager(persistencePort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: Permission 영속화 후 ID 반환")
        void shouldPersistAndReturnId() {
            // given
            Permission permission = PermissionFixture.createNewCustomPermission();
            Long expectedId = PermissionFixture.defaultIdValue();

            given(persistencePort.persist(permission)).willReturn(expectedId);

            // when
            Long result = sut.persist(permission);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(persistencePort).should().persist(permission);
        }

        @Test
        @DisplayName("CommandPort에 Permission을 위임하여 영속화")
        void shouldDelegateToPersistencePort() {
            // given
            Permission permission = PermissionFixture.create();
            Long persistedId = 1L;

            given(persistencePort.persist(permission)).willReturn(persistedId);

            // when
            sut.persist(permission);

            // then
            then(persistencePort).should().persist(permission);
        }
    }

    @Nested
    @DisplayName("persistAllAndReturnKeyToIdMap 메서드")
    class PersistAllAndReturnKeyToIdMap {

        @Test
        @DisplayName("성공: 다건 영속화 후 permissionKey → ID 매핑 반환")
        void shouldPersistAllAndReturnKeyToIdMap() {
            // given - ID가 없는 새 Permission을 사용 (equals 충돌 방지)
            Permission p1 = PermissionFixture.createNewCustomWithResourceAndAction("user", "read");
            Permission p2 =
                    PermissionFixture.createNewCustomWithResourceAndAction("role", "create");
            List<Permission> permissions = List.of(p1, p2);

            given(persistencePort.persist(p1)).willReturn(1L);
            given(persistencePort.persist(p2)).willReturn(2L);

            // when
            var result = sut.persistAllAndReturnKeyToIdMap(permissions);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get("user:read")).isEqualTo(1L);
            assertThat(result.get("role:create")).isEqualTo(2L);
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 Map 반환")
        void shouldReturnEmptyMap_WhenInputIsEmpty() {
            // when
            var result = sut.persistAllAndReturnKeyToIdMap(List.of());

            // then
            assertThat(result).isEmpty();
            then(persistencePort).shouldHaveNoInteractions();
        }
    }
}
