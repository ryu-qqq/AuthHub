package com.ryuqq.authhub.application.permission.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.permission.manager.PermissionReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.exception.DuplicatePermissionKeyException;
import com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.service.id.ServiceId;
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
 * PermissionValidator 단위 테스트
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
@DisplayName("PermissionValidator 단위 테스트")
class PermissionValidatorTest {

    @Mock private PermissionReadManager readManager;

    private PermissionValidator sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionValidator(readManager);
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공: 권한이 존재하면 해당 권한 반환")
        void shouldReturnPermission_WhenExists() {
            // given
            PermissionId id = PermissionFixture.defaultId();
            Permission expected = PermissionFixture.create();

            given(readManager.findById(id)).willReturn(expected);

            // when
            Permission result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().findById(id);
        }

        @Test
        @DisplayName("실패: 권한이 존재하지 않으면 PermissionNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            PermissionId id = PermissionFixture.defaultId();

            given(readManager.findById(id)).willThrow(new PermissionNotFoundException(id));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(PermissionNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateKeyNotDuplicated 메서드")
    class ValidateKeyNotDuplicated {

        @Test
        @DisplayName("성공: 권한 키가 중복되지 않으면 예외 없음")
        void shouldNotThrow_WhenKeyIsNotDuplicated() {
            // given
            ServiceId serviceId = null;
            String permissionKey = "user:read";

            given(readManager.existsByServiceIdAndPermissionKey(serviceId, permissionKey))
                    .willReturn(false);

            // when & then
            assertThatCode(() -> sut.validateKeyNotDuplicated(serviceId, permissionKey))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: 권한 키가 중복되면 DuplicatePermissionKeyException 발생")
        void shouldThrowException_WhenKeyIsDuplicated() {
            // given
            ServiceId serviceId = null;
            String permissionKey = "user:read";

            given(readManager.existsByServiceIdAndPermissionKey(serviceId, permissionKey))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateKeyNotDuplicated(serviceId, permissionKey))
                    .isInstanceOf(DuplicatePermissionKeyException.class);
        }
    }

    @Nested
    @DisplayName("validateAllExist 메서드")
    class ValidateAllExist {

        @Test
        @DisplayName("성공: 모든 ID가 존재하면 Permission 목록 반환")
        void shouldReturnPermissions_WhenAllExist() {
            // given
            List<PermissionId> ids = List.of(PermissionFixture.defaultId());
            List<Permission> expected = List.of(PermissionFixture.create());

            given(readManager.findAllByIds(ids)).willReturn(expected);

            // when
            List<Permission> result = sut.validateAllExist(ids);

            // then
            assertThat(result).hasSize(1).containsExactly(PermissionFixture.create());
            then(readManager).should().findAllByIds(ids);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 ID가 있으면 PermissionNotFoundException 발생")
        void shouldThrowException_WhenAnyIdNotExists() {
            // given
            PermissionId existingId = PermissionFixture.defaultId();
            PermissionId missingId = PermissionId.of(999L);
            List<PermissionId> ids = List.of(existingId, missingId);
            List<Permission> found = List.of(PermissionFixture.create());

            given(readManager.findAllByIds(ids)).willReturn(found);

            // when & then
            assertThatThrownBy(() -> sut.validateAllExist(ids))
                    .isInstanceOf(PermissionNotFoundException.class);
        }
    }
}
