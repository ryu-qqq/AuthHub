package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserQueryDslRepository;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
 * UserQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryAdapter 단위 테스트")
class UserQueryAdapterTest {

    @Mock private UserQueryDslRepository repository;

    @Mock private UserJpaEntityMapper mapper;

    private UserQueryAdapter adapter;

    private static final UUID USER_UUID = UserFixture.defaultUUID();
    private static final UUID TENANT_UUID = UserFixture.defaultTenantUUID();
    private static final UUID ORG_UUID = UserFixture.defaultOrganizationUUID();
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @BeforeEach
    void setUp() {
        adapter = new UserQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 사용자를 성공적으로 조회한다")
        void shouldFindUserByIdSuccessfully() {
            // given
            UserId userId = UserId.of(USER_UUID);
            User expectedUser = UserFixture.create();
            UserJpaEntity entity = createUserEntity();

            given(repository.findByUserId(USER_UUID)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedUser);

            // when
            Optional<User> result = adapter.findById(userId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedUser);
            verify(repository).findByUserId(USER_UUID);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenUserNotFound() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());

            given(repository.findByUserId(userId.value())).willReturn(Optional.empty());

            // when
            Optional<User> result = adapter.findById(userId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByTenantIdAndIdentifier 메서드")
    class FindByTenantIdAndIdentifierTest {

        @Test
        @DisplayName("테넌트 ID와 식별자로 사용자를 조회한다")
        void shouldFindUserByTenantIdAndIdentifier() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            String identifier = "user@example.com";
            User expectedUser = UserFixture.create();
            UserJpaEntity entity = createUserEntity();

            given(repository.findByTenantIdAndIdentifier(TENANT_UUID, identifier))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedUser);

            // when
            Optional<User> result = adapter.findByTenantIdAndIdentifier(tenantId, identifier);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedUser);
        }

        @Test
        @DisplayName("존재하지 않는 식별자로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenIdentifierNotFound() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            String identifier = "nonexistent@example.com";

            given(repository.findByTenantIdAndIdentifier(TENANT_UUID, identifier))
                    .willReturn(Optional.empty());

            // when
            Optional<User> result = adapter.findByTenantIdAndIdentifier(tenantId, identifier);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndOrganizationIdAndIdentifier 메서드")
    class ExistsByTenantIdAndOrganizationIdAndIdentifierTest {

        @Test
        @DisplayName("식별자가 존재하면 true를 반환한다")
        void shouldReturnTrueWhenIdentifierExists() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            OrganizationId organizationId = OrganizationId.of(ORG_UUID);
            String identifier = "user@example.com";

            given(
                            repository.existsByTenantIdAndOrganizationIdAndIdentifier(
                                    TENANT_UUID, ORG_UUID, identifier))
                    .willReturn(true);

            // when
            boolean result =
                    adapter.existsByTenantIdAndOrganizationIdAndIdentifier(
                            tenantId, organizationId, identifier);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("식별자가 존재하지 않으면 false를 반환한다")
        void shouldReturnFalseWhenIdentifierNotExists() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            OrganizationId organizationId = OrganizationId.of(ORG_UUID);
            String identifier = "nonexistent@example.com";

            given(
                            repository.existsByTenantIdAndOrganizationIdAndIdentifier(
                                    TENANT_UUID, ORG_UUID, identifier))
                    .willReturn(false);

            // when
            boolean result =
                    adapter.existsByTenantIdAndOrganizationIdAndIdentifier(
                            tenantId, organizationId, identifier);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("search 메서드")
    class SearchTest {

        @Test
        @DisplayName("검색 조건으로 사용자 목록을 조회한다")
        void shouldSearchUsersSuccessfully() {
            // given
            SearchUsersQuery query = SearchUsersQuery.of(TENANT_UUID, ORG_UUID, null, null, 0, 20);
            User user1 = UserFixture.create();
            User user2 = UserFixture.createWithIdentifier("user2@example.com");
            UserJpaEntity entity1 = createUserEntity();
            UserJpaEntity entity2 = createUserEntity();

            given(
                            repository.search(
                                    eq(TENANT_UUID),
                                    eq(ORG_UUID),
                                    any(),
                                    any(),
                                    anyInt(),
                                    anyInt()))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(any(UserJpaEntity.class))).willReturn(user1, user2);

            // when
            List<User> results = adapter.search(query);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoResults() {
            // given
            SearchUsersQuery query =
                    SearchUsersQuery.of(TENANT_UUID, ORG_UUID, "nonexistent", null, 0, 20);

            given(
                            repository.search(
                                    eq(TENANT_UUID),
                                    eq(ORG_UUID),
                                    any(),
                                    any(),
                                    anyInt(),
                                    anyInt()))
                    .willReturn(List.of());

            // when
            List<User> results = adapter.search(query);

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("상태 필터로 사용자를 검색한다")
        void shouldSearchUsersWithStatusFilter() {
            // given
            SearchUsersQuery query =
                    SearchUsersQuery.of(TENANT_UUID, ORG_UUID, null, "ACTIVE", 0, 20);
            User user = UserFixture.create();
            UserJpaEntity entity = createUserEntity();

            given(
                            repository.search(
                                    eq(TENANT_UUID),
                                    eq(ORG_UUID),
                                    any(),
                                    eq(UserStatus.ACTIVE),
                                    anyInt(),
                                    anyInt()))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(any(UserJpaEntity.class))).willReturn(user);

            // when
            List<User> results = adapter.search(query);

            // then
            assertThat(results).hasSize(1);
        }
    }

    private UserJpaEntity createUserEntity() {
        return UserJpaEntity.of(
                1L,
                USER_UUID,
                TENANT_UUID,
                ORG_UUID,
                "user@example.com",
                "hashed_password",
                UserStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }
}
