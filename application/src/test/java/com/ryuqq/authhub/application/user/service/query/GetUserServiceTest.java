package com.ryuqq.authhub.application.user.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.query.GetUserQuery;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
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
 * GetUserService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserService 단위 테스트")
class GetUserServiceTest {

    @Mock private UserReadManager readManager;

    @Mock private UserAssembler assembler;

    private GetUserService service;

    @BeforeEach
    void setUp() {
        service = new GetUserService(readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("사용자를 성공적으로 조회한다")
        void shouldGetUserSuccessfully() {
            // given
            User existingUser = UserFixture.create();
            GetUserQuery query = new GetUserQuery(existingUser.userIdValue());
            UserResponse expectedResponse =
                    new UserResponse(
                            existingUser.userIdValue(),
                            existingUser.tenantIdValue(),
                            existingUser.organizationIdValue(),
                            existingUser.getIdentifier(),
                            existingUser.getPhoneNumber(),
                            existingUser.getUserStatus().name(),
                            existingUser.getCreatedAt(),
                            existingUser.getUpdatedAt());

            given(readManager.getById(UserId.of(query.userId()))).willReturn(existingUser);
            given(assembler.toResponse(existingUser)).willReturn(expectedResponse);

            // when
            UserResponse response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(readManager).getById(UserId.of(query.userId()));
            verify(assembler).toResponse(existingUser);
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외를 발생시킨다")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            UUID nonExistingUserId = UUID.randomUUID();
            GetUserQuery query = new GetUserQuery(nonExistingUserId);

            given(readManager.getById(UserId.of(query.userId())))
                    .willThrow(new UserNotFoundException(nonExistingUserId));

            // when & then
            assertThatThrownBy(() -> service.execute(query))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}
