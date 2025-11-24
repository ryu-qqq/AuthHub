package com.ryuqq.authhub.domain.user.vo;

import com.ryuqq.authhub.domain.user.vo.fixture.UserIdFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
}
