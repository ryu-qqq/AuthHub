package com.ryuqq.authhub.application.organization.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.organization.port.out.command.OrganizationCommandPort;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OrganizationCommandManager 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>CommandManager는 CommandPort 위임 + @Transactional 관리 담당
 *   <li>Port 호출이 올바르게 위임되는지 검증
 *   <li>반환값이 올바르게 전달되는지 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationCommandManager 단위 테스트")
class OrganizationCommandManagerTest {

    @Mock private OrganizationCommandPort persistencePort;

    private OrganizationCommandManager sut;

    @BeforeEach
    void setUp() {
        sut = new OrganizationCommandManager(persistencePort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: Organization 영속화 후 ID 반환")
        void shouldPersistAndReturnId() {
            // given
            Organization organization = OrganizationFixture.createNew();
            String expectedId = OrganizationFixture.defaultIdString();

            given(persistencePort.persist(organization)).willReturn(expectedId);

            // when
            String result = sut.persist(organization);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(persistencePort).should().persist(organization);
        }

        @Test
        @DisplayName("CommandPort에 Organization을 위임하여 영속화")
        void shouldDelegateToPersistencePort() {
            // given
            Organization organization = OrganizationFixture.create();
            String persistedId = "persisted-org-id";

            given(persistencePort.persist(organization)).willReturn(persistedId);

            // when
            sut.persist(organization);

            // then
            then(persistencePort).should().persist(organization);
        }

        @Test
        @DisplayName("새 조직 영속화 시 Port가 반환한 ID를 그대로 반환")
        void shouldReturnIdFromPort_WhenPersistingNewOrganization() {
            // given
            Organization newOrganization = OrganizationFixture.createNew();
            String generatedId = "new-generated-id";

            given(persistencePort.persist(newOrganization)).willReturn(generatedId);

            // when
            String result = sut.persist(newOrganization);

            // then
            assertThat(result).isEqualTo(generatedId);
        }

        @Test
        @DisplayName("기존 조직 업데이트 시에도 ID 반환")
        void shouldReturnId_WhenUpdatingExistingOrganization() {
            // given
            Organization existingOrganization = OrganizationFixture.create();
            String existingId = OrganizationFixture.defaultIdString();

            given(persistencePort.persist(existingOrganization)).willReturn(existingId);

            // when
            String result = sut.persist(existingOrganization);

            // then
            assertThat(result).isEqualTo(existingId);
        }
    }
}
