package com.ryuqq.authhub.integration.repository.role;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.fixture.RoleJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleQueryDslRepository;
import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.role.query.criteria.RoleSearchCriteria;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleSearchField;
import com.ryuqq.authhub.domain.role.vo.RoleSortKey;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.integration.common.base.RepositoryTestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Role QueryDSL Repository 통합 테스트.
 *
 * <p>QueryDSL 기반의 복잡한 쿼리 동작을 검증합니다.
 *
 * <ul>
 *   <li>Soft Delete 필터링 (deletedAt IS NULL)
 *   <li>findByRoleId - ID로 단건 조회
 *   <li>existsByRoleId - ID 존재 여부
 *   <li>existsByTenantIdAndName - 테넌트 내 역할 이름 존재 여부
 *   <li>findByTenantIdAndName - 테넌트 내 역할 이름으로 단건 조회
 *   <li>findAllByCriteria - 조건 검색
 *   <li>countByCriteria - 조건 개수
 *   <li>findAllByIds - ID 목록으로 다건 조회
 * </ul>
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.ROLE)
@DisplayName("역할 QueryDSL Repository 테스트")
class RoleQueryDslRepositoryTest extends RepositoryTestBase {

    @Autowired private RoleJpaRepository jpaRepository;
    @Autowired private RoleQueryDslRepository queryDslRepository;
    @Autowired private TenantJpaRepository tenantJpaRepository;

    private TenantJpaEntity savedTenant;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        tenantJpaRepository.deleteAll();

        savedTenant = tenantJpaRepository.save(TenantJpaEntityFixture.create());

