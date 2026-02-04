package com.ryuqq.authhub.application.service.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.service.dto.response.ServicePageResult;
import com.ryuqq.authhub.application.service.dto.response.ServiceResult;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.fixture.ServiceFixture;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ServiceAssembler 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Assembler는 Domain → Result 변환 담당 (순수 변환 → Mock 불필요)
 *   <li>변환 결과의 필드값 정합성 검증에 집중
 *   <li>null/빈 목록 등 경계 조건 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ServiceAssembler 단위 테스트")
class ServiceAssemblerTest {

    private ServiceAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ServiceAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공: Service Domain → ServiceResult 변환")
        void shouldConvertServiceToResult() {
            // given
            Service service = ServiceFixture.create();

            // when
            ServiceResult result = sut.toResult(service);

            // then
            assertThat(result.serviceId()).isEqualTo(service.serviceIdValue());
            assertThat(result.serviceCode()).isEqualTo(service.serviceCodeValue());
            assertThat(result.name()).isEqualTo(service.nameValue());
            assertThat(result.description()).isEqualTo(service.descriptionValue());
            assertThat(result.status()).isEqualTo(service.statusValue());
            assertThat(result.createdAt()).isEqualTo(service.createdAt());
            assertThat(result.updatedAt()).isEqualTo(service.updatedAt());
        }

        @Test
        @DisplayName("비활성 Service도 정상 변환")
        void shouldConvertInactiveService() {
            // given
            Service service = ServiceFixture.createInactive();

            // when
            ServiceResult result = sut.toResult(service);

            // then
            assertThat(result.status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("설명 없는 Service도 정상 변환")
        void shouldConvertServiceWithoutDescription() {
            // given
            Service service = ServiceFixture.createWithoutDescription();

            // when
            ServiceResult result = sut.toResult(service);

            // then
            assertThat(result.serviceId()).isEqualTo(service.serviceIdValue());
            assertThat(result.serviceCode()).isEqualTo(service.serviceCodeValue());
        }
    }

    @Nested
    @DisplayName("toResultList 메서드")
    class ToResultList {

        @Test
        @DisplayName("성공: Service 목록 → ServiceResult 목록 변환")
        void shouldConvertServiceListToResultList() {
            // given
            List<Service> services =
                    List.of(ServiceFixture.create(), ServiceFixture.createWithCode("SVC_B2B"));

            // when
            List<ServiceResult> result = sut.toResultList(services);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).serviceCode()).isEqualTo("SVC_STORE");
            assertThat(result.get(1).serviceCode()).isEqualTo("SVC_B2B");
        }

        @Test
        @DisplayName("빈 목록이면 빈 리스트 반환")
        void shouldReturnEmptyList_WhenEmptyInput() {
            // given
            List<Service> services = Collections.emptyList();

            // when
            List<ServiceResult> result = sut.toResultList(services);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null이면 빈 리스트 반환")
        void shouldReturnEmptyList_WhenNullInput() {
            // when
            List<ServiceResult> result = sut.toResultList(null);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult 메서드")
    class ToPageResult {

        @Test
        @DisplayName("성공: Service 목록 + 페이징 정보 → ServicePageResult 변환")
        void shouldConvertToPageResult() {
            // given
            List<Service> services = List.of(ServiceFixture.create());
            int page = 0;
            int size = 20;
            long totalElements = 1L;

            // when
            ServicePageResult result = sut.toPageResult(services, page, size, totalElements);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.pageMeta()).isNotNull();
        }

        @Test
        @DisplayName("빈 목록일 때 빈 content 반환")
        void shouldReturnEmptyContent_WhenEmptyList() {
            // given
            List<Service> services = Collections.emptyList();

            // when
            ServicePageResult result = sut.toPageResult(services, 0, 20, 0L);

            // then
            assertThat(result.content()).isEmpty();
        }
    }
}
