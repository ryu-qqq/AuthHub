package com.ryuqq.authhub.adapter.out.persistence.endpointpermission.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.entity.EndpointPermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.mapper.EndpointPermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.repository.EndpointPermissionQueryDslRepository;
import com.ryuqq.authhub.application.endpointpermission.dto.query.SearchEndpointPermissionsQuery;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.fixture.EndpointPermissionFixture;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointPath;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * EndpointPermissionQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("EndpointPermissionQueryAdapter 단위 테스트")
class EndpointPermissionQueryAdapterTest {

    @Mock private EndpointPermissionQueryDslRepository repository;

    @Mock private EndpointPermissionJpaEntityMapper mapper;

    private EndpointPermissionQueryAdapter adapter;

    private static final UUID ENDPOINT_PERMISSION_UUID = UUID.randomUUID();
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @BeforeEach
    void setUp() {
        adapter = new EndpointPermissionQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 엔드포인트 권한을 성공적으로 조회한다")
        void shouldFindEndpointPermissionByIdSuccessfully() {
            // given
            EndpointPermissionId id = EndpointPermissionId.of(ENDPOINT_PERMISSION_UUID);
            EndpointPermission expectedPermission = EndpointPermissionFixture.createProtected();
            EndpointPermissionJpaEntity entity = createProtectedEntity();

            given(repository.findByEndpointPermissionId(ENDPOINT_PERMISSION_UUID))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedPermission);

            // when
            Optional<EndpointPermission> result = adapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedPermission);
            verify(repository).findByEndpointPermissionId(ENDPOINT_PERMISSION_UUID);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenNotFound() {
            // given
            EndpointPermissionId id = EndpointPermissionId.of(UUID.randomUUID());

            given(repository.findByEndpointPermissionId(id.value())).willReturn(Optional.empty());

            // when
            Optional<EndpointPermission> result = adapter.findById(id);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByServiceNameAndPathAndMethod 메서드")
    class FindByServiceNameAndPathAndMethodTest {

        @Test
        @DisplayName("서비스명 + 경로 + 메서드로 조회한다")
        void shouldFindByServiceNameAndPathAndMethod() {
            // given
            ServiceName serviceName = ServiceName.of("auth-hub");
            EndpointPath path = EndpointPath.of("/api/v1/users");
            HttpMethod method = HttpMethod.GET;
            EndpointPermission expectedPermission = EndpointPermissionFixture.createProtected();
            EndpointPermissionJpaEntity entity = createProtectedEntity();

            given(repository.findByServiceNameAndPathAndMethod("auth-hub", "/api/v1/users", "GET"))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedPermission);

            // when
            Optional<EndpointPermission> result =
                    adapter.findByServiceNameAndPathAndMethod(serviceName, path, method);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedPermission);
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenNotFound() {
            // given
            ServiceName serviceName = ServiceName.of("unknown-service");
            EndpointPath path = EndpointPath.of("/unknown");
            HttpMethod method = HttpMethod.GET;

            given(
                            repository.findByServiceNameAndPathAndMethod(
                                    "unknown-service", "/unknown", "GET"))
                    .willReturn(Optional.empty());

            // when
            Optional<EndpointPermission> result =
                    adapter.findByServiceNameAndPathAndMethod(serviceName, path, method);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByServiceNameAndPathAndMethod 메서드")
    class ExistsByServiceNameAndPathAndMethodTest {

        @Test
        @DisplayName("존재하면 true를 반환한다")
        void shouldReturnTrueWhenExists() {
            // given
            ServiceName serviceName = ServiceName.of("auth-hub");
            EndpointPath path = EndpointPath.of("/api/v1/users");
            HttpMethod method = HttpMethod.GET;

            given(
                            repository.existsByServiceNameAndPathAndMethod(
                                    "auth-hub", "/api/v1/users", "GET"))
                    .willReturn(true);

            // when
            boolean result = adapter.existsByServiceNameAndPathAndMethod(serviceName, path, method);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false를 반환한다")
        void shouldReturnFalseWhenNotExists() {
            // given
            ServiceName serviceName = ServiceName.of("unknown-service");
            EndpointPath path = EndpointPath.of("/unknown");
            HttpMethod method = HttpMethod.GET;

            given(
                            repository.existsByServiceNameAndPathAndMethod(
                                    "unknown-service", "/unknown", "GET"))
                    .willReturn(false);

            // when
            boolean result = adapter.existsByServiceNameAndPathAndMethod(serviceName, path, method);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByServiceNameAndMethod 메서드")
    class FindAllByServiceNameAndMethodTest {

        @Test
        @DisplayName("서비스명 + 메서드로 목록을 조회한다")
        void shouldFindAllByServiceNameAndMethod() {
            // given
            ServiceName serviceName = ServiceName.of("auth-hub");
            HttpMethod method = HttpMethod.GET;
            EndpointPermission permission1 = EndpointPermissionFixture.createProtected();
            EndpointPermission permission2 = EndpointPermissionFixture.createPublic();
            EndpointPermissionJpaEntity entity1 = createProtectedEntity();
            EndpointPermissionJpaEntity entity2 = createPublicEntity();

            given(repository.findAllByServiceNameAndMethod("auth-hub", "GET"))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(any(EndpointPermissionJpaEntity.class)))
                    .willReturn(permission1, permission2);

            // when
            List<EndpointPermission> results =
                    adapter.findAllByServiceNameAndMethod(serviceName, method);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoResults() {
            // given
            ServiceName serviceName = ServiceName.of("unknown-service");
            HttpMethod method = HttpMethod.GET;

            given(repository.findAllByServiceNameAndMethod("unknown-service", "GET"))
                    .willReturn(List.of());

            // when
            List<EndpointPermission> results =
                    adapter.findAllByServiceNameAndMethod(serviceName, method);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("search 메서드")
    class SearchTest {

        @Test
        @DisplayName("검색 조건으로 엔드포인트 권한 목록을 조회한다")
        void shouldSearchEndpointPermissionsSuccessfully() {
            // given
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery(null, null, null, null, 0, 20);
            EndpointPermission permission1 = EndpointPermissionFixture.createProtected();
            EndpointPermission permission2 = EndpointPermissionFixture.createPublic();
            EndpointPermissionJpaEntity entity1 = createProtectedEntity();
            EndpointPermissionJpaEntity entity2 = createPublicEntity();

            given(repository.search(any(), any(), any(), any(), anyInt(), anyInt()))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(any(EndpointPermissionJpaEntity.class)))
                    .willReturn(permission1, permission2);

            // when
            List<EndpointPermission> results = adapter.search(query);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("서비스명 필터로 검색한다")
        void shouldSearchWithServiceNameFilter() {
            // given
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery("auth-hub", null, null, null, 0, 20);
            EndpointPermission permission = EndpointPermissionFixture.createProtected();
            EndpointPermissionJpaEntity entity = createProtectedEntity();

            given(repository.search(eq("auth-hub"), any(), any(), any(), anyInt(), anyInt()))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(any(EndpointPermissionJpaEntity.class))).willReturn(permission);

            // when
            List<EndpointPermission> results = adapter.search(query);

            // then
            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("공개 여부 필터로 검색한다")
        void shouldSearchWithIsPublicFilter() {
            // given
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery(null, null, null, true, 0, 20);
            EndpointPermission publicPermission = EndpointPermissionFixture.createPublic();
            EndpointPermissionJpaEntity entity = createPublicEntity();

            given(repository.search(any(), any(), any(), eq(true), anyInt(), anyInt()))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(any(EndpointPermissionJpaEntity.class)))
                    .willReturn(publicPermission);

            // when
            List<EndpointPermission> results = adapter.search(query);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).isPublic()).isTrue();
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoResults() {
            // given
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery("nonexistent", null, null, null, 0, 20);

            given(repository.search(any(), any(), any(), any(), anyInt(), anyInt()))
                    .willReturn(List.of());

            // when
            List<EndpointPermission> results = adapter.search(query);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("count 메서드")
    class CountTest {

        @Test
        @DisplayName("검색 조건으로 엔드포인트 권한 개수를 조회한다")
        void shouldCountEndpointPermissionsSuccessfully() {
            // given
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery(null, null, null, null, 0, 20);

            given(repository.count(any(), any(), any(), any())).willReturn(10L);

            // when
            long result = adapter.count(query);

            // then
            assertThat(result).isEqualTo(10L);
        }

        @Test
        @DisplayName("조건에 맞는 결과가 없으면 0을 반환한다")
        void shouldReturnZeroWhenNoResults() {
            // given
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery("nonexistent", null, null, null, 0, 20);

            given(repository.count(any(), any(), any(), any())).willReturn(0L);

            // when
            long result = adapter.count(query);

            // then
            assertThat(result).isEqualTo(0L);
        }
    }

    private EndpointPermissionJpaEntity createPublicEntity() {
        return EndpointPermissionJpaEntity.of(
                1L,
                ENDPOINT_PERMISSION_UUID,
                "auth-hub",
                "/api/v1/health",
                "GET",
                "헬스체크 엔드포인트",
                true,
                null,
                null,
                0L,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }

    private EndpointPermissionJpaEntity createProtectedEntity() {
        return EndpointPermissionJpaEntity.of(
                1L,
                ENDPOINT_PERMISSION_UUID,
                "auth-hub",
                "/api/v1/users",
                "GET",
                "사용자 목록 조회",
                false,
                "user:read",
                "ADMIN,USER_MANAGER",
                0L,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }
}