        flushAndClear();
    }

    @Nested
    @DisplayName("findByRoleId 테스트")
    class FindByRoleIdTest {

        @Test
        @DisplayName("활성 역할 조회 성공")
        void shouldFindActiveRole() {
            // given
            RoleJpaEntity entity =
                    jpaRepository.save(
                            RoleJpaEntity.of(
                                    null,
                                    savedTenant.getTenantId(),
                                    null,
                                    "ROLE_ADMIN",
                                    "관리자",
                                    "테스트 역할",
                                    RoleType.CUSTOM,
                                    RoleScope.TENANT,
                                    RoleJpaEntityFixture.fixedTime(),
                                    RoleJpaEntityFixture.fixedTime(),
                                    null));
            flushAndClear();

            // when
            Optional<RoleJpaEntity> found = queryDslRepository.findByRoleId(entity.getRoleId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("ROLE_ADMIN");
        }

        @Test
        @DisplayName("삭제된 역할은 조회되지 않음 (Soft Delete 필터)")
        void shouldNotFindDeletedRole() {
            // given
            RoleJpaEntity entity =
                    jpaRepository.save(
                            RoleJpaEntity.of(
                                    null,
                                    savedTenant.getTenantId(),
                                    null,
                                    "ROLE_DELETED",
                                    "삭제된역할",
                                    null,
                                    RoleType.CUSTOM,
                                    RoleScope.TENANT,
                                    RoleJpaEntityFixture.fixedTime(),
                                    RoleJpaEntityFixture.fixedTime(),
                                    RoleJpaEntityFixture.fixedTime()));
            flushAndClear();

            // when
            Optional<RoleJpaEntity> found = queryDslRepository.findByRoleId(entity.getRoleId());

            // then
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회시 빈 Optional 반환")
        void shouldReturnEmptyWhenNotFound() {
            // when
            Optional<RoleJpaEntity> found = queryDslRepository.findByRoleId(999999L);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByRoleId 테스트")
    class ExistsByRoleIdTest {

        @Test
        @DisplayName("활성 역할 존재 확인 - true")
        void shouldReturnTrueForActiveRole() {
            // given
            RoleJpaEntity entity =
                    jpaRepository.save(
                            RoleJpaEntity.of(
                                    null,
                                    savedTenant.getTenantId(),
                                    null,
                                    "ROLE_EXISTS",
                                    "존재역할",
                                    null,
                                    RoleType.CUSTOM,
                                    RoleScope.TENANT,
                                    RoleJpaEntityFixture.fixedTime(),
                                    RoleJpaEntityFixture.fixedTime(),
                                    null));
            flushAndClear();

            // when
            boolean exists = queryDslRepository.existsByRoleId(entity.getRoleId());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("삭제된 역할은 존재하지 않음으로 판단")
        void shouldReturnFalseForDeletedRole() {
            // given
            RoleJpaEntity entity =
                    jpaRepository.save(
                            RoleJpaEntity.of(
                                    null,
                                    savedTenant.getTenantId(),
                                    null,
                                    "ROLE_DELETED_EXISTS",
                                    "삭제됨",
                                    null,
                                    RoleType.CUSTOM,
                                    RoleScope.TENANT,
                                    RoleJpaEntityFixture.fixedTime(),
                                    RoleJpaEntityFixture.fixedTime(),
                                    RoleJpaEntityFixture.fixedTime()));
            flushAndClear();

            // when
            boolean exists = queryDslRepository.existsByRoleId(entity.getRoleId());

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findByTenantIdAndName 테스트")
    class FindByTenantIdAndNameTest {

        @Test
        @DisplayName("테넌트 내 역할명으로 조회 성공")
        void shouldFindByTenantIdAndName() {
            // given
            jpaRepository.save(
                    RoleJpaEntity.of(
                            null,
                            savedTenant.getTenantId(),
                            null,
                            "ROLE_FIND",
                            "조회역할",
                            null,
                            RoleType.CUSTOM,
                            RoleScope.TENANT,
                            RoleJpaEntityFixture.fixedTime(),
                            RoleJpaEntityFixture.fixedTime(),
                            null));
            flushAndClear();

            // when
            Optional<RoleJpaEntity> found =
                    queryDslRepository.findByTenantIdAndServiceIdAndName(
                            savedTenant.getTenantId(), null, "ROLE_FIND");

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("ROLE_FIND");
        }

        @Test
        @DisplayName("삭제된 역할은 조회되지 않음")
        void shouldNotFindDeletedRoleByName() {
            // given
            jpaRepository.save(
                    RoleJpaEntity.of(
                            null,
                            savedTenant.getTenantId(),
                            null,
                            "ROLE_DELETED_FIND",
                            "삭제됨",
                            null,
                            RoleType.CUSTOM,
                            RoleScope.TENANT,
                            RoleJpaEntityFixture.fixedTime(),
                            RoleJpaEntityFixture.fixedTime(),
                            RoleJpaEntityFixture.fixedTime()));
            flushAndClear();

            // when
            Optional<RoleJpaEntity> found =
                    queryDslRepository.findByTenantIdAndServiceIdAndName(
                            savedTenant.getTenantId(), null, "ROLE_DELETED_FIND");

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndName 테스트")
    class ExistsByTenantIdAndNameTest {

        @Test
        @DisplayName("테넌트 내 역할명 존재 확인 - true")
        void shouldReturnTrueForExistingName() {
            // given
            jpaRepository.save(
                    RoleJpaEntity.of(
                            null,
                            savedTenant.getTenantId(),
                            null,
                            "ROLE_UNIQUE",
                            "유일역할",
                            null,
                            RoleType.CUSTOM,
                            RoleScope.TENANT,
                            RoleJpaEntityFixture.fixedTime(),
                            RoleJpaEntityFixture.fixedTime(),
                            null));
            flushAndClear();

            // when
            boolean exists =
                    queryDslRepository.existsByTenantIdAndServiceIdAndName(
                            savedTenant.getTenantId(), null, "ROLE_UNIQUE");

            // then
            assertThat(exists).isTrue();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 테스트")
    class FindAllByCriteriaTest {

        @Test
        @DisplayName("테넌트별 역할 목록 조회 - 삭제된 역할 제외")
        void shouldFindAllExcludingDeleted() {
            // given
            jpaRepository.save(
                    RoleJpaEntity.of(
                            null,
                            savedTenant.getTenantId(),
                            null,
                            "ROLE_ACTIVE_1",
                            "활성역할1",
                            null,
                            RoleType.CUSTOM,
                            RoleScope.TENANT,
                            RoleJpaEntityFixture.fixedTime(),
                            RoleJpaEntityFixture.fixedTime(),
                            null));
            jpaRepository.save(
                    RoleJpaEntity.of(
                            null,
                            savedTenant.getTenantId(),
                            null,
                            "ROLE_DELETED_CRITERIA",
                            "삭제역할",
                            null,
                            RoleType.CUSTOM,
                            RoleScope.TENANT,
                            RoleJpaEntityFixture.fixedTime(),
                            RoleJpaEntityFixture.fixedTime(),
                            RoleJpaEntityFixture.fixedTime()));
            flushAndClear();

            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            TenantId.of(savedTenant.getTenantId()),
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            RoleSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<RoleJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("ROLE_ACTIVE_1");
        }
    }

    @Nested
    @DisplayName("findAllByIds 테스트")
    class FindAllByIdsTest {

        @Test
        @DisplayName("ID 목록으로 역할 목록 조회")
        void shouldFindByIds() {
            // given
            RoleJpaEntity role1 =
                    jpaRepository.save(
                            RoleJpaEntity.of(
                                    null,
                                    savedTenant.getTenantId(),
                                    null,
                                    "ROLE_IDS_1",
                                    "ID목록1",
                                    null,
                                    RoleType.CUSTOM,
                                    RoleScope.TENANT,
                                    RoleJpaEntityFixture.fixedTime(),
                                    RoleJpaEntityFixture.fixedTime(),
                                    null));
            RoleJpaEntity role2 =
                    jpaRepository.save(
                            RoleJpaEntity.of(
                                    null,
                                    savedTenant.getTenantId(),
                                    null,
                                    "ROLE_IDS_2",
                                    "ID목록2",
                                    null,
                                    RoleType.CUSTOM,
                                    RoleScope.TENANT,
                                    RoleJpaEntityFixture.fixedTime(),
                                    RoleJpaEntityFixture.fixedTime(),
                                    null));
            flushAndClear();

            // when
            List<RoleJpaEntity> found =
                    queryDslRepository.findAllByIds(List.of(role1.getRoleId(), role2.getRoleId()));

            // then
            assertThat(found).hasSize(2);
            assertThat(found)
                    .extracting(RoleJpaEntity::getName)
                    .containsExactlyInAnyOrder("ROLE_IDS_1", "ROLE_IDS_2");
        }

        @Test
        @DisplayName("삭제된 역할은 ID 목록 조회에서 제외됨")
        void shouldExcludeDeletedRoles() {
            // given
            RoleJpaEntity activeRole =
                    jpaRepository.save(
                            RoleJpaEntity.of(
                                    null,
                                    savedTenant.getTenantId(),
                                    null,
                                    "ROLE_ACTIVE_IDS",
                                    "활성",
                                    null,
                                    RoleType.CUSTOM,
                                    RoleScope.TENANT,
                                    RoleJpaEntityFixture.fixedTime(),
                                    RoleJpaEntityFixture.fixedTime(),
                                    null));
            RoleJpaEntity deletedRole =
                    jpaRepository.save(
                            RoleJpaEntity.of(
                                    null,
                                    savedTenant.getTenantId(),
                                    null,
                                    "ROLE_DELETED_IDS",
                                    "삭제",
                                    null,
                                    RoleType.CUSTOM,
                                    RoleScope.TENANT,
                                    RoleJpaEntityFixture.fixedTime(),
                                    RoleJpaEntityFixture.fixedTime(),
                                    RoleJpaEntityFixture.fixedTime()));
            flushAndClear();

            // when
            List<RoleJpaEntity> found =
                    queryDslRepository.findAllByIds(
                            List.of(activeRole.getRoleId(), deletedRole.getRoleId()));

            // then
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getName()).isEqualTo("ROLE_ACTIVE_IDS");
        }
    }
}
