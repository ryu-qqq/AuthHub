package com.ryuqq.authhub.integration.repository.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.fixture.OrganizationJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.fixture.UserJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.query.criteria.UserSearchCriteria;
import com.ryuqq.authhub.domain.user.vo.UserSearchField;
import com.ryuqq.authhub.domain.user.vo.UserSortKey;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.integration.common.base.RepositoryTestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User QueryDSL Repository 통합 테스트.
 *
 * <p>QueryDSL 기반의 복잡한 쿼리 동작을 검증합니다.
 *
 * <ul>
 *   <li>Soft Delete 필터링 (deletedAt IS NULL)
 *   <li>동적 검색 조건 (UserConditionBuilder)
 *   <li>정렬 및 페이징
 *   <li>existsByOrganizationIdAndIdentifier, existsByOrganizationIdAndPhoneNumber
 * </ul>
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.USER)
@DisplayName("사용자 QueryDSL Repository 테스트")
class UserQueryDslRepositoryTest extends RepositoryTestBase {

    @Autowired private UserJpaRepository jpaRepository;
    @Autowired private UserQueryDslRepository queryDslRepository;
    @Autowired private OrganizationJpaRepository organizationJpaRepository;
    @Autowired private TenantJpaRepository tenantJpaRepository;

    private TenantJpaEntity savedTenant;
    private OrganizationJpaEntity savedOrganization;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        organizationJpaRepository.deleteAll();
        tenantJpaRepository.deleteAll();

        savedTenant = tenantJpaRepository.save(TenantJpaEntityFixture.create());
        savedOrganization =
                organizationJpaRepository.save(
                        OrganizationJpaEntityFixture.createWithTenant(savedTenant.getTenantId()));

