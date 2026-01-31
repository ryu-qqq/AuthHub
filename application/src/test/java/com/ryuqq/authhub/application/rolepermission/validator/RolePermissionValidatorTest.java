package com.ryuqq.authhub.application.rolepermission.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionReadManager;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.rolepermission.exception.PermissionInUseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RolePermissionValidator 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Validator는 조회 기반 검증 로직 담당 → ReadManager 협력 검증
 *   <li>검증 실패 시 PermissionInUseException 발생 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RolePermissionValidator 단위 테스트")
class RolePermissionValidatorTest {

    @Mock private RolePermissionReadManager readManager;

    private RolePermissionValidator sut;

    @BeforeEach
    void setUp() {
        sut = new RolePermissionValidator(readManager);
    }

    @Nested
    @DisplayName("validateNotInUse 메서드")
    class ValidateNotInUse {

        @Test
        @DisplayName("성공: 권한이 사용 중이 아니면 예외 없음")
        void shouldNotThrow_WhenPermissionNotInUse() {
            // given
            PermissionId permissionId = PermissionFixture.defaultId();

            given(readManager.existsByPermissionId(permissionId)).willReturn(false);

            // when & then
            assertThatCode(() -> sut.validateNotInUse(permissionId)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: 권한이 사용 중이면 PermissionInUseException 발생")
        void shouldThrowException_WhenPermissionInUse() {
            // given
            PermissionId permissionId = PermissionFixture.defaultId();

            given(readManager.existsByPermissionId(permissionId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNotInUse(permissionId))
                    .isInstanceOf(PermissionInUseException.class);
            then(readManager).should().existsByPermissionId(permissionId);
        }
    }
}
