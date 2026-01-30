package com.ryuqq.authhub.application.rolepermission.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.rolepermission.port.out.command.RolePermissionCommandPort;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.fixture.RolePermissionFixture;
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
 * RolePermissionCommandManager 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>CommandManager는 CommandPort 위임 담당
 *   <li>Port 호출이 올바르게 위임되는지 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RolePermissionCommandManager 단위 테스트")
class RolePermissionCommandManagerTest {

    @Mock private RolePermissionCommandPort persistencePort;

    private RolePermissionCommandManager sut;

    @BeforeEach
    void setUp() {
        sut = new RolePermissionCommandManager(persistencePort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: RolePermission 영속화 후 반환")
        void shouldPersistAndReturn() {
            // given
            RolePermission rolePermission = RolePermissionFixture.createNew();
            RolePermission persisted = RolePermissionFixture.create();

            given(persistencePort.persist(rolePermission)).willReturn(persisted);

            // when
            RolePermission result = sut.persist(rolePermission);

            // then
            assertThat(result).isEqualTo(persisted);
            then(persistencePort).should().persist(rolePermission);
        }
    }

    @Nested
    @DisplayName("persistAll 메서드")
    class PersistAll {

        @Test
        @DisplayName("성공: RolePermission 목록 영속화 후 반환")
        void shouldPersistAllAndReturn() {
            // given
            List<RolePermission> rolePermissions = List.of(RolePermissionFixture.createNew());
            List<RolePermission> persisted = List.of(RolePermissionFixture.create());

            given(persistencePort.persistAll(rolePermissions)).willReturn(persisted);

            // when
            List<RolePermission> result = sut.persistAll(rolePermissions);

            // then
            assertThat(result).hasSize(1);
            then(persistencePort).should().persistAll(rolePermissions);
        }
    }

    @Nested
    @DisplayName("deleteAll 메서드")
    class DeleteAll {

        @Test
        @DisplayName("성공: 역할-권한 관계 다건 삭제 위임")
        void shouldDelegateDeleteAll() {
            // given
            RoleId roleId = RolePermissionFixture.defaultRoleId();
            List<PermissionId> permissionIds = List.of(RolePermissionFixture.defaultPermissionId());

            // when
            sut.deleteAll(roleId, permissionIds);

            // then
            then(persistencePort).should().deleteAll(roleId, permissionIds);
        }
    }
}
