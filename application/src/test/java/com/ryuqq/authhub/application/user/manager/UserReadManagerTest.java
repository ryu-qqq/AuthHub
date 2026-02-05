package com.ryuqq.authhub.application.user.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
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
 * UserReadManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserReadManager 단위 테스트")
class UserReadManagerTest {

    @Mock private UserQueryPort queryPort;

    private UserReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new UserReadManager(queryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: 사용자가 존재하면 해당 사용자 반환")
        void shouldReturnUser_WhenExists() {
            // given
            UserId id = UserFixture.defaultId();
            User expected = UserFixture.create();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            User result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패: 사용자가 존재하지 않으면 UserNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            UserId id = UserFixture.defaultId();

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findById(id)).isInstanceOf(UserNotFoundException.class);
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

            given(queryPort.existsByOrganizationIdAndIdentifier(organizationId, identifier))
                    .willReturn(true);

            // when
            boolean result = sut.existsByOrganizationIdAndIdentifier(organizationId, identifier);

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("findAllBySearchCriteria 메서드")
    class FindAllBySearchCriteria {

        @Test
        @DisplayName("성공: Criteria에 맞는 사용자 목록 반환")
        void shouldReturnUsers_MatchingCriteria() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.ofDefault(
                            List.of(UserFixture.defaultOrganizationId()),
                            null,
                            null,
                            DateRange.of(null, null),
                            0,
                            10);
            List<User> expected = List.of(UserFixture.create());

            given(queryPort.findAllBySearchCriteria(criteria)).willReturn(expected);

            // when
            List<User> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllBySearchCriteria(criteria);
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
            given(queryPort.existsById(id)).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
            then(queryPort).should().existsById(id);
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            UserId id = UserFixture.defaultId();
            given(queryPort.existsById(id)).willReturn(false);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByIdentifier 메서드")
    class FindByIdentifier {

        @Test
        @DisplayName("존재하면 Optional에 User 반환")
        void shouldReturnOptionalWithUser_WhenExists() {
            // given
            Identifier identifier = UserFixture.defaultIdentifier();
            User expected = UserFixture.create();
            given(queryPort.findByIdentifier(identifier)).willReturn(Optional.of(expected));

            // when
            Optional<User> result = sut.findByIdentifier(identifier);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expected);
            then(queryPort).should().findByIdentifier(identifier);
        }

        @Test
        @DisplayName("존재하지 않으면 Optional.empty 반환")
        void shouldReturnEmpty_WhenNotExists() {
            // given
            Identifier identifier = UserFixture.defaultIdentifier();
            given(queryPort.findByIdentifier(identifier)).willReturn(Optional.empty());

            // when
            Optional<User> result = sut.findByIdentifier(identifier);

            // then
            assertThat(result).isEmpty();
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
            given(queryPort.existsByOrganizationIdAndPhoneNumber(organizationId, phoneNumber))
                    .willReturn(true);

            // when
            boolean result = sut.existsByOrganizationIdAndPhoneNumber(organizationId, phoneNumber);

            // then
            assertThat(result).isTrue();
            then(queryPort)
                    .should()
                    .existsByOrganizationIdAndPhoneNumber(organizationId, phoneNumber);
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            OrganizationId organizationId = UserFixture.defaultOrganizationId();
            PhoneNumber phoneNumber = PhoneNumber.of("010-9999-8888");
            given(queryPort.existsByOrganizationIdAndPhoneNumber(organizationId, phoneNumber))
                    .willReturn(false);

            // when
            boolean result = sut.existsByOrganizationIdAndPhoneNumber(organizationId, phoneNumber);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("countBySearchCriteria 메서드")
    class CountBySearchCriteria {

        @Test
        @DisplayName("성공: Criteria에 맞는 사용자 수 반환")
        void shouldReturnCount_MatchingCriteria() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.ofDefault(
                            List.of(UserFixture.defaultOrganizationId()),
                            null,
                            null,
                            DateRange.of(null, null),
                            0,
                            10);
            given(queryPort.countBySearchCriteria(criteria)).willReturn(5L);

            // when
            long result = sut.countBySearchCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
            then(queryPort).should().countBySearchCriteria(criteria);
        }
    }
}
