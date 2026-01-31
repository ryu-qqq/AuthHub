package com.ryuqq.authhub.application.organization.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.organization.dto.response.OrganizationPageResult;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResult;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrganizationAssembler 단위 테스트
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
@DisplayName("OrganizationAssembler 단위 테스트")
class OrganizationAssemblerTest {

    private OrganizationAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new OrganizationAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드 (단건 변환)")
    class ToResult {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Result로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToResult() {
            // given
            Organization organization = OrganizationFixture.create();

            // when
            OrganizationResult result = sut.toResult(organization);

            // then
            assertThat(result.organizationId()).isEqualTo(organization.organizationIdValue());
            assertThat(result.tenantId()).isEqualTo(organization.tenantIdValue());
            assertThat(result.name()).isEqualTo(organization.nameValue());
            assertThat(result.status()).isEqualTo(organization.statusValue());
            assertThat(result.createdAt()).isEqualTo(organization.createdAt());
            assertThat(result.updatedAt()).isEqualTo(organization.updatedAt());
        }

        @Test
        @DisplayName("다양한 상태의 Organization이 올바르게 변환됨")
        void shouldMapCorrectly_ForDifferentStatuses() {
            // given
            Organization activeOrg = OrganizationFixture.create();
            Organization inactiveOrg = OrganizationFixture.createInactive();

            // when
            OrganizationResult activeResult = sut.toResult(activeOrg);
            OrganizationResult inactiveResult = sut.toResult(inactiveOrg);

            // then
            assertThat(activeResult.status()).isEqualTo("ACTIVE");
            assertThat(inactiveResult.status()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("toResultList 메서드 (목록 변환)")
    class ToResultList {

        @Test
        @DisplayName("성공: Domain 목록이 Result 목록으로 올바르게 변환됨")
        void shouldMapAllOrganizations_ToResultList() {
            // given
            Organization org1 = OrganizationFixture.createWithName("Org 1");
            Organization org2 = OrganizationFixture.createWithName("Org 2");
            List<Organization> organizations = List.of(org1, org2);

            // when
            List<OrganizationResult> results = sut.toResultList(organizations);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).name()).isEqualTo("Org 1");
            assertThat(results.get(1).name()).isEqualTo("Org 2");
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 목록 반환")
        void shouldReturnEmptyList_WhenInputIsEmpty() {
            // given
            List<Organization> emptyList = Collections.emptyList();

            // when
            List<OrganizationResult> results = sut.toResultList(emptyList);

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("null 입력 시 빈 목록 반환")
        void shouldReturnEmptyList_WhenInputIsNull() {
            // when
            List<OrganizationResult> results = sut.toResultList(null);

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
            Organization org1 = OrganizationFixture.createWithName("Page Org 1");
            Organization org2 = OrganizationFixture.createWithName("Page Org 2");
            List<Organization> organizations = List.of(org1, org2);
            int page = 0;
            int size = 10;
            long totalElements = 25L;

            // when
            OrganizationPageResult result =
                    sut.toPageResult(organizations, page, size, totalElements);

            // then
            assertThat(result.content()).hasSize(2);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }

        @Test
        @DisplayName("content의 각 항목이 올바르게 변환됨")
        void shouldMapContentItems_Correctly() {
            // given
            Organization org = OrganizationFixture.create();
            List<Organization> organizations = List.of(org);

            // when
            OrganizationPageResult result = sut.toPageResult(organizations, 0, 10, 1L);

            // then
            OrganizationResult content = result.content().get(0);
            assertThat(content.organizationId()).isEqualTo(org.organizationIdValue());
            assertThat(content.name()).isEqualTo(org.nameValue());
        }

        @Test
        @DisplayName("빈 목록으로 PageResult 생성 가능")
        void shouldCreatePageResult_WithEmptyContent() {
            // given
            List<Organization> emptyList = Collections.emptyList();

            // when
            OrganizationPageResult result = sut.toPageResult(emptyList, 0, 10, 0L);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }
    }
}
