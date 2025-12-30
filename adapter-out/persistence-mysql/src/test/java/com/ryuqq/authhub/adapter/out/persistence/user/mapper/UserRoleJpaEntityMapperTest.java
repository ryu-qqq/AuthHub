package com.ryuqq.authhub.adapter.out.persistence.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserRoleJpaEntityMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleJpaEntityMapper 단위 테스트")
class UserRoleJpaEntityMapperTest {

    @Mock private UuidHolder uuidHolder;

    private UserRoleJpaEntityMapper mapper;

    private static final UUID USER_ROLE_UUID =
            UUID.fromString("01941234-5678-7000-8000-123456789000");
    private static final UUID USER_UUID = UUID.fromString("01941234-5678-7000-8000-000000000001");
    private static final UUID ROLE_UUID = UUID.fromString("01941234-5678-7000-8000-123456789111");
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        mapper = new UserRoleJpaEntityMapper(uuidHolder);
    }

    @Nested
    @DisplayName("toEntity 메서드")
    class ToEntityTest {

        @Test
        @DisplayName("Domain을 Entity로 변환한다")
        void shouldConvertDomainToEntity() {
            // given
            UserRole domain =
                    UserRole.reconstitute(
                            UserId.of(USER_UUID), RoleId.of(ROLE_UUID), FIXED_INSTANT);

            given(uuidHolder.random()).willReturn(USER_ROLE_UUID);

            // when
            UserRoleJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getUserRoleId()).isEqualTo(USER_ROLE_UUID);
            assertThat(entity.getUserId()).isEqualTo(USER_UUID);
            assertThat(entity.getRoleId()).isEqualTo(ROLE_UUID);
            assertThat(entity.getAssignedAt()).isEqualTo(FIXED_INSTANT);
        }

        @Test
        @DisplayName("다른 역할도 Entity로 변환된다")
        void shouldConvertAnotherRoleToEntity() {
            // given
            UUID anotherRoleId = UUID.fromString("01941234-5678-7000-8000-123456789222");
            UUID generatedUuid = UUID.randomUUID();
            UserRole domain =
                    UserRole.reconstitute(
                            UserId.of(USER_UUID), RoleId.of(anotherRoleId), Instant.now());

            given(uuidHolder.random()).willReturn(generatedUuid);

            // when
            UserRoleJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getUserRoleId()).isEqualTo(generatedUuid);
            assertThat(entity.getUserId()).isEqualTo(USER_UUID);
            assertThat(entity.getRoleId()).isEqualTo(anotherRoleId);
            assertThat(entity.getAssignedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("toDomain 메서드")
    class ToDomainTest {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void shouldConvertEntityToDomain() {
            // given
            UserRoleJpaEntity entity =
                    UserRoleJpaEntity.of(
                            UUID.fromString("01941234-5678-7000-8000-123456789999"),
                            USER_UUID,
                            ROLE_UUID,
                            FIXED_INSTANT);

            // when
            UserRole domain = mapper.toDomain(entity);

            // then
            assertThat(domain.userIdValue()).isEqualTo(USER_UUID);
            assertThat(domain.roleIdValue()).isEqualTo(ROLE_UUID);
            assertThat(domain.getAssignedAt()).isEqualTo(FIXED_INSTANT);
        }
    }

    @Nested
    @DisplayName("양방향 변환")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain → Entity → Domain 변환이 일관성을 유지한다")
        void shouldMaintainConsistencyInBidirectionalConversion() {
            // given
            UserRole originalDomain =
                    UserRole.reconstitute(
                            UserId.of(USER_UUID), RoleId.of(ROLE_UUID), FIXED_INSTANT);

            given(uuidHolder.random()).willReturn(USER_ROLE_UUID);

            // when
            UserRoleJpaEntity entity = mapper.toEntity(originalDomain);
            UserRoleJpaEntity entityWithId =
                    UserRoleJpaEntity.of(
                            entity.getUserRoleId(),
                            entity.getUserId(),
                            entity.getRoleId(),
                            entity.getAssignedAt());
            UserRole convertedDomain = mapper.toDomain(entityWithId);

            // then
            assertThat(convertedDomain.userIdValue()).isEqualTo(originalDomain.userIdValue());
            assertThat(convertedDomain.roleIdValue()).isEqualTo(originalDomain.roleIdValue());
            assertThat(convertedDomain.getAssignedAt()).isEqualTo(originalDomain.getAssignedAt());
        }
    }
}
