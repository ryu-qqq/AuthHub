package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionEndpointJpaEntity 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Entity는 데이터 컨테이너 → of() 팩토리, getter, 상태 확인 메서드 검증
 *   <li>Mock 불필요 (순수 객체 생성/조회)
 *   <li>SoftDeletableEntity 상속 기능 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionEndpointJpaEntity 단위 테스트")
class PermissionEndpointJpaEntityTest {

    private static final Long ENDPOINT_ID = 1L;
    private static final Long PERMISSION_ID = 10L;
    private static final String SERVICE_NAME = "authhub";
    private static final String URL_PATTERN = "/api/v1/users";
    private static final String DESCRIPTION = "사용자 목록 조회";
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T00:00:00Z");

    @Nested
    @DisplayName("of() 팩토리 메서드")
    class OfFactoryMethod {

        @Test
        @DisplayName("성공: 모든 필드가 올바르게 설정됨")
        void shouldSetAllFields_Correctly() {
            // when
            PermissionEndpointJpaEntity entity =
                    PermissionEndpointJpaEntity.of(
                            ENDPOINT_ID,
                            PERMISSION_ID,
                            SERVICE_NAME,
                            URL_PATTERN,
                            HttpMethod.GET,
                            DESCRIPTION,
                            false,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getPermissionEndpointId()).isEqualTo(ENDPOINT_ID);
            assertThat(entity.getPermissionId()).isEqualTo(PERMISSION_ID);
            assertThat(entity.getServiceName()).isEqualTo(SERVICE_NAME);
            assertThat(entity.getUrlPattern()).isEqualTo(URL_PATTERN);
            assertThat(entity.getHttpMethod()).isEqualTo(HttpMethod.GET);
            assertThat(entity.getDescription()).isEqualTo(DESCRIPTION);
            assertThat(entity.isPublic()).isFalse();
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("deletedAt이 null이면 활성 상태")
        void shouldBeActive_WhenDeletedAtIsNull() {
            // when
            PermissionEndpointJpaEntity entity =
                    PermissionEndpointJpaEntity.of(
                            ENDPOINT_ID,
                            PERMISSION_ID,
                            SERVICE_NAME,
                            URL_PATTERN,
                            HttpMethod.GET,
                            DESCRIPTION,
                            false,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.isActive()).isTrue();
            assertThat(entity.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("deletedAt이 설정되면 삭제 상태")
        void shouldBeDeleted_WhenDeletedAtIsSet() {
            // given
            Instant deletedAt = Instant.parse("2025-01-03T00:00:00Z");

            // when
            PermissionEndpointJpaEntity entity =
                    PermissionEndpointJpaEntity.of(
                            ENDPOINT_ID,
                            PERMISSION_ID,
                            SERVICE_NAME,
                            URL_PATTERN,
                            HttpMethod.GET,
                            DESCRIPTION,
                            false,
                            CREATED_AT,
                            UPDATED_AT,
                            deletedAt);

            // then
            assertThat(entity.isDeleted()).isTrue();
            assertThat(entity.isActive()).isFalse();
            assertThat(entity.getDeletedAt()).isEqualTo(deletedAt);
        }

        @Test
        @DisplayName("description이 null이어도 생성 가능")
        void shouldAllowNullDescription() {
            // when
            PermissionEndpointJpaEntity entity =
                    PermissionEndpointJpaEntity.of(
                            ENDPOINT_ID,
                            PERMISSION_ID,
                            SERVICE_NAME,
                            URL_PATTERN,
                            HttpMethod.POST,
                            null,
                            true,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getDescription()).isNull();
            assertThat(entity.isPublic()).isTrue();
            assertThat(entity.getHttpMethod()).isEqualTo(HttpMethod.POST);
        }
    }

    @Nested
    @DisplayName("HttpMethod 필드")
    class HttpMethodField {

        @Test
        @DisplayName("GET 메서드가 올바르게 설정됨")
        void shouldSetGetMethod() {
            PermissionEndpointJpaEntity entity =
                    PermissionEndpointJpaEntity.of(
                            ENDPOINT_ID,
                            PERMISSION_ID,
                            SERVICE_NAME,
                            URL_PATTERN,
                            HttpMethod.GET,
                            null,
                            false,
                            CREATED_AT,
                            UPDATED_AT,
                            null);
            assertThat(entity.getHttpMethod()).isEqualTo(HttpMethod.GET);
        }

        @Test
        @DisplayName("POST, PUT, DELETE 메서드가 올바르게 설정됨")
        void shouldSetOtherMethods() {
            for (HttpMethod method :
                    new HttpMethod[] {
                        HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH
                    }) {
                PermissionEndpointJpaEntity entity =
                        PermissionEndpointJpaEntity.of(
                                ENDPOINT_ID,
                                PERMISSION_ID,
                                SERVICE_NAME,
                                URL_PATTERN,
                                method,
                                null,
                                false,
                                CREATED_AT,
                                UPDATED_AT,
                                null);
                assertThat(entity.getHttpMethod()).isEqualTo(method);
            }
        }
    }

    @Nested
    @DisplayName("isPublic 필드")
    class IsPublicField {

        @Test
        @DisplayName("공개 엔드포인트가 true로 설정됨")
        void shouldSetPublicTrue() {
            PermissionEndpointJpaEntity entity =
                    PermissionEndpointJpaEntity.of(
                            ENDPOINT_ID,
                            PERMISSION_ID,
                            SERVICE_NAME,
                            URL_PATTERN,
                            HttpMethod.GET,
                            null,
                            true,
                            CREATED_AT,
                            UPDATED_AT,
                            null);
            assertThat(entity.isPublic()).isTrue();
        }
    }
}
