package com.ryuqq.authhub.adapter.out.persistence.role.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.PermissionJpaEntity;
import com.ryuqq.authhub.domain.role.aggregate.Permission;
import com.ryuqq.authhub.domain.role.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.vo.PermissionCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * PermissionJpaEntityMapper 테스트
 *
 * <p>Domain ↔ Entity 변환 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("PermissionJpaEntityMapper 테스트")
class PermissionJpaEntityMapperTest {

    private PermissionJpaEntityMapper mapper;

    private static final Long ID = 1L;
    private static final String CODE = "user:read";
    private static final String DESCRIPTION = "Read user information";

    @BeforeEach
    void setUp() {
        mapper = new PermissionJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity() 메서드는")
    class ToEntityMethod {

        @Test
        @DisplayName("기존 Permission을 Entity로 변환한다")
        void shouldConvertExistingPermissionToEntity() {
            // Given
            Permission permission =
                    Permission.reconstitute(
                            PermissionId.of(ID), PermissionCode.of(CODE), DESCRIPTION);

            // When
            PermissionJpaEntity entity = mapper.toEntity(permission);

            // Then
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getCode()).isEqualTo(CODE);
            assertThat(entity.getDescription()).isEqualTo(DESCRIPTION);
        }

        @Test
        @DisplayName("설명 없는 Permission을 Entity로 변환한다 (null → 빈 문자열)")
        void shouldConvertPermissionWithoutDescriptionToEntity() {
            // Given - Domain에서 null description은 빈 문자열로 정규화됨
            Permission permission =
                    Permission.reconstitute(PermissionId.of(ID), PermissionCode.of(CODE), null);

            // When
            PermissionJpaEntity entity = mapper.toEntity(permission);

            // Then - Domain이 null을 빈 문자열로 변환하므로 Entity도 빈 문자열
            assertThat(entity.getDescription()).isEmpty();
        }

        @Test
        @DisplayName("와일드카드 Permission을 Entity로 변환한다")
        void shouldConvertWildcardPermissionToEntity() {
            // Given
            Permission permission =
                    Permission.reconstitute(
                            PermissionId.of(ID),
                            PermissionCode.of("user:*"),
                            "All user permissions");

            // When
            PermissionJpaEntity entity = mapper.toEntity(permission);

            // Then
            assertThat(entity.getCode()).isEqualTo("user:*");
        }
    }

    @Nested
    @DisplayName("toDomain() 메서드는")
    class ToDomainMethod {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void shouldConvertEntityToDomain() {
            // Given
            PermissionJpaEntity entity = PermissionJpaEntity.of(ID, CODE, DESCRIPTION);

            // When
            Permission permission = mapper.toDomain(entity);

            // Then
            assertThat(permission.permissionIdValue()).isEqualTo(ID);
            assertThat(permission.codeValue()).isEqualTo(CODE);
            assertThat(permission.getDescription()).isEqualTo(DESCRIPTION);
        }

        @Test
        @DisplayName("설명 없는 Entity를 Domain으로 변환한다 (null → 빈 문자열)")
        void shouldConvertEntityWithoutDescriptionToDomain() {
            // Given
            PermissionJpaEntity entity = PermissionJpaEntity.of(ID, CODE, null);

            // When
            Permission permission = mapper.toDomain(entity);

            // Then - Domain이 null description을 빈 문자열로 정규화함
            assertThat(permission.getDescription()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPermissionCode() 메서드는")
    class ToPermissionCodeMethod {

        @Test
        @DisplayName("Entity에서 PermissionCode를 추출한다")
        void shouldExtractPermissionCodeFromEntity() {
            // Given
            PermissionJpaEntity entity = PermissionJpaEntity.of(ID, CODE, DESCRIPTION);

            // When
            PermissionCode permissionCode = mapper.toPermissionCode(entity);

            // Then
            assertThat(permissionCode.value()).isEqualTo(CODE);
        }

        @Test
        @DisplayName("와일드카드 코드 Entity에서 PermissionCode를 추출한다")
        void shouldExtractWildcardPermissionCodeFromEntity() {
            // Given
            PermissionJpaEntity entity = PermissionJpaEntity.of(ID, "admin:*", "Admin permissions");

            // When
            PermissionCode permissionCode = mapper.toPermissionCode(entity);

            // Then
            assertThat(permissionCode.value()).isEqualTo("admin:*");
        }
    }
}
