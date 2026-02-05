package com.ryuqq.authhub.adapter.out.persistence.userrole.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.userrole.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.userrole.fixture.UserRoleJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.userrole.mapper.UserRoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.userrole.repository.UserRoleJpaRepository;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import com.ryuqq.authhub.domain.userrole.fixture.UserRoleFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserRoleCommandAdapter 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Adapter는 Repository 위임 + Mapper 변환 담당
 *   <li>Repository/Mapper를 Mock으로 대체
 *   <li>위임 및 변환 흐름 검증
 *   <li>Hard Delete 로직 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleCommandAdapter 단위 테스트")
class UserRoleCommandAdapterTest {

    private static final String USER_ID = UserRoleFixture.defaultUserIdString();
    private static final Long ROLE_ID = UserRoleFixture.defaultRoleIdValue();

    @Mock private UserRoleJpaRepository repository;

    @Mock private UserRoleJpaEntityMapper mapper;

    private UserRoleCommandAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new UserRoleCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: Domain → Entity 변환 후 저장하고 Domain 반환")
        void shouldConvertAndPersist_ThenReturnDomain() {
            // given
            UserRole domain = UserRoleFixture.create();
            UserRoleJpaEntity entity = UserRoleJpaEntityFixture.create();
            UserRole savedDomain = UserRoleFixture.create();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);
            given(mapper.toDomain(entity)).willReturn(savedDomain);

            // when
            UserRole result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(savedDomain);
        }

        @Test
        @DisplayName("Mapper를 통해 Domain → Entity 변환")
        void shouldUseMapper_ToConvertDomainToEntity() {
            // given
            UserRole domain = UserRoleFixture.create();
            UserRoleJpaEntity entity = UserRoleJpaEntityFixture.create();
            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            sut.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
        }

        @Test
        @DisplayName("Repository를 통해 Entity 저장")
        void shouldUseRepository_ToSaveEntity() {
            // given
            UserRole domain = UserRoleFixture.create();
            UserRoleJpaEntity entity = UserRoleJpaEntityFixture.create();
            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            sut.persist(domain);

            // then
            then(repository).should().save(entity);
        }
    }

    @Nested
    @DisplayName("persistAll 메서드")
    class PersistAll {

        @Test
        @DisplayName("성공: 다건 저장 후 Domain 목록 반환")
        void shouldPersistAll_ThenReturnDomainList() {
            // given
            UserRole domain1 = UserRoleFixture.create();
            UserRole domain2 = UserRoleFixture.createWithRole(2L);
            UserRoleJpaEntity entity1 = UserRoleJpaEntityFixture.create();
            UserRoleJpaEntity entity2 = UserRoleJpaEntityFixture.createWith(USER_ID, 2L);
            List<UserRole> domains = List.of(domain1, domain2);
            List<UserRoleJpaEntity> entities = List.of(entity1, entity2);

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);
            given(repository.saveAll(anyList())).willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<UserRole> result = sut.persistAll(domains);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0)).isEqualTo(domain1);
            assertThat(result.get(1)).isEqualTo(domain2);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class Delete {

        @Test
        @DisplayName("성공: userId와 roleId로 Hard Delete")
        void shouldDelete_ByUserIdAndRoleId() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();
            RoleId roleId = UserRoleFixture.defaultRoleId();

            // when
            sut.delete(userId, roleId);

            // then
            then(repository).should().deleteByUserIdAndRoleId(USER_ID, ROLE_ID);
        }
    }

    @Nested
    @DisplayName("deleteAllByUserId 메서드")
    class DeleteAllByUserId {

        @Test
        @DisplayName("성공: 사용자의 모든 관계 Hard Delete")
        void shouldDeleteAll_ByUserId() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();

            // when
            sut.deleteAllByUserId(userId);

            // then
            then(repository).should().deleteAllByUserId(USER_ID);
        }
    }

    @Nested
    @DisplayName("deleteAll 메서드")
    class DeleteAll {

        @Test
        @DisplayName("성공: 사용자의 특정 역할들 Hard Delete")
        void shouldDeleteAll_ByUserIdAndRoleIds() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();
            List<RoleId> roleIds = List.of(RoleId.of(1L), RoleId.of(2L));

            // when
            sut.deleteAll(userId, roleIds);

            // then
            then(repository).should().deleteAllByUserIdAndRoleIdIn(USER_ID, List.of(1L, 2L));
        }
    }
}
