package com.ryuqq.authhub.integration.repository.rolepermission;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.repository.RolePermissionJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.repository.RolePermissionQueryDslRepository;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.domain.rolepermission.query.criteria.RolePermissionSearchCriteria;
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
 * RolePermission QueryDSL Repository 통합 테스트.
 *
 * <p>QueryDSL 기반의 역할-권한 관계 쿼리 동작을 검증합니다.
 *
 * <ul>
 *   <li>exists - 관계 존재 여부
 *   <li>findByRoleIdAndPermissionId - roleId + permissionId로 단건 조회
 *   <li>findAllByRoleId - 역할의 권한 목록 조회
 *   <li>findAllByPermissionId - 권한이 부여된 역할 목록 조회
 *   <li>existsByPermissionId - 권한 사용 여부 확인
 *   <li>findAllByCriteria - 조건 검색
 *   <li>countByCriteria - 조건 개수
 *   <li>findGrantedPermissionIds - 역할에 부여된 권한 ID 목록
 *   <li>findAllByRoleIds - 여러 역할의 권한 목록 조회
 * </ul>
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.ROLE)
@DisplayName("역할-권한 QueryDSL Repository 테스트")
class RolePermissionQueryDslRepositoryTest extends RepositoryTestBase {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    @Autowired private RolePermissionJpaRepository jpaRepository;
    @Autowired private RolePermissionQueryDslRepository queryDslRepository;
    @Autowired private RoleJpaRepository roleJpaRepository;
    @Autowired private PermissionJpaRepository permissionJpaRepository;

    private RoleJpaEntity savedRole;
    private PermissionJpaEntity savedPermission;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        roleJpaRepository.deleteAll();
        permissionJpaRepository.deleteAll();

        savedRole =
                roleJpaRepository.save(
                        RoleJpaEntity.of(
                                null,
                                null,
                                null,
                                "RP_ADMIN",
                                "관리자",
                                "역할",
                                RoleType.CUSTOM,
                                RoleScope.GLOBAL,
                                FIXED_TIME,
                                FIXED_TIME,
                                null));

        savedPermission =
                permissionJpaRepository.save(
                        PermissionJpaEntity.of(
                                null,
                                null,
                                "rp:read",
                                "rp",
                                "read",
                                "역할권한 조회",
                                PermissionType.CUSTOM,
                                FIXED_TIME,
                                FIXED_TIME,
                                null));

