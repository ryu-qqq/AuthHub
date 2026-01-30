package com.ryuqq.authhub.adapter.out.persistence.permission.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionJpaEntity 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Entity는 데이터 컨테이너 → of() 팩토리, getter, 상태 확인 메서드 검증
 *   <li>Mock 불필요 (순수 객체 생성/조회)
 *   <li>SoftDeletableEntity 상속 기능 검증
 *   <li>Global Only 설계 (tenantId 없음)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionJpaEntity 단위 테스트")
class PermissionJpaEntityTest {

    private static final Long PERMISSION_ID = 1L;
    private static final String PERMISSION_KEY = "user:read";
    private static final String RESOURCE = "user";
    private static final String ACTION = "read";
    private static final String DESCRIPTION = "사용자 조회 권한";
    private static final PermissionType TYPE = PermissionType.CUSTOM;
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T00:00:00Z");

    @Nested
    @DisplayName("of() 팩토리 메서드")
    class OfFactoryMethod {

        @Test
        @DisplayName("성공: 모든 필드가 올바르게 설정됨")
        void shouldSetAllFields_Correctly() {
            // when
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            PERMISSION_ID,
                            PERMISSION_KEY,
                            RESOURCE,
                            ACTION,
                            DESCRIPTION,
                            TYPE,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getPermissionId()).isEqualTo(PERMISSION_ID);
            assertThat(entity.getPermissionKey()).isEqualTo(PERMISSION_KEY);
            assertThat(entity.getResource()).isEqualTo(RESOURCE);
            assertThat(entity.getAction()).isEqualTo(ACTION);
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
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            PERMISSION_ID,
                            PERMISSION_KEY,
                            RESOURCE,
                            ACTION,
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
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            PERMISSION_ID,
                            PERMISSION_KEY,
                            RESOURCE,
                            ACTION,
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
        @DisplayName("description이 null인 경우도 허용")
        void shouldAllowNullDescription() {
            // when
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            PERMISSION_ID,
                            PERMISSION_KEY,
                            RESOURCE,
                            ACTION,
                            null,
                            TYPE,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getDescription()).isNull();
        }
    }

    @Nested
    @DisplayName("권한 유형 관련 메서드")
    class PermissionTypeMethods {

        @Test
        @DisplayName("SYSTEM 유형이 올바르게 설정됨")
        void shouldSetSystemType() {
            // when
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            PERMISSION_ID,
                            PERMISSION_KEY,
                            RESOURCE,
                            ACTION,
                            DESCRIPTION,
                            PermissionType.SYSTEM,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getType()).isEqualTo(PermissionType.SYSTEM);
        }

        @Test
        @DisplayName("CUSTOM 유형이 올바르게 설정됨")
        void shouldSetCustomType() {
            // when
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            PERMISSION_ID,
                            PERMISSION_KEY,
                            RESOURCE,
                            ACTION,
                            DESCRIPTION,
                            PermissionType.CUSTOM,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getType()).isEqualTo(PermissionType.CUSTOM);
        }
    }

    @Nested
    @DisplayName("권한 키 구성 요소")
    class PermissionKeyComponents {

        @Test
        @DisplayName("resource와 action이 올바르게 분리됨")
        void shouldSeparateResourceAndAction() {
            // given
            String resource = "organization";
            String action = "manage";
            String permissionKey = resource + ":" + action;

            // when
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            PERMISSION_ID,
                            permissionKey,
                            resource,
                            action,
                            DESCRIPTION,
                            TYPE,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getPermissionKey()).isEqualTo("organization:manage");
            assertThat(entity.getResource()).isEqualTo("organization");
            assertThat(entity.getAction()).isEqualTo("manage");
        }
    }

    @Nested
    @DisplayName("감사 필드 (BaseAuditEntity 상속)")
    class AuditFields {

        @Test
        @DisplayName("createdAt이 올바르게 설정됨")
        void shouldSetCreatedAt() {
            // when
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            PERMISSION_ID,
                            PERMISSION_KEY,
                            RESOURCE,
                            ACTION,
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
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            PERMISSION_ID,
                            PERMISSION_KEY,
                            RESOURCE,
                            ACTION,
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
