package com.ryuqq.authhub.application.tenant.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.tenant.dto.response.TenantConfigResult;
import com.ryuqq.authhub.application.tenant.dto.response.TenantPageResult;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResult;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantAssembler 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantAssembler 단위 테스트")
class TenantAssemblerTest {

    private TenantAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new TenantAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Result로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToResult() {
            // given
            Tenant tenant = TenantFixture.create();

            // when
            TenantResult result = sut.toResult(tenant);

            // then
            assertThat(result.tenantId()).isEqualTo(tenant.tenantIdValue());
            assertThat(result.name()).isEqualTo(tenant.nameValue());
            assertThat(result.status()).isEqualTo(tenant.statusValue());
            assertThat(result.createdAt()).isEqualTo(tenant.createdAt());
            assertThat(result.updatedAt()).isEqualTo(tenant.updatedAt());
        }
    }

    @Nested
    @DisplayName("toResultList 메서드")
    class ToResultList {

        @Test
        @DisplayName("성공: Domain 목록이 Result 목록으로 올바르게 변환됨")
        void shouldMapAllTenants_ToResultList() {
            // given
            Tenant t1 = TenantFixture.createWithName("Tenant 1");
            Tenant t2 = TenantFixture.createWithName("Tenant 2");
            List<Tenant> tenants = List.of(t1, t2);

            // when
            List<TenantResult> results = sut.toResultList(tenants);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).name()).isEqualTo("Tenant 1");
            assertThat(results.get(1).name()).isEqualTo("Tenant 2");
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 목록 반환")
        void shouldReturnEmptyList_WhenInputIsEmpty() {
            // when
            List<TenantResult> results = sut.toResultList(Collections.emptyList());

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult 메서드")
    class ToPageResult {

        @Test
        @DisplayName("성공: Domain 목록과 페이징 정보가 PageResult로 올바르게 변환됨")
        void shouldCreatePageResult_WithCorrectPagination() {
            // given
            Tenant tenant = TenantFixture.create();
            List<Tenant> tenants = List.of(tenant);
            int page = 0;
            int size = 10;
            long totalElements = 25L;

            // when
            TenantPageResult result = sut.toPageResult(tenants, page, size, totalElements);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }
    }

    @Nested
    @DisplayName("toConfigResult 메서드")
    class ToConfigResult {

        @Test
        @DisplayName("성공: Domain이 TenantConfigResult로 올바르게 변환됨")
        void shouldMapToConfigResult() {
            // given
            Tenant tenant = TenantFixture.create();

            // when
            TenantConfigResult result = sut.toConfigResult(tenant);

            // then
            assertThat(result.tenantId()).isEqualTo(tenant.tenantIdValue());
            assertThat(result.name()).isEqualTo(tenant.nameValue());
            assertThat(result.status()).isEqualTo(tenant.statusValue());
            assertThat(result.active()).isEqualTo(tenant.isActive());
        }
    }
}
