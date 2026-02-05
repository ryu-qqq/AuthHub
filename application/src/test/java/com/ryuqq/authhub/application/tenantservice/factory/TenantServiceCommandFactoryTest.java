package com.ryuqq.authhub.application.tenantservice.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.tenantservice.dto.command.SubscribeTenantServiceCommand;
import com.ryuqq.authhub.application.tenantservice.dto.command.UpdateTenantServiceStatusCommand;
import com.ryuqq.authhub.application.tenantservice.fixture.TenantServiceCommandFixtures;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.fixture.TenantServiceFixture;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantServiceCommandFactory 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Factory는 Command → Domain 변환 담당
 *   <li>시간 생성 로직이 올바르게 적용되는지 검증
 *   <li>Domain 객체의 상태가 올바르게 초기화되는지 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantServiceCommandFactory 단위 테스트")
class TenantServiceCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private TenantServiceCommandFactory sut;

    private static final Instant FIXED_TIME = TenantServiceFixture.fixedTime();

    @BeforeEach
    void setUp() {
        sut = new TenantServiceCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드 (Domain 객체 생성)")
    class Create {

        @Test
        @DisplayName("성공: Command로부터 TenantService 도메인 객체 생성")
        void shouldCreateTenantService_FromCommand() {
            // given
            SubscribeTenantServiceCommand command = TenantServiceCommandFixtures.subscribeCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            TenantService result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tenantIdValue()).isEqualTo(command.tenantId());
            assertThat(result.serviceIdValue()).isEqualTo(command.serviceId());
            assertThat(result.subscribedAt()).isEqualTo(FIXED_TIME);
            assertThat(result.createdAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("TimeProvider를 통해 생성 시간 설정")
        void shouldSetCreatedAt_ThroughTimeProvider() {
            // given
            SubscribeTenantServiceCommand command = TenantServiceCommandFixtures.subscribeCommand();
            Instant expectedTime = Instant.parse("2025-06-15T10:30:00Z");

            given(timeProvider.now()).willReturn(expectedTime);

            // when
            TenantService result = sut.create(command);

            // then
            assertThat(result.subscribedAt()).isEqualTo(expectedTime);
            assertThat(result.createdAt()).isEqualTo(expectedTime);
        }

        @Test
        @DisplayName("생성된 TenantService는 ACTIVE 상태")
        void shouldCreateTenantService_WithActiveStatus() {
            // given
            SubscribeTenantServiceCommand command = TenantServiceCommandFixtures.subscribeCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            TenantService result = sut.create(command);

            // then
            assertThat(result.statusValue()).isEqualTo("ACTIVE");
        }
    }

    @Nested
    @DisplayName("createStatusChangeContext 메서드")
    class CreateStatusChangeContext {

        @Test
        @DisplayName("성공: Command로부터 StatusChangeContext 생성")
        void shouldCreateStatusChangeContext_FromCommand() {
            // given
            UpdateTenantServiceStatusCommand command =
                    TenantServiceCommandFixtures.updateStatusCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            StatusChangeContext<TenantServiceId> result = sut.createStatusChangeContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.tenantServiceId());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("TimeProvider를 통해 변경 시간 설정")
        void shouldSetChangedAt_ThroughTimeProvider() {
            // given
            UpdateTenantServiceStatusCommand command =
                    TenantServiceCommandFixtures.updateStatusCommand();
            Instant expectedTime = Instant.parse("2025-07-20T14:00:00Z");

            given(timeProvider.now()).willReturn(expectedTime);

            // when
            StatusChangeContext<TenantServiceId> result = sut.createStatusChangeContext(command);

            // then
            assertThat(result.changedAt()).isEqualTo(expectedTime);
        }
    }
}
