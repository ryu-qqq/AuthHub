package com.ryuqq.authhub.domain.permissionendpoint.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.permissionendpoint.vo.ServiceName;
import com.ryuqq.authhub.domain.permissionendpoint.vo.UrlPattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionEndpointUpdateData Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionEndpointUpdateData Value Object 테스트")
class PermissionEndpointUpdateDataTest {

    private static final String DEFAULT_SERVICE_NAME = "authhub";
    private static final String DEFAULT_URL_PATTERN = "/api/v1/users";
    private static final String DEFAULT_DESCRIPTION = "사용자 목록 조회";
    private static final boolean DEFAULT_IS_PUBLIC = false;

    @Nested
    @DisplayName("PermissionEndpointUpdateData 생성 테스트 (VO 타입 파라미터)")
    class CreateWithVOTypesTests {

        @Test
        @DisplayName("VO 타입 파라미터로 정상 생성한다")
        void shouldCreateWithVOTypes() {
            // given
            ServiceName serviceName = ServiceName.of(DEFAULT_SERVICE_NAME);
            UrlPattern urlPattern = UrlPattern.of(DEFAULT_URL_PATTERN);
            HttpMethod httpMethod = HttpMethod.GET;

            // when
            PermissionEndpointUpdateData updateData =
                    PermissionEndpointUpdateData.of(
                            serviceName,
                            urlPattern,
                            httpMethod,
                            DEFAULT_DESCRIPTION,
                            DEFAULT_IS_PUBLIC);

            // then
            assertThat(updateData.serviceName()).isEqualTo(serviceName);
            assertThat(updateData.urlPattern()).isEqualTo(urlPattern);
            assertThat(updateData.httpMethod()).isEqualTo(httpMethod);
            assertThat(updateData.description()).isEqualTo(DEFAULT_DESCRIPTION);
            assertThat(updateData.isPublic()).isEqualTo(DEFAULT_IS_PUBLIC);
        }
    }

    @Nested
    @DisplayName("PermissionEndpointUpdateData 생성 테스트 (문자열 파라미터)")
    class CreateWithStringTypesTests {

        @Test
        @DisplayName("문자열 파라미터로 정상 생성한다")
        void shouldCreateWithStringTypes() {
            // when
            PermissionEndpointUpdateData updateData =
                    PermissionEndpointUpdateData.of(
                            DEFAULT_SERVICE_NAME,
                            DEFAULT_URL_PATTERN,
                            "GET",
                            DEFAULT_DESCRIPTION,
                            DEFAULT_IS_PUBLIC);

            // then
            assertThat(updateData.serviceName().value()).isEqualTo(DEFAULT_SERVICE_NAME);
            assertThat(updateData.urlPattern().value()).isEqualTo(DEFAULT_URL_PATTERN);
            assertThat(updateData.httpMethod()).isEqualTo(HttpMethod.GET);
            assertThat(updateData.description()).isEqualTo(DEFAULT_DESCRIPTION);
            assertThat(updateData.isPublic()).isEqualTo(DEFAULT_IS_PUBLIC);
        }

        @Test
        @DisplayName("유효하지 않은 서비스 이름이면 예외가 발생한다")
        void shouldThrowExceptionWhenInvalidServiceName() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    PermissionEndpointUpdateData.of(
                                            "InvalidService",
                                            DEFAULT_URL_PATTERN,
                                            "GET",
                                            DEFAULT_DESCRIPTION,
                                            DEFAULT_IS_PUBLIC))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("serviceName");
        }

        @Test
        @DisplayName("유효하지 않은 URL 패턴이면 예외가 발생한다")
        void shouldThrowExceptionWhenInvalidUrlPattern() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    PermissionEndpointUpdateData.of(
                                            DEFAULT_SERVICE_NAME,
                                            "api/v1/users",
                                            "GET",
                                            DEFAULT_DESCRIPTION,
                                            DEFAULT_IS_PUBLIC))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("urlPattern");
        }

        @Test
        @DisplayName("유효하지 않은 HTTP 메서드이면 예외가 발생한다")
        void shouldThrowExceptionWhenInvalidHttpMethod() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    PermissionEndpointUpdateData.of(
                                            DEFAULT_SERVICE_NAME,
                                            DEFAULT_URL_PATTERN,
                                            "INVALID",
                                            DEFAULT_DESCRIPTION,
                                            DEFAULT_IS_PUBLIC))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("HttpMethod");
        }
    }

    @Nested
    @DisplayName("PermissionEndpointUpdateData 동등성 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 UpdateData는 동등하다")
        void shouldBeEqualWhenSameValues() {
            // given
            PermissionEndpointUpdateData data1 =
                    PermissionEndpointUpdateData.of(
                            DEFAULT_SERVICE_NAME,
                            DEFAULT_URL_PATTERN,
                            "GET",
                            DEFAULT_DESCRIPTION,
                            DEFAULT_IS_PUBLIC);
            PermissionEndpointUpdateData data2 =
                    PermissionEndpointUpdateData.of(
                            DEFAULT_SERVICE_NAME,
                            DEFAULT_URL_PATTERN,
                            "GET",
                            DEFAULT_DESCRIPTION,
                            DEFAULT_IS_PUBLIC);

            // then
            assertThat(data1).isEqualTo(data2);
            assertThat(data1.hashCode()).isEqualTo(data2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 UpdateData는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValues() {
            // given
            PermissionEndpointUpdateData data1 =
                    PermissionEndpointUpdateData.of(
                            DEFAULT_SERVICE_NAME,
                            DEFAULT_URL_PATTERN,
                            "GET",
                            DEFAULT_DESCRIPTION,
                            DEFAULT_IS_PUBLIC);
            PermissionEndpointUpdateData data2 =
                    PermissionEndpointUpdateData.of(
                            DEFAULT_SERVICE_NAME,
                            DEFAULT_URL_PATTERN,
                            "POST",
                            DEFAULT_DESCRIPTION,
                            DEFAULT_IS_PUBLIC);

            // then
            assertThat(data1).isNotEqualTo(data2);
        }
    }
}
