package com.ryuqq.authhub.adapter.out.persistence.endpointpermission.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.common.MapperTestSupport;
import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.entity.EndpointPermissionJpaEntity;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.fixture.EndpointPermissionFixture;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointDescription;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointPath;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import com.ryuqq.authhub.domain.endpointpermission.vo.RequiredPermissions;
import com.ryuqq.authhub.domain.endpointpermission.vo.RequiredRoles;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * EndpointPermissionJpaEntityMapper 단위 테스트
 *
 * <p>Entity ↔ Domain 변환 로직을 검증합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("EndpointPermissionJpaEntityMapper 단위 테스트")
class EndpointPermissionJpaEntityMapperTest extends MapperTestSupport {

    private EndpointPermissionJpaEntityMapper mapper;

    private static final UUID TEST_UUID = UUID.randomUUID();
    private static final LocalDateTime FIXED_LOCAL_TIME = LocalDateTime.of(2025, 1, 1, 12, 0, 0);
    private static final Instant FIXED_INSTANT = FIXED_LOCAL_TIME.toInstant(ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        mapper = new EndpointPermissionJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드 - Domain → Entity 변환")
    class ToEntityTest {

        @Test
        @DisplayName("[성공] 보호된 엔드포인트를 Entity로 변환한다")
        void shouldConvertProtectedEndpointToEntity() {
            // given
            EndpointPermission domain =
                    EndpointPermission.reconstitute(
                            EndpointPermissionId.of(TEST_UUID),
                            ServiceName.of("auth-hub"),
                            EndpointPath.of("/api/v1/users"),
                            HttpMethod.GET,
                            EndpointDescription.of("사용자 목록 조회"),
                            false,
                            RequiredPermissions.of(Set.of("user:read", "user:list")),
                            RequiredRoles.of(Set.of("ADMIN", "USER_MANAGER")),
                            1L,
                            false,
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            EndpointPermissionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isNull(); // 신규 저장 시 null
            assertThat(entity.getEndpointPermissionId()).isEqualTo(TEST_UUID);
            assertThat(entity.getServiceName()).isEqualTo("auth-hub");
            assertThat(entity.getPath()).isEqualTo("/api/v1/users");
            assertThat(entity.getMethod()).isEqualTo("GET");
            assertThat(entity.getDescription()).isEqualTo("사용자 목록 조회");
            assertThat(entity.isPublic()).isFalse();
            assertThat(entity.getVersion()).isEqualTo(1L);
            assertThat(entity.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("[성공] 공개 엔드포인트를 Entity로 변환한다")
        void shouldConvertPublicEndpointToEntity() {
            // given
            EndpointPermission domain = EndpointPermissionFixture.createPublic();

            // when
            EndpointPermissionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity).isNotNull();
            assertThat(entity.isPublic()).isTrue();
            assertThat(entity.getRequiredPermissions()).isNull();
            assertThat(entity.getRequiredRoles()).isNull();
        }

        @Test
        @DisplayName("[성공] requiredPermissions Set을 콤마 구분 문자열로 변환한다")
        void shouldConvertPermissionsSetToCommaSeparatedString() {
            // given
            EndpointPermission domain =
                    EndpointPermission.reconstitute(
                            EndpointPermissionId.of(TEST_UUID),
                            ServiceName.of("auth-hub"),
                            EndpointPath.of("/api/v1/users"),
                            HttpMethod.DELETE,
                            EndpointDescription.empty(),
                            false,
                            RequiredPermissions.of(Set.of("user:delete", "user:write")),
                            RequiredRoles.empty(),
                            0L,
                            false,
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            EndpointPermissionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getRequiredPermissions()).isNotNull();
            // Set은 순서가 없으므로 contains로 검증
            assertThat(entity.getRequiredPermissions()).contains("user:delete");
            assertThat(entity.getRequiredPermissions()).contains("user:write");
        }

        @Test
        @DisplayName("[성공] requiredRoles Set을 콤마 구분 문자열로 변환한다")
        void shouldConvertRolesSetToCommaSeparatedString() {
            // given
            EndpointPermission domain =
                    EndpointPermission.reconstitute(
                            EndpointPermissionId.of(TEST_UUID),
                            ServiceName.of("auth-hub"),
                            EndpointPath.of("/api/v1/admin"),
                            HttpMethod.GET,
                            EndpointDescription.empty(),
                            false,
                            RequiredPermissions.empty(),
                            RequiredRoles.of(Set.of("SUPER_ADMIN", "ADMIN")),
                            0L,
                            false,
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            EndpointPermissionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getRequiredRoles()).isNotNull();
            assertThat(entity.getRequiredRoles()).contains("SUPER_ADMIN");
            assertThat(entity.getRequiredRoles()).contains("ADMIN");
        }

        @Test
        @DisplayName("[성공] Instant를 LocalDateTime으로 변환한다")
        void shouldConvertInstantToLocalDateTime() {
            // given
            EndpointPermission domain =
                    EndpointPermission.reconstitute(
                            EndpointPermissionId.of(TEST_UUID),
                            ServiceName.of("auth-hub"),
                            EndpointPath.of("/api/v1/users"),
                            HttpMethod.GET,
                            EndpointDescription.empty(),
                            true,
                            RequiredPermissions.empty(),
                            RequiredRoles.empty(),
                            0L,
                            false,
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            EndpointPermissionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getCreatedAt()).isEqualTo(FIXED_LOCAL_TIME);
            assertThat(entity.getUpdatedAt()).isEqualTo(FIXED_LOCAL_TIME);
        }

        @Test
        @DisplayName("[성공] 빈 권한/역할은 null로 변환한다")
        void shouldConvertEmptyPermissionsAndRolesToNull() {
            // given
            EndpointPermission domain = EndpointPermissionFixture.createPublic();

            // when
            EndpointPermissionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getRequiredPermissions()).isNull();
            assertThat(entity.getRequiredRoles()).isNull();
        }
    }

    @Nested
    @DisplayName("toDomain 메서드 - Entity → Domain 변환")
    class ToDomainTest {

        @Test
        @DisplayName("[성공] 보호된 엔드포인트 Entity를 Domain으로 변환한다")
        void shouldConvertProtectedEntityToDomain() {
            // given
            EndpointPermissionJpaEntity entity =
                    EndpointPermissionJpaEntity.of(
                            1L,
                            TEST_UUID,
                            "auth-hub",
                            "/api/v1/users",
                            "GET",
                            "사용자 목록 조회",
                            false,
                            "user:read,user:list",
                            "ADMIN,USER_MANAGER",
                            1L,
                            false,
                            FIXED_LOCAL_TIME,
                            FIXED_LOCAL_TIME);

            // when
            EndpointPermission domain = mapper.toDomain(entity);

            // then
            assertThat(domain).isNotNull();
            assertThat(domain.endpointPermissionIdValue()).isEqualTo(TEST_UUID);
            assertThat(domain.serviceNameValue()).isEqualTo("auth-hub");
            assertThat(domain.pathValue()).isEqualTo("/api/v1/users");
            assertThat(domain.methodValue()).isEqualTo("GET");
            assertThat(domain.descriptionValue()).isEqualTo("사용자 목록 조회");
            assertThat(domain.isPublic()).isFalse();
            assertThat(domain.getVersion()).isEqualTo(1L);
            assertThat(domain.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("[성공] 공개 엔드포인트 Entity를 Domain으로 변환한다")
        void shouldConvertPublicEntityToDomain() {
            // given
            EndpointPermissionJpaEntity entity =
                    EndpointPermissionJpaEntity.of(
                            1L,
                            TEST_UUID,
                            "auth-hub",
                            "/api/v1/health",
                            "GET",
                            "헬스체크",
                            true,
                            null,
                            null,
                            0L,
                            false,
                            FIXED_LOCAL_TIME,
                            FIXED_LOCAL_TIME);

            // when
            EndpointPermission domain = mapper.toDomain(entity);

            // then
            assertThat(domain).isNotNull();
            assertThat(domain.isPublic()).isTrue();
            assertThat(domain.requiredPermissionValues()).isEmpty();
            assertThat(domain.requiredRoleValues()).isEmpty();
        }

        @Test
        @DisplayName("[성공] 콤마 구분 문자열을 Set으로 변환한다")
        void shouldConvertCommaSeparatedStringToSet() {
            // given
            EndpointPermissionJpaEntity entity =
                    EndpointPermissionJpaEntity.of(
                            1L,
                            TEST_UUID,
                            "auth-hub",
                            "/api/v1/users",
                            "DELETE",
                            null,
                            false,
                            "user:delete,user:write,user:admin",
                            "SUPER_ADMIN,ADMIN",
                            0L,
                            false,
                            FIXED_LOCAL_TIME,
                            FIXED_LOCAL_TIME);

            // when
            EndpointPermission domain = mapper.toDomain(entity);

            // then
            assertThat(domain.requiredPermissionValues())
                    .containsExactlyInAnyOrder("user:delete", "user:write", "user:admin");
            assertThat(domain.requiredRoleValues())
                    .containsExactlyInAnyOrder("SUPER_ADMIN", "ADMIN");
        }

        @Test
        @DisplayName("[성공] LocalDateTime을 Instant로 변환한다")
        void shouldConvertLocalDateTimeToInstant() {
            // given
            EndpointPermissionJpaEntity entity =
                    EndpointPermissionJpaEntity.of(
                            1L,
                            TEST_UUID,
                            "auth-hub",
                            "/api/v1/users",
                            "GET",
                            null,
                            true,
                            null,
                            null,
                            0L,
                            false,
                            FIXED_LOCAL_TIME,
                            FIXED_LOCAL_TIME);

            // when
            EndpointPermission domain = mapper.toDomain(entity);

            // then
            assertThat(domain.createdAt()).isEqualTo(FIXED_INSTANT);
            assertThat(domain.updatedAt()).isEqualTo(FIXED_INSTANT);
        }

        @Test
        @DisplayName("[성공] null description은 빈 EndpointDescription으로 변환한다")
        void shouldConvertNullDescriptionToEmptyDescription() {
            // given
            EndpointPermissionJpaEntity entity =
                    EndpointPermissionJpaEntity.of(
                            1L,
                            TEST_UUID,
                            "auth-hub",
                            "/api/v1/users",
                            "GET",
                            null, // null description
                            true,
                            null,
                            null,
                            0L,
                            false,
                            FIXED_LOCAL_TIME,
                            FIXED_LOCAL_TIME);

            // when
            EndpointPermission domain = mapper.toDomain(entity);

            // then - EndpointDescription.empty()는 value가 null인 객체를 생성함
            assertThat(domain.descriptionValue()).isNull();
        }

        @Test
        @DisplayName("[성공] 빈 문자열 권한/역할은 빈 Set으로 변환한다")
        void shouldConvertEmptyStringToEmptySet() {
            // given
            EndpointPermissionJpaEntity entity =
                    EndpointPermissionJpaEntity.of(
                            1L,
                            TEST_UUID,
                            "auth-hub",
                            "/api/v1/users",
                            "GET",
                            null,
                            true,
                            "", // 빈 문자열
                            "  ", // 공백만
                            0L,
                            false,
                            FIXED_LOCAL_TIME,
                            FIXED_LOCAL_TIME);

            // when
            EndpointPermission domain = mapper.toDomain(entity);

            // then
            assertThat(domain.requiredPermissionValues()).isEmpty();
            assertThat(domain.requiredRoleValues()).isEmpty();
        }

        @Test
        @DisplayName("[성공] 공백이 포함된 콤마 구분 문자열을 trim하여 Set으로 변환한다")
        void shouldTrimWhitespaceInCommaSeparatedString() {
            // given
            EndpointPermissionJpaEntity entity =
                    EndpointPermissionJpaEntity.of(
                            1L,
                            TEST_UUID,
                            "auth-hub",
                            "/api/v1/users",
                            "GET",
                            null,
                            false,
                            " user:read , user:write ", // 공백 포함
                            " ADMIN , USER ", // 공백 포함
                            0L,
                            false,
                            FIXED_LOCAL_TIME,
                            FIXED_LOCAL_TIME);

            // when
            EndpointPermission domain = mapper.toDomain(entity);

            // then
            assertThat(domain.requiredPermissionValues())
                    .containsExactlyInAnyOrder("user:read", "user:write");
            assertThat(domain.requiredRoleValues()).containsExactlyInAnyOrder("ADMIN", "USER");
        }
    }

    @Nested
    @DisplayName("양방향 변환 테스트")
    class RoundTripTest {

        @Test
        @DisplayName("[성공] Domain → Entity → Domain 변환 시 동등성이 유지된다")
        void shouldMaintainEqualityAfterDomainToEntityToDomainConversion() {
            // given
            EndpointPermission originalDomain =
                    EndpointPermission.reconstitute(
                            EndpointPermissionId.of(TEST_UUID),
                            ServiceName.of("auth-hub"),
                            EndpointPath.of("/api/v1/users/{userId}"),
                            HttpMethod.PUT,
                            EndpointDescription.of("사용자 정보 수정"),
                            false,
                            RequiredPermissions.of(Set.of("user:write")),
                            RequiredRoles.of(Set.of("ADMIN")),
                            5L,
                            false,
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            EndpointPermissionJpaEntity entity = mapper.toEntity(originalDomain);
            // Entity에는 ID가 없으므로, 실제 저장 시나리오를 모방하여 ID 설정
            EndpointPermissionJpaEntity entityWithId =
                    EndpointPermissionJpaEntity.of(
                            1L,
                            entity.getEndpointPermissionId(),
                            entity.getServiceName(),
                            entity.getPath(),
                            entity.getMethod(),
                            entity.getDescription(),
                            entity.isPublic(),
                            entity.getRequiredPermissions(),
                            entity.getRequiredRoles(),
                            entity.getVersion(),
                            entity.isDeleted(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());
            EndpointPermission convertedDomain = mapper.toDomain(entityWithId);

            // then
            assertThat(convertedDomain.endpointPermissionIdValue())
                    .isEqualTo(originalDomain.endpointPermissionIdValue());
            assertThat(convertedDomain.serviceNameValue())
                    .isEqualTo(originalDomain.serviceNameValue());
            assertThat(convertedDomain.pathValue()).isEqualTo(originalDomain.pathValue());
            assertThat(convertedDomain.methodValue()).isEqualTo(originalDomain.methodValue());
            assertThat(convertedDomain.descriptionValue())
                    .isEqualTo(originalDomain.descriptionValue());
            assertThat(convertedDomain.isPublic()).isEqualTo(originalDomain.isPublic());
            assertThat(convertedDomain.requiredPermissionValues())
                    .isEqualTo(originalDomain.requiredPermissionValues());
            assertThat(convertedDomain.requiredRoleValues())
                    .isEqualTo(originalDomain.requiredRoleValues());
            assertThat(convertedDomain.getVersion()).isEqualTo(originalDomain.getVersion());
            assertThat(convertedDomain.isDeleted()).isEqualTo(originalDomain.isDeleted());
        }

        @Test
        @DisplayName("[성공] 공개 엔드포인트 양방향 변환 시 동등성이 유지된다")
        void shouldMaintainEqualityForPublicEndpoint() {
            // given
            EndpointPermission originalDomain =
                    EndpointPermission.reconstitute(
                            EndpointPermissionId.of(TEST_UUID),
                            ServiceName.of("auth-hub"),
                            EndpointPath.of("/api/v1/health"),
                            HttpMethod.GET,
                            EndpointDescription.of("헬스체크"),
                            true,
                            RequiredPermissions.empty(),
                            RequiredRoles.empty(),
                            0L,
                            false,
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            EndpointPermissionJpaEntity entity = mapper.toEntity(originalDomain);
            EndpointPermissionJpaEntity entityWithId =
                    EndpointPermissionJpaEntity.of(
                            1L,
                            entity.getEndpointPermissionId(),
                            entity.getServiceName(),
                            entity.getPath(),
                            entity.getMethod(),
                            entity.getDescription(),
                            entity.isPublic(),
                            entity.getRequiredPermissions(),
                            entity.getRequiredRoles(),
                            entity.getVersion(),
                            entity.isDeleted(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());
            EndpointPermission convertedDomain = mapper.toDomain(entityWithId);

            // then
            assertThat(convertedDomain.isPublic()).isTrue();
            assertThat(convertedDomain.requiredPermissionValues()).isEmpty();
            assertThat(convertedDomain.requiredRoleValues()).isEmpty();
        }
    }

    @Nested
    @DisplayName("HTTP Method 변환 테스트")
    class HttpMethodConversionTest {

        @Test
        @DisplayName("[성공] 모든 HTTP Method를 올바르게 변환한다")
        void shouldConvertAllHttpMethods() {
            // given
            HttpMethod[] methods = HttpMethod.values();

            for (HttpMethod method : methods) {
                EndpointPermission domain =
                        EndpointPermission.reconstitute(
                                EndpointPermissionId.of(UUID.randomUUID()),
                                ServiceName.of("test-service"),
                                EndpointPath.of("/api/test"),
                                method,
                                EndpointDescription.empty(),
                                true,
                                RequiredPermissions.empty(),
                                RequiredRoles.empty(),
                                0L,
                                false,
                                FIXED_INSTANT,
                                FIXED_INSTANT);

                // when
                EndpointPermissionJpaEntity entity = mapper.toEntity(domain);

                // then
                assertThat(entity.getMethod()).isEqualTo(method.name());
            }
        }
    }
}
