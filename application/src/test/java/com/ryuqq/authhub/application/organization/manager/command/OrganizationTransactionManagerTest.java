package com.ryuqq.authhub.application.organization.manager.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.organization.port.out.command.OrganizationPersistencePort;
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
 * OrganizationTransactionManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationTransactionManager 단위 테스트")
class OrganizationTransactionManagerTest {

    @Mock private OrganizationPersistencePort persistencePort;

    private OrganizationTransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        transactionManager = new OrganizationTransactionManager(persistencePort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("조직을 영속화하고 반환한다")
        void shouldPersistOrganization() {
            // given
            Organization newOrganization = OrganizationFixture.createNew();
            Organization savedOrganization = OrganizationFixture.create();
            given(persistencePort.persist(newOrganization)).willReturn(savedOrganization);

            // when
            Organization result = transactionManager.persist(newOrganization);

            // then
            assertThat(result).isEqualTo(savedOrganization);
            verify(persistencePort).persist(newOrganization);
        }

        @Test
        @DisplayName("기존 조직을 업데이트한다")
        void shouldUpdateExistingOrganization() {
            // given
            Organization existingOrganization = OrganizationFixture.create();
            given(persistencePort.persist(existingOrganization)).willReturn(existingOrganization);

            // when
            Organization result = transactionManager.persist(existingOrganization);

            // then
            assertThat(result).isEqualTo(existingOrganization);
            verify(persistencePort).persist(existingOrganization);
        }
    }
}