        flushAndClear();
    }

    @Nested
    @DisplayName("findByUserId 테스트")
    class FindByUserIdTest {

        @Test
        @DisplayName("활성 사용자 조회 성공")
        void shouldFindActiveUser() {
            // given
            String userId = UUID.randomUUID().toString();
            UserJpaEntity entity =
                    UserJpaEntityFixture.create(
                            userId,
                            savedOrganization.getOrganizationId(),
                            "findme@example.com",
                            "010-1234-5678",
                            "$2a$10$hashedpassword",
                            UserStatus.ACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            null);
            jpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<UserJpaEntity> found = queryDslRepository.findByUserId(userId);

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getIdentifier()).isEqualTo("findme@example.com");
        }

        @Test
        @DisplayName("삭제된 사용자는 조회되지 않음 (Soft Delete 필터)")
        void shouldNotFindDeletedUser() {
            // given
            String userId = UUID.randomUUID().toString();
            UserJpaEntity entity =
                    UserJpaEntityFixture.create(
                            userId,
                            savedOrganization.getOrganizationId(),
                            "deleted@example.com",
                            "010-9999-9999",
                            "$2a$10$hashedpassword",
                            UserStatus.INACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime());
            jpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<UserJpaEntity> found = queryDslRepository.findByUserId(userId);

            // then
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회시 빈 Optional 반환")
        void shouldReturnEmptyWhenNotFound() {
            // when
            Optional<UserJpaEntity> found =
                    queryDslRepository.findByUserId("01941234-5678-7000-8000-000000000000");

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByUserId 테스트")
    class ExistsByUserIdTest {

        @Test
        @DisplayName("활성 사용자 존재 확인 - true")
        void shouldReturnTrueForActiveUser() {
            // given
            String userId = UUID.randomUUID().toString();
            UserJpaEntity entity =
                    UserJpaEntityFixture.create(
                            userId,
                            savedOrganization.getOrganizationId(),
                            "exists@example.com",
                            "010-1111-1111",
                            "$2a$10$hashedpassword",
                            UserStatus.ACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            null);
            jpaRepository.save(entity);
            flushAndClear();

            // when
            boolean exists = queryDslRepository.existsByUserId(userId);

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("삭제된 사용자는 존재하지 않음으로 판단")
        void shouldReturnFalseForDeletedUser() {
            // given
            String userId = UUID.randomUUID().toString();
            UserJpaEntity entity =
                    UserJpaEntityFixture.create(
                            userId,
                            savedOrganization.getOrganizationId(),
                            "deleted@example.com",
                            null,
                            "$2a$10$hashedpassword",
                            UserStatus.INACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime());
            jpaRepository.save(entity);
            flushAndClear();

            // when
            boolean exists = queryDslRepository.existsByUserId(userId);

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findByIdentifier 테스트")
    class FindByIdentifierTest {

        @Test
        @DisplayName("식별자로 사용자 조회 성공")
        void shouldFindByIdentifier() {
            // given
            String userId = UUID.randomUUID().toString();
            UserJpaEntity entity =
                    UserJpaEntityFixture.create(
                            userId,
                            savedOrganization.getOrganizationId(),
                            "login@example.com",
                            "010-2222-2222",
                            "$2a$10$hashedpassword",
                            UserStatus.ACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            null);
            jpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<UserJpaEntity> found =
                    queryDslRepository.findByIdentifier("login@example.com");

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getUserId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("삭제된 사용자는 식별자로 조회되지 않음")
        void shouldNotFindDeletedUserByIdentifier() {
            // given
            jpaRepository.save(
                    UserJpaEntityFixture.create(
                            UUID.randomUUID().toString(),
                            savedOrganization.getOrganizationId(),
                            "deleted-login@example.com",
                            null,
                            "$2a$10$hashedpassword",
                            UserStatus.INACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime()));
            flushAndClear();

            // when
            Optional<UserJpaEntity> found =
                    queryDslRepository.findByIdentifier("deleted-login@example.com");

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByOrganizationIdAndIdentifier 테스트")
    class ExistsByOrganizationIdAndIdentifierTest {

        @Test
        @DisplayName("조직 내 식별자 존재 확인 - true")
        void shouldReturnTrueForExistingIdentifier() {
            // given
            jpaRepository.save(
                    UserJpaEntityFixture.create(
                            UUID.randomUUID().toString(),
                            savedOrganization.getOrganizationId(),
                            "unique@example.com",
                            "010-3333-3333",
                            "$2a$10$hashedpassword",
                            UserStatus.ACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            null));
            flushAndClear();

            // when
            boolean exists =
                    queryDslRepository.existsByOrganizationIdAndIdentifier(
                            savedOrganization.getOrganizationId(), "unique@example.com");

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 식별자 - false")
        void shouldReturnFalseForNonExistentIdentifier() {
            // when
            boolean exists =
                    queryDslRepository.existsByOrganizationIdAndIdentifier(
                            savedOrganization.getOrganizationId(), "nonexistent@example.com");

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 테스트")
    class FindAllByCriteriaTest {

        @Test
        @DisplayName("조건 검색 - 삭제된 사용자 제외")
        void shouldFindAllExcludingDeleted() {
            // given
            String orgId = savedOrganization.getOrganizationId();
            jpaRepository.save(
                    UserJpaEntityFixture.create(
                            UUID.randomUUID().toString(),
                            orgId,
                            "active1@example.com",
                            "010-1111-1111",
                            "$2a$10$hashedpassword",
                            UserStatus.ACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            null));
            jpaRepository.save(
                    UserJpaEntityFixture.create(
                            UUID.randomUUID().toString(),
                            orgId,
                            "deleted@example.com",
                            null,
                            "$2a$10$hashedpassword",
                            UserStatus.INACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime()));
            flushAndClear();

            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            List.of(OrganizationId.of(orgId)),
                            null,
                            UserSearchField.IDENTIFIER,
                            null,
                            DateRange.of(null, null),
                            UserSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<UserJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getIdentifier()).isEqualTo("active1@example.com");
        }

        @Test
        @DisplayName("식별자 검색 - 부분 일치")
        void shouldSearchByIdentifier() {
            // given
            String orgId = savedOrganization.getOrganizationId();
            jpaRepository.save(
                    UserJpaEntityFixture.create(
                            UUID.randomUUID().toString(),
                            orgId,
                            "testuser@example.com",
                            "010-1234-5678",
                            "$2a$10$hashedpassword",
                            UserStatus.ACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            null));
            flushAndClear();

            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            List.of(OrganizationId.of(orgId)),
                            "testuser",
                            UserSearchField.IDENTIFIER,
                            null,
                            DateRange.of(null, null),
                            UserSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<UserJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getIdentifier()).contains("testuser");
        }
    }

    @Nested
    @DisplayName("countByCriteria 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("조건에 맞는 사용자 수 조회")
        void shouldCountByCriteria() {
            // given
            String orgId = savedOrganization.getOrganizationId();
            jpaRepository.save(
                    UserJpaEntityFixture.create(
                            UUID.randomUUID().toString(),
                            orgId,
                            "count1@example.com",
                            null,
                            "$2a$10$hashedpassword",
                            UserStatus.ACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            null));
            jpaRepository.save(
                    UserJpaEntityFixture.create(
                            UUID.randomUUID().toString(),
                            orgId,
                            "count2@example.com",
                            null,
                            "$2a$10$hashedpassword",
                            UserStatus.ACTIVE,
                            UserJpaEntityFixture.fixedTime(),
                            UserJpaEntityFixture.fixedTime(),
                            null));
            flushAndClear();

            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            List.of(OrganizationId.of(orgId)),
                            null,
                            UserSearchField.IDENTIFIER,
                            null,
                            DateRange.of(null, null),
                            UserSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            long count = queryDslRepository.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2);
        }
    }
}
