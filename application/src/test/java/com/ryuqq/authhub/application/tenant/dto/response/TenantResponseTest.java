package com.ryuqq.authhub.application.tenant.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TenantResponse DTO 설계 테스트
 *
 * <p>Kent Beck TDD - Red Phase: 실패하는 테스트 먼저 작성
 *
 * <p>설계 원칙:
 * <ul>
 *   <li>Pure Java Record (Lombok 금지)</li>
 *   <li>테넌트 정보 조회 응답 (ID, 이름, 상태, 시간정보)</li>
 *   <li>불변성 보장</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("TenantResponse DTO 설계")
class TenantResponseTest {

    @Nested
    @DisplayName("Record 구조 검증")
    class RecordStructure {

        @Test
        @DisplayName("Record 타입이어야 한다")
        void shouldBeRecord() {
            assertThat(TenantResponse.class.isRecord()).isTrue();
        }

        @Test
        @DisplayName("tenantId 필드를 가져야 한다")
        void shouldHaveTenantIdField() throws NoSuchMethodException {
            var method = TenantResponse.class.getMethod("tenantId");
            assertThat(method.getReturnType()).isEqualTo(Long.class);
        }

        @Test
        @DisplayName("name 필드를 가져야 한다")
        void shouldHaveNameField() throws NoSuchMethodException {
            var method = TenantResponse.class.getMethod("name");
            assertThat(method.getReturnType()).isEqualTo(String.class);
        }

        @Test
        @DisplayName("status 필드를 가져야 한다")
        void shouldHaveStatusField() throws NoSuchMethodException {
            var method = TenantResponse.class.getMethod("status");
            assertThat(method.getReturnType()).isEqualTo(String.class);
        }

        @Test
        @DisplayName("createdAt 필드를 가져야 한다")
        void shouldHaveCreatedAtField() throws NoSuchMethodException {
            var method = TenantResponse.class.getMethod("createdAt");
            assertThat(method.getReturnType()).isEqualTo(Instant.class);
        }

        @Test
        @DisplayName("updatedAt 필드를 가져야 한다")
        void shouldHaveUpdatedAtField() throws NoSuchMethodException {
            var method = TenantResponse.class.getMethod("updatedAt");
            assertThat(method.getReturnType()).isEqualTo(Instant.class);
        }
    }

    @Nested
    @DisplayName("인스턴스 생성")
    class InstanceCreation {

        @Test
        @DisplayName("모든 필드로 생성할 수 있어야 한다")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();

            var response = new TenantResponse(
                    1L,
                    "테스트 테넌트",
                    "ACTIVE",
                    now,
                    now
            );

            assertThat(response.tenantId()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("테스트 테넌트");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.createdAt()).isEqualTo(now);
            assertThat(response.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("필드 순서")
    class FieldOrder {

        @Test
        @DisplayName("필드 순서가 정확해야 한다 (tenantId, name, status, createdAt, updatedAt)")
        void shouldHaveCorrectFieldOrder() {
            var components = TenantResponse.class.getRecordComponents();

            assertThat(components).hasSize(5);
            assertThat(components[0].getName()).isEqualTo("tenantId");
            assertThat(components[1].getName()).isEqualTo("name");
            assertThat(components[2].getName()).isEqualTo("status");
            assertThat(components[3].getName()).isEqualTo("createdAt");
            assertThat(components[4].getName()).isEqualTo("updatedAt");
        }
    }
}
