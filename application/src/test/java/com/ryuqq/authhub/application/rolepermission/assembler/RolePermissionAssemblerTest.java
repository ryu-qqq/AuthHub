package com.ryuqq.authhub.application.rolepermission.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.rolepermission.dto.response.RolePermissionPageResult;
import com.ryuqq.authhub.application.rolepermission.dto.response.RolePermissionResult;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.fixture.RolePermissionFixture;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RolePermissionAssembler 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Assembler는 Domain → Result 변환만 담당
 *   <li>Mock 불필요 (순수 변환 로직)
 *   <li>모든 필드가 올바르게 매핑되는지 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RolePermissionAssembler 단위 테스트")
class RolePermissionAssemblerTest {

    private RolePermissionAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new RolePermissionAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드 (단건 변환)")
    class ToResult {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Result로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToResult() {
            // given
            RolePermission rolePermission = RolePermissionFixture.create();

            // when
            RolePermissionResult result = sut.toResult(rolePermission);

            // then
            assertThat(result.rolePermissionId()).isEqualTo(rolePermission.rolePermissionIdValue());
            assertThat(result.roleId()).isEqualTo(rolePermission.roleIdValue());
            assertThat(result.permissionId()).isEqualTo(rolePermission.permissionIdValue());
            assertThat(result.createdAt()).isEqualTo(rolePermission.createdAt());
        }
    }

    @Nested
    @DisplayName("toResults 메서드 (목록 변환)")
    class ToResults {

        @Test
        @DisplayName("성공: Domain 목록이 Result 목록으로 올바르게 변환됨")
        void shouldMapAllRolePermissions_ToResults() {
            // given
            RolePermission rp1 = RolePermissionFixture.createWithRole(1L);
            RolePermission rp2 = RolePermissionFixture.createWithRole(2L);
            List<RolePermission> rolePermissions = List.of(rp1, rp2);

            // when
            List<RolePermissionResult> results = sut.toResults(rolePermissions);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).roleId()).isEqualTo(1L);
            assertThat(results.get(1).roleId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 목록 반환")
        void shouldReturnEmptyList_WhenInputIsEmpty() {
            // given
            List<RolePermission> emptyList = Collections.emptyList();

            // when
            List<RolePermissionResult> results = sut.toResults(emptyList);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult 메서드 (페이지 변환)")
    class ToPageResult {

        @Test
        @DisplayName("성공: Domain 목록과 페이징 정보가 PageResult로 올바르게 변환됨")
        void shouldCreatePageResult_WithCorrectPagination() {
            // given
            RolePermission rp = RolePermissionFixture.create();
            List<RolePermission> rolePermissions = List.of(rp);
            int pageNumber = 0;
            int pageSize = 10;
            long totalElements = 25L;

            // when
            RolePermissionPageResult result =
                    sut.toPageResult(rolePermissions, pageNumber, pageSize, totalElements);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.pageMeta().page()).isEqualTo(pageNumber);
            assertThat(result.pageMeta().size()).isEqualTo(pageSize);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }
    }
}
