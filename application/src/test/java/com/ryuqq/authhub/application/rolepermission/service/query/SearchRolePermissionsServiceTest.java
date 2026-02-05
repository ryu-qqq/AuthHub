package com.ryuqq.authhub.application.rolepermission.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.rolepermission.assembler.RolePermissionAssembler;
import com.ryuqq.authhub.application.rolepermission.dto.query.RolePermissionSearchParams;
import com.ryuqq.authhub.application.rolepermission.dto.response.RolePermissionPageResult;
import com.ryuqq.authhub.application.rolepermission.factory.RolePermissionQueryFactory;
import com.ryuqq.authhub.application.rolepermission.fixture.RolePermissionQueryFixtures;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionReadManager;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.fixture.RolePermissionFixture;
import com.ryuqq.authhub.domain.rolepermission.query.criteria.RolePermissionSearchCriteria;
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
 * SearchRolePermissionsService 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Service는 오케스트레이션만 담당 → Factory, Manager, Assembler 호출 검증
 *   <li>BDDMockito 스타일로 Given-When-Then 구조 명확화
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchRolePermissionsService 단위 테스트")
class SearchRolePermissionsServiceTest {

    @Mock private RolePermissionQueryFactory queryFactory;

    @Mock private RolePermissionReadManager readManager;

    @Mock private RolePermissionAssembler assembler;

    private SearchRolePermissionsService sut;

    @BeforeEach
    void setUp() {
        sut = new SearchRolePermissionsService(queryFactory, readManager, assembler);
    }

    @Nested
    @DisplayName("search 메서드")
    class Search {

        @Test
        @DisplayName("성공: Factory → Manager → Assembler 순서로 호출")
        void shouldOrchestrate_FactoryThenManagerThenAssembler() {
            // given
            RolePermissionSearchParams params =
                    RolePermissionQueryFixtures.searchParamsWithRoleId(
                            RolePermissionQueryFixtures.defaultRoleId());
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.ofRoleId(
                            RolePermissionFixture.defaultRoleId(), 0, 10);
            List<RolePermission> rolePermissions = List.of(RolePermissionFixture.create());
            RolePermissionPageResult expectedResult =
                    RolePermissionPageResult.of(List.of(), 0, 10, 1L);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllBySearchCriteria(criteria)).willReturn(rolePermissions);
            given(readManager.countBySearchCriteria(criteria)).willReturn(1L);
            given(assembler.toPageResult(rolePermissions, 0, 10, 1L)).willReturn(expectedResult);

            // when
            RolePermissionPageResult result = sut.search(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(queryFactory).should().toCriteria(params);
            then(readManager).should().findAllBySearchCriteria(criteria);
            then(readManager).should().countBySearchCriteria(criteria);
            then(assembler).should().toPageResult(rolePermissions, 0, 10, 1L);
        }
    }
}
