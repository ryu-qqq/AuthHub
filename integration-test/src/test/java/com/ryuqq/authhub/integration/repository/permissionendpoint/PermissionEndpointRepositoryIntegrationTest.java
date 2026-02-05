package com.ryuqq.authhub.integration.repository.permissionendpoint;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.PermissionEndpointJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository.PermissionEndpointJpaRepository;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.integration.common.base.RepositoryTestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PermissionEndpointJpaRepository 통합 테스트.
 *
 * <p>Repository 레이어의 CRUD 및 쿼리 기능을 검증합니다.
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.PERMISSION)
class PermissionEndpointRepositoryIntegrationTest extends RepositoryTestBase {

    @Autowired private PermissionEndpointJpaRepository permissionEndpointJpaRepository;

    @Autowired private PermissionJpaRepository permissionJpaRepository;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final String DEFAULT_SERVICE_NAME = "authhub";
    private static final boolean DEFAULT_IS_PUBLIC = false;

    private PermissionJpaEntity savedPermission;

    @BeforeEach
    void setUp() {
        permissionEndpointJpaRepository.deleteAll();
        permissionJpaRepository.deleteAll();

        PermissionJpaEntity permission =
                PermissionJpaEntity.of(
                        null,
                        null,
                        "user:read",
                        "user",
                        "read",
                        "사용자 조회 권한",
                        PermissionType.CUSTOM,
                        FIXED_TIME,
                        FIXED_TIME,
                        null);
        savedPermission = permissionJpaRepository.save(permission);
        flushAndClear();
    }

    @Nested
    @DisplayName("save 테스트")
    class SaveTest {

        @Test
        @DisplayName("권한 엔드포인트를 저장할 수 있다")
        void shouldSavePermissionEndpoint() {
            // given
            PermissionEndpointJpaEntity entity =
                    PermissionEndpointJpaEntity.of(
                            null,
                            savedPermission.getPermissionId(),
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/users/{id}",
                            HttpMethod.GET,
                            "사용자 조회 API",
                            DEFAULT_IS_PUBLIC,
                            FIXED_TIME,
                            FIXED_TIME,
                            null);

            // when
            PermissionEndpointJpaEntity saved = permissionEndpointJpaRepository.save(entity);
            flushAndClear();

            // then
            assertThat(saved.getPermissionEndpointId()).isNotNull();

            Optional<PermissionEndpointJpaEntity> found =
                    permissionEndpointJpaRepository.findById(saved.getPermissionEndpointId());
            assertThat(found).isPresent();
            assertThat(found.get().getUrlPattern()).isEqualTo("/api/v1/users/{id}");
            assertThat(found.get().getHttpMethod()).isEqualTo(HttpMethod.GET);
        }
    }

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 권한 엔드포인트를 ID로 조회할 수 있다")
        void shouldFindExistingPermissionEndpoint() {
            // given
            PermissionEndpointJpaEntity entity =
                    PermissionEndpointJpaEntity.of(
                            null,
                            savedPermission.getPermissionId(),
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/roles",
                            HttpMethod.POST,
                            "역할 생성 API",
                            DEFAULT_IS_PUBLIC,
                            FIXED_TIME,
                            FIXED_TIME,
                            null);
            PermissionEndpointJpaEntity saved = permissionEndpointJpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<PermissionEndpointJpaEntity> found =
                    permissionEndpointJpaRepository.findById(saved.getPermissionEndpointId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getUrlPattern()).isEqualTo("/api/v1/roles");
            assertThat(found.get().getHttpMethod()).isEqualTo(HttpMethod.POST);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentId() {
            // when
            Optional<PermissionEndpointJpaEntity> found =
                    permissionEndpointJpaRepository.findById(99999L);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByUrlPatternAndHttpMethod 테스트")
    class FindByUrlPatternAndHttpMethodTest {

        @Test
        @DisplayName("URL 패턴과 HTTP 메서드로 조회할 수 있다")
        void shouldFindByUrlPatternAndHttpMethod() {
            // given
            PermissionEndpointJpaEntity entity =
                    PermissionEndpointJpaEntity.of(
                            null,
                            savedPermission.getPermissionId(),
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/users",
                            HttpMethod.GET,
                            "사용자 목록 조회",
                            DEFAULT_IS_PUBLIC,
                            FIXED_TIME,
                            FIXED_TIME,
                            null);
            permissionEndpointJpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<PermissionEndpointJpaEntity> found =
                    permissionEndpointJpaRepository.findByUrlPatternAndHttpMethodAndDeletedAtIsNull(
                            "/api/v1/users", HttpMethod.GET);

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getUrlPattern()).isEqualTo("/api/v1/users");
        }
    }

    @Nested
    @DisplayName("findAllByPermissionId 테스트")
    class FindAllByPermissionIdTest {

        @Test
        @DisplayName("권한 ID로 연결된 모든 엔드포인트를 조회할 수 있다")
        void shouldFindAllByPermissionId() {
            // given
            PermissionEndpointJpaEntity entity1 =
                    PermissionEndpointJpaEntity.of(
                            null,
                            savedPermission.getPermissionId(),
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/users",
                            HttpMethod.GET,
                            null,
                            DEFAULT_IS_PUBLIC,
                            FIXED_TIME,
                            FIXED_TIME,
                            null);
            PermissionEndpointJpaEntity entity2 =
                    PermissionEndpointJpaEntity.of(
                            null,
                            savedPermission.getPermissionId(),
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/users",
                            HttpMethod.POST,
                            null,
                            DEFAULT_IS_PUBLIC,
                            FIXED_TIME,
                            FIXED_TIME,
                            null);
            permissionEndpointJpaRepository.save(entity1);
            permissionEndpointJpaRepository.save(entity2);
            flushAndClear();

            // when
            List<PermissionEndpointJpaEntity> found =
                    permissionEndpointJpaRepository.findAllByPermissionIdAndDeletedAtIsNull(
                            savedPermission.getPermissionId());

            // then
            assertThat(found).hasSize(2);
        }
    }

    @Nested
    @DisplayName("delete 테스트")
    class DeleteTest {

        @Test
        @DisplayName("권한 엔드포인트를 삭제할 수 있다")
        void shouldDeletePermissionEndpoint() {
            // given
            PermissionEndpointJpaEntity entity =
                    PermissionEndpointJpaEntity.of(
                            null,
                            savedPermission.getPermissionId(),
                            DEFAULT_SERVICE_NAME,
                            "/api/v1/delete-me",
                            HttpMethod.DELETE,
                            null,
                            DEFAULT_IS_PUBLIC,
                            FIXED_TIME,
                            FIXED_TIME,
                            null);
            PermissionEndpointJpaEntity saved = permissionEndpointJpaRepository.save(entity);
            flushAndClear();

            // when
            permissionEndpointJpaRepository.deleteById(saved.getPermissionEndpointId());
            flushAndClear();

            // then
            Optional<PermissionEndpointJpaEntity> found =
                    permissionEndpointJpaRepository.findById(saved.getPermissionEndpointId());
            assertThat(found).isEmpty();
        }
    }
}
