package com.ryuqq.authhub.domain.service.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.service.fixture.ServiceFixture;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import com.ryuqq.authhub.domain.service.vo.ServiceDescription;
import com.ryuqq.authhub.domain.service.vo.ServiceName;
import com.ryuqq.authhub.domain.service.vo.ServiceStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Service Aggregate 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("Service Aggregate 테스트")
class ServiceTest {

    private static final Instant NOW = Instant.parse("2025-01-15T10:00:00Z");

    @Nested
    @DisplayName("Service 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("새로운 Service를 성공적으로 생성한다")
        void shouldCreateServiceSuccessfully() {
            // given
            ServiceCode serviceCode = ServiceCode.of("SVC_B2B");
            ServiceName name = ServiceName.of("B2B 서비스");
            ServiceDescription description = ServiceDescription.of("B2B 전용 서비스");

            // when
            Service service = Service.create(serviceCode, name, description, NOW);

            // then
            assertThat(service.serviceCodeValue()).isEqualTo("SVC_B2B");
            assertThat(service.nameValue()).isEqualTo("B2B 서비스");
            assertThat(service.descriptionValue()).isEqualTo("B2B 전용 서비스");
            assertThat(service.isActive()).isTrue();
            assertThat(service.isNew()).isTrue();
            assertThat(service.createdAt()).isEqualTo(NOW);
            assertThat(service.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("설명 없이 Service를 생성한다")
        void shouldCreateServiceWithoutDescription() {
            // given
            ServiceCode serviceCode = ServiceCode.of("SVC_MINIMAL");
            ServiceName name = ServiceName.of("Minimal Service");

            // when
            Service service = Service.create(serviceCode, name, null, NOW);

            // then
            assertThat(service.descriptionValue()).isNull();
            assertThat(service.isActive()).isTrue();
        }

        @Test
        @DisplayName("reconstitute로 Service를 재구성한다")
        void shouldReconstituteFromDatabase() {
            // given
            Service service = ServiceFixture.create();

            // then
            assertThat(service.serviceIdValue()).isEqualTo(ServiceFixture.defaultIdValue());
            assertThat(service.serviceCodeValue()).isEqualTo(ServiceFixture.defaultCodeValue());
            assertThat(service.isActive()).isTrue();
            assertThat(service.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("Service 이름 변경 테스트")
    class NameChangeTests {

        @Test
        @DisplayName("Service 이름을 변경한다")
        void shouldChangeName() {
            // given
            Service service = ServiceFixture.create();
            ServiceName newName = ServiceName.of("변경된 서비스명");

            // when
            service.changeName(newName, NOW);

            // then
            assertThat(service.nameValue()).isEqualTo("변경된 서비스명");
            assertThat(service.updatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("Service 설명 변경 테스트")
    class DescriptionChangeTests {

        @Test
        @DisplayName("Service 설명을 변경한다")
        void shouldChangeDescription() {
            // given
            Service service = ServiceFixture.create();
            ServiceDescription newDescription = ServiceDescription.of("변경된 설명");

            // when
            service.changeDescription(newDescription, NOW);

            // then
            assertThat(service.descriptionValue()).isEqualTo("변경된 설명");
            assertThat(service.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("Service 설명을 빈 값으로 변경한다")
        void shouldChangeDescriptionToEmpty() {
            // given
            Service service = ServiceFixture.create();

            // when
            service.changeDescription(null, NOW);

            // then
            assertThat(service.descriptionValue()).isNull();
            assertThat(service.updatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("Service 상태 변경 테스트")
    class StatusChangeTests {

        @Test
        @DisplayName("ACTIVE 상태를 INACTIVE로 변경한다")
        void shouldChangeStatusToInactive() {
            // given
            Service service = ServiceFixture.create();
            assertThat(service.isActive()).isTrue();

            // when
            service.changeStatus(ServiceStatus.INACTIVE, NOW);

            // then
            assertThat(service.isActive()).isFalse();
            assertThat(service.statusValue()).isEqualTo("INACTIVE");
            assertThat(service.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("INACTIVE 상태를 ACTIVE로 변경한다")
        void shouldChangeStatusToActive() {
            // given
            Service service = ServiceFixture.createInactive();
            assertThat(service.isActive()).isFalse();

            // when
            service.changeStatus(ServiceStatus.ACTIVE, NOW);

            // then
            assertThat(service.isActive()).isTrue();
            assertThat(service.statusValue()).isEqualTo("ACTIVE");
            assertThat(service.updatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("Service 수정 (UpdateData 패턴) 테스트")
    class UpdateDataTests {

        @Test
        @DisplayName("UpdateData로 이름만 변경한다")
        void shouldUpdateNameOnly() {
            // given
            Service service = ServiceFixture.create();
            ServiceUpdateData updateData = new ServiceUpdateData("새 이름", null, null);

            // when
            service.update(updateData, NOW);

            // then
            assertThat(service.nameValue()).isEqualTo("새 이름");
            assertThat(service.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("UpdateData로 설명만 변경한다")
        void shouldUpdateDescriptionOnly() {
            // given
            Service service = ServiceFixture.create();
            ServiceUpdateData updateData = new ServiceUpdateData(null, "새 설명", null);

            // when
            service.update(updateData, NOW);

            // then
            assertThat(service.descriptionValue()).isEqualTo("새 설명");
            assertThat(service.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("UpdateData로 상태만 변경한다")
        void shouldUpdateStatusOnly() {
            // given
            Service service = ServiceFixture.create();
            ServiceUpdateData updateData = new ServiceUpdateData(null, null, "INACTIVE");

            // when
            service.update(updateData, NOW);

            // then
            assertThat(service.statusValue()).isEqualTo("INACTIVE");
            assertThat(service.isActive()).isFalse();
            assertThat(service.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("UpdateData로 모든 필드를 변경한다")
        void shouldUpdateAllFields() {
            // given
            Service service = ServiceFixture.create();
            ServiceUpdateData updateData = new ServiceUpdateData("새 이름", "새 설명", "INACTIVE");

            // when
            service.update(updateData, NOW);

            // then
            assertThat(service.nameValue()).isEqualTo("새 이름");
            assertThat(service.descriptionValue()).isEqualTo("새 설명");
            assertThat(service.statusValue()).isEqualTo("INACTIVE");
            assertThat(service.updatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("Service Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("isActive는 ACTIVE 상태에서 true를 반환한다")
        void isActiveShouldReturnTrueWhenActive() {
            // given
            Service activeService = ServiceFixture.create();
            Service inactiveService = ServiceFixture.createInactive();

            // then
            assertThat(activeService.isActive()).isTrue();
            assertThat(inactiveService.isActive()).isFalse();
        }

        @Test
        @DisplayName("isNew는 ID가 없을 때 true를 반환한다")
        void isNewShouldReturnTrueWhenNoId() {
            // given
            Service newService = ServiceFixture.createNew();
            Service existingService = ServiceFixture.create();

            // then
            assertThat(newService.isNew()).isTrue();
            assertThat(existingService.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("Service equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 serviceId를 가진 Service는 동등하다")
        void shouldBeEqualWhenSameServiceId() {
            // given
            Service service1 = ServiceFixture.create();
            Service service2 = ServiceFixture.create();

            // then
            assertThat(service1).isEqualTo(service2);
            assertThat(service1.hashCode()).isEqualTo(service2.hashCode());
        }

        @Test
        @DisplayName("ID가 없을 때 serviceCode로 동등성을 판단한다")
        void shouldBeEqualBySameServiceCodeWhenNoId() {
            // given
            Service service1 =
                    Service.create(
                            ServiceCode.of("SVC_TEST"),
                            ServiceName.of("Test"),
                            ServiceDescription.of("Desc"),
                            NOW);
            Service service2 =
                    Service.create(
                            ServiceCode.of("SVC_TEST"),
                            ServiceName.of("Test"),
                            ServiceDescription.of("Desc"),
                            NOW);

            // then
            assertThat(service1).isEqualTo(service2);
            assertThat(service1.hashCode()).isEqualTo(service2.hashCode());
        }

        @Test
        @DisplayName("다른 serviceCode를 가진 Service는 동등하지 않다 (ID 없을 때)")
        void shouldNotBeEqualWhenDifferentServiceCode() {
            // given
            Service service1 =
                    Service.create(
                            ServiceCode.of("SVC_A"),
                            ServiceName.of("Service A"),
                            ServiceDescription.of("A"),
                            NOW);
            Service service2 =
                    Service.create(
                            ServiceCode.of("SVC_B"),
                            ServiceName.of("Service B"),
                            ServiceDescription.of("B"),
                            NOW);

            // then
            assertThat(service1).isNotEqualTo(service2);
        }
    }
}
