package com.ryuqq.authhub.adapter.in.rest.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.fixture.UserApiFixture;
import com.ryuqq.authhub.application.user.dto.response.UserResult;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserQueryApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UserQueryApiMapper 단위 테스트")
class UserQueryApiMapperTest {

    private UserQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserQueryApiMapper();
    }

    @Nested
    @DisplayName("toApiResponse(UserResult) 메서드는")
    class ToApiResponse {

        @Test
        @DisplayName("UserResult를 UserApiResponse로 변환한다")
        void shouldConvertToUserApiResponse() {
            // Given
            Instant fixedTime = UserApiFixture.fixedTime();
            UserResult result =
                    new UserResult(
                            UserApiFixture.defaultUserId(),
                            UserApiFixture.defaultOrganizationId(),
                            UserApiFixture.defaultIdentifier(),
                            "010-1234-5678",
                            "ACTIVE",
                            fixedTime,
                            fixedTime);

            // When
            UserApiResponse response = mapper.toApiResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(UserApiFixture.defaultUserId());
            assertThat(response.organizationId()).isEqualTo(UserApiFixture.defaultOrganizationId());
            assertThat(response.identifier()).isEqualTo(UserApiFixture.defaultIdentifier());
            assertThat(response.phoneNumber()).isEqualTo("010-1234-5678");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.createdAt()).isEqualTo(fixedTime);
            assertThat(response.updatedAt()).isEqualTo(fixedTime);
        }

        @Test
        @DisplayName("전화번호가 null인 결과도 처리한다")
        void shouldHandleNullPhoneNumber() {
            // Given
            Instant fixedTime = UserApiFixture.fixedTime();
            UserResult result =
                    new UserResult(
                            UserApiFixture.defaultUserId(),
                            UserApiFixture.defaultOrganizationId(),
                            UserApiFixture.defaultIdentifier(),
                            null,
                            "ACTIVE",
                            fixedTime,
                            fixedTime);

            // When
            UserApiResponse response = mapper.toApiResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(UserApiFixture.defaultUserId());
            assertThat(response.phoneNumber()).isNull();
            assertThat(response.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("다양한 상태의 사용자도 처리한다")
        void shouldHandleDifferentStatuses() {
            // Given
            Instant fixedTime = UserApiFixture.fixedTime();
            UserResult inactiveResult =
                    new UserResult(
                            UserApiFixture.defaultUserId(),
                            UserApiFixture.defaultOrganizationId(),
                            UserApiFixture.defaultIdentifier(),
                            "010-1234-5678",
                            "INACTIVE",
                            fixedTime,
                            fixedTime);

            UserResult suspendedResult =
                    new UserResult(
                            UserApiFixture.defaultUserId(),
                            UserApiFixture.defaultOrganizationId(),
                            UserApiFixture.defaultIdentifier(),
                            "010-1234-5678",
                            "SUSPENDED",
                            fixedTime,
                            fixedTime);

            // When
            UserApiResponse inactiveResponse = mapper.toApiResponse(inactiveResult);
            UserApiResponse suspendedResponse = mapper.toApiResponse(suspendedResult);

            // Then
            assertThat(inactiveResponse.status()).isEqualTo("INACTIVE");
            assertThat(suspendedResponse.status()).isEqualTo("SUSPENDED");
        }
    }
}