        flushAndClear();
    }

    @Nested
    @DisplayName("exists 테스트")
    class ExistsTest {

        @Test
        @DisplayName("역할-권한 관계 존재 확인 - true")
        void shouldReturnTrueWhenExists() {
            // given
            jpaRepository.save(
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), savedPermission.getPermissionId(), FIXED_TIME));
            flushAndClear();

            // when
            boolean exists =
                    queryDslRepository.exists(
                            savedRole.getRoleId(), savedPermission.getPermissionId());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 관계 - false")
        void shouldReturnFalseWhenNotExists() {
            // when
            boolean exists = queryDslRepository.exists(999999L, 999999L);

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findByRoleIdAndPermissionId 테스트")
    class FindByRoleIdAndPermissionIdTest {

        @Test
        @DisplayName("roleId + permissionId로 조회 성공")
        void shouldFindByRoleIdAndPermissionId() {
            // given
            jpaRepository.save(
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), savedPermission.getPermissionId(), FIXED_TIME));
            flushAndClear();

            // when
            Optional<RolePermissionJpaEntity> found =
                    queryDslRepository.findByRoleIdAndPermissionId(
                            savedRole.getRoleId(), savedPermission.getPermissionId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getRoleId()).isEqualTo(savedRole.getRoleId());
            assertThat(found.get().getPermissionId()).isEqualTo(savedPermission.getPermissionId());
        }

        @Test
        @DisplayName("존재하지 않는 관계 - 빈 Optional")
        void shouldReturnEmptyWhenNotFound() {
            // when
            Optional<RolePermissionJpaEntity> found =
                    queryDslRepository.findByRoleIdAndPermissionId(999999L, 999999L);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByRoleId 테스트")
    class FindAllByRoleIdTest {

        @Test
        @DisplayName("역할의 권한 목록 조회")
        void shouldFindAllByRoleId() {
            // given
            PermissionJpaEntity perm2 =
                    permissionJpaRepository.save(
                            PermissionJpaEntity.of(
                                    null,
                                    null,
                                    "rp:write",
                                    "rp",
                                    "write",
                                    null,
                                    PermissionType.CUSTOM,
                                    FIXED_TIME,
                                    FIXED_TIME,
                                    null));
            flushAndClear();

            jpaRepository.save(
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), savedPermission.getPermissionId(), FIXED_TIME));
            jpaRepository.save(
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), perm2.getPermissionId(), FIXED_TIME));
            flushAndClear();

            // when
            List<RolePermissionJpaEntity> found =
                    queryDslRepository.findAllByRoleId(savedRole.getRoleId());

            // then
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("findAllByPermissionId 테스트")
    class FindAllByPermissionIdTest {

        @Test
        @DisplayName("권한이 부여된 역할 목록 조회")
        void shouldFindAllByPermissionId() {
            // given
            RoleJpaEntity role2 =
                    roleJpaRepository.save(
                            RoleJpaEntity.of(
                                    null,
                                    null,
                                    null,
                                    "RP_USER",
                                    "사용자",
                                    null,
                                    RoleType.CUSTOM,
                                    RoleScope.GLOBAL,
                                    FIXED_TIME,
                                    FIXED_TIME,
                                    null));
            flushAndClear();

            jpaRepository.save(
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), savedPermission.getPermissionId(), FIXED_TIME));
            jpaRepository.save(
                    RolePermissionJpaEntity.create(
                            role2.getRoleId(), savedPermission.getPermissionId(), FIXED_TIME));
            flushAndClear();

            // when
            List<RolePermissionJpaEntity> found =
                    queryDslRepository.findAllByPermissionId(savedPermission.getPermissionId());

            // then
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("existsByPermissionId 테스트")
    class ExistsByPermissionIdTest {

        @Test
        @DisplayName("권한 사용 중 - true")
        void shouldReturnTrueWhenPermissionInUse() {
            // given
            jpaRepository.save(
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), savedPermission.getPermissionId(), FIXED_TIME));
            flushAndClear();

            // when
            boolean exists =
                    queryDslRepository.existsByPermissionId(savedPermission.getPermissionId());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("권한 미사용 - false")
        void shouldReturnFalseWhenPermissionNotInUse() {
            // when
            boolean exists = queryDslRepository.existsByPermissionId(999999L);

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findGrantedPermissionIds 테스트")
    class FindGrantedPermissionIdsTest {

        @Test
        @DisplayName("역할에 부여된 권한 ID 목록 조회")
        void shouldFindGrantedPermissionIds() {
            // given
            PermissionJpaEntity perm2 =
                    permissionJpaRepository.save(
                            PermissionJpaEntity.of(
                                    null,
                                    null,
                                    "rp:granted",
                                    "rp",
                                    "granted",
                                    null,
                                    PermissionType.CUSTOM,
                                    FIXED_TIME,
                                    FIXED_TIME,
                                    null));
            flushAndClear();

            jpaRepository.save(
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), savedPermission.getPermissionId(), FIXED_TIME));
            jpaRepository.save(
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), perm2.getPermissionId(), FIXED_TIME));
            flushAndClear();

            // when
            List<Long> grantedIds =
                    queryDslRepository.findGrantedPermissionIds(
                            savedRole.getRoleId(),
                            List.of(savedPermission.getPermissionId(), perm2.getPermissionId()));

            // then
            assertThat(grantedIds).hasSize(2);
            assertThat(grantedIds)
                    .containsExactlyInAnyOrder(
                            savedPermission.getPermissionId(), perm2.getPermissionId());
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 테스트")
    class FindAllByCriteriaTest {

        @Test
        @DisplayName("역할 ID로 조건 검색")
        void shouldFindByRoleIdCriteria() {
            // given
            jpaRepository.save(
                    RolePermissionJpaEntity.create(
                            savedRole.getRoleId(), savedPermission.getPermissionId(), FIXED_TIME));
            flushAndClear();

            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.ofRoleId(RoleId.of(savedRole.getRoleId()), 0, 20);

            // when
            List<RolePermissionJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getRoleId()).isEqualTo(savedRole.getRoleId());
        }
    }
}
