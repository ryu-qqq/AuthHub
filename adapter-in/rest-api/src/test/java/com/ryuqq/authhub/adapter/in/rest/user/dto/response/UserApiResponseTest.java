package com.ryuqq.authhub.adapter.in.rest.user.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ryuqq.authhub.application.user.dto.response.UserResponse;

/**
 * UserApiResponse 단위 테스트
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
@DisplayName("UserApiResponse 테스트")
class UserApiResponseTest {

    @Nested
    @DisplayName("Record 기본 동작 테스트")
    class RecordBasicTest {

        @Test
        @DisplayName("모든 필드로 인스턴스 생성")
        void givenAllFields_whenCreate_thenSuccess() {
            // given
            UUID userId = UUID.randomUUID();
            Long tenantId = 1L;
            Long organizationId = 10L;
            String userType = "ADMIN";
            String status = "ACTIVE";
            String name = "홍길동";
            String phoneNumber = "010-1234-5678";
            Instant createdAt = Instant.now();
            Instant updatedAt = Instant.now();

            // when
            UserApiResponse response = new UserApiResponse(
                    userId, tenantId, organizationId, userType, status,
                    name, phoneNumber, createdAt, updatedAt);

            // then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.tenantId()).isEqualTo(tenantId);
            assertThat(response.organizationId()).isEqualTo(organizationId);
            assertThat(response.userType()).isEqualTo(userType);
            assertThat(response.status()).isEqualTo(status);
            assertThat(response.name()).isEqualTo(name);
            assertThat(response.phoneNumber()).isEqualTo(phoneNumber);
            assertThat(response.createdAt()).isEqualTo(createdAt);
            assertThat(response.updatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("from() 팩토리 메서드 테스트")
    class FromFactoryTest {

        @Test
        @DisplayName("UserResponse로부터 생성")
        void givenUserResponse_whenFrom_thenSuccess() {
            // given
            UUID userId = UUID.randomUUID();
            Long tenantId = 1L;
            Long organizationId = 10L;
            String userType = "ADMIN";
            String status = "ACTIVE";
            String name = "홍길동";
            String phoneNumber = "010-1234-5678";
            Instant createdAt = Instant.now();
            Instant updatedAt = Instant.now();

            UserResponse useCaseResponse = new UserResponse(
                    userId, tenantId, organizationId, userType, status,
                    name, phoneNumber, createdAt, updatedAt);

            // when
            UserApiResponse apiResponse = UserApiResponse.from(useCaseResponse);

            // then
            assertThat(apiResponse.userId()).isEqualTo(userId);
            assertThat(apiResponse.tenantId()).isEqualTo(tenantId);
            assertThat(apiResponse.organizationId()).isEqualTo(organizationId);
            assertThat(apiResponse.userType()).isEqualTo(userType);
            assertThat(apiResponse.status()).isEqualTo(status);
            assertThat(apiResponse.name()).isEqualTo(name);
            assertThat(apiResponse.phoneNumber()).isEqualTo(phoneNumber);
            assertThat(apiResponse.createdAt()).isEqualTo(createdAt);
            assertThat(apiResponse.updatedAt()).isEqualTo(updatedAt);
        }
    }
}
