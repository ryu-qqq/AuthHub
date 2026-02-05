package com.ryuqq.authhub.application.user.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.response.UserResult;
import com.ryuqq.authhub.application.user.manager.UserReadManager;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.id.UserId;
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

    private GetUserService sut;

    @BeforeEach
    void setUp() {
        sut = new GetUserService(readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: ReadManager로 조회 후 Assembler로 Result 반환")
        void shouldReturnResult_WhenUserExists() {
            // given
            String userId = UserFixture.defaultIdString();
            User user = UserFixture.create();
            UserResult expectedResult =
                    new UserResult(
                            user.userIdValue(),
                            user.organizationIdValue(),
                            user.identifierValue(),
                            user.phoneNumberValue(),
                            user.statusValue(),
                            user.createdAt(),
                            user.updatedAt());

            given(readManager.findById(UserId.of(userId))).willReturn(user);
            given(assembler.toResult(user)).willReturn(expectedResult);

            // when
            UserResult result = sut.execute(userId);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(readManager).should().findById(UserId.of(userId));
            then(assembler).should().toResult(user);
        }

        @Test
        @DisplayName("실패: 사용자 미존재 시 UserNotFoundException 발생")
        void shouldThrowException_WhenUserNotFound() {
            // given
            String userId = UserFixture.defaultIdString();
            given(readManager.findById(UserId.of(userId)))
                    .willThrow(new UserNotFoundException(UserFixture.defaultId()));

            // when & then
            assertThatThrownBy(() -> sut.execute(userId)).isInstanceOf(UserNotFoundException.class);
            then(assembler).shouldHaveNoInteractions();
        }
    }
}
