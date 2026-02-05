package com.ryuqq.authhub.application.tenantservice.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServicePageResult;
import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServiceResult;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.fixture.TenantServiceFixture;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantServiceAssembler 단위 테스트
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
@DisplayName("TenantServiceAssembler 단위 테스트")
class TenantServiceAssemblerTest {

    private TenantServiceAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new TenantServiceAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드 (단건 변환)")
    class ToResult {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Result로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToResult() {
            // given
            TenantService tenantService = TenantServiceFixture.create();

            // when
            TenantServiceResult result = sut.toResult(tenantService);

            // then
            assertThat(result.tenantServiceId()).isEqualTo(tenantService.tenantServiceIdValue());
            assertThat(result.tenantId()).isEqualTo(tenantService.tenantIdValue());
            assertThat(result.serviceId()).isEqualTo(tenantService.serviceIdValue());
            assertThat(result.status()).isEqualTo(tenantService.statusValue());
            assertThat(result.subscribedAt()).isEqualTo(tenantService.subscribedAt());
            assertThat(result.createdAt()).isEqualTo(tenantService.createdAt());
            assertThat(result.updatedAt()).isEqualTo(tenantService.updatedAt());
        }

        @Test
        @DisplayName("INACTIVE 상태의 Domain도 올바르게 변환")
        void shouldMapInactiveTenantService_ToResult() {
            // given
            TenantService tenantService =
                    TenantServiceFixture.createWithStatus(TenantServiceStatus.INACTIVE);

            // when
            TenantServiceResult result = sut.toResult(tenantService);

            // then
            assertThat(result.status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("SUSPENDED 상태의 Domain도 올바르게 변환")
        void shouldMapSuspendedTenantService_ToResult() {
            // given
            TenantService tenantService =
                    TenantServiceFixture.createWithStatus(TenantServiceStatus.SUSPENDED);

            // when
            TenantServiceResult result = sut.toResult(tenantService);

            // then
            assertThat(result.status()).isEqualTo("SUSPENDED");
        }
    }

    @Nested
    @DisplayName("toResultList 메서드 (목록 변환)")
    class ToResultList {

        @Test
        @DisplayName("성공: Domain 목록이 Result 목록으로 올바르게 변환됨")
        void shouldMapAllTenantServices_ToResultList() {
            // given
            TenantService ts1 = TenantServiceFixture.create();
            TenantService ts2 = TenantServiceFixture.createInactive();
            List<TenantService> tenantServices = List.of(ts1, ts2);

            // when
            List<TenantServiceResult> results = sut.toResultList(tenantServices);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).status()).isEqualTo("ACTIVE");
            assertThat(results.get(1).status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 목록 반환")
        void shouldReturnEmptyList_WhenInputIsEmpty() {
            // given
            List<TenantService> emptyList = Collections.emptyList();

            // when
            List<TenantServiceResult> results = sut.toResultList(emptyList);

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("null 입력 시 빈 목록 반환")
        void shouldReturnEmptyList_WhenInputIsNull() {
            // when
            List<TenantServiceResult> results = sut.toResultList(null);

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
            TenantService ts1 = TenantServiceFixture.create();
            TenantService ts2 = TenantServiceFixture.createInactive();
            List<TenantService> tenantServices = List.of(ts1, ts2);
            int page = 0;
            int size = 10;
            long totalElements = 25L;

            // when
            TenantServicePageResult result =
                    sut.toPageResult(tenantServices, page, size, totalElements);

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
            List<TenantService> emptyList = Collections.emptyList();

            // when
            TenantServicePageResult result = sut.toPageResult(emptyList, 0, 10, 0L);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("페이지 번호와 크기가 올바르게 설정됨")
        void shouldSetPageMetadata_Correctly() {
            // given
            TenantService tenantService = TenantServiceFixture.create();
            List<TenantService> tenantServices = List.of(tenantService);

            // when
            TenantServicePageResult result = sut.toPageResult(tenantServices, 2, 20, 100L);

            // then
            assertThat(result.pageMeta().page()).isEqualTo(2);
            assertThat(result.pageMeta().size()).isEqualTo(20);
            assertThat(result.pageMeta().totalPages()).isEqualTo(5);
        }
    }
}
