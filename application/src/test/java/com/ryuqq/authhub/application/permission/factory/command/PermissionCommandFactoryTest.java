package com.ryuqq.authhub.application.permission.factory.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import java.time.Clock;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionCommandFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionCommandFactory 단위 테스트")
class PermissionCommandFactoryTest {

    private PermissionCommandFactory factory;
    private Clock clock;
    private UuidHolder uuidHolder;

    @BeforeEach
    void setUp() {
        clock = PermissionFixture.fixedClock();
        uuidHolder = UUID::randomUUID;
        factory = new PermissionCommandFactory(clock, uuidHolder);
    }

    @Nested
    @DisplayName("create 메서드")
    class CreateTest {

        @Test
        @DisplayName("시스템 권한을 생성한다")
        void shouldCreateSystemPermission() {
            // given
            CreatePermissionCommand command =
                    new CreatePermissionCommand("user", "admin", "시스템 관리자 권한", true);

            // when
            Permission result = factory.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.keyValue()).isEqualTo("user:admin");
            assertThat(result.resourceValue()).isEqualTo("user");
            assertThat(result.actionValue()).isEqualTo("admin");
            assertThat(result.isSystem()).isTrue();
        }

        @Test
        @DisplayName("커스텀 권한을 생성한다")
        void shouldCreateCustomPermission() {
            // given
            CreatePermissionCommand command =
                    new CreatePermissionCommand("report", "export", "리포트 내보내기 권한", false);

            // when
            Permission result = factory.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.keyValue()).isEqualTo("report:export");
            assertThat(result.resourceValue()).isEqualTo("report");
            assertThat(result.actionValue()).isEqualTo("export");
            assertThat(result.isSystem()).isFalse();
        }

        @Test
        @DisplayName("description이 null이면 null로 생성한다")
        void shouldCreateWithNullDescription() {
            // given
            CreatePermissionCommand command =
                    new CreatePermissionCommand("dashboard", "view", null, false);

            // when
            Permission result = factory.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.keyValue()).isEqualTo("dashboard:view");
        }

        @Test
        @DisplayName("다양한 resource와 action 조합으로 권한을 생성한다")
        void shouldCreatePermissionWithVariousResourceAndAction() {
            // given
            CreatePermissionCommand command1 =
                    new CreatePermissionCommand("organization", "manage", "조직 관리 권한", true);
            CreatePermissionCommand command2 =
                    new CreatePermissionCommand("tenant", "read", "테넌트 조회 권한", false);

            // when
            Permission result1 = factory.create(command1);
            Permission result2 = factory.create(command2);

            // then
            assertThat(result1.keyValue()).isEqualTo("organization:manage");
            assertThat(result1.isSystem()).isTrue();
            assertThat(result2.keyValue()).isEqualTo("tenant:read");
            assertThat(result2.isSystem()).isFalse();
        }
    }
}
