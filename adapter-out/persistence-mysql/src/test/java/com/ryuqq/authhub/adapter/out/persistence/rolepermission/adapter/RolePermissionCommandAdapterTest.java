package com.ryuqq.authhub.adapter.out.persistence.rolepermission.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.fixture.RolePermissionJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.mapper.RolePermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.repository.RolePermissionJpaRepository;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.fixture.RolePermissionFixture;
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
 * RolePermissionCommandAdapter 단위 테스트
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
@DisplayName("RolePermissionCommandAdapter 단위 테스트")
class RolePermissionCommandAdapterTest {

    @Mock private RolePermissionJpaRepository repository;

    @Mock private RolePermissionJpaEntityMapper mapper;

    private RolePermissionCommandAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new RolePermissionCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: Domain → Entity 변환 후 저장하고 Domain 반환")
        void shouldConvertAndPersist_ThenReturnDomain() {
            // given
            RolePermission domain = RolePermissionFixture.create();
            RolePermissionJpaEntity entity = RolePermissionJpaEntityFixture.create();
            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            RolePermission result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(domain);
            then(mapper).should().toEntity(domain);
            then(repository).should().save(entity);
            then(mapper).should().toDomain(entity);
        }
    }

    @Nested
    @DisplayName("persistAll 메서드")
    class PersistAll {

        @Test
        @DisplayName("성공: 다건 저장 후 Domain 목록 반환")
        void shouldPersistAll_ThenReturnDomainList() {
            // given
            List<RolePermission> domains =
                    List.of(
                            RolePermissionFixture.create(),
                            RolePermissionFixture.createWithRoleAndPermission(2L, 2L));
            List<RolePermissionJpaEntity> entities =
                    List.of(
                            RolePermissionJpaEntityFixture.create(),
                            RolePermissionJpaEntityFixture.createNew());
            given(mapper.toEntity(domains.get(0))).willReturn(entities.get(0));
            given(mapper.toEntity(domains.get(1))).willReturn(entities.get(1));
            given(repository.saveAll(anyList())).willReturn(entities);
            given(mapper.toDomain(entities.get(0))).willReturn(domains.get(0));
            given(mapper.toDomain(entities.get(1))).willReturn(domains.get(1));

            // when
            List<RolePermission> result = sut.persistAll(domains);

            // then
            assertThat(result).hasSize(2);
            then(repository).should().saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class Delete {

        @Test
        @DisplayName("성공: roleId와 permissionId로 삭제")
        void shouldDelete_ByRoleIdAndPermissionId() {
            // given
            RoleId roleId = RoleId.of(1L);
            PermissionId permissionId = PermissionId.of(2L);

            // when
            sut.delete(roleId, permissionId);

            // then
            then(repository).should().deleteByRoleIdAndPermissionId(1L, 2L);
        }
    }

    @Nested
    @DisplayName("deleteAllByRoleId 메서드")
    class DeleteAllByRoleId {

        @Test
        @DisplayName("성공: 역할의 모든 관계 삭제")
        void shouldDeleteAll_ByRoleId() {
            // given
            RoleId roleId = RoleId.of(1L);

            // when
            sut.deleteAllByRoleId(roleId);

            // then
            then(repository).should().deleteAllByRoleId(1L);
        }
    }

    @Nested
    @DisplayName("deleteAll 메서드")
    class DeleteAll {

        @Test
        @DisplayName("성공: 역할의 특정 권한들 삭제")
        void shouldDeleteAll_ByRoleIdAndPermissionIds() {
            // given
            RoleId roleId = RoleId.of(1L);
            List<PermissionId> permissionIds = List.of(PermissionId.of(2L), PermissionId.of(3L));

            // when
            sut.deleteAll(roleId, permissionIds);

            // then
            then(repository).should().deleteAllByRoleIdAndPermissionIdIn(1L, List.of(2L, 3L));
        }
    }
}
