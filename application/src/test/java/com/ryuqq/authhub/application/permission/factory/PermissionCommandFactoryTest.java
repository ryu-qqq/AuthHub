package com.ryuqq.authhub.application.permission.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;
import com.ryuqq.authhub.application.permission.fixture.PermissionCommandFixtures;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.PermissionUpdateData;
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
 * PermissionCommandFactory 단위 테스트
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
@DisplayName("PermissionCommandFactory 단위 테스트")
class PermissionCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private PermissionCommandFactory sut;

    private static final Instant FIXED_TIME = PermissionFixture.fixedTime();

    @BeforeEach
    void setUp() {
        sut = new PermissionCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드 (Domain 객체 생성)")
    class Create {

        @Test
        @DisplayName("성공: Command로부터 Permission 도메인 객체 생성")
        void shouldCreatePermission_FromCommand() {
            // given
            CreatePermissionCommand command = PermissionCommandFixtures.createCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            Permission result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.resourceValue()).isEqualTo(command.resource());
            assertThat(result.actionValue()).isEqualTo(command.action());
            assertThat(result.descriptionValue()).isEqualTo(command.description());
            assertThat(result.createdAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("시스템 권한 Command로부터 SYSTEM 타입 Permission 생성")
        void shouldCreateSystemPermission_WhenIsSystemTrue() {
            // given
            CreatePermissionCommand command = PermissionCommandFixtures.createSystemCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            Permission result = sut.create(command);

            // then
            assertThat(result.typeValue()).isEqualTo("SYSTEM");
        }

        @Test
        @DisplayName("TimeProvider를 통해 생성 시간 설정")
        void shouldSetCreatedAt_ThroughTimeProvider() {
            // given
            CreatePermissionCommand command = PermissionCommandFixtures.createCommand();
            Instant expectedTime = Instant.parse("2025-06-15T10:30:00Z");

            given(timeProvider.now()).willReturn(expectedTime);

            // when
            Permission result = sut.create(command);

            // then
            assertThat(result.createdAt()).isEqualTo(expectedTime);
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공: Command로부터 UpdateContext 생성")
        void shouldCreateUpdateContext_FromCommand() {
            // given
            UpdatePermissionCommand command = PermissionCommandFixtures.updateCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<PermissionId, PermissionUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.permissionId());
            assertThat(result.updateData().description()).isEqualTo(command.description());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }

    @Nested
    @DisplayName("createDeleteContext 메서드")
    class CreateDeleteContext {

        @Test
        @DisplayName("성공: Command로부터 StatusChangeContext 생성")
        void shouldCreateDeleteContext_FromCommand() {
            // given
            DeletePermissionCommand command = PermissionCommandFixtures.deleteCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            StatusChangeContext<PermissionId> result = sut.createDeleteContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.permissionId());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
