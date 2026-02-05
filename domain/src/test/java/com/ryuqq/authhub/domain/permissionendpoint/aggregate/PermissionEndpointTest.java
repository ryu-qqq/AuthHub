package com.ryuqq.authhub.domain.permissionendpoint.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permissionendpoint.fixture.PermissionEndpointFixture;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.permissionendpoint.vo.ServiceName;
import com.ryuqq.authhub.domain.permissionendpoint.vo.UrlPattern;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PermissionEndpoint Aggregate 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("PermissionEndpoint Aggregate 테스트")
class PermissionEndpointTest {

    private static final Instant NOW = Instant.parse("2025-01-15T10:00:00Z");
    private static final String DEFAULT_SERVICE_NAME = "authhub";
    private static final boolean DEFAULT_IS_PUBLIC = false;

    @Nested
    @DisplayName("PermissionEndpoint 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("새로운 엔드포인트를 성공적으로 생성한다")
        void shouldCreateEndpointSuccessfully() {
            // given
            String urlPattern = "/api/v1/users";
            HttpMethod method = HttpMethod.GET;
            String description = "사용자 목록 조회";

            // when
            PermissionEndpoint endpoint =
                    PermissionEndpoint.create(
                            1L,
                            DEFAULT_SERVICE_NAME,
                            urlPattern,
                            method,
                            description,
                            DEFAULT_IS_PUBLIC,
                            NOW);

            // then
            assertThat(endpoint.permissionIdValue()).isEqualTo(1L);
            assertThat(endpoint.serviceNameValue()).isEqualTo(DEFAULT_SERVICE_NAME);
            assertThat(endpoint.urlPatternValue()).isEqualTo(urlPattern);
            assertThat(endpoint.httpMethodValue()).isEqualTo("GET");
            assertThat(endpoint.descriptionValue()).isEqualTo(description);
            assertThat(endpoint.isPublicEndpoint()).isEqualTo(DEFAULT_IS_PUBLIC);
            assertThat(endpoint.isNew()).isTrue();
            assertThat(endpoint.isDeleted()).isFalse();
            assertThat(endpoint.isActive()).isTrue();
        }

        @Test
        @DisplayName("Long permissionId로 엔드포인트를 생성한다")
        void shouldCreateEndpointWithLongPermissionId() {
            // when
            PermissionEndpoint endpoint =
                    PermissionEndpoint.create(
                            1L,
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/users",
                            HttpMethod.GET,
                            "설명",
                            DEFAULT_IS_PUBLIC,
                            NOW);

            // then
            assertThat(endpoint.permissionIdValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("permissionId가 null이면 예외가 발생한다")
        void shouldThrowExceptionWhenPermissionIdIsNull() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    PermissionEndpoint.create(
                                            (Long) null,
                                            DEFAULT_SERVICE_NAME,
                                            "/api/v1/users",
                                            HttpMethod.GET,
                                            "설명",
                                            DEFAULT_IS_PUBLIC,
                                            NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("PermissionId");
        }

        @Test
        @DisplayName("urlPattern이 null이면 예외가 발생한다")
        void shouldThrowExceptionWhenUrlPatternIsNull() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    PermissionEndpoint.create(
                                            1L,
                                            DEFAULT_SERVICE_NAME,
                                            null,
                                            HttpMethod.GET,
                                            "설명",
                                            DEFAULT_IS_PUBLIC,
                                            NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("urlPattern");
        }

        @Test
        @DisplayName("urlPattern이 빈 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenUrlPatternIsBlank() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    PermissionEndpoint.create(
                                            1L,
                                            DEFAULT_SERVICE_NAME,
                                            "",
                                            HttpMethod.GET,
                                            "설명",
                                            DEFAULT_IS_PUBLIC,
                                            NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("urlPattern");
        }

        @Test
        @DisplayName("urlPattern이 /로 시작하지 않으면 예외가 발생한다")
        void shouldThrowExceptionWhenUrlPatternDoesNotStartWithSlash() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    PermissionEndpoint.create(
                                            1L,
                                            DEFAULT_SERVICE_NAME,
                                            "api/v1/users",
                                            HttpMethod.GET,
                                            "설명",
                                            DEFAULT_IS_PUBLIC,
                                            NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("'/'로 시작");
        }

        @Test
        @DisplayName("serviceName이 null이면 예외가 발생한다")
        void shouldThrowExceptionWhenServiceNameIsNull() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    PermissionEndpoint.create(
                                            1L,
                                            null,
                                            "/api/v1/users",
                                            HttpMethod.GET,
                                            "설명",
                                            DEFAULT_IS_PUBLIC,
                                            NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("serviceName");
        }

        @Test
        @DisplayName("serviceName이 빈 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenServiceNameIsBlank() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    PermissionEndpoint.create(
                                            1L,
                                            "",
                                            "/api/v1/users",
                                            HttpMethod.GET,
                                            "설명",
                                            DEFAULT_IS_PUBLIC,
                                            NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("serviceName");
        }

        @Test
        @DisplayName("description은 null을 허용한다")
        void shouldAllowNullDescription() {
            // when
            PermissionEndpoint endpoint =
                    PermissionEndpoint.create(
                            1L,
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/users",
                            HttpMethod.GET,
                            null,
                            DEFAULT_IS_PUBLIC,
                            NOW);

            // then
            assertThat(endpoint.descriptionValue()).isNull();
        }
    }

    @Nested
    @DisplayName("PermissionEndpoint 엣지 케이스 테스트")
    class EdgeCaseTests {

        @Test
        @DisplayName("update()에서 updateData가 null이면 NullPointerException이 발생한다")
        void shouldThrowExceptionWhenUpdateDataIsNull() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();

            // when & then
            assertThatThrownBy(() -> endpoint.update(null, NOW))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("matches()에서 requestUrl이 null이면 NullPointerException이 발생한다")
        void shouldThrowExceptionWhenRequestUrlIsNull() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();

            // when & then
            assertThatThrownBy(() -> endpoint.matches(null, HttpMethod.GET))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("matches()에서 requestMethod가 null이면 false를 반환한다")
        void shouldReturnFalseWhenRequestMethodIsNull() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();

            // when & then - httpMethod.equals(null)은 false를 반환
            assertThat(endpoint.matches("/api/v1/users", null)).isFalse();
        }

        @Test
        @DisplayName("matches()에서 빈 URL은 매칭되지 않는다")
        void shouldNotMatchEmptyUrl() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();

            // when & then
            assertThat(endpoint.matches("", HttpMethod.GET)).isFalse();
        }

        // httpMethod는 enum이므로 VO가 아니며, aggregate에서 null check를 하지 않음
        // httpMethod가 null인 경우 검증은 Application Layer에서 수행

        @Test
        @DisplayName("VO 타입 파라미터로 엔드포인트를 생성한다")
        void shouldCreateWithVOTypes() {
            // given
            PermissionId permissionId = PermissionId.of(1L);
            ServiceName serviceName = ServiceName.of(DEFAULT_SERVICE_NAME);
            UrlPattern urlPattern = UrlPattern.of("/api/v1/users");
            HttpMethod httpMethod = HttpMethod.GET;
            String description = "사용자 목록 조회";

            // when
            PermissionEndpoint endpoint =
                    PermissionEndpoint.create(
                            permissionId,
                            serviceName,
                            urlPattern,
                            httpMethod,
                            description,
                            DEFAULT_IS_PUBLIC,
                            NOW);

            // then
            assertThat(endpoint.permissionIdValue()).isEqualTo(1L);
            assertThat(endpoint.serviceNameValue()).isEqualTo(DEFAULT_SERVICE_NAME);
            assertThat(endpoint.urlPatternValue()).isEqualTo("/api/v1/users");
            assertThat(endpoint.httpMethodValue()).isEqualTo("GET");
            assertThat(endpoint.descriptionValue()).isEqualTo(description);
            assertThat(endpoint.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("PermissionEndpoint 재구성 테스트")
    class ReconstituteTests {

        @Test
        @DisplayName("VO 타입 파라미터로 엔드포인트를 재구성한다")
        void shouldReconstituteWithVOTypes() {
            // given
            PermissionEndpointId endpointId = PermissionEndpointId.of(1L);
            PermissionId permissionId = PermissionId.of(1L);
            ServiceName serviceName = ServiceName.of(DEFAULT_SERVICE_NAME);
            UrlPattern urlPattern = UrlPattern.of("/api/v1/users");
            HttpMethod httpMethod = HttpMethod.GET;
            String description = "사용자 목록 조회";
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = NOW;
            Instant updatedAt = NOW;

            // when
            PermissionEndpoint endpoint =
                    PermissionEndpoint.reconstitute(
                            endpointId,
                            permissionId,
                            serviceName,
                            urlPattern,
                            httpMethod,
                            description,
                            DEFAULT_IS_PUBLIC,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(endpoint.permissionEndpointIdValue()).isEqualTo(1L);
            assertThat(endpoint.permissionIdValue()).isEqualTo(1L);
            assertThat(endpoint.serviceNameValue()).isEqualTo(DEFAULT_SERVICE_NAME);
            assertThat(endpoint.urlPatternValue()).isEqualTo("/api/v1/users");
            assertThat(endpoint.httpMethodValue()).isEqualTo("GET");
            assertThat(endpoint.descriptionValue()).isEqualTo(description);
            assertThat(endpoint.isNew()).isFalse();
        }

        @Test
        @DisplayName("Long 타입 파라미터로 엔드포인트를 재구성한다")
        void shouldReconstituteWithLongTypes() {
            // given
            Long endpointId = 1L;
            Long permissionId = 1L;
            String serviceName = DEFAULT_SERVICE_NAME;
            String urlPattern = "/api/v1/users";
            HttpMethod httpMethod = HttpMethod.GET;
            String description = "사용자 목록 조회";
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = NOW;
            Instant updatedAt = NOW;

            // when
            PermissionEndpoint endpoint =
                    PermissionEndpoint.reconstitute(
                            endpointId,
                            permissionId,
                            serviceName,
                            urlPattern,
                            httpMethod,
                            description,
                            DEFAULT_IS_PUBLIC,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(endpoint.permissionEndpointIdValue()).isEqualTo(1L);
            assertThat(endpoint.permissionIdValue()).isEqualTo(1L);
            assertThat(endpoint.serviceNameValue()).isEqualTo(DEFAULT_SERVICE_NAME);
            assertThat(endpoint.urlPatternValue()).isEqualTo("/api/v1/users");
            assertThat(endpoint.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("URL 패턴 매칭 테스트")
    class UrlPatternMatchingTests {

        @Test
        @DisplayName("정확한 URL 경로가 매칭된다")
        void shouldMatchExactUrlPath() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();

            // when & then
            assertThat(endpoint.matches("/api/v1/users", HttpMethod.GET)).isTrue();
        }

        @Test
        @DisplayName("경로 변수를 포함한 URL이 매칭된다")
        void shouldMatchUrlWithPathVariable() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.createWithPathVariable();

            // when & then
            assertThat(endpoint.matches("/api/v1/users/123", HttpMethod.GET)).isTrue();
            assertThat(endpoint.matches("/api/v1/users/abc-def", HttpMethod.GET)).isTrue();
            assertThat(
                            endpoint.matches(
                                    "/api/v1/users/01941234-5678-7000-8000-123456789abc",
                                    HttpMethod.GET))
                    .isTrue();
        }

        @Test
        @DisplayName("중첩 경로 변수를 포함한 URL이 매칭된다")
        void shouldMatchUrlWithNestedPathVariables() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.createWithNestedPathVariable();

            // when & then
            assertThat(
                            endpoint.matches(
                                    "/api/v1/organizations/org123/members/member456",
                                    HttpMethod.GET))
                    .isTrue();
        }

        @Test
        @DisplayName("와일드카드 패턴이 모든 하위 경로를 매칭한다")
        void shouldMatchWildcardPattern() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.createWithWildcard();

            // when & then
            assertThat(endpoint.matches("/api/v1/admin/users", HttpMethod.GET)).isTrue();
            assertThat(endpoint.matches("/api/v1/admin/roles/permissions", HttpMethod.GET))
                    .isTrue();
            assertThat(endpoint.matches("/api/v1/admin/a/b/c/d", HttpMethod.GET)).isTrue();
        }

        @Test
        @DisplayName("HTTP 메서드가 다르면 매칭되지 않는다")
        void shouldNotMatchWhenHttpMethodDiffers() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.createGetEndpoint();

            // when & then
            assertThat(endpoint.matches("/api/v1/users", HttpMethod.POST)).isFalse();
            assertThat(endpoint.matches("/api/v1/users", HttpMethod.PUT)).isFalse();
            assertThat(endpoint.matches("/api/v1/users", HttpMethod.DELETE)).isFalse();
        }

        @Test
        @DisplayName("URL 경로가 다르면 매칭되지 않는다")
        void shouldNotMatchWhenUrlPathDiffers() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();

            // when & then
            assertThat(endpoint.matches("/api/v1/roles", HttpMethod.GET)).isFalse();
            assertThat(endpoint.matches("/api/v2/users", HttpMethod.GET)).isFalse();
        }

        @Test
        @DisplayName("경로 변수 패턴에서 추가 세그먼트가 있으면 매칭되지 않는다")
        void shouldNotMatchWhenExtraPathSegmentExists() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.createWithPathVariable();

            // when & then
            assertThat(endpoint.matches("/api/v1/users/123/orders", HttpMethod.GET)).isFalse();
        }

        @ParameterizedTest
        @DisplayName("다양한 URL 패턴 매칭 시나리오")
        @CsvSource({
            "/api/v1/users, /api/v1/users, GET, true",
            "/api/v1/users/{id}, /api/v1/users/123, GET, true",
            "/api/v1/users/{id}, /api/v1/users/abc, GET, true",
            "/api/v1/users/{id}, /api/v1/users/123/extra, GET, false",
            "/api/v1/users, /api/v1/roles, GET, false",
        })
        void shouldMatchVariousPatterns(
                String pattern, String requestUrl, String method, boolean expectedMatch) {
            // given
            PermissionEndpoint endpoint =
                    PermissionEndpointFixture.createWithPatternAndMethod(
                            pattern, HttpMethod.valueOf(method));

            // when & then
            assertThat(endpoint.matches(requestUrl, HttpMethod.valueOf(method)))
                    .isEqualTo(expectedMatch);
        }
    }

    @Nested
    @DisplayName("PermissionEndpoint 수정 테스트")
    class UpdateTests {

        @Test
        @DisplayName("엔드포인트 정보를 수정한다")
        void shouldUpdateEndpoint() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();
            PermissionEndpointUpdateData updateData =
                    PermissionEndpointUpdateData.of(
                            DEFAULT_SERVICE_NAME,
                            "/api/v2/users",
                            "POST",
                            "새로운 설명",
                            DEFAULT_IS_PUBLIC);

            // when
            endpoint.update(updateData, NOW);

            // then
            assertThat(endpoint.urlPatternValue()).isEqualTo("/api/v2/users");
            assertThat(endpoint.httpMethodValue()).isEqualTo("POST");
            assertThat(endpoint.descriptionValue()).isEqualTo("새로운 설명");
            assertThat(endpoint.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("URL 패턴만 수정한다")
        void shouldUpdateOnlyUrlPattern() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();
            String originalMethod = endpoint.httpMethodValue();
            String originalDescription = endpoint.descriptionValue();
            PermissionEndpointUpdateData updateData =
                    PermissionEndpointUpdateData.of(
                            DEFAULT_SERVICE_NAME,
                            "/api/v2/users",
                            originalMethod,
                            originalDescription,
                            DEFAULT_IS_PUBLIC);

            // when
            endpoint.update(updateData, NOW);

            // then
            assertThat(endpoint.urlPatternValue()).isEqualTo("/api/v2/users");
            assertThat(endpoint.httpMethodValue()).isEqualTo(originalMethod);
            assertThat(endpoint.descriptionValue()).isEqualTo(originalDescription);
        }

        @Test
        @DisplayName("수정 시 URL 패턴이 /로 시작하지 않으면 예외가 발생한다")
        void shouldThrowExceptionWhenUpdateUrlPatternInvalid() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    PermissionEndpointUpdateData.of(
                                            DEFAULT_SERVICE_NAME,
                                            "api/v2/users",
                                            "GET",
                                            "설명",
                                            DEFAULT_IS_PUBLIC))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("'/'로 시작");
        }
    }

    @Nested
    @DisplayName("PermissionEndpoint 삭제/복원 테스트")
    class DeleteRestoreTests {

        @Test
        @DisplayName("엔드포인트를 삭제(소프트 삭제)한다")
        void shouldDeleteEndpoint() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();

            // when
            endpoint.delete(NOW);

            // then
            assertThat(endpoint.isDeleted()).isTrue();
            assertThat(endpoint.isActive()).isFalse();
            assertThat(endpoint.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("삭제된 엔드포인트를 복원한다")
        void shouldRestoreEndpoint() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.createDeleted();
            assertThat(endpoint.isDeleted()).isTrue();

            // when
            endpoint.restore(NOW);

            // then
            assertThat(endpoint.isDeleted()).isFalse();
            assertThat(endpoint.isActive()).isTrue();
            assertThat(endpoint.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("활성 상태에서 삭제 후 복원한다")
        void shouldRestoreAfterDelete() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();
            assertThat(endpoint.isActive()).isTrue();

            // when
            endpoint.delete(NOW);
            assertThat(endpoint.isDeleted()).isTrue();

            Instant restoreTime = NOW.plusSeconds(100);
            endpoint.restore(restoreTime);

            // then
            assertThat(endpoint.isDeleted()).isFalse();
            assertThat(endpoint.isActive()).isTrue();
            assertThat(endpoint.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("이미 삭제된 엔드포인트를 다시 삭제한다")
        void shouldDeleteAlreadyDeletedEndpoint() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.createDeleted();
            assertThat(endpoint.isDeleted()).isTrue();
            Instant firstDeleteTime = endpoint.getDeletionStatus().deletedAt();

            // when
            Instant secondDeleteTime = NOW.plusSeconds(100);
            endpoint.delete(secondDeleteTime);

            // then
            assertThat(endpoint.isDeleted()).isTrue();
            assertThat(endpoint.getDeletionStatus().deletedAt()).isNotNull();
            assertThat(endpoint.getDeletionStatus().deletedAt()).isEqualTo(secondDeleteTime);
            assertThat(endpoint.updatedAt()).isEqualTo(secondDeleteTime);
        }
    }

    @Nested
    @DisplayName("PermissionEndpoint Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("isNew는 ID가 없을 때 true를 반환한다")
        void isNewShouldReturnTrueWhenIdIsNull() {
            // given
            PermissionEndpoint newEndpoint = PermissionEndpointFixture.createNew();
            PermissionEndpoint existingEndpoint = PermissionEndpointFixture.create();

            // then
            assertThat(newEndpoint.isNew()).isTrue();
            assertThat(existingEndpoint.isNew()).isFalse();
        }

        @Test
        @DisplayName("permissionEndpointIdValue는 신규 생성 시 null을 반환한다")
        void permissionEndpointIdValueShouldReturnNullForNewEndpoint() {
            // given
            PermissionEndpoint newEndpoint = PermissionEndpointFixture.createNew();

            // when & then
            assertThat(newEndpoint.permissionEndpointIdValue()).isNull();
        }

        @Test
        @DisplayName("permissionEndpointIdValue는 기존 엔드포인트에서 ID를 반환한다")
        void permissionEndpointIdValueShouldReturnIdForExistingEndpoint() {
            // given
            PermissionEndpoint existingEndpoint = PermissionEndpointFixture.create();

            // when & then
            assertThat(existingEndpoint.permissionEndpointIdValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("serviceNameValue는 서비스 이름을 반환한다")
        void serviceNameValueShouldReturnServiceName() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();

            // when & then
            assertThat(endpoint.serviceNameValue()).isEqualTo(DEFAULT_SERVICE_NAME);
        }

        @Test
        @DisplayName("urlPatternValue는 URL 패턴을 반환한다")
        void urlPatternValueShouldReturnUrlPattern() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();

            // when & then
            assertThat(endpoint.urlPatternValue()).isEqualTo("/api/v1/users");
        }

        @Test
        @DisplayName("httpMethodValue는 HTTP 메서드 이름을 반환한다")
        void httpMethodValueShouldReturnHttpMethodName() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();

            // when & then
            assertThat(endpoint.httpMethodValue()).isEqualTo("GET");
        }

        @Test
        @DisplayName("descriptionValue는 설명을 반환한다")
        void descriptionValueShouldReturnDescription() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();

            // when & then
            assertThat(endpoint.descriptionValue()).isEqualTo("사용자 목록 조회 엔드포인트");
        }
    }

    @Nested
    @DisplayName("PermissionEndpoint equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 permissionEndpointId를 가진 엔드포인트는 동등하다")
        void shouldBeEqualWhenSameEndpointId() {
            // given
            PermissionEndpoint endpoint1 = PermissionEndpointFixture.create();
            PermissionEndpoint endpoint2 = PermissionEndpointFixture.create();

            // then
            assertThat(endpoint1).isEqualTo(endpoint2);
            assertThat(endpoint1.hashCode()).isEqualTo(endpoint2.hashCode());
        }

        @Test
        @DisplayName("ID가 없는 경우 permissionId, urlPattern, httpMethod로 동등성을 판단한다")
        void shouldUseCompositeKeyWhenIdIsNull() {
            // given
            PermissionEndpoint endpoint1 =
                    PermissionEndpointFixture.createNewWithPattern("/api/v1/users", HttpMethod.GET);
            PermissionEndpoint endpoint2 =
                    PermissionEndpointFixture.createNewWithPattern("/api/v1/users", HttpMethod.GET);

            // then
            assertThat(endpoint1).isEqualTo(endpoint2);
        }

        @Test
        @DisplayName("httpMethod가 다르면 동등하지 않다 (ID가 없는 경우)")
        void shouldNotBeEqualWhenDifferentHttpMethod() {
            // given
            PermissionEndpoint endpoint1 =
                    PermissionEndpointFixture.createNewWithPattern("/api/v1/users", HttpMethod.GET);
            PermissionEndpoint endpoint2 =
                    PermissionEndpointFixture.createNewWithPattern(
                            "/api/v1/users", HttpMethod.POST);

            // then
            assertThat(endpoint1).isNotEqualTo(endpoint2);
        }
    }

    @Nested
    @DisplayName("PermissionEndpoint toString() 테스트")
    class ToStringTests {

        @Test
        @DisplayName("toString은 엔드포인트 정보를 포함한다")
        void toStringShouldContainEndpointInformation() {
            // given
            PermissionEndpoint endpoint = PermissionEndpointFixture.create();

            // when
            String toString = endpoint.toString();

            // then - record VO의 toString은 "TypeName[value=X]" 형식으로 출력됨
            assertThat(toString).contains("PermissionEndpoint");
            assertThat(toString).contains("permissionEndpointId=PermissionEndpointId[value=1]");
            assertThat(toString).contains("permissionId=PermissionId[value=1]");
            assertThat(toString).contains("serviceName=ServiceName[value=authhub]");
            assertThat(toString).contains("urlPattern=UrlPattern[value=/api/v1/users]");
            assertThat(toString).contains("httpMethod=GET");
            assertThat(toString).contains("isPublic=false");
            assertThat(toString).contains("deleted=false");
        }

        @Test
        @DisplayName("toString은 신규 엔드포인트에서도 정상 동작한다")
        void toStringShouldWorkForNewEndpoint() {
            // given
            PermissionEndpoint newEndpoint = PermissionEndpointFixture.createNew();

            // when
            String toString = newEndpoint.toString();

            // then - 신규 객체는 permissionEndpointId=null
            assertThat(toString).contains("PermissionEndpoint");
            assertThat(toString).contains("permissionEndpointId=null");
            assertThat(toString).contains("permissionId=PermissionId[value=1]");
        }
    }
}
