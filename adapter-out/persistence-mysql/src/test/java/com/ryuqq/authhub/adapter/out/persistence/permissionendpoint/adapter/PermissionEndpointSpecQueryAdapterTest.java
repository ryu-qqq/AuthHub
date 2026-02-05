package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository.PermissionEndpointQueryDslRepository;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecResult;
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
 * PermissionEndpointSpecQueryAdapter 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Adapter는 QueryDslRepository 위임만 담당
 *   <li>Repository를 Mock으로 대체
 *   <li>Gateway용 스펙 조회 흐름 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionEndpointSpecQueryAdapter 단위 테스트")
class PermissionEndpointSpecQueryAdapterTest {

    @Mock private PermissionEndpointQueryDslRepository queryDslRepository;

    private PermissionEndpointSpecQueryAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionEndpointSpecQueryAdapter(queryDslRepository);
    }

    @Nested
    @DisplayName("findAllActiveSpecs 메서드")
    class FindAllActiveSpecs {

        @Test
        @DisplayName("성공: Repository 결과를 그대로 반환")
        void shouldReturnSpecs_FromRepository() {
            // given
            List<EndpointPermissionSpecResult> expected =
                    List.of(
                            new EndpointPermissionSpecResult(
                                    "authhub",
                                    "/api/v1/users",
                                    "GET",
                                    List.of("user:read"),
                                    List.of(),
                                    false,
                                    "사용자 조회"));

            given(queryDslRepository.findAllActiveSpecs()).willReturn(expected);

            // when
            List<EndpointPermissionSpecResult> result = sut.findAllActiveSpecs();

            // then
            assertThat(result).isEqualTo(expected);
            then(queryDslRepository).should().findAllActiveSpecs();
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            given(queryDslRepository.findAllActiveSpecs()).willReturn(List.of());
            assertThat(sut.findAllActiveSpecs()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findLatestUpdatedAt 메서드")
    class FindLatestUpdatedAt {

        @Test
        @DisplayName("성공: Repository 결과를 그대로 반환")
        void shouldReturnInstant_FromRepository() {
            Instant expected = Instant.parse("2025-01-15T12:00:00Z");
            given(queryDslRepository.findLatestUpdatedAt()).willReturn(expected);

            Instant result = sut.findLatestUpdatedAt();

            assertThat(result).isEqualTo(expected);
            then(queryDslRepository).should().findLatestUpdatedAt();
        }

        @Test
        @DisplayName("null이면 null 반환")
        void shouldReturnNull_WhenRepositoryReturnsNull() {
            given(queryDslRepository.findLatestUpdatedAt()).willReturn(null);
            assertThat(sut.findLatestUpdatedAt()).isNull();
        }
    }
}
