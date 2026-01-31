package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.fixture.UserJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.query.criteria.UserSearchCriteria;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import java.util.List;
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
 * UserQueryAdapter 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Adapter는 Repository 위임 + Mapper 변환 담당
 *   <li>QueryDslRepository/Mapper를 Mock으로 대체
 *   <li>Entity → Domain 변환 흐름 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryAdapter 단위 테스트")
class UserQueryAdapterTest {

    @Mock private UserQueryDslRepository repository;

    @Mock private UserJpaEntityMapper mapper;

    private UserQueryAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new UserQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            UserId id = UserFixture.defaultId();
            UserJpaEntity entity = UserJpaEntityFixture.create();
            User expectedDomain = UserFixture.create();

            given(repository.findByUserId(id.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<User> result = sut.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            UserId id = UserFixture.defaultId();

            given(repository.findByUserId(id.value())).willReturn(Optional.empty());

            // when
            Optional<User> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("UserId에서 value 추출하여 Repository 호출")
        void shouldExtractIdValue_AndCallRepository() {
            // given
            UserId id = UserFixture.defaultId();

            given(repository.findByUserId(id.value())).willReturn(Optional.empty());

            // when
            sut.findById(id);

            // then
            then(repository).should().findByUserId(id.value());
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            UserId id = UserFixture.defaultId();

            given(repository.existsByUserId(id.value())).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            UserId id = UserFixture.defaultId();

            given(repository.existsByUserId(id.value())).willReturn(false);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByOrganizationIdAndIdentifier 메서드")
    class ExistsByOrganizationIdAndIdentifier {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            OrganizationId organizationId = UserFixture.defaultOrganizationId();
            Identifier identifier = UserFixture.defaultIdentifier();

            given(
                            repository.existsByOrganizationIdAndIdentifier(
                                    organizationId.value(), identifier.value()))
                    .willReturn(true);

            // when
            boolean result = sut.existsByOrganizationIdAndIdentifier(organizationId, identifier);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            OrganizationId organizationId = UserFixture.defaultOrganizationId();
            Identifier identifier = UserFixture.defaultIdentifier();

            given(
                            repository.existsByOrganizationIdAndIdentifier(
                                    organizationId.value(), identifier.value()))
                    .willReturn(false);

            // when
            boolean result = sut.existsByOrganizationIdAndIdentifier(organizationId, identifier);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByOrganizationIdAndPhoneNumber 메서드")
    class ExistsByOrganizationIdAndPhoneNumber {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            OrganizationId organizationId = UserFixture.defaultOrganizationId();
            PhoneNumber phoneNumber = PhoneNumber.of("010-1234-5678");

            given(
                            repository.existsByOrganizationIdAndPhoneNumber(
                                    organizationId.value(), phoneNumber.value()))
                    .willReturn(true);

            // when
            boolean result = sut.existsByOrganizationIdAndPhoneNumber(organizationId, phoneNumber);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            OrganizationId organizationId = UserFixture.defaultOrganizationId();
            PhoneNumber phoneNumber = PhoneNumber.of("010-1234-5678");

            given(
                            repository.existsByOrganizationIdAndPhoneNumber(
                                    organizationId.value(), phoneNumber.value()))
                    .willReturn(false);

            // when
            boolean result = sut.existsByOrganizationIdAndPhoneNumber(organizationId, phoneNumber);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByIdentifier 메서드")
    class FindByIdentifier {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            Identifier identifier = UserFixture.defaultIdentifier();
            UserJpaEntity entity = UserJpaEntityFixture.create();
            User expectedDomain = UserFixture.create();

            given(repository.findByIdentifier(identifier.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<User> result = sut.findByIdentifier(identifier);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            Identifier identifier = UserFixture.defaultIdentifier();

            given(repository.findByIdentifier(identifier.value())).willReturn(Optional.empty());

            // when
            Optional<User> result = sut.findByIdentifier(identifier);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllBySearchCriteria 메서드")
    class FindAllBySearchCriteria {

        @Test
        @DisplayName("성공: Entity 목록을 Domain 목록으로 변환하여 반환")
        void shouldFindAndConvertAll_ThenReturnDomainList() {
            // given
            UserSearchCriteria criteria = createTestCriteria();
            UserJpaEntity entity1 = UserJpaEntityFixture.createWithIdentifier("user1@example.com");
            UserJpaEntity entity2 = UserJpaEntityFixture.createWithIdentifier("user2@example.com");
            User domain1 = UserFixture.createWithIdentifier("user1@example.com");
            User domain2 = UserFixture.createWithIdentifier("user2@example.com");

            given(repository.findAllByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<User> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            // given
            UserSearchCriteria criteria = createTestCriteria();

            given(repository.findAllByCriteria(criteria)).willReturn(List.of());

            // when
            List<User> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countBySearchCriteria 메서드")
    class CountBySearchCriteria {

        @Test
        @DisplayName("성공: Repository 결과를 그대로 반환")
        void shouldReturnCount_FromRepository() {
            // given
            UserSearchCriteria criteria = createTestCriteria();

            given(repository.countByCriteria(criteria)).willReturn(25L);

            // when
            long result = sut.countBySearchCriteria(criteria);

            // then
            assertThat(result).isEqualTo(25L);
        }

        @Test
        @DisplayName("결과가 없으면 0 반환")
        void shouldReturnZero_WhenNoResults() {
            // given
            UserSearchCriteria criteria = createTestCriteria();

            given(repository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countBySearchCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    // ==================== Helper Methods ====================

    private UserSearchCriteria createTestCriteria() {
        return UserSearchCriteria.ofDefault(null, null, null, DateRange.of(null, null), 0, 10);
    }
}
