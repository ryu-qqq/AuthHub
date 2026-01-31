package com.ryuqq.authhub.adapter.out.persistence.token.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.token.dto.UserContextProjection;
import com.ryuqq.authhub.application.token.dto.composite.UserContextComposite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserContextCompositeMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserContextCompositeMapper 단위 테스트")
class UserContextCompositeMapperTest {

    private UserContextCompositeMapper sut;

    @BeforeEach
    void setUp() {
        sut = new UserContextCompositeMapper();
    }

    @Nested
    @DisplayName("toComposite 메서드")
    class ToComposite {

        @Test
        @DisplayName("성공: Projection의 모든 필드가 Composite로 올바르게 매핑됨")
        void shouldMapAllFields_FromProjectionToComposite() {
            // given
            UserContextProjection projection =
                    new UserContextProjection(
                            "user-123",
                            "test@example.com",
                            "Test User",
                            "tenant-456",
                            "Test Tenant",
                            "org-789",
                            "Test Organization");

            // when
            UserContextComposite result = sut.toComposite(projection);

            // then
            assertThat(result.userId()).isEqualTo("user-123");
            assertThat(result.email()).isEqualTo("test@example.com");
            assertThat(result.name()).isEqualTo("Test User");
            assertThat(result.tenantId()).isEqualTo("tenant-456");
            assertThat(result.tenantName()).isEqualTo("Test Tenant");
            assertThat(result.organizationId()).isEqualTo("org-789");
            assertThat(result.organizationName()).isEqualTo("Test Organization");
        }

        @Test
        @DisplayName("성공: 다른 값으로 Projection 변환")
        void shouldMapDifferentValues_Correctly() {
            // given
            UserContextProjection projection =
                    new UserContextProjection(
                            "019450eb-4f1e-7000-8000-000000000001",
                            "admin@company.com",
                            "admin@company.com",
                            "019450eb-4f1e-7000-8000-000000000002",
                            "Company Tenant",
                            "019450eb-4f1e-7000-8000-000000000003",
                            "Engineering Org");

            // when
            UserContextComposite result = sut.toComposite(projection);

            // then
            assertThat(result.userId()).isEqualTo("019450eb-4f1e-7000-8000-000000000001");
            assertThat(result.email()).isEqualTo("admin@company.com");
            assertThat(result.name()).isEqualTo("admin@company.com");
            assertThat(result.tenantId()).isEqualTo("019450eb-4f1e-7000-8000-000000000002");
            assertThat(result.tenantName()).isEqualTo("Company Tenant");
            assertThat(result.organizationId()).isEqualTo("019450eb-4f1e-7000-8000-000000000003");
            assertThat(result.organizationName()).isEqualTo("Engineering Org");
        }
    }
}
