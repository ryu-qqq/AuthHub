package com.ryuqq.authhub.adapter.out.persistence.permission.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionJpaEntityMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionJpaEntityMapper 단위 테스트")
class PermissionJpaEntityMapperTest {

    private PermissionJpaEntityMapper mapper;

    private static final UUID PERMISSION_UUID =
            UUID.fromString("01941234-5678-7000-8000-123456789001");
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");
    private static final LocalDateTime FIXED_LOCAL_DATE_TIME =
            LocalDateTime.ofInstant(FIXED_INSTANT, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        mapper = new PermissionJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity 메서드")
    class ToEntityTest {

        @Test
        @DisplayName("Domain을 Entity로 변환한다")
        void shouldConvertDomainToEntity() {
            // given
            Permission domain = PermissionFixture.create();

            // when
            PermissionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getPermissionKey()).isEqualTo(domain.keyValue());
            assertThat(entity.getResource()).isEqualTo(domain.resourceValue());
            assertThat(entity.getAction()).isEqualTo(domain.actionValue());
            assertThat(entity.getDescription()).isEqualTo(domain.descriptionValue());
            assertThat(entity.getType()).isEqualTo(domain.getType());
            assertThat(entity.isDeleted()).isEqualTo(domain.isDeleted());
        }

        @Test
        @DisplayName("시간 필드가 올바르게 변환된다")
        void shouldConvertTimeFieldsCorrectly() {
            // given
            Permission domain = PermissionFixture.create();

            // when
            PermissionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getCreatedAt()).isEqualTo(FIXED_LOCAL_DATE_TIME);
            assertThat(entity.getUpdatedAt()).isEqualTo(FIXED_LOCAL_DATE_TIME);
        }

        @Test
        @DisplayName("시스템 권한도 Entity로 변환된다")
        void shouldConvertSystemPermissionToEntity() {
            // given
            Permission domain = PermissionFixture.createSystem();

            // when
            PermissionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getType()).isEqualTo(PermissionType.SYSTEM);
        }
    }

    @Nested
    @DisplayName("toDomain 메서드")
    class ToDomainTest {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void shouldConvertEntityToDomain() {
            // given
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            1L,
                            PERMISSION_UUID,
                            "user:read",
                            "user",
                            "read",
                            "사용자 조회 권한",
                            PermissionType.CUSTOM,
                            false,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Permission domain = mapper.toDomain(entity);

            // then
            assertThat(domain.permissionIdValue()).isEqualTo(PERMISSION_UUID);
            assertThat(domain.keyValue()).isEqualTo("user:read");
            assertThat(domain.resourceValue()).isEqualTo("user");
            assertThat(domain.actionValue()).isEqualTo("read");
            assertThat(domain.descriptionValue()).isEqualTo("사용자 조회 권한");
            assertThat(domain.getType()).isEqualTo(PermissionType.CUSTOM);
            assertThat(domain.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("시간 필드가 올바르게 변환된다")
        void shouldConvertTimeFieldsCorrectly() {
            // given
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            1L,
                            PERMISSION_UUID,
                            "user:read",
                            "user",
                            "read",
                            "설명",
                            PermissionType.CUSTOM,
                            false,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Permission domain = mapper.toDomain(entity);

            // then
            assertThat(domain.createdAt()).isEqualTo(FIXED_INSTANT);
            assertThat(domain.updatedAt()).isEqualTo(FIXED_INSTANT);
        }

        @Test
        @DisplayName("null description은 빈 PermissionDescription으로 변환된다")
        void shouldConvertNullDescriptionToEmpty() {
            // given
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            1L,
                            PERMISSION_UUID,
                            "user:read",
                            "user",
                            "read",
                            null,
                            PermissionType.CUSTOM,
                            false,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Permission domain = mapper.toDomain(entity);

            // then - PermissionDescription.empty()는 빈 문자열("")을 가진 객체를 생성함
            assertThat(domain.descriptionValue()).isEmpty();
        }

        @Test
        @DisplayName("삭제된 권한도 올바르게 변환된다")
        void shouldConvertDeletedPermission() {
            // given
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            1L,
                            PERMISSION_UUID,
                            "deleted:permission",
                            "deleted",
                            "permission",
                            "삭제된 권한",
                            PermissionType.CUSTOM,
                            true,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Permission domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("시스템 권한 타입이 올바르게 변환된다")
        void shouldConvertSystemPermissionType() {
            // given
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(
                            1L,
                            PERMISSION_UUID,
                            "system:admin",
                            "system",
                            "admin",
                            "시스템 관리 권한",
                            PermissionType.SYSTEM,
                            false,
                            FIXED_LOCAL_DATE_TIME,
                            FIXED_LOCAL_DATE_TIME);

            // when
            Permission domain = mapper.toDomain(entity);

            // then
            assertThat(domain.getType()).isEqualTo(PermissionType.SYSTEM);
        }
    }

    @Nested
    @DisplayName("양방향 변환")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain → Entity → Domain 변환이 일관성을 유지한다")
        void shouldMaintainConsistencyInBidirectionalConversion() {
            // given
            Permission originalDomain = PermissionFixture.createReconstituted();

            // when
            PermissionJpaEntity entity = mapper.toEntity(originalDomain);
            PermissionJpaEntity entityWithId =
                    PermissionJpaEntity.of(
                            1L,
                            originalDomain.permissionIdValue(),
                            entity.getPermissionKey(),
                            entity.getResource(),
                            entity.getAction(),
                            entity.getDescription(),
                            entity.getType(),
                            entity.isDeleted(),
                            entity.getCreatedAt(),
                            entity.getUpdatedAt());
            Permission convertedDomain = mapper.toDomain(entityWithId);

            // then
            assertThat(convertedDomain.permissionIdValue())
                    .isEqualTo(originalDomain.permissionIdValue());
            assertThat(convertedDomain.keyValue()).isEqualTo(originalDomain.keyValue());
            assertThat(convertedDomain.resourceValue()).isEqualTo(originalDomain.resourceValue());
            assertThat(convertedDomain.actionValue()).isEqualTo(originalDomain.actionValue());
            assertThat(convertedDomain.getType()).isEqualTo(originalDomain.getType());
            assertThat(convertedDomain.isDeleted()).isEqualTo(originalDomain.isDeleted());
        }
    }
}
