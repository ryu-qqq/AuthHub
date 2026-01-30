package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.fixture.RoleJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.role.mapper.RoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleJpaRepository;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RoleCommandAdapter 단위 테스트
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
@DisplayName("RoleCommandAdapter 단위 테스트")
class RoleCommandAdapterTest {

    @Mock private RoleJpaRepository repository;

    @Mock private RoleJpaEntityMapper mapper;

    private RoleCommandAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new RoleCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: Domain → Entity 변환 후 저장하고 ID 반환")
        void shouldConvertAndPersist_ThenReturnId() {
            // given
            Role domain = RoleFixture.create();
            RoleJpaEntity entity = RoleJpaEntityFixture.create();
            Long expectedId = RoleJpaEntityFixture.defaultRoleId();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("Mapper를 통해 Domain → Entity 변환")
        void shouldUseMapper_ToConvertDomainToEntity() {
            // given
            Role domain = RoleFixture.create();
            RoleJpaEntity entity = RoleJpaEntityFixture.create();

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
            Role domain = RoleFixture.create();
            RoleJpaEntity entity = RoleJpaEntityFixture.create();

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
            Role newDomain = RoleFixture.createNewCustomRole();
            RoleJpaEntity entity = RoleJpaEntityFixture.create();

            given(mapper.toEntity(newDomain)).willReturn(entity);
            given(repository.save(any(RoleJpaEntity.class))).willReturn(entity);

            // when
            Long result = sut.persist(newDomain);

            // then
            assertThat(result).isNotNull();
            then(mapper).should().toEntity(newDomain);
            then(repository).should().save(entity);
        }
    }
}
