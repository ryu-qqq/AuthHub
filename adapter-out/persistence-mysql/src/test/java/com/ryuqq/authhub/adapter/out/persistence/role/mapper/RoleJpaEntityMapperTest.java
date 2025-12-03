package com.ryuqq.authhub.adapter.out.persistence.role.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.PermissionCode;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * RoleJpaEntityMapper 테스트
 *
 * <p>Domain ↔ Entity 변환 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("RoleJpaEntityMapper 테스트")
class RoleJpaEntityMapperTest {

    private RoleJpaEntityMapper mapper;

    private static final Long ID = 1L;
    private static final Long TENANT_ID = 100L;
    private static final String NAME = "ROLE_ADMIN";
    private static final String DESCRIPTION = "Administrator role";
    private static final boolean IS_SYSTEM = true;
    private static final Set<PermissionCode> PERMISSIONS =
            Set.of(PermissionCode.of("user:read"), PermissionCode.of("user:write"));

    @BeforeEach
    void setUp() {
        mapper = new RoleJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity() 메서드는")
    class ToEntityMethod {

        @Test
        @DisplayName("기존 Role을 Entity로 변환한다")
        void shouldConvertExistingRoleToEntity() {
            // Given
            Role role =
                    Role.reconstitute(
                            RoleId.of(ID),
                            TenantId.of(TENANT_ID),
                            RoleName.of(NAME),
                            DESCRIPTION,
                            IS_SYSTEM,
                            PERMISSIONS);

            // When
            RoleJpaEntity entity = mapper.toEntity(role);

            // Then
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getTenantId()).isEqualTo(TENANT_ID);
            assertThat(entity.getName()).isEqualTo(NAME);
            assertThat(entity.getDescription()).isEqualTo(DESCRIPTION);
            assertThat(entity.isSystem()).isEqualTo(IS_SYSTEM);
        }

        @Test
        @DisplayName("TenantId가 null인 Role을 Entity로 변환한다 (시스템 전역 역할)")
        void shouldConvertRoleWithNullTenantToEntity() {
            // Given
            Role role =
                    Role.reconstitute(
                            RoleId.of(ID),
                            null,
                            RoleName.of(NAME),
                            DESCRIPTION,
                            IS_SYSTEM,
                            PERMISSIONS);

            // When
            RoleJpaEntity entity = mapper.toEntity(role);

            // Then
            assertThat(entity.getTenantId()).isNull();
        }

        @Test
        @DisplayName("신규 Role은 ID가 null인 Entity로 변환한다")
        void shouldConvertNewRoleToEntityWithNullId() {
            // Given
            Role role =
                    Role.of(
                            null,
                            TenantId.of(TENANT_ID),
                            RoleName.of(NAME),
                            DESCRIPTION,
                            IS_SYSTEM,
                            PERMISSIONS);

            // When
            RoleJpaEntity entity = mapper.toEntity(role);

            // Then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getName()).isEqualTo(NAME);
        }
    }

    @Nested
    @DisplayName("toDomain() 메서드는")
    class ToDomainMethod {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void shouldConvertEntityToDomain() {
            // Given
            RoleJpaEntity entity = RoleJpaEntity.of(ID, TENANT_ID, NAME, DESCRIPTION, IS_SYSTEM);

            // When
            Role role = mapper.toDomain(entity, PERMISSIONS);

            // Then
            assertThat(role.roleIdValue()).isEqualTo(ID);
            assertThat(role.tenantIdValue()).isEqualTo(TENANT_ID);
            assertThat(role.nameValue()).isEqualTo(NAME);
            assertThat(role.getDescription()).isEqualTo(DESCRIPTION);
            assertThat(role.isSystem()).isEqualTo(IS_SYSTEM);
            assertThat(role.getPermissions()).isEqualTo(PERMISSIONS);
        }

        @Test
        @DisplayName("TenantId가 null인 Entity를 Domain으로 변환한다")
        void shouldConvertEntityWithNullTenantToDomain() {
            // Given
            RoleJpaEntity entity = RoleJpaEntity.of(ID, null, NAME, DESCRIPTION, IS_SYSTEM);

            // When
            Role role = mapper.toDomain(entity, PERMISSIONS);

            // Then
            assertThat(role.getTenantId()).isNull();
        }

        @Test
        @DisplayName("빈 permissions로 Domain을 생성한다")
        void shouldConvertEntityWithEmptyPermissionsToDomain() {
            // Given
            RoleJpaEntity entity = RoleJpaEntity.of(ID, TENANT_ID, NAME, DESCRIPTION, IS_SYSTEM);

            // When
            Role role = mapper.toDomain(entity, Set.of());

            // Then
            assertThat(role.getPermissions()).isEmpty();
        }
    }
}
