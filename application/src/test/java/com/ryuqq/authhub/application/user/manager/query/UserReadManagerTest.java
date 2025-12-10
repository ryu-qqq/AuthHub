package com.ryuqq.authhub.application.user.manager.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
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
 * UserReadManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserReadManager 단위 테스트")
class UserReadManagerTest {

    @Mock private UserQueryPort queryPort;

    private UserReadManager readManager;

    @BeforeEach
    void setUp() {
        readManager = new UserReadManager(queryPort);
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetByIdTest {

        @Test
        @DisplayName("ID로 사용자를 조회한다")
        void shouldGetUserById() {
            // given
            UserId userId = UserFixture.defaultId();
            User expectedUser = UserFixture.create();
            given(queryPort.findById(userId)).willReturn(Optional.of(expectedUser));

            // when
            User result = readManager.getById(userId);

            // then
            assertThat(result).isEqualTo(expectedUser);
        }

        @Test
        @DisplayName("사용자가 없으면 UserNotFoundException을 발생시킨다")
        void shouldThrowWhenUserNotFound() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            given(queryPort.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> readManager.getById(userId))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getByTenantIdAndIdentifier 메서드")
    class GetByTenantIdAndIdentifierTest {

        @Test
        @DisplayName("테넌트 ID와 식별자로 사용자를 조회한다")
        void shouldGetUserByTenantIdAndIdentifier() {
            // given
            TenantId tenantId = UserFixture.defaultTenantId();
            String identifier = "user@example.com";
            User expectedUser = UserFixture.create();
            given(queryPort.findByTenantIdAndIdentifier(tenantId, identifier))
                    .willReturn(Optional.of(expectedUser));

            // when
            User result = readManager.getByTenantIdAndIdentifier(tenantId, identifier);

            // then
            assertThat(result).isEqualTo(expectedUser);
        }

        @Test
        @DisplayName("사용자가 없으면 UserNotFoundException을 발생시킨다")
        void shouldThrowWhenUserNotFoundByIdentifier() {
            // given
            TenantId tenantId = UserFixture.defaultTenantId();
            String identifier = "nonexistent@example.com";
            given(queryPort.findByTenantIdAndIdentifier(tenantId, identifier))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                            () -> readManager.getByTenantIdAndIdentifier(tenantId, identifier))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndOrganizationIdAndIdentifier 메서드")
    class ExistsByTenantIdAndOrganizationIdAndIdentifierTest {

        @Test
        @DisplayName("중복 식별자가 있으면 true를 반환한다")
        void shouldReturnTrueWhenExists() {
            // given
            TenantId tenantId = UserFixture.defaultTenantId();
            OrganizationId orgId = UserFixture.defaultOrganizationId();
            String identifier = "user@example.com";
            given(queryPort.existsByTenantIdAndOrganizationIdAndIdentifier(
                            tenantId, orgId, identifier))
                    .willReturn(true);

            // when
            boolean result =
                    readManager.existsByTenantIdAndOrganizationIdAndIdentifier(
                            tenantId, orgId, identifier);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("중복 식별자가 없으면 false를 반환한다")
        void shouldReturnFalseWhenNotExists() {
            // given
            TenantId tenantId = UserFixture.defaultTenantId();
            OrganizationId orgId = UserFixture.defaultOrganizationId();
            String identifier = "new@example.com";
            given(queryPort.existsByTenantIdAndOrganizationIdAndIdentifier(
                            tenantId, orgId, identifier))
                    .willReturn(false);

            // when
            boolean result =
                    readManager.existsByTenantIdAndOrganizationIdAndIdentifier(
                            tenantId, orgId, identifier);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("search 메서드")
    class SearchTest {

        @Test
        @DisplayName("검색 조건에 맞는 사용자 목록을 반환한다")
        void shouldSearchUsers() {
            // given
            SearchUsersQuery query =
                    new SearchUsersQuery(
                            UserFixture.defaultTenantUUID(),
                            UserFixture.defaultOrganizationUUID(),
                            null,
                            null,
                            0,
                            10);
            List<User> expectedUsers = List.of(UserFixture.create());
            given(queryPort.search(query)).willReturn(expectedUsers);

            // when
            List<User> result = readManager.search(query);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).isEqualTo(expectedUsers);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoResults() {
            // given
            SearchUsersQuery query =
                    new SearchUsersQuery(
                            UserFixture.defaultTenantUUID(), null, null, null, 0, 10);
            given(queryPort.search(query)).willReturn(List.of());

            // when
            List<User> result = readManager.search(query);

            // then
            assertThat(result).isEmpty();
        }
    }
}
