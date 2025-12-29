package com.ryuqq.authhub.application.permission.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.permission.assembler.PermissionAssembler;
import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.permission.manager.query.PermissionReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import java.time.Instant;
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

        private static final Instant CREATED_FROM = Instant.parse("2025-01-01T00:00:00Z");
        private static final Instant CREATED_TO = Instant.parse("2025-12-31T23:59:59Z");

        @Test
        @DisplayName("권한 목록을 성공적으로 검색한다")
        void shouldSearchPermissionsSuccessfully() {
            // given
            Permission permission1 = PermissionFixture.create();
            Permission permission2 = PermissionFixture.create("order", "read");
            SearchPermissionsQuery query =
                    SearchPermissionsQuery.of(null, null, null, CREATED_FROM, CREATED_TO, 0, 20);

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
            given(readManager.count(query)).willReturn(2L);
            given(assembler.toResponse(any(Permission.class))).willReturn(response1, response2);

            // when
            PageResponse<PermissionResponse> pageResponse = service.execute(query);

            // then
            assertThat(pageResponse.content()).hasSize(2);
            assertThat(pageResponse.content().get(0)).isNotNull();
            assertThat(pageResponse.content().get(1)).isNotNull();
            assertThat(pageResponse.totalElements()).isEqualTo(2);
            verify(readManager).search(query);
            verify(readManager).count(query);
        }

        @Test
        @DisplayName("권한이 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoPermissionsFound() {
            // given
            SearchPermissionsQuery query =
                    SearchPermissionsQuery.of(
                            "nonexistent", null, null, CREATED_FROM, CREATED_TO, 0, 20);

            given(readManager.search(query)).willReturn(List.of());
            given(readManager.count(query)).willReturn(0L);

            // when
            PageResponse<PermissionResponse> pageResponse = service.execute(query);

            // then
            assertThat(pageResponse.content()).isEmpty();
            assertThat(pageResponse.totalElements()).isZero();
            verify(readManager).search(query);
            verify(readManager).count(query);
        }

        @Test
        @DisplayName("리소스별로 권한을 필터링한다")
        void shouldFilterPermissionsByResource() {
            // given
            Permission permission = PermissionFixture.create("user", "read");
            SearchPermissionsQuery query =
                    SearchPermissionsQuery.of("user", null, null, CREATED_FROM, CREATED_TO, 0, 20);
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
            given(readManager.count(query)).willReturn(1L);
            given(assembler.toResponse(any(Permission.class))).willReturn(expectedResponse);

            // when
            PageResponse<PermissionResponse> pageResponse = service.execute(query);

            // then
            assertThat(pageResponse.content()).hasSize(1);
            assertThat(pageResponse.content().get(0).resource()).isEqualTo("user");
            assertThat(pageResponse.totalElements()).isEqualTo(1);
            verify(readManager).search(query);
            verify(readManager).count(query);
        }
    }
}
