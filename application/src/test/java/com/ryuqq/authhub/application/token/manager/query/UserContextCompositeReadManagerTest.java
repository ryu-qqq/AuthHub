package com.ryuqq.authhub.application.token.manager.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.token.dto.composite.UserContextComposite;
import com.ryuqq.authhub.application.token.port.out.query.UserContextCompositeQueryPort;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserContextCompositeReadManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserContextCompositeReadManager 단위 테스트")
class UserContextCompositeReadManagerTest {

    @Mock private UserContextCompositeQueryPort queryPort;

    private UserContextCompositeReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new UserContextCompositeReadManager(queryPort);
    }

    @Nested
    @DisplayName("getUserContextByUserId 메서드")
    class GetUserContextByUserId {

        @Test
        @DisplayName("성공: 사용자 컨텍스트가 존재하면 해당 Composite 반환")
        void shouldReturnComposite_WhenExists() {
            // given
            UserId userId = UserId.of("019450eb-4f1e-7000-8000-000000000001");
            UserContextComposite expected =
                    UserContextComposite.builder()
                            .userId(userId.value())
                            .email("test@example.com")
                            .name("Test User")
                            .tenantId("tenant-123")
                            .tenantName("Test Tenant")
                            .organizationId("org-456")
                            .organizationName("Test Organization")
                            .build();

            given(queryPort.findUserContextByUserId(userId)).willReturn(Optional.of(expected));

            // when
            UserContextComposite result = sut.getUserContextByUserId(userId);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findUserContextByUserId(userId);
        }

        @Test
        @DisplayName("실패: 사용자 컨텍스트가 존재하지 않으면 UserNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            UserId userId = UserId.of("019450eb-4f1e-7000-8000-000000000001");

            given(queryPort.findUserContextByUserId(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getUserContextByUserId(userId))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}
