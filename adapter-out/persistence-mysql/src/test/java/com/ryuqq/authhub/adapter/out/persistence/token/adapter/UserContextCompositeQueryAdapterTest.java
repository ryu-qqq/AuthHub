package com.ryuqq.authhub.adapter.out.persistence.token.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.token.dto.UserContextProjection;
import com.ryuqq.authhub.adapter.out.persistence.token.mapper.UserContextCompositeMapper;
import com.ryuqq.authhub.adapter.out.persistence.token.repository.UserContextCompositeQueryDslRepository;
import com.ryuqq.authhub.application.token.dto.composite.UserContextComposite;
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
 * UserContextCompositeQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserContextCompositeQueryAdapter 단위 테스트")
class UserContextCompositeQueryAdapterTest {

    @Mock private UserContextCompositeQueryDslRepository repository;
    @Mock private UserContextCompositeMapper mapper;

    private UserContextCompositeQueryAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new UserContextCompositeQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("findUserContextByUserId 메서드")
    class FindUserContextByUserId {

        @Test
        @DisplayName("성공: 사용자 컨텍스트가 존재하면 Composite로 변환하여 반환")
        void shouldReturnComposite_WhenProjectionExists() {
            // given
            UserId userId = UserId.of("019450eb-4f1e-7000-8000-000000000001");
            UserContextProjection projection =
                    new UserContextProjection(
                            userId.value(),
                            "test@example.com",
                            "Test User",
                            "tenant-123",
                            "Test Tenant",
                            "org-456",
                            "Test Organization");

            UserContextComposite expectedComposite =
                    UserContextComposite.builder()
                            .userId(userId.value())
                            .email("test@example.com")
                            .name("Test User")
                            .tenantId("tenant-123")
                            .tenantName("Test Tenant")
                            .organizationId("org-456")
                            .organizationName("Test Organization")
                            .build();

            given(repository.findUserContextByUserId(userId.value()))
                    .willReturn(Optional.of(projection));
            given(mapper.toComposite(projection)).willReturn(expectedComposite);

            // when
            Optional<UserContextComposite> result = sut.findUserContextByUserId(userId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedComposite);
            then(repository).should().findUserContextByUserId(userId.value());
            then(mapper).should().toComposite(projection);
        }

        @Test
        @DisplayName("성공: 사용자 컨텍스트가 존재하지 않으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenProjectionNotExists() {
            // given
            UserId userId = UserId.of("019450eb-4f1e-7000-8000-000000000001");

            given(repository.findUserContextByUserId(userId.value())).willReturn(Optional.empty());

            // when
            Optional<UserContextComposite> result = sut.findUserContextByUserId(userId);

            // then
            assertThat(result).isEmpty();
            then(repository).should().findUserContextByUserId(userId.value());
            then(mapper).shouldHaveNoInteractions();
        }
    }
}
