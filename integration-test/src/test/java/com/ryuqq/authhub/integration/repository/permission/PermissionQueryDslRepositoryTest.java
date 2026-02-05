package com.ryuqq.authhub.integration.repository.permission;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.fixture.PermissionJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.permission.query.criteria.PermissionSearchCriteria;
import com.ryuqq.authhub.domain.permission.vo.PermissionSearchField;
import com.ryuqq.authhub.domain.permission.vo.PermissionSortKey;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
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
 * Permission QueryDSL Repository 통합 테스트.
 *
 * <p>QueryDSL 기반의 복잡한 쿼리 동작을 검증합니다.
 *
 * <ul>
 *   <li>Soft Delete 필터링 (deletedAt IS NULL)
 *   <li>findByPermissionId - ID로 단건 조회
 *   <li>existsByPermissionId - ID 존재 여부
 *   <li>existsByPermissionKey - 권한 키 존재 여부
 *   <li>findByPermissionKey - 권한 키로 단건 조회
 *   <li>findAllByCriteria - 조건 검색
 *   <li>countByCriteria - 조건 개수
 *   <li>findAllByIds - ID 목록으로 다건 조회
 *   <li>findAllByPermissionKeys - 권한 키 목록으로 다건 조회
 * </ul>
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.PERMISSION)
@DisplayName("권한 QueryDSL Repository 테스트")
class PermissionQueryDslRepositoryTest extends RepositoryTestBase {

