package com.ryuqq.authhub.integration.repository.rolepermission;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.repository.RolePermissionJpaRepository;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.integration.common.base.RepositoryTestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * RolePermissionJpaRepository 통합 테스트.
 *
 * <p>Repository 레이어의 CRUD 및 쿼리 기능을 검증합니다.
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.ROLE)
class RolePermissionRepositoryIntegrationTest extends RepositoryTestBase {

    @Autowired private RolePermissionJpaRepository rolePermissionJpaRepository;

    @Autowired private RoleJpaRepository roleJpaRepository;

    @Autowired private PermissionJpaRepository permissionJpaRepository;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    private RoleJpaEntity savedRole;
    private PermissionJpaEntity savedPermission;

    @BeforeEach
    void setUp() {
        rolePermissionJpaRepository.deleteAll();
        roleJpaRepository.deleteAll();
        permissionJpaRepository.deleteAll();

        RoleJpaEntity role =
                RoleJpaEntity.of(
                        null,
                        null,
                        "ADMIN",
                        "관리자",
                        "관리자 역할",
                        RoleType.CUSTOM,
                        FIXED_TIME,
                        FIXED_TIME,
                        null);
        savedRole = roleJpaRepository.save(role);

        PermissionJpaEntity permission =
                PermissionJpaEntity.of(
                        null,
                        "user:read",
                        "user",
                        "read",
                        "사용자 조회",
                        PermissionType.CUSTOM,
                        FIXED_TIME,
                        FIXED_TIME,
                        null);
        savedPermission = permissionJpaRepository.save(permission);

        flushAndClear();
    }

    @Nested
    @DisplayName("save 테스트")
    class SaveTest {

        @Test
        @DisplayName("역할-권한 관계를 저장할 수 있다")
        void shouldSaveRolePermission() {
            // given
            RolePermissionJpaEntity entity =
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), savedPermission.getPermissionId(), FIXED_TIME);

            // when
            RolePermissionJpaEntity saved = rolePermissionJpaRepository.save(entity);
            flushAndClear();

            // then
            assertThat(saved.getRolePermissionId()).isNotNull();

            Optional<RolePermissionJpaEntity> found =
                    rolePermissionJpaRepository.findById(saved.getRolePermissionId());
            assertThat(found).isPresent();
            assertThat(found.get().getRoleId()).isEqualTo(savedRole.getRoleId());
            assertThat(found.get().getPermissionId()).isEqualTo(savedPermission.getPermissionId());
        }
    }

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 역할-권한 관계를 ID로 조회할 수 있다")
        void shouldFindExistingRolePermission() {
            // given
            RolePermissionJpaEntity entity =
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), savedPermission.getPermissionId(), FIXED_TIME);
            RolePermissionJpaEntity saved = rolePermissionJpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<RolePermissionJpaEntity> found =
                    rolePermissionJpaRepository.findById(saved.getRolePermissionId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getRoleId()).isEqualTo(savedRole.getRoleId());
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentId() {
            // when
            Optional<RolePermissionJpaEntity> found = rolePermissionJpaRepository.findById(99999L);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("delete 테스트")
    class DeleteTest {

        @Test
        @DisplayName("역할-권한 관계를 삭제할 수 있다")
        void shouldDeleteRolePermission() {
            // given
            RolePermissionJpaEntity entity =
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), savedPermission.getPermissionId(), FIXED_TIME);
            RolePermissionJpaEntity saved = rolePermissionJpaRepository.save(entity);
            flushAndClear();

            // when
            rolePermissionJpaRepository.deleteById(saved.getRolePermissionId());
            flushAndClear();

            // then
            Optional<RolePermissionJpaEntity> found =
                    rolePermissionJpaRepository.findById(saved.getRolePermissionId());
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("roleId와 permissionId로 역할-권한 관계를 삭제할 수 있다")
        void shouldDeleteByRoleIdAndPermissionId() {
            // given
            RolePermissionJpaEntity entity =
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), savedPermission.getPermissionId(), FIXED_TIME);
            rolePermissionJpaRepository.save(entity);
            flushAndClear();

            // when
            rolePermissionJpaRepository.deleteByRoleIdAndPermissionId(
                    savedRole.getRoleId(), savedPermission.getPermissionId());
            flushAndClear();

            // then
            List<RolePermissionJpaEntity> found =
                    rolePermissionJpaRepository.findAll().stream()
                            .filter(
                                    rp ->
                                            rp.getRoleId().equals(savedRole.getRoleId())
                                                    && rp.getPermissionId()
                                                            .equals(
                                                                    savedPermission
                                                                            .getPermissionId()))
                            .toList();
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("역할의 모든 권한 관계를 삭제할 수 있다")
        void shouldDeleteAllByRoleId() {
            // given
            PermissionJpaEntity permission2 =
                    PermissionJpaEntity.of(
                            null,
                            "user:write",
                            "user",
                            "write",
                            "사용자 쓰기",
                            PermissionType.CUSTOM,
                            FIXED_TIME,
                            FIXED_TIME,
                            null);
            PermissionJpaEntity savedPermission2 = permissionJpaRepository.save(permission2);
            flushAndClear();

            RolePermissionJpaEntity entity1 =
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), savedPermission.getPermissionId(), FIXED_TIME);
            RolePermissionJpaEntity entity2 =
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), savedPermission2.getPermissionId(), FIXED_TIME);
            rolePermissionJpaRepository.save(entity1);
            rolePermissionJpaRepository.save(entity2);
            flushAndClear();

            // when
            rolePermissionJpaRepository.deleteAllByRoleId(savedRole.getRoleId());
            flushAndClear();

            // then
            List<RolePermissionJpaEntity> found =
                    rolePermissionJpaRepository.findAll().stream()
                            .filter(rp -> rp.getRoleId().equals(savedRole.getRoleId()))
                            .toList();
            assertThat(found).isEmpty();
        }
    }
}
