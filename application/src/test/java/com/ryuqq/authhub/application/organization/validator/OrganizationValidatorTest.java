package com.ryuqq.authhub.application.organization.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.organization.manager.OrganizationReadManager;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.DuplicateOrganizationNameException;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OrganizationValidator 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Validator는 조회 기반 검증 로직 담당 → ReadManager 협력 검증
 *   <li>검증 실패 시 적절한 DomainException 발생 검증
 *   <li>검증 성공 시 예외 없이 정상 반환 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationValidator 단위 테스트")
class OrganizationValidatorTest {

    @Mock private OrganizationReadManager readManager;

    private OrganizationValidator sut;

    @BeforeEach
    void setUp() {
        sut = new OrganizationValidator(readManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공: 조직이 존재하면 해당 조직 반환")
        void shouldReturnOrganization_WhenExists() {
            // given
            OrganizationId id = OrganizationFixture.defaultId();
            Organization expected = OrganizationFixture.create();

            given(readManager.findById(id)).willReturn(expected);

            // when
            Organization result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().findById(id);
        }

        @Test
        @DisplayName("실패: 조직이 존재하지 않으면 OrganizationNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            OrganizationId id = OrganizationFixture.defaultId();

            given(readManager.findById(id)).willThrow(new OrganizationNotFoundException(id));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(OrganizationNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateNameNotDuplicated 메서드 (신규 생성용)")
    class ValidateNameNotDuplicated {

        @Test
        @DisplayName("성공: 이름이 중복되지 않으면 예외 없음")
        void shouldNotThrow_WhenNameIsNotDuplicated() {
            // given
            TenantId tenantId = OrganizationFixture.defaultTenantId();
            OrganizationName name = OrganizationName.of("Unique Name");

            given(readManager.existsByTenantIdAndName(tenantId, name)).willReturn(false);

            // when & then
            assertThatCode(() -> sut.validateNameNotDuplicated(tenantId, name))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: 이름이 중복되면 DuplicateOrganizationNameException 발생")
        void shouldThrowException_WhenNameIsDuplicated() {
            // given
            TenantId tenantId = OrganizationFixture.defaultTenantId();
            OrganizationName name = OrganizationName.of("Duplicate Name");

            given(readManager.existsByTenantIdAndName(tenantId, name)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNameNotDuplicated(tenantId, name))
                    .isInstanceOf(DuplicateOrganizationNameException.class);
        }
    }

    @Nested
    @DisplayName("validateNameNotDuplicatedExcluding 메서드 (수정 시 자기 자신 제외)")
    class ValidateNameNotDuplicatedExcluding {

        @Test
        @DisplayName("성공: 새 이름이 기존 이름과 같으면 검증 통과")
        void shouldNotThrow_WhenNewNameEqualsCurrentName() {
            // given
            Organization organization = OrganizationFixture.createWithName("Same Name");
            OrganizationName newName = OrganizationName.of("Same Name");

            // when & then
            assertThatCode(() -> sut.validateNameNotDuplicatedExcluding(newName, organization))
                    .doesNotThrowAnyException();

            // ReadManager는 호출되지 않아야 함 (같은 이름이므로 조회 불필요)
            then(readManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("성공: 새 이름이 다르고 중복되지 않으면 검증 통과")
        void shouldNotThrow_WhenNewNameIsUniqueAndDifferent() {
            // given
            Organization organization = OrganizationFixture.createWithName("Old Name");
            OrganizationName newName = OrganizationName.of("New Unique Name");

            given(readManager.existsByTenantIdAndName(organization.getTenantId(), newName))
                    .willReturn(false);

            // when & then
            assertThatCode(() -> sut.validateNameNotDuplicatedExcluding(newName, organization))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: 새 이름이 다른 조직에서 이미 사용 중이면 예외 발생")
        void shouldThrowException_WhenNewNameIsDuplicatedByOther() {
            // given
            Organization organization = OrganizationFixture.createWithName("Old Name");
            OrganizationName newName = OrganizationName.of("Already Used Name");

            given(readManager.existsByTenantIdAndName(organization.getTenantId(), newName))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNameNotDuplicatedExcluding(newName, organization))
                    .isInstanceOf(DuplicateOrganizationNameException.class);
        }
    }
}
