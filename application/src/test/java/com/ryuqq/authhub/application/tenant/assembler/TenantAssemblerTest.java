package com.ryuqq.authhub.application.tenant.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
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

    private TenantAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new TenantAssembler();
    }

    @Nested
    @DisplayName("toResponse 메서드")
    class ToResponseTest {

        @Test
        @DisplayName("Domain을 Response로 변환한다")
        void shouldConvertDomainToResponse() {
            // given
            Tenant tenant = TenantFixture.create();

            // when
            TenantResponse response = assembler.toResponse(tenant);

            // then
            assertThat(response.tenantId()).isEqualTo(tenant.tenantIdValue());
            assertThat(response.name()).isEqualTo(tenant.nameValue());
            assertThat(response.status()).isEqualTo(tenant.statusValue());
            assertThat(response.createdAt()).isEqualTo(tenant.createdAt());
            assertThat(response.updatedAt()).isEqualTo(tenant.updatedAt());
        }
    }

    @Nested
    @DisplayName("toResponseList 메서드")
    class ToResponseListTest {

        @Test
        @DisplayName("Domain 목록을 Response 목록으로 변환한다")
        void shouldConvertDomainListToResponseList() {
            // given
            List<Tenant> tenants =
                    List.of(
                            TenantFixture.createWithName("Tenant A"),
                            TenantFixture.createWithName("Tenant B"));

            // when
            List<TenantResponse> responses = assembler.toResponseList(tenants);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).name()).isEqualTo("Tenant A");
            assertThat(responses.get(1).name()).isEqualTo("Tenant B");
        }

        @Test
        @DisplayName("빈 목록은 빈 목록을 반환한다")
        void shouldReturnEmptyListForEmptyInput() {
            // given
            List<Tenant> tenants = List.of();

            // when
            List<TenantResponse> responses = assembler.toResponseList(tenants);

            // then
            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("null 목록은 빈 목록을 반환한다")
        void shouldReturnEmptyListForNullInput() {
            // when
            List<TenantResponse> responses = assembler.toResponseList(null);

            // then
            assertThat(responses).isEmpty();
        }
    }
}
