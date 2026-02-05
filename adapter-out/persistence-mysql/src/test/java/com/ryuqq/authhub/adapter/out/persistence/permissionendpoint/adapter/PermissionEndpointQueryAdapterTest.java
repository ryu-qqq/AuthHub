package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.PermissionEndpointJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.fixture.PermissionEndpointJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.mapper.PermissionEndpointJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository.PermissionEndpointJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository.PermissionEndpointQueryDslRepository;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.fixture.PermissionEndpointFixture;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import com.ryuqq.authhub.domain.permissionendpoint.query.criteria.PermissionEndpointSearchCriteria;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PermissionEndpointQueryAdapter 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Adapter는 JpaRepository/QueryDslRepository 위임 + Mapper 변환 담당
 *   <li>Repository/Mapper를 Mock으로 대체
 *   <li>Entity → Domain 변환 흐름 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionEndpointQueryAdapter 단위 테스트")
class PermissionEndpointQueryAdapterTest {

    @Mock private PermissionEndpointJpaRepository jpaRepository;

    @Mock private PermissionEndpointQueryDslRepository queryDslRepository;

    @Mock private PermissionEndpointJpaEntityMapper mapper;

    private PermissionEndpointQueryAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionEndpointQueryAdapter(jpaRepository, queryDslRepository, mapper);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            PermissionEndpointId id = PermissionEndpointFixture.defaultId();
            PermissionEndpointJpaEntity entity = PermissionEndpointJpaEntityFixture.create();
            PermissionEndpoint expectedDomain = PermissionEndpointFixture.create();

