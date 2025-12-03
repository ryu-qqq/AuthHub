package com.ryuqq.authhub.adapter.in.rest.user.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ryuqq.authhub.application.user.dto.response.CreateUserResponse;

/**
 * CreateUserApiResponse 단위 테스트
 *
 * <p>검증 범위:
 * <ul>
 *   <li>Record 불변성 검증</li>
 *   <li>필드 접근 검증</li>
 *   <li>from() 팩토리 메서드 검증</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("dto")
@DisplayName("CreateUserApiResponse 테스트")
class CreateUserApiResponseTest {

    @Nested
    @DisplayName("Record 기본 동작 테스트")
    class RecordBasicTest {

        @Test
        @DisplayName("모든 필드로 인스턴스 생성")
        void givenAllFields_whenCreate_thenSuccess() {
            // given
            UUID userId = UUID.randomUUID();
            Instant createdAt = Instant.now();

            // when
            CreateUserApiResponse response = new CreateUserApiResponse(userId, createdAt);

            // then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.createdAt()).isEqualTo(createdAt);
        }
    }

    @Nested
    @DisplayName("from() 팩토리 메서드 테스트")
    class FromFactoryTest {

        @Test
        @DisplayName("CreateUserResponse로부터 생성")
        void givenCreateUserResponse_whenFrom_thenSuccess() {
            // given
            UUID userId = UUID.randomUUID();
            Instant createdAt = Instant.now();
            CreateUserResponse useCaseResponse = new CreateUserResponse(userId, createdAt);

            // when
            CreateUserApiResponse apiResponse = CreateUserApiResponse.from(useCaseResponse);

            // then
            assertThat(apiResponse.userId()).isEqualTo(userId);
            assertThat(apiResponse.createdAt()).isEqualTo(createdAt);
        }
    }
}