    @Autowired private PermissionJpaRepository jpaRepository;
    @Autowired private PermissionQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        flushAndClear();
    }

    @Nested
    @DisplayName("findByPermissionId 테스트")
    class FindByPermissionIdTest {

        @Test
        @DisplayName("활성 권한 조회 성공")
        void shouldFindActivePermission() {
            // given
            PermissionJpaEntity entity =
                    jpaRepository.save(
                            PermissionJpaEntity.of(
                                    null,
                                    null,
                                    "perm:read",
                                    "perm",
                                    "read",
                                    "권한 조회",
                                    PermissionType.CUSTOM,
                                    PermissionJpaEntityFixture.fixedTime(),
                                    PermissionJpaEntityFixture.fixedTime(),
                                    null));
            flushAndClear();

            // when
            Optional<PermissionJpaEntity> found =
                    queryDslRepository.findByPermissionId(entity.getPermissionId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getPermissionKey()).isEqualTo("perm:read");
        }

        @Test
        @DisplayName("삭제된 권한은 조회되지 않음 (Soft Delete 필터)")
        void shouldNotFindDeletedPermission() {
            // given
            PermissionJpaEntity entity =
                    jpaRepository.save(
                            PermissionJpaEntity.of(
                                    null,
                                    null,
                                    "perm:deleted",
                                    "perm",
                                    "deleted",
                                    null,
                                    PermissionType.CUSTOM,
                                    PermissionJpaEntityFixture.fixedTime(),
                                    PermissionJpaEntityFixture.fixedTime(),
                                    PermissionJpaEntityFixture.fixedTime()));
            flushAndClear();

            // when
            Optional<PermissionJpaEntity> found =
                    queryDslRepository.findByPermissionId(entity.getPermissionId());

            // then
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회시 빈 Optional 반환")
        void shouldReturnEmptyWhenNotFound() {
            // when
            Optional<PermissionJpaEntity> found = queryDslRepository.findByPermissionId(999999L);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByPermissionKey 테스트")
    class FindByPermissionKeyTest {

        @Test
        @DisplayName("권한 키로 조회 성공")
        void shouldFindByPermissionKey() {
            // given
            jpaRepository.save(
                    PermissionJpaEntity.of(
                            null,
                            null,
                            "user:write",
                            "user",
                            "write",
                            "사용자 쓰기",
                            PermissionType.CUSTOM,
                            PermissionJpaEntityFixture.fixedTime(),
                            PermissionJpaEntityFixture.fixedTime(),
                            null));
            flushAndClear();

            // when
            Optional<PermissionJpaEntity> found =
                    queryDslRepository.findByServiceIdAndPermissionKey(null, "user:write");

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getPermissionKey()).isEqualTo("user:write");
        }

        @Test
        @DisplayName("삭제된 권한은 키로 조회되지 않음")
        void shouldNotFindDeletedPermissionByKey() {
            // given
            jpaRepository.save(
                    PermissionJpaEntity.of(
                            null,
                            null,
                            "perm:deleted_key",
                            "perm",
                            "deleted",
                            null,
                            PermissionType.CUSTOM,
                            PermissionJpaEntityFixture.fixedTime(),
                            PermissionJpaEntityFixture.fixedTime(),
                            PermissionJpaEntityFixture.fixedTime()));
            flushAndClear();

            // when
            Optional<PermissionJpaEntity> found =
                    queryDslRepository.findByServiceIdAndPermissionKey(null, "perm:deleted_key");

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByPermissionKey 테스트")
    class ExistsByPermissionKeyTest {

        @Test
        @DisplayName("존재하는 권한 키 - true")
        void shouldReturnTrueForExistingKey() {
            // given
            jpaRepository.save(
                    PermissionJpaEntity.of(
                            null,
                            null,
                            "exists:key",
                            "exists",
                            "key",
                            null,
                            PermissionType.CUSTOM,
                            PermissionJpaEntityFixture.fixedTime(),
                            PermissionJpaEntityFixture.fixedTime(),
                            null));
            flushAndClear();

            // when
            boolean exists =
                    queryDslRepository.existsByServiceIdAndPermissionKey(null, "exists:key");

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 권한 키 - false")
        void shouldReturnFalseForNonExistentKey() {
            // when
            boolean exists =
                    queryDslRepository.existsByServiceIdAndPermissionKey(null, "nonexistent:key");

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 테스트")
    class FindAllByCriteriaTest {

        @Test
        @DisplayName("조건 검색 - 삭제된 권한 제외")
        void shouldFindAllExcludingDeleted() {
            // given
            jpaRepository.save(
                    PermissionJpaEntity.of(
                            null,
                            null,
                            "criteria:active",
                            "criteria",
                            "active",
                            null,
                            PermissionType.CUSTOM,
                            PermissionJpaEntityFixture.fixedTime(),
                            PermissionJpaEntityFixture.fixedTime(),
                            null));
            jpaRepository.save(
                    PermissionJpaEntity.of(
                            null,
                            null,
                            "criteria:deleted",
                            "criteria",
                            "deleted",
                            null,
                            PermissionType.CUSTOM,
                            PermissionJpaEntityFixture.fixedTime(),
                            PermissionJpaEntityFixture.fixedTime(),
                            PermissionJpaEntityFixture.fixedTime()));
            flushAndClear();

            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            null,
                            "criteria",
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DateRange.of(null, null),
                            PermissionSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<PermissionJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPermissionKey()).isEqualTo("criteria:active");
        }
    }

    @Nested
    @DisplayName("findAllByIds 테스트")
    class FindAllByIdsTest {

        @Test
        @DisplayName("ID 목록으로 권한 목록 조회")
        void shouldFindByIds() {
            // given
            PermissionJpaEntity perm1 =
                    jpaRepository.save(
                            PermissionJpaEntity.of(
                                    null,
                                    null,
                                    "ids:1",
                                    "ids",
                                    "1",
                                    null,
                                    PermissionType.CUSTOM,
                                    PermissionJpaEntityFixture.fixedTime(),
                                    PermissionJpaEntityFixture.fixedTime(),
                                    null));
            PermissionJpaEntity perm2 =
                    jpaRepository.save(
                            PermissionJpaEntity.of(
                                    null,
                                    null,
                                    "ids:2",
                                    "ids",
                                    "2",
                                    null,
                                    PermissionType.CUSTOM,
                                    PermissionJpaEntityFixture.fixedTime(),
                                    PermissionJpaEntityFixture.fixedTime(),
                                    null));
            flushAndClear();

            // when
            List<PermissionJpaEntity> found =
                    queryDslRepository.findAllByIds(
                            List.of(perm1.getPermissionId(), perm2.getPermissionId()));

            // then
            assertThat(found).hasSize(2);
            assertThat(found)
                    .extracting(PermissionJpaEntity::getPermissionKey)
                    .containsExactlyInAnyOrder("ids:1", "ids:2");
        }

        @Test
        @DisplayName("삭제된 권한은 ID 목록 조회에서 제외됨")
        void shouldExcludeDeletedPermissions() {
            // given
            PermissionJpaEntity activePerm =
                    jpaRepository.save(
                            PermissionJpaEntity.of(
                                    null,
                                    null,
                                    "active:ids",
                                    "active",
                                    "ids",
                                    null,
                                    PermissionType.CUSTOM,
                                    PermissionJpaEntityFixture.fixedTime(),
                                    PermissionJpaEntityFixture.fixedTime(),
                                    null));
            PermissionJpaEntity deletedPerm =
                    jpaRepository.save(
                            PermissionJpaEntity.of(
                                    null,
                                    null,
                                    "deleted:ids",
                                    "deleted",
                                    "ids",
                                    null,
                                    PermissionType.CUSTOM,
                                    PermissionJpaEntityFixture.fixedTime(),
                                    PermissionJpaEntityFixture.fixedTime(),
                                    PermissionJpaEntityFixture.fixedTime()));
            flushAndClear();

            // when
            List<PermissionJpaEntity> found =
                    queryDslRepository.findAllByIds(
                            List.of(activePerm.getPermissionId(), deletedPerm.getPermissionId()));

            // then
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getPermissionKey()).isEqualTo("active:ids");
        }
    }

    @Nested
    @DisplayName("findAllByPermissionKeys 테스트")
    class FindAllByPermissionKeysTest {

        @Test
        @DisplayName("권한 키 목록으로 다건 조회")
        void shouldFindByPermissionKeys() {
            // given
            jpaRepository.save(
                    PermissionJpaEntity.of(
                            null,
                            null,
                            "bulk:key1",
                            "bulk",
                            "key1",
                            null,
                            PermissionType.CUSTOM,
                            PermissionJpaEntityFixture.fixedTime(),
                            PermissionJpaEntityFixture.fixedTime(),
                            null));
            jpaRepository.save(
                    PermissionJpaEntity.of(
                            null,
                            null,
                            "bulk:key2",
                            "bulk",
                            "key2",
                            null,
                            PermissionType.CUSTOM,
                            PermissionJpaEntityFixture.fixedTime(),
                            PermissionJpaEntityFixture.fixedTime(),
                            null));
            flushAndClear();

            // when
            List<PermissionJpaEntity> found =
                    queryDslRepository.findAllByPermissionKeys(List.of("bulk:key1", "bulk:key2"));

            // then
            assertThat(found).hasSize(2);
            assertThat(found)
                    .extracting(PermissionJpaEntity::getPermissionKey)
                    .containsExactlyInAnyOrder("bulk:key1", "bulk:key2");
        }
    }
}
