package com.ryuqq.authhub.application.endpointpermission.manager.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.endpointpermission.dto.query.SearchEndpointPermissionsQuery;
import com.ryuqq.authhub.application.endpointpermission.port.out.query.EndpointPermissionQueryPort;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.fixture.EndpointPermissionFixture;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointPath;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
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
 * EndpointPermissionReadManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("EndpointPermissionReadManager 단위 테스트")
class EndpointPermissionReadManagerTest {

    @Mock private EndpointPermissionQueryPort queryPort;

    private EndpointPermissionReadManager readManager;

    @BeforeEach
    void setUp() {
        readManager = new EndpointPermissionReadManager(queryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 엔드포인트 권한을 조회한다")
        void shouldFindById() {
            // given
            EndpointPermissionId id = EndpointPermissionFixture.createEndpointPermissionId();
            EndpointPermission expected = EndpointPermissionFixture.createReconstituted(id.value());
            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Optional<EndpointPermission> result = readManager.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenNotFound() {
            // given
            EndpointPermissionId id = EndpointPermissionId.of(UUID.randomUUID());
            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when
            Optional<EndpointPermission> result = readManager.findById(id);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByServiceNameAndPathAndMethod 메서드")
    class FindByServiceNameAndPathAndMethodTest {

        @Test
        @DisplayName("서비스명, 경로, 메서드로 엔드포인트 권한을 조회한다")
        void shouldFindByServiceNameAndPathAndMethod() {
            // given
            ServiceName serviceName = EndpointPermissionFixture.createServiceName();
            EndpointPath path = EndpointPermissionFixture.createEndpointPath();
            HttpMethod method = HttpMethod.GET;
            EndpointPermission expected = EndpointPermissionFixture.createReconstituted();
            given(queryPort.findByServiceNameAndPathAndMethod(serviceName, path, method))
                    .willReturn(Optional.of(expected));

            // when
            Optional<EndpointPermission> result =
                    readManager.findByServiceNameAndPathAndMethod(serviceName, path, method);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenNotFound() {
            // given
            ServiceName serviceName = ServiceName.of("unknown-service");
            EndpointPath path = EndpointPath.of("/api/unknown");
            HttpMethod method = HttpMethod.POST;
            given(queryPort.findByServiceNameAndPathAndMethod(serviceName, path, method))
                    .willReturn(Optional.empty());

            // when
            Optional<EndpointPermission> result =
                    readManager.findByServiceNameAndPathAndMethod(serviceName, path, method);

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
            ServiceName serviceName = EndpointPermissionFixture.createServiceName();
            EndpointPath path = EndpointPermissionFixture.createEndpointPath();
            HttpMethod method = HttpMethod.GET;
            given(queryPort.existsByServiceNameAndPathAndMethod(serviceName, path, method))
                    .willReturn(true);

            // when
            boolean result =
                    readManager.existsByServiceNameAndPathAndMethod(serviceName, path, method);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false를 반환한다")
        void shouldReturnFalseWhenNotExists() {
            // given
            ServiceName serviceName = ServiceName.of("unknown-service");
            EndpointPath path = EndpointPath.of("/api/unknown");
            HttpMethod method = HttpMethod.DELETE;
            given(queryPort.existsByServiceNameAndPathAndMethod(serviceName, path, method))
                    .willReturn(false);

            // when
            boolean result =
                    readManager.existsByServiceNameAndPathAndMethod(serviceName, path, method);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByServiceNameAndMethod 메서드")
    class FindAllByServiceNameAndMethodTest {

        @Test
        @DisplayName("서비스명과 메서드로 엔드포인트 권한 목록을 조회한다")
        void shouldFindAllByServiceNameAndMethod() {
            // given
            ServiceName serviceName = EndpointPermissionFixture.createServiceName();
            HttpMethod method = HttpMethod.GET;
            List<EndpointPermission> expected =
                    List.of(
                            EndpointPermissionFixture.createReconstituted(),
                            EndpointPermissionFixture.createReconstituted(UUID.randomUUID()));
            given(queryPort.findAllByServiceNameAndMethod(serviceName, method)).willReturn(expected);

            // when
            List<EndpointPermission> result =
                    readManager.findAllByServiceNameAndMethod(serviceName, method);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoResults() {
            // given
            ServiceName serviceName = ServiceName.of("empty-service");
            HttpMethod method = HttpMethod.PUT;
            given(queryPort.findAllByServiceNameAndMethod(serviceName, method))
                    .willReturn(List.of());

            // when
            List<EndpointPermission> result =
                    readManager.findAllByServiceNameAndMethod(serviceName, method);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("search 메서드")
    class SearchTest {

        @Test
        @DisplayName("검색 조건에 맞는 엔드포인트 권한 목록을 반환한다")
        void shouldSearchEndpointPermissions() {
            // given
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery("auth-hub", null, null, null, 0, 10);
            List<EndpointPermission> expected =
                    List.of(EndpointPermissionFixture.createReconstituted());
            given(queryPort.search(query)).willReturn(expected);

            // when
            List<EndpointPermission> result = readManager.search(query);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoResults() {
            // given
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery("nonexistent", null, null, null, 0, 10);
            given(queryPort.search(query)).willReturn(List.of());

            // when
            List<EndpointPermission> result = readManager.search(query);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("count 메서드")
    class CountTest {

        @Test
        @DisplayName("검색 조건에 맞는 총 개수를 반환한다")
        void shouldCountEndpointPermissions() {
            // given
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery("auth-hub", null, null, null, 0, 10);
            given(queryPort.count(query)).willReturn(5L);

            // when
            long result = readManager.count(query);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("결과가 없으면 0을 반환한다")
        void shouldReturnZeroWhenNoResults() {
            // given
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery("nonexistent", null, null, null, 0, 10);
            given(queryPort.count(query)).willReturn(0L);

            // when
            long result = readManager.count(query);

            // then
            assertThat(result).isZero();
        }
    }
}
