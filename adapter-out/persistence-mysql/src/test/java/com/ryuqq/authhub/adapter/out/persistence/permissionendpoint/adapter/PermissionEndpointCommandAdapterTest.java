package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.PermissionEndpointJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.fixture.PermissionEndpointJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.mapper.PermissionEndpointJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository.PermissionEndpointJpaRepository;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.fixture.PermissionEndpointFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PermissionEndpointCommandAdapter 단위 테스트
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
@DisplayName("PermissionEndpointCommandAdapter 단위 테스트")
class PermissionEndpointCommandAdapterTest {

    @Mock private PermissionEndpointJpaRepository repository;

    @Mock private PermissionEndpointJpaEntityMapper mapper;

    private PermissionEndpointCommandAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionEndpointCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: Domain → Entity 변환 후 저장하고 ID 반환")
        void shouldConvertAndPersist_ThenReturnId() {
            // given
            PermissionEndpoint domain = PermissionEndpointFixture.create();
            PermissionEndpointJpaEntity entity = PermissionEndpointJpaEntityFixture.create();
            Long expectedId = PermissionEndpointJpaEntityFixture.defaultPermissionEndpointId();

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
            PermissionEndpoint domain = PermissionEndpointFixture.create();
            PermissionEndpointJpaEntity entity = PermissionEndpointJpaEntityFixture.create();

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
            PermissionEndpoint domain = PermissionEndpointFixture.create();
            PermissionEndpointJpaEntity entity = PermissionEndpointJpaEntityFixture.create();

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
            PermissionEndpoint newDomain = PermissionEndpointFixture.createNew();
            PermissionEndpointJpaEntity entity = PermissionEndpointJpaEntityFixture.create();

            given(mapper.toEntity(newDomain)).willReturn(entity);
            given(repository.save(any(PermissionEndpointJpaEntity.class))).willReturn(entity);

            // when
            Long result = sut.persist(newDomain);

            // then
            assertThat(result).isNotNull();
            then(mapper).should().toEntity(newDomain);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("null domain 전달 시 예외 발생")
        void shouldThrow_WhenDomainIsNull() {
            // when & then
            assertThatThrownBy(() -> sut.persist(null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Repository.save() 예외 발생 시 호출자에게 전파")
        void shouldPropagateException_WhenRepositorySaveThrows() {
            // given
            PermissionEndpoint domain = PermissionEndpointFixture.create();
            PermissionEndpointJpaEntity entity = PermissionEndpointJpaEntityFixture.create();
            RuntimeException expected = new RuntimeException("DB error");

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willThrow(expected);

            // when & then
            assertThatThrownBy(() -> sut.persist(domain)).isSameAs(expected);
        }
    }
}
