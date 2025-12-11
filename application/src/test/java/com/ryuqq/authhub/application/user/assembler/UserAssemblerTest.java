package com.ryuqq.authhub.application.user.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserAssembler 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserAssembler 단위 테스트")
class UserAssemblerTest {

    private UserAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new UserAssembler();
    }

    @Nested
    @DisplayName("toResponse 메서드")
    class ToResponseTest {

        @Test
        @DisplayName("User를 UserResponse로 변환한다")
        void shouldConvertToResponse() {
            // given
            User user = UserFixture.create();

            // when
            UserResponse result = assembler.toResponse(user);

            // then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(user.userIdValue());
            assertThat(result.tenantId()).isEqualTo(user.tenantIdValue());
            assertThat(result.organizationId()).isEqualTo(user.organizationIdValue());
            assertThat(result.identifier()).isEqualTo(user.getIdentifier());
            assertThat(result.status()).isEqualTo(user.getUserStatus().name());
            assertThat(result.createdAt()).isEqualTo(user.getCreatedAt());
            assertThat(result.updatedAt()).isEqualTo(user.getUpdatedAt());
        }

        @Test
        @DisplayName("비활성 사용자를 UserResponse로 변환한다")
        void shouldConvertInactiveUserToResponse() {
            // given
            User inactiveUser = UserFixture.createWithStatus(UserStatus.INACTIVE);

            // when
            UserResponse result = assembler.toResponse(inactiveUser);

            // then
            assertThat(result.status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("다양한 상태의 사용자를 올바르게 변환한다")
        void shouldConvertUserWithVariousStatus() {
            // given
            User lockedUser = UserFixture.createWithStatus(UserStatus.LOCKED);

            // when
            UserResponse result = assembler.toResponse(lockedUser);

            // then
            assertThat(result.status()).isEqualTo("LOCKED");
        }
    }

    @Nested
    @DisplayName("toResponseList 메서드")
    class ToResponseListTest {

        @Test
        @DisplayName("User 목록을 UserResponse 목록으로 변환한다")
        void shouldConvertToResponseList() {
            // given
            User user1 = UserFixture.create();
            User user2 = UserFixture.createWithIdentifier("another@example.com");
            List<User> users = List.of(user1, user2);

            // when
            List<UserResponse> result = assembler.toResponseList(users);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).userId()).isEqualTo(user1.userIdValue());
            assertThat(result.get(1).identifier()).isEqualTo("another@example.com");
        }

        @Test
        @DisplayName("빈 목록은 빈 목록을 반환한다")
        void shouldReturnEmptyListForEmptyInput() {
            // when
            List<UserResponse> result = assembler.toResponseList(List.of());

            // then
            assertThat(result).isEmpty();
        }
    }
}
