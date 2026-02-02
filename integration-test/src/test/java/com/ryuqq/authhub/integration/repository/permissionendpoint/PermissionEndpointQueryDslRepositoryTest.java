package com.ryuqq.authhub.integration.repository.permissionendpoint;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.PermissionEndpointJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository.PermissionEndpointJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository.PermissionEndpointQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import com.ryuqq.authhub.domain.permissionendpoint.query.criteria.PermissionEndpointSearchCriteria;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointSearchField;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointSortKey;
import com.ryuqq.authhub.integration.common.base.RepositoryTestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PermissionEndpoint QueryDSL Repository 통합 테스트.
 *
 * <p>QueryDSL 기반의 복잡한 쿼리 동작을 검증합니다.
 *
 * <ul>
 *   <li>findAllBySearchCriteria - 조건 검색
 *   <li>countBySearchCriteria - 조건 개수
 *   <li>findByUrlPatternLike - URL 패턴 검색
 *   <li>findAllByUrlPatterns - URL 패턴 목록으로 다건 조회
 * </ul>
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.PERMISSION)
@DisplayName("권한 엔드포인트 QueryDSL Repository 테스트")
class PermissionEndpointQueryDslRepositoryTest extends RepositoryTestBase {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final String DEFAULT_SERVICE_NAME = "authhub";
    private static final boolean DEFAULT_IS_PUBLIC = false;

    @Autowired private PermissionEndpointJpaRepository jpaRepository;
    @Autowired private PermissionEndpointQueryDslRepository queryDslRepository;
    @Autowired private PermissionJpaRepository permissionJpaRepository;

    private PermissionJpaEntity savedPermission;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        permissionJpaRepository.deleteAll();

        savedPermission =
                permissionJpaRepository.save(
                        PermissionJpaEntity.of(
                                null,
                                "user:read",
                                "user",
                                "read",
                                "사용자 조회",
                                PermissionType.CUSTOM,
                                FIXED_TIME,
                                FIXED_TIME,
                                null));

        flushAndClear();
    }

    @Nested
    @DisplayName("findAllBySearchCriteria 테스트")
    class FindAllBySearchCriteriaTest {

        @Test
        @DisplayName("권한별 엔드포인트 목록 조회")
        void shouldFindAllByPermissionId() {
            // given
            jpaRepository.save(
                    PermissionEndpointJpaEntity.of(
                            null,
                            savedPermission.getPermissionId(),
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/users",
                            HttpMethod.GET,
                            "사용자 목록",
                            DEFAULT_IS_PUBLIC,
                            FIXED_TIME,
                            FIXED_TIME,
                            null));
            jpaRepository.save(
                    PermissionEndpointJpaEntity.of(
                            null,
                            savedPermission.getPermissionId(),
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/users/{id}",
                            HttpMethod.GET,
                            "사용자 상세",
                            DEFAULT_IS_PUBLIC,
                            FIXED_TIME,
                            FIXED_TIME,
                            null));
            flushAndClear();

            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            List.of(savedPermission.getPermissionId()),
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DateRange.of(null, null),
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<PermissionEndpointJpaEntity> result =
                    queryDslRepository.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("URL 패턴 검색 - 부분 일치")
        void shouldSearchByUrlPattern() {
            // given
            jpaRepository.save(
                    PermissionEndpointJpaEntity.of(
                            null,
                            savedPermission.getPermissionId(),
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/organizations",
                            HttpMethod.GET,
                            "조직 목록",
                            DEFAULT_IS_PUBLIC,
                            FIXED_TIME,
                            FIXED_TIME,
                            null));
            flushAndClear();

            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            List.of(savedPermission.getPermissionId()),
                            "organizations",
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DateRange.of(null, null),
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<PermissionEndpointJpaEntity> result =
                    queryDslRepository.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUrlPattern()).contains("organizations");
        }
    }

    @Nested
    @DisplayName("countBySearchCriteria 테스트")
    class CountBySearchCriteriaTest {

        @Test
        @DisplayName("조건에 맞는 엔드포인트 수 조회")
        void shouldCountByCriteria() {
            // given
            jpaRepository.save(
                    PermissionEndpointJpaEntity.of(
                            null,
                            savedPermission.getPermissionId(),
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/count1",
                            HttpMethod.GET,
                            null,
                            DEFAULT_IS_PUBLIC,
                            FIXED_TIME,
                            FIXED_TIME,
                            null));
            jpaRepository.save(
                    PermissionEndpointJpaEntity.of(
                            null,
                            savedPermission.getPermissionId(),
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/count2",
                            HttpMethod.POST,
                            null,
                            DEFAULT_IS_PUBLIC,
                            FIXED_TIME,
                            FIXED_TIME,
                            null));
            flushAndClear();

            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            List.of(savedPermission.getPermissionId()),
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DateRange.of(null, null),
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            long count = queryDslRepository.countBySearchCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("findByUrlPatternLike 테스트")
    class FindByUrlPatternLikeTest {

        @Test
        @DisplayName("URL 패턴 LIKE 검색")
        void shouldFindByUrlPatternLike() {
            // given
            jpaRepository.save(
                    PermissionEndpointJpaEntity.of(
                            null,
                            savedPermission.getPermissionId(),
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/tenants/{id}",
                            HttpMethod.GET,
                            null,
                            DEFAULT_IS_PUBLIC,
                            FIXED_TIME,
                            FIXED_TIME,
                            null));
            flushAndClear();

            // when
            List<PermissionEndpointJpaEntity> found =
                    queryDslRepository.findByUrlPatternLike("tenants");

            // then
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getUrlPattern()).contains("tenants");
        }
    }

    @Nested
    @DisplayName("findAllByUrlPatterns 테스트")
    class FindAllByUrlPatternsTest {

        @Test
        @DisplayName("URL 패턴 목록으로 다건 조회")
        void shouldFindByUrlPatterns() {
            // given
            jpaRepository.save(
                    PermissionEndpointJpaEntity.of(
                            null,
                            savedPermission.getPermissionId(),
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/pattern1",
                            HttpMethod.GET,
                            null,
                            DEFAULT_IS_PUBLIC,
                            FIXED_TIME,
                            FIXED_TIME,
                            null));
            jpaRepository.save(
                    PermissionEndpointJpaEntity.of(
                            null,
                            savedPermission.getPermissionId(),
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/pattern2",
                            HttpMethod.POST,
                            null,
                            DEFAULT_IS_PUBLIC,
                            FIXED_TIME,
                            FIXED_TIME,
                            null));
            flushAndClear();

            // when
            List<PermissionEndpointJpaEntity> found =
                    queryDslRepository.findAllByUrlPatterns(
                            List.of("/api/v1/pattern1", "/api/v1/pattern2"));

            // then
            assertThat(found).hasSize(2);
            assertThat(found)
                    .extracting(PermissionEndpointJpaEntity::getUrlPattern)
                    .containsExactlyInAnyOrder("/api/v1/pattern1", "/api/v1/pattern2");
        }

        @Test
        @DisplayName("빈 목록으로 조회시 빈 목록 반환")
        void shouldReturnEmptyForEmptyList() {
            // when
            List<PermissionEndpointJpaEntity> found =
                    queryDslRepository.findAllByUrlPatterns(List.of());

            // then
            assertThat(found).isEmpty();
        }
    }
}