            given(jpaRepository.findById(id.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<PermissionEndpoint> result = sut.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            PermissionEndpointId id = PermissionEndpointFixture.defaultId();
            given(jpaRepository.findById(id.value())).willReturn(Optional.empty());

            // when
            Optional<PermissionEndpoint> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            PermissionEndpointId id = PermissionEndpointFixture.defaultId();
            given(jpaRepository.existsById(id.value())).willReturn(true);
            assertThat(sut.existsById(id)).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            PermissionEndpointId id = PermissionEndpointFixture.defaultId();
            given(jpaRepository.existsById(id.value())).willReturn(false);
            assertThat(sut.existsById(id)).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByUrlPatternAndHttpMethod 메서드")
    class ExistsByUrlPatternAndHttpMethod {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            given(
                            jpaRepository.existsByUrlPatternAndHttpMethodAndDeletedAtIsNull(
                                    "/api/v1/users", HttpMethod.GET))
                    .willReturn(true);
            assertThat(sut.existsByUrlPatternAndHttpMethod("/api/v1/users", HttpMethod.GET))
                    .isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            given(
                            jpaRepository.existsByUrlPatternAndHttpMethodAndDeletedAtIsNull(
                                    "/api/v1/users", HttpMethod.GET))
                    .willReturn(false);
            assertThat(sut.existsByUrlPatternAndHttpMethod("/api/v1/users", HttpMethod.GET))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("findByUrlPatternAndHttpMethod 메서드")
    class FindByUrlPatternAndHttpMethod {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환")
        void shouldFindAndConvert_WhenExists() {
            PermissionEndpointJpaEntity entity = PermissionEndpointJpaEntityFixture.create();
            PermissionEndpoint domain = PermissionEndpointFixture.create();
            given(
                            jpaRepository.findByUrlPatternAndHttpMethodAndDeletedAtIsNull(
                                    "/api/v1/users", HttpMethod.GET))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            Optional<PermissionEndpoint> result =
                    sut.findByUrlPatternAndHttpMethod("/api/v1/users", HttpMethod.GET);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenNotFound() {
            given(
                            jpaRepository.findByUrlPatternAndHttpMethodAndDeletedAtIsNull(
                                    "/api/v1/users", HttpMethod.GET))
                    .willReturn(Optional.empty());
            assertThat(sut.findByUrlPatternAndHttpMethod("/api/v1/users", HttpMethod.GET))
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByPermissionId 메서드")
    class FindAllByPermissionId {

        @Test
        @DisplayName("성공: Entity 목록을 Domain 목록으로 변환")
        void shouldFindAndConvertAll() {
            PermissionId permissionId = PermissionEndpointFixture.defaultPermissionId();
            PermissionEndpointJpaEntity entity = PermissionEndpointJpaEntityFixture.create();
            PermissionEndpoint domain = PermissionEndpointFixture.create();
            given(jpaRepository.findAllByPermissionIdAndDeletedAtIsNull(permissionId.value()))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            List<PermissionEndpoint> result = sut.findAllByPermissionId(permissionId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            PermissionId permissionId = PermissionEndpointFixture.defaultPermissionId();
            given(jpaRepository.findAllByPermissionIdAndDeletedAtIsNull(permissionId.value()))
                    .willReturn(List.of());
            assertThat(sut.findAllByPermissionId(permissionId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllBySearchCriteria 메서드")
    class FindAllBySearchCriteria {

        @Test
        @DisplayName("성공: QueryDsl 조회 후 Domain 목록 변환")
        void shouldFindAndConvertAll() {
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.forPermission(1L, 0, 10);
            PermissionEndpointJpaEntity entity = PermissionEndpointJpaEntityFixture.create();
            PermissionEndpoint domain = PermissionEndpointFixture.create();
            given(queryDslRepository.findAllBySearchCriteria(criteria)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            List<PermissionEndpoint> result = sut.findAllBySearchCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.forPermission(1L, 0, 10);
            given(queryDslRepository.findAllBySearchCriteria(criteria)).willReturn(List.of());
            assertThat(sut.findAllBySearchCriteria(criteria)).isEmpty();
        }
    }

    @Nested
    @DisplayName("countBySearchCriteria 메서드")
    class CountBySearchCriteria {

        @Test
        @DisplayName("Repository 결과를 그대로 반환")
        void shouldReturnCount_FromRepository() {
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.forPermission(1L, 0, 10);
            given(queryDslRepository.countBySearchCriteria(criteria)).willReturn(5L);
            assertThat(sut.countBySearchCriteria(criteria)).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("findMatchingEndpoints 메서드")
    class FindMatchingEndpoints {

        @Test
        @DisplayName("QueryDsl 조회 후 Domain 변환 및 매칭 필터")
        void shouldFindAndFilterMatching() {
            PermissionEndpointJpaEntity entity =
                    PermissionEndpointJpaEntityFixture.createWithPatternAndMethod(
                            "/api/v1/users", HttpMethod.GET);
            PermissionEndpoint domain = PermissionEndpointFixture.create();
            given(queryDslRepository.findByUrlPatternLike("/api/v1/users"))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            List<PermissionEndpoint> result =
                    sut.findMatchingEndpoints("/api/v1/users", HttpMethod.GET);

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findAllByUrlPatterns 메서드")
    class FindAllByUrlPatterns {

        @Test
        @DisplayName("성공: URL 패턴 목록으로 조회 후 Domain 변환")
        void shouldFindAndConvertAll() {
            List<String> patterns = List.of("/api/v1/users", "/api/v1/roles");
            PermissionEndpointJpaEntity entity = PermissionEndpointJpaEntityFixture.create();
            PermissionEndpoint domain = PermissionEndpointFixture.create();
            given(queryDslRepository.findAllByUrlPatterns(patterns)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            List<PermissionEndpoint> result = sut.findAllByUrlPatterns(patterns);

            assertThat(result).hasSize(1);
            then(queryDslRepository).should().findAllByUrlPatterns(patterns);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            given(queryDslRepository.findAllByUrlPatterns(List.of("/none"))).willReturn(List.of());
            assertThat(sut.findAllByUrlPatterns(List.of("/none"))).isEmpty();
        }
    }
}
