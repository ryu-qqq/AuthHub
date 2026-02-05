package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.fixture.UserJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserJpaRepository;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserCommandAdapter 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Adapter는 Repository 위임 + Mapper 변환 담당
 *   <li>Repository/Mapper를 Mock으로 대체
 *   <li>위임 및 변환 흐름 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandAdapter 단위 테스트")
class UserCommandAdapterTest {

    @Mock private UserJpaRepository repository;

    @Mock private UserJpaEntityMapper mapper;

    private UserCommandAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new UserCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: Domain → Entity 변환 후 저장하고 ID 반환")
        void shouldConvertAndPersist_ThenReturnId() {
            // given
            User domain = UserFixture.create();
            UserJpaEntity entity = UserJpaEntityFixture.create();
            String expectedId = UserJpaEntityFixture.defaultUserId();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            String result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("Mapper를 통해 Domain → Entity 변환")
        void shouldUseMapper_ToConvertDomainToEntity() {
            // given
            User domain = UserFixture.create();
            UserJpaEntity entity = UserJpaEntityFixture.create();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            sut.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
        }

        @Test
        @DisplayName("Repository를 통해 Entity 저장")
        void shouldUseRepository_ToSaveEntity() {
            // given
            User domain = UserFixture.create();
            UserJpaEntity entity = UserJpaEntityFixture.create();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            sut.persist(domain);

            // then
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("새 Domain 저장 시에도 동일한 흐름")
        void shouldFollowSameFlow_WhenPersistingNewDomain() {
            // given
            User newDomain = UserFixture.createNew();
            UserJpaEntity entity = UserJpaEntityFixture.create();

            given(mapper.toEntity(newDomain)).willReturn(entity);
            given(repository.save(any(UserJpaEntity.class))).willReturn(entity);

            // when
            String result = sut.persist(newDomain);

            // then
            assertThat(result).isNotNull();
            then(mapper).should().toEntity(newDomain);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("user가 null이면 예외 발생")
        void shouldThrow_WhenUserIsNull() {
            // given
            given(mapper.toEntity(null))
                    .willThrow(new NullPointerException("user must not be null"));

            // when & then
            assertThatThrownBy(() -> sut.persist(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("user");
        }

        @Test
        @DisplayName("Repository.save() 예외는 그대로 전파")
        void shouldPropagateException_WhenRepositorySaveFails() {
            // given
            User domain = UserFixture.create();
            UserJpaEntity entity = UserJpaEntityFixture.create();
            RuntimeException repoException = new RuntimeException("DB constraint violation");

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willThrow(repoException);

            // when & then
            assertThatThrownBy(() -> sut.persist(domain)).isSameAs(repoException);
        }
    }
}
