package com.ryuqq.authhub.application.permission.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.permission.assembler.PermissionAssembler;
import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.permission.manager.query.PermissionReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
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
 * SearchPermissionsService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchPermissionsService 단위 테스트")
class SearchPermissionsServiceTest {

    @Mock private PermissionReadManager readManager;

    @Mock private PermissionAssembler assembler;

    private SearchPermissionsService service;

    @BeforeEach
    void setUp() {
        service = new SearchPermissionsService(readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("권한 목록을 성공적으로 검색한다")
        void shouldSearchPermissionsSuccessfully() {
            // given
            Permission permission1 = PermissionFixture.create();
            Permission permission2 = PermissionFixture.create("order", "read");
            SearchPermissionsQuery query = new SearchPermissionsQuery(null, null, null, 0, 20);

            PermissionResponse response1 =
                    new PermissionResponse(
                            permission1.permissionIdValue(),
                            permission1.keyValue(),
                            permission1.resourceValue(),
                            permission1.actionValue(),
                            permission1.descriptionValue(),
                            permission1.getType().name(),
                            permission1.createdAt(),
                            permission1.updatedAt());
            PermissionResponse response2 =
                    new PermissionResponse(
                            permission2.permissionIdValue(),
                            permission2.keyValue(),
                            permission2.resourceValue(),
                            permission2.actionValue(),
                            permission2.descriptionValue(),
                            permission2.getType().name(),
                            permission2.createdAt(),
                            permission2.updatedAt());

            given(readManager.search(query)).willReturn(List.of(permission1, permission2));
            given(assembler.toResponse(any(Permission.class))).willReturn(response1, response2);

            // when
            List<PermissionResponse> responses = service.execute(query);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0)).isNotNull();
            assertThat(responses.get(1)).isNotNull();
            verify(readManager).search(query);
        }

        @Test
        @DisplayName("권한이 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoPermissionsFound() {
            // given
            SearchPermissionsQuery query =
                    new SearchPermissionsQuery("nonexistent", null, null, 0, 20);

            given(readManager.search(query)).willReturn(List.of());

            // when
            List<PermissionResponse> responses = service.execute(query);

            // then
            assertThat(responses).isEmpty();
            verify(readManager).search(query);
        }

        @Test
        @DisplayName("리소스별로 권한을 필터링한다")
        void shouldFilterPermissionsByResource() {
            // given
            Permission permission = PermissionFixture.create("user", "read");
            SearchPermissionsQuery query = new SearchPermissionsQuery("user", null, null, 0, 20);
            PermissionResponse expectedResponse =
                    new PermissionResponse(
                            permission.permissionIdValue(),
                            permission.keyValue(),
                            permission.resourceValue(),
                            permission.actionValue(),
                            permission.descriptionValue(),
                            permission.getType().name(),
                            permission.createdAt(),
                            permission.updatedAt());

            given(readManager.search(query)).willReturn(List.of(permission));
            given(assembler.toResponse(any(Permission.class))).willReturn(expectedResponse);

            // when
            List<PermissionResponse> responses = service.execute(query);

            // then
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).resource()).isEqualTo("user");
            verify(readManager).search(query);
        }
    }
}
