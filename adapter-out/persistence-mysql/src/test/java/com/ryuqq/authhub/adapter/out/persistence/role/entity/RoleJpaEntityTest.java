package com.ryuqq.authhub.adapter.out.persistence.role.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.role.vo.RoleType;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleJpaEntity 단위 테스트
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
@DisplayName("RoleJpaEntity 단위 테스트")
class RoleJpaEntityTest {

    private static final Long ROLE_ID = 1L;
    private static final String TENANT_ID = "tenant-uuid-123";
    private static final String NAME = "TEST_ROLE";
    private static final String DISPLAY_NAME = "테스트 역할";
    private static final String DESCRIPTION = "테스트용 역할입니다";
    private static final RoleType TYPE = RoleType.CUSTOM;
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T00:00:00Z");

    @Nested
    @DisplayName("of() 팩토리 메서드")
    class OfFactoryMethod {

        @Test
        @DisplayName("성공: 모든 필드가 올바르게 설정됨")
        void shouldSetAllFields_Correctly() {
            // when
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            ROLE_ID,
                            TENANT_ID,
                            NAME,
                            DISPLAY_NAME,
                            DESCRIPTION,
                            TYPE,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getRoleId()).isEqualTo(ROLE_ID);
            assertThat(entity.getTenantId()).isEqualTo(TENANT_ID);
            assertThat(entity.getName()).isEqualTo(NAME);
            assertThat(entity.getDisplayName()).isEqualTo(DISPLAY_NAME);
            assertThat(entity.getDescription()).isEqualTo(DESCRIPTION);
            assertThat(entity.getType()).isEqualTo(TYPE);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("deletedAt이 null이면 활성 상태")
        void shouldBeActive_WhenDeletedAtIsNull() {
            // when
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            ROLE_ID,
                            TENANT_ID,
                            NAME,
                            DISPLAY_NAME,
                            DESCRIPTION,
                            TYPE,
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
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            ROLE_ID,
                            TENANT_ID,
                            NAME,
                            DISPLAY_NAME,
                            DESCRIPTION,
                            TYPE,
                            CREATED_AT,
                            UPDATED_AT,
                            deletedAt);

            // then
            assertThat(entity.isDeleted()).isTrue();
            assertThat(entity.isActive()).isFalse();
            assertThat(entity.getDeletedAt()).isEqualTo(deletedAt);
        }

        @Test
        @DisplayName("tenantId가 null인 경우도 허용 (Global 역할)")
        void shouldAllowNullTenantId() {
            // when
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            ROLE_ID,
                            null,
                            NAME,
                            DISPLAY_NAME,
                            DESCRIPTION,
                            TYPE,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getTenantId()).isNull();
        }
    }

    @Nested
    @DisplayName("역할 유형 관련 메서드")
    class RoleTypeMethods {

        @Test
        @DisplayName("SYSTEM 유형이 올바르게 설정됨")
        void shouldSetSystemType() {
            // when
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            ROLE_ID,
                            null,
                            NAME,
                            DISPLAY_NAME,
                            DESCRIPTION,
                            RoleType.SYSTEM,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getType()).isEqualTo(RoleType.SYSTEM);
        }

        @Test
        @DisplayName("CUSTOM 유형이 올바르게 설정됨")
        void shouldSetCustomType() {
            // when
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            ROLE_ID,
                            null,
                            NAME,
                            DISPLAY_NAME,
                            DESCRIPTION,
                            RoleType.CUSTOM,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getType()).isEqualTo(RoleType.CUSTOM);
        }
    }

    @Nested
    @DisplayName("감사 필드 (BaseAuditEntity 상속)")
    class AuditFields {

        @Test
        @DisplayName("createdAt이 올바르게 설정됨")
        void shouldSetCreatedAt() {
            // when
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            ROLE_ID,
                            TENANT_ID,
                            NAME,
                            DISPLAY_NAME,
                            DESCRIPTION,
                            TYPE,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
        }

        @Test
        @DisplayName("updatedAt이 올바르게 설정됨")
        void shouldSetUpdatedAt() {
            // when
            RoleJpaEntity entity =
                    RoleJpaEntity.of(
                            ROLE_ID,
                            TENANT_ID,
                            NAME,
                            DISPLAY_NAME,
                            DESCRIPTION,
                            TYPE,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
        }
    }
}
