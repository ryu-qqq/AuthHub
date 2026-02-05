package com.ryuqq.authhub.domain.userrole.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.fixture.UserRoleFixture;
import com.ryuqq.authhub.domain.userrole.id.UserRoleId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserRole Aggregate 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UserRole Aggregate 테스트")
class UserRoleTest {

    private static final Instant NOW = Instant.parse("2025-01-15T10:00:00Z");

    @Nested
    @DisplayName("UserRole 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("새로운 UserRole을 성공적으로 생성한다")
        void shouldCreateUserRoleSuccessfully() {
            // given
            UserId userId = UserId.of("01941234-5678-7000-8000-123456789999");
            RoleId roleId = RoleId.of(1L);

            // when
            UserRole userRole = UserRole.create(userId, roleId, NOW);

            // then
            assertThat(userRole.userIdValue()).isEqualTo(userId.value());
            assertThat(userRole.roleIdValue()).isEqualTo(roleId.value());
            assertThat(userRole.isNew()).isTrue();
            assertThat(userRole.createdAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("UserRole Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("isNew는 ID가 없을 때 true를 반환한다")
        void isNewShouldReturnTrueWhenIdIsNull() {
            // given
            UserRole newUserRole = UserRoleFixture.createNew();
            UserRole existingUserRole = UserRoleFixture.create();

            // then
            assertThat(newUserRole.isNew()).isTrue();
            assertThat(existingUserRole.isNew()).isFalse();
        }

        @Test
        @DisplayName("userIdValue는 사용자 ID 문자열을 반환한다")
        void userIdValueShouldReturnUserIdString() {
            // given
            UserRole userRole = UserRoleFixture.create();

            // then
            assertThat(userRole.userIdValue()).isEqualTo(UserRoleFixture.defaultUserIdString());
        }

        @Test
        @DisplayName("roleIdValue는 역할 ID Long 값을 반환한다")
        void roleIdValueShouldReturnRoleIdLong() {
            // given
            UserRole userRole = UserRoleFixture.create();

            // then
            assertThat(userRole.roleIdValue()).isEqualTo(UserRoleFixture.defaultRoleIdValue());
        }

        @Test
        @DisplayName("userRoleIdValue는 ID가 있으면 Long 값을 반환한다")
        void userRoleIdValueShouldReturnLongWhenIdExists() {
            // given
            UserRole userRole = UserRoleFixture.create();

            // then
            assertThat(userRole.userRoleIdValue()).isEqualTo(UserRoleFixture.defaultIdValue());
        }

        @Test
        @DisplayName("userRoleIdValue는 ID가 없으면 null을 반환한다")
        void userRoleIdValueShouldReturnNullWhenIdIsNull() {
            // given
            UserRole newUserRole = UserRoleFixture.createNew();

            // then
            assertThat(newUserRole.userRoleIdValue()).isNull();
        }

        @Test
        @DisplayName("createdAt은 생성 시간을 반환한다")
        void createdAtShouldReturnCreationTime() {
            // given
            UserRole userRole = UserRoleFixture.create();

            // then
            assertThat(userRole.createdAt()).isEqualTo(UserRoleFixture.fixedTime());
        }
    }

    @Nested
    @DisplayName("UserRole reconstitute 테스트")
    class ReconstituteTests {

        @Test
        @DisplayName("reconstitute()로 기존 UserRole을 재구성한다")
        void shouldReconstituteExistingUserRole() {
            // given
            UserRoleId userRoleId = UserRoleFixture.defaultId();
            UserId userId = UserRoleFixture.defaultUserId();
            RoleId roleId = UserRoleFixture.defaultRoleId();
            Instant createdAt = UserRoleFixture.fixedTime();

            // when
            UserRole userRole = UserRole.reconstitute(userRoleId, userId, roleId, createdAt);

            // then
            assertThat(userRole.getUserRoleId()).isEqualTo(userRoleId);
            assertThat(userRole.getUserId()).isEqualTo(userId);
            assertThat(userRole.getRoleId()).isEqualTo(roleId);
            assertThat(userRole.createdAt()).isEqualTo(createdAt);
            assertThat(userRole.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("UserRole toString 테스트")
    class ToStringTests {

        @Test
        @DisplayName("toString()은 UserRole 정보를 문자열로 반환한다")
        void toStringShouldReturnUserRoleInfo() {
            // given
            UserRole userRole = UserRoleFixture.create();

            // when
            String result = userRole.toString();

            // then
            assertThat(result).contains("UserRole{");
            assertThat(result).contains("userRoleId=");
            assertThat(result).contains("userId=");
            assertThat(result).contains("roleId=");
        }

        @Test
        @DisplayName("toString()은 ID가 없는 경우에도 정상 동작한다")
        void toStringShouldWorkWhenIdIsNull() {
            // given
            UserRole newUserRole = UserRoleFixture.createNew();

            // when
            String result = newUserRole.toString();

            // then
            assertThat(result).contains("UserRole{");
            assertThat(result).contains("userRoleId=null");
        }
    }

    @Nested
    @DisplayName("UserRole equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 userRoleId를 가진 UserRole은 동등하다")
        void shouldBeEqualWhenSameUserRoleId() {
            // given
            UserRole userRole1 = UserRoleFixture.create();
            UserRole userRole2 = UserRoleFixture.create();

            // then
            assertThat(userRole1).isEqualTo(userRole2);
            assertThat(userRole1.hashCode()).isEqualTo(userRole2.hashCode());
        }

        @Test
        @DisplayName("ID가 없는 경우 userId와 roleId로 동등성을 판단한다")
        void shouldUseUserIdAndRoleIdWhenIdIsNull() {
            // given
            UserRole newUserRole1 =
                    UserRoleFixture.createNewWithUserAndRole(
                            "01941234-5678-7000-8000-123456789abc", 1L);
            UserRole newUserRole2 =
                    UserRoleFixture.createNewWithUserAndRole(
                            "01941234-5678-7000-8000-123456789abc", 1L);

            // then - 같은 userId + roleId이므로 동등함
            assertThat(newUserRole1).isEqualTo(newUserRole2);
        }

        @Test
        @DisplayName("userId가 다르면 동등하지 않다 (ID가 없는 경우)")
        void shouldNotBeEqualWhenDifferentUserId() {
            // given
            UserRole userRole1 =
                    UserRoleFixture.createNewWithUserAndRole(
                            "01941234-5678-7000-8000-123456789abc", 1L);
            UserRole userRole2 =
                    UserRoleFixture.createNewWithUserAndRole(
                            "01941234-5678-7000-8000-123456789def", 1L);

            // then
            assertThat(userRole1).isNotEqualTo(userRole2);
        }

        @Test
        @DisplayName("roleId가 다르면 동등하지 않다 (ID가 없는 경우)")
        void shouldNotBeEqualWhenDifferentRoleId() {
            // given
            UserRole userRole1 =
                    UserRoleFixture.createNewWithUserAndRole(
                            "01941234-5678-7000-8000-123456789abc", 1L);
            UserRole userRole2 =
                    UserRoleFixture.createNewWithUserAndRole(
                            "01941234-5678-7000-8000-123456789abc", 2L);

            // then
            assertThat(userRole1).isNotEqualTo(userRole2);
        }
    }
}
