package com.ryuqq.authhub.domain.user.identifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.user.identifier.fixture.UserIdFixture;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserId VO 테스트")
class UserIdTest {

    @Test
    @DisplayName("유효한 UUID로 UserId 생성 성공")
    void shouldCreateUserIdWithValidUUID() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        UserId userId = UserIdFixture.aUserId(uuid);

        // Then
        assertThat(userId).isNotNull();
        assertThat(userId.value()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("null UUID로 UserId 생성 시 예외 발생")
    void shouldThrowExceptionWhenNullUUID() {
        // Given
        UUID nullUuid = null;

        // When & Then
        assertThatThrownBy(() -> new UserId(nullUuid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UserId는 null일 수 없습니다");
    }

    @Test
    @DisplayName("[forNew] 새로운 UUID가 자동 생성된 UserId 반환")
    void forNew_shouldCreateUserIdWithRandomUUID() {
        // When
        UserId userId1 = UserId.forNew();
        UserId userId2 = UserId.forNew();

        // Then
        assertThat(userId1).isNotNull();
        assertThat(userId1.value()).isNotNull();
        assertThat(userId2).isNotNull();
        assertThat(userId2.value()).isNotNull();
        assertThat(userId1.value()).isNotEqualTo(userId2.value());
    }

    @Test
    @DisplayName("[of] 정적 팩토리 메서드로 UserId 생성 성공")
    void of_shouldCreateUserIdWithGivenUUID() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        UserId userId = UserId.of(uuid);

        // Then
        assertThat(userId).isNotNull();
        assertThat(userId.value()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("[equals/hashCode] 동일한 UUID를 가진 UserId는 동등함")
    void equals_shouldReturnTrueForSameUUID() {
        // Given
        UUID uuid = UUID.randomUUID();
        UserId userId1 = UserId.of(uuid);
        UserId userId2 = UserId.of(uuid);

        // Then
        assertThat(userId1).isEqualTo(userId2);
        assertThat(userId1.hashCode()).isEqualTo(userId2.hashCode());
    }

    @Test
    @DisplayName("[equals/hashCode] 다른 UUID를 가진 UserId는 동등하지 않음")
    void equals_shouldReturnFalseForDifferentUUID() {
        // Given
        UserId userId1 = UserId.forNew();
        UserId userId2 = UserId.forNew();

        // Then
        assertThat(userId1).isNotEqualTo(userId2);
    }
}
