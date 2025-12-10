package com.ryuqq.authhub.application.organization.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrganizationAssembler 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("OrganizationAssembler 단위 테스트")
class OrganizationAssemblerTest {

    private OrganizationAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new OrganizationAssembler();
    }

    @Nested
    @DisplayName("toResponse 메서드")
    class ToResponseTest {

        @Test
        @DisplayName("Organization을 OrganizationResponse로 변환한다")
        void shouldConvertToResponse() {
            // given
            Organization organization = OrganizationFixture.create();

            // when
            OrganizationResponse result = assembler.toResponse(organization);

            // then
            assertThat(result).isNotNull();
            assertThat(result.organizationId()).isEqualTo(organization.organizationIdValue());
            assertThat(result.tenantId()).isEqualTo(organization.tenantIdValue());
            assertThat(result.name()).isEqualTo(organization.nameValue());
            assertThat(result.status()).isEqualTo(organization.statusValue());
            assertThat(result.createdAt()).isEqualTo(organization.createdAt());
            assertThat(result.updatedAt()).isEqualTo(organization.updatedAt());
        }

        @Test
        @DisplayName("비활성화된 조직을 올바르게 변환한다")
        void shouldConvertInactiveOrganization() {
            // given
            Organization inactiveOrg = OrganizationFixture.createInactive();

            // when
            OrganizationResponse result = assembler.toResponse(inactiveOrg);

            // then
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("toResponseList 메서드")
    class ToResponseListTest {

        @Test
        @DisplayName("Organization 목록을 OrganizationResponse 목록으로 변환한다")
        void shouldConvertToResponseList() {
            // given
            List<Organization> organizations =
                    List.of(
                            OrganizationFixture.create(),
                            OrganizationFixture.createWithName("Second Org"));

            // when
            List<OrganizationResponse> result = assembler.toResponseList(organizations);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).name()).isEqualTo("Test Organization");
            assertThat(result.get(1).name()).isEqualTo("Second Org");
        }

        @Test
        @DisplayName("빈 목록은 빈 목록을 반환한다")
        void shouldReturnEmptyListForEmptyInput() {
            // when
            List<OrganizationResponse> result = assembler.toResponseList(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null 목록은 빈 목록을 반환한다")
        void shouldReturnEmptyListForNullInput() {
            // when
            List<OrganizationResponse> result = assembler.toResponseList(null);

            // then
            assertThat(result).isEmpty();
        }
    }
}
