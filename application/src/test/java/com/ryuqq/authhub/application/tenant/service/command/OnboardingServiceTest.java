package com.ryuqq.authhub.application.tenant.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.tenant.dto.bundle.OnboardingBundle;
import com.ryuqq.authhub.application.tenant.dto.command.OnboardingCommand;
import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import com.ryuqq.authhub.application.tenant.factory.OnboardingFactory;
import com.ryuqq.authhub.application.tenant.fixture.TenantCommandFixtures;
import com.ryuqq.authhub.application.tenant.internal.OnboardingFacade;
import com.ryuqq.authhub.application.tenant.manager.OnboardingIdempotencyCommandManager;
import com.ryuqq.authhub.application.tenant.manager.OnboardingIdempotencyQueryManager;
import com.ryuqq.authhub.application.tenant.validator.OnboardingValidator;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OnboardingService 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Service는 오케스트레이션만 담당 → 협력 객체 호출 순서/조건 검증
 *   <li>BDDMockito 스타일로 Given-When-Then 구조 명확화
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OnboardingService 단위 테스트")
class OnboardingServiceTest {

    @Mock private OnboardingValidator validator;
    @Mock private OnboardingFactory onboardingFactory;
    @Mock private OnboardingFacade onboardingFacade;
    @Mock private OnboardingIdempotencyQueryManager idempotencyQueryManager;
    @Mock private OnboardingIdempotencyCommandManager idempotencyCommandManager;

    private OnboardingService sut;

    @BeforeEach
    void setUp() {
        sut =
                new OnboardingService(
                        onboardingFacade,
                        onboardingFactory,
                        validator,
                        idempotencyQueryManager,
                        idempotencyCommandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: 캐시 미스 시 Validator → Factory → Facade 순서로 호출하고 결과 캐싱")
        void shouldOrchestrate_WhenCacheMiss() {
            // given
            OnboardingCommand command = TenantCommandFixtures.createOnboardingCommand();
            OnboardingBundle bundle = TenantCommandFixtures.createOnboardingBundle();
            OnboardingResult expectedResult = new OnboardingResult("tenant-id", "organization-id");

            given(idempotencyQueryManager.findByIdempotencyKey(command.idempotencyKey()))
                    .willReturn(Optional.empty());
            given(onboardingFactory.create(command)).willReturn(bundle);
            given(onboardingFacade.persist(bundle)).willReturn(expectedResult);

            // when
            OnboardingResult result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedResult);
            assertThat(result.tenantId()).isEqualTo("tenant-id");
            assertThat(result.organizationId()).isEqualTo("organization-id");

            then(idempotencyQueryManager).should().findByIdempotencyKey(command.idempotencyKey());
            then(validator).should().validateNameNotDuplicated(any(TenantName.class));
            then(onboardingFactory).should().create(command);
            then(onboardingFacade).should().persist(bundle);
            then(idempotencyCommandManager)
                    .should()
                    .save(eq(command.idempotencyKey()), eq(expectedResult));
        }

        @Test
        @DisplayName("성공: 캐시 히트 시 저장된 결과 반환하고 실제 온보딩 실행 안함")
        void shouldReturnCachedResult_WhenCacheHit() {
            // given
            OnboardingCommand command = TenantCommandFixtures.createOnboardingCommand();
            OnboardingResult cachedResult = new OnboardingResult("cached-tenant", "cached-org");

            given(idempotencyQueryManager.findByIdempotencyKey(command.idempotencyKey()))
                    .willReturn(Optional.of(cachedResult));

            // when
            OnboardingResult result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(cachedResult);

            then(idempotencyQueryManager).should().findByIdempotencyKey(command.idempotencyKey());
            then(validator).shouldHaveNoInteractions();
            then(onboardingFactory).shouldHaveNoInteractions();
            then(onboardingFacade).shouldHaveNoInteractions();
            then(idempotencyCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패: 중복 테넌트 이름일 경우 DuplicateTenantNameException 발생")
        void shouldThrowException_WhenTenantNameIsDuplicated() {
            // given
            OnboardingCommand command = TenantCommandFixtures.createOnboardingCommand();
            TenantName name = TenantName.of(command.tenantName());

            given(idempotencyQueryManager.findByIdempotencyKey(command.idempotencyKey()))
                    .willReturn(Optional.empty());
            willThrow(new DuplicateTenantNameException(name))
                    .given(validator)
                    .validateNameNotDuplicated(any(TenantName.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(DuplicateTenantNameException.class);

            then(onboardingFactory).should(never()).create(any());
            then(onboardingFacade).should(never()).persist(any());
            then(idempotencyCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("검증 통과 후 Factory가 OnboardingBundle 생성")
        void shouldCreateBundle_WhenValidationPasses() {
            // given
            OnboardingCommand command = TenantCommandFixtures.createOnboardingCommand();
            OnboardingBundle bundle = TenantCommandFixtures.createOnboardingBundle();

            given(idempotencyQueryManager.findByIdempotencyKey(command.idempotencyKey()))
                    .willReturn(Optional.empty());
            given(onboardingFactory.create(command)).willReturn(bundle);
            given(onboardingFacade.persist(bundle))
                    .willReturn(new OnboardingResult("t-id", "o-id"));

            // when
            sut.execute(command);

            // then
            then(onboardingFactory).should().create(command);
        }

        @Test
        @DisplayName("Facade를 통해 번들 저장 수행")
        void shouldPersistBundle_ThroughFacade() {
            // given
            OnboardingCommand command = TenantCommandFixtures.createOnboardingCommand();
            OnboardingBundle bundle = TenantCommandFixtures.createOnboardingBundle();

            given(idempotencyQueryManager.findByIdempotencyKey(command.idempotencyKey()))
                    .willReturn(Optional.empty());
            given(onboardingFactory.create(command)).willReturn(bundle);
            given(onboardingFacade.persist(bundle))
                    .willReturn(new OnboardingResult("persisted-tenant", "persisted-org"));

            // when
            sut.execute(command);

            // then
            then(onboardingFacade).should().persist(bundle);
        }
    }
}
