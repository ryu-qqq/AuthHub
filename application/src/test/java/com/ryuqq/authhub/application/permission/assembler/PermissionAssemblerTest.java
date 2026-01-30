package com.ryuqq.authhub.application.permission.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.permission.dto.response.PermissionPageResult;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResult;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionAssembler 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Assembler는 Domain → Result 변환만 담당
 *   <li>Mock 불필요 (순수 변환 로직)
 *   <li>모든 필드가 올바르게 매핑되는지 검증
 *   <li>Edge case (null, empty) 처리 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionAssembler 단위 테스트")
class PermissionAssemblerTest {

    private PermissionAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드 (단건 변환)")
    class ToResult {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Result로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToResult() {
            // given
            Permission permission = PermissionFixture.create();

            // when
            PermissionResult result = sut.toResult(permission);

            // then
            assertThat(result.permissionId()).isEqualTo(permission.permissionIdValue());
            assertThat(result.permissionKey()).isEqualTo(permission.permissionKeyValue());
            assertThat(result.resource()).isEqualTo(permission.resourceValue());
            assertThat(result.action()).isEqualTo(permission.actionValue());
            assertThat(result.description()).isEqualTo(permission.descriptionValue());
            assertThat(result.type()).isEqualTo(permission.typeValue());
            assertThat(result.createdAt()).isEqualTo(permission.createdAt());
            assertThat(result.updatedAt()).isEqualTo(permission.updatedAt());
        }
    }

    @Nested
    @DisplayName("toResultList 메서드 (목록 변환)")
    class ToResultList {

        @Test
        @DisplayName("성공: Domain 목록이 Result 목록으로 올바르게 변환됨")
        void shouldMapAllPermissions_ToResultList() {
            // given
            Permission p1 = PermissionFixture.createWithResourceAndAction("user", "read");
            Permission p2 = PermissionFixture.createWithResourceAndAction("role", "create");
            List<Permission> permissions = List.of(p1, p2);

            // when
            List<PermissionResult> results = sut.toResultList(permissions);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).permissionKey()).isEqualTo("user:read");
            assertThat(results.get(1).permissionKey()).isEqualTo("role:create");
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 목록 반환")
        void shouldReturnEmptyList_WhenInputIsEmpty() {
            // given
            List<Permission> emptyList = Collections.emptyList();

            // when
            List<PermissionResult> results = sut.toResultList(emptyList);

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("null 입력 시 빈 목록 반환")
        void shouldReturnEmptyList_WhenInputIsNull() {
            // when
            List<PermissionResult> results = sut.toResultList(null);

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
            Permission p1 = PermissionFixture.create();
            Permission p2 = PermissionFixture.createSystemPermission();
            List<Permission> permissions = List.of(p1, p2);
            int page = 0;
            int size = 10;
            long totalElements = 25L;

            // when
            PermissionPageResult result = sut.toPageResult(permissions, page, size, totalElements);

            // then
            assertThat(result.content()).hasSize(2);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }

        @Test
        @DisplayName("빈 목록으로 PageResult 생성 가능")
        void shouldCreatePageResult_WithEmptyContent() {
            // given
            List<Permission> emptyList = Collections.emptyList();

            // when
            PermissionPageResult result = sut.toPageResult(emptyList, 0, 10, 0L);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }
    }
}
