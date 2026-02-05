package com.ryuqq.authhub.application.role.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.role.dto.response.RolePageResult;
import com.ryuqq.authhub.application.role.dto.response.RoleResult;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleAssembler 단위 테스트
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
@DisplayName("RoleAssembler 단위 테스트")
class RoleAssemblerTest {

    private RoleAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new RoleAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드 (단건 변환)")
    class ToResult {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Result로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToResult() {
            // given
            Role role = RoleFixture.create();

            // when
            RoleResult result = sut.toResult(role);

            // then
            assertThat(result.roleId()).isEqualTo(role.roleIdValue());
            assertThat(result.tenantId()).isEqualTo(role.tenantIdValue());
            assertThat(result.name()).isEqualTo(role.nameValue());
            assertThat(result.displayName()).isEqualTo(role.displayNameValue());
            assertThat(result.description()).isEqualTo(role.descriptionValue());
            assertThat(result.type()).isEqualTo(role.typeValue());
            assertThat(result.createdAt()).isEqualTo(role.createdAt());
            assertThat(result.updatedAt()).isEqualTo(role.updatedAt());
        }
    }

    @Nested
    @DisplayName("toResultList 메서드 (목록 변환)")
    class ToResultList {

        @Test
        @DisplayName("성공: Domain 목록이 Result 목록으로 올바르게 변환됨")
        void shouldMapAllRoles_ToResultList() {
            // given
            Role r1 = RoleFixture.createCustomRoleWithName("ROLE_ONE");
            Role r2 = RoleFixture.createCustomRoleWithName("ROLE_TWO");
            List<Role> roles = List.of(r1, r2);

            // when
            List<RoleResult> results = sut.toResultList(roles);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).name()).isEqualTo("ROLE_ONE");
            assertThat(results.get(1).name()).isEqualTo("ROLE_TWO");
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 목록 반환")
        void shouldReturnEmptyList_WhenInputIsEmpty() {
            // given
            List<Role> emptyList = Collections.emptyList();

            // when
            List<RoleResult> results = sut.toResultList(emptyList);

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("null 입력 시 빈 목록 반환")
        void shouldReturnEmptyList_WhenInputIsNull() {
            // when
            List<RoleResult> results = sut.toResultList(null);

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
            Role r1 = RoleFixture.create();
            Role r2 = RoleFixture.createTenantRole();
            List<Role> roles = List.of(r1, r2);
            int page = 0;
            int size = 10;
            long totalElements = 25L;

            // when
            RolePageResult result = sut.toPageResult(roles, page, size, totalElements);

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
            List<Role> emptyList = Collections.emptyList();

            // when
            RolePageResult result = sut.toPageResult(emptyList, 0, 10, 0L);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("roles가 null이면 빈 content로 PageResult 반환")
        void shouldReturnEmptyContent_WhenNullInput() {
            // when
            RolePageResult result = sut.toPageResult(null, 0, 20, 0L);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.pageMeta()).isNotNull();
        }
    }
}
