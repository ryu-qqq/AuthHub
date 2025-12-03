package com.ryuqq.authhub.adapter.in.rest.tenant.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ryuqq.authhub.application.tenant.dto.response.CreateTenantResponse;

/**
 * CreateTenantApiResponse 단위 테스트
 *
 * <p>검증 범위:
 * <ul>
 *   <li>Record 생성 및 접근자</li>
 *   <li>from() 팩토리 메서드</li>
 *   <li>equals/hashCode</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("dto")
@DisplayName("CreateTenantApiResponse 테스트")
class CreateTenantApiResponseTest {

    @Nested
    @DisplayName("Record 기본 동작 테스트")
    class RecordBasicBehaviorTest {

        @Test
        @DisplayName("tenantId가 주어지면 Record가 정상 생성된다")
        void givenTenantId_whenCreate_thenRecordCreated() {
            // given
            Long tenantId = 1L;

            // when
            CreateTenantApiResponse response = new CreateTenantApiResponse(tenantId);

            // then
            assertThat(response.tenantId()).isEqualTo(tenantId);
        }

        @Test
        @DisplayName("동일한 값을 가진 두 Record는 동등하다")
        void givenSameValues_whenCompare_thenEqual() {
            // given
            CreateTenantApiResponse response1 = new CreateTenantApiResponse(1L);
            CreateTenantApiResponse response2 = new CreateTenantApiResponse(1L);

            // then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }

    @Nested
    @DisplayName("from() 팩토리 메서드 테스트")
    class FactoryMethodTest {

        @Test
        @DisplayName("CreateTenantResponse로부터 정상적으로 변환된다")
        void givenUseCaseResponse_whenFrom_thenCorrectlyMapped() {
            // given
            Long tenantId = 1L;
            CreateTenantResponse useCaseResponse = new CreateTenantResponse(tenantId);

            // when
            CreateTenantApiResponse apiResponse = CreateTenantApiResponse.from(useCaseResponse);

            // then
            assertThat(apiResponse.tenantId()).isEqualTo(tenantId);
        }
    }
}
