package com.ryuqq.authhub.application.role.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.role.manager.RoleReadManager;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.exception.DuplicateRoleNameException;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
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
 * RoleValidator 단위 테스트
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
@DisplayName("RoleValidator 단위 테스트")
class RoleValidatorTest {

    @Mock private RoleReadManager readManager;

    private RoleValidator sut;

    @BeforeEach
    void setUp() {
        sut = new RoleValidator(readManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공: 역할이 존재하면 해당 역할 반환")
        void shouldReturnRole_WhenExists() {
            // given
            RoleId id = RoleFixture.defaultId();
            Role expected = RoleFixture.create();

            given(readManager.findById(id)).willReturn(expected);

            // when
            Role result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().findById(id);
        }

        @Test
        @DisplayName("실패: 역할이 존재하지 않으면 RoleNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            RoleId id = RoleFixture.defaultId();

            given(readManager.findById(id)).willThrow(new RoleNotFoundException(id));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(RoleNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateNameNotDuplicated 메서드")
    class ValidateNameNotDuplicated {

        @Test
        @DisplayName("성공: 이름이 중복되지 않으면 예외 없음")
        void shouldNotThrow_WhenNameIsNotDuplicated() {
            // given
            TenantId tenantId = RoleFixture.defaultTenantId();
            RoleName name = RoleName.of("UNIQUE_ROLE");

            given(readManager.existsByTenantIdAndName(tenantId, name)).willReturn(false);

            // when & then
            assertThatCode(() -> sut.validateNameNotDuplicated(tenantId, name))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: 이름이 중복되면 DuplicateRoleNameException 발생")
        void shouldThrowException_WhenNameIsDuplicated() {
            // given
            TenantId tenantId = RoleFixture.defaultTenantId();
            RoleName name = RoleName.of("DUPLICATE_ROLE");

            given(readManager.existsByTenantIdAndName(tenantId, name)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNameNotDuplicated(tenantId, name))
                    .isInstanceOf(DuplicateRoleNameException.class);
        }
    }

    @Nested
    @DisplayName("validateAllExist 메서드")
    class ValidateAllExist {

        @Test
        @DisplayName("성공: 모든 ID가 존재하면 Role 목록 반환")
        void shouldReturnRoles_WhenAllExist() {
            // given
            List<RoleId> ids = List.of(RoleFixture.defaultId());
            List<Role> expected = List.of(RoleFixture.create());

            given(readManager.findAllByIds(ids)).willReturn(expected);

            // when
            List<Role> result = sut.validateAllExist(ids);

            // then
            assertThat(result).hasSize(1).containsExactly(RoleFixture.create());
            then(readManager).should().findAllByIds(ids);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 ID가 있으면 RoleNotFoundException 발생")
        void shouldThrowException_WhenAnyIdNotExists() {
            // given
            RoleId existingId = RoleFixture.defaultId();
            RoleId missingId = RoleId.of(999L);
            List<RoleId> ids = List.of(existingId, missingId);
            List<Role> found = List.of(RoleFixture.create());

            given(readManager.findAllByIds(ids)).willReturn(found);

            // when & then
            assertThatThrownBy(() -> sut.validateAllExist(ids))
                    .isInstanceOf(RoleNotFoundException.class);
        }
    }
}
