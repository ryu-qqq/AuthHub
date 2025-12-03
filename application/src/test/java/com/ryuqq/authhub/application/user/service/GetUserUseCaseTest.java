package com.ryuqq.authhub.application.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.assembler.UserQueryAssembler;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.port.in.GetUserUseCase;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * GetUserUseCase 단위 테스트
 *
 * <p>Query UseCase 테스트 규칙:
 *
 * <ul>
 *   <li>읽기 전용 트랜잭션
 *   <li>Manager 사용 안함 (데이터 변경 없음)
 *   <li>QueryAssembler로 Response 변환
 *   <li>도메인 예외 사용
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserUseCase 테스트")
class GetUserUseCaseTest {

    @Mock private UserQueryPort userQueryPort;

    @Mock private UserQueryAssembler userQueryAssembler;

    private GetUserUseCase getUserUseCase;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final Long TENANT_ID = 1L;
    private static final Instant FIXED_TIME = Instant.parse("2025-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        getUserUseCase = new GetUserService(userQueryPort, userQueryAssembler);
    }

    @Nested
    @DisplayName("execute() - 사용자 조회")
    class Execute {

        @Test
        @DisplayName("존재하는 사용자를 성공적으로 조회해야 한다")
        void shouldGetUserSuccessfully() {
            // Given
            User existingUser = createMockUser();
            UserResponse expectedResponse = createMockResponse();

            given(userQueryPort.findById(UserId.of(USER_ID))).willReturn(Optional.of(existingUser));
            given(userQueryAssembler.toResponse(existingUser)).willReturn(expectedResponse);

            // When
            UserResponse response = getUserUseCase.execute(USER_ID);

            // Then
            assertThat(response).isNotNull();
            assertThat(response).isEqualTo(expectedResponse);

            verify(userQueryPort).findById(UserId.of(USER_ID));
            verify(userQueryAssembler).toResponse(existingUser);
        }
    }

    @Nested
    @DisplayName("검증 실패 시나리오")
    class ValidationFailure {

        @Test
        @DisplayName("존재하지 않는 사용자 조회 시 UserNotFoundException이 발생해야 한다")
        void shouldThrowWhenUserNotFound() {
            // Given
            given(userQueryPort.findById(UserId.of(USER_ID))).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> getUserUseCase.execute(USER_ID))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    // ========== Helper Methods ==========

    private User createMockUser() {
        return mock(User.class);
    }

    private UserResponse createMockResponse() {
        return new UserResponse(
                USER_ID,
                TENANT_ID,
                1L,
                "PUBLIC",
                "ACTIVE",
                "홍길동",
                "+821012345678",
                FIXED_TIME,
                FIXED_TIME);
    }
}
