package com.ryuqq.authhub.application.permission.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionAssembler 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionAssembler 단위 테스트")
class PermissionAssemblerTest {

    private PermissionAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new PermissionAssembler();
    }

    @Nested
    @DisplayName("toResponse 메서드")
    class ToResponseTest {

        @Test
        @DisplayName("Permission을 PermissionResponse로 변환한다")
        void shouldConvertToResponse() {
            // given
            Permission permission = PermissionFixture.createReconstituted();

            // when
            PermissionResponse result = assembler.toResponse(permission);

            // then
            assertThat(result).isNotNull();
            assertThat(result.permissionId()).isEqualTo(permission.permissionIdValue());
            assertThat(result.key()).isEqualTo(permission.keyValue());
            assertThat(result.resource()).isEqualTo(permission.resourceValue());
            assertThat(result.action()).isEqualTo(permission.actionValue());
            assertThat(result.description()).isEqualTo(permission.descriptionValue());
            assertThat(result.type()).isEqualTo("CUSTOM");
            assertThat(result.createdAt()).isEqualTo(permission.createdAt());
            assertThat(result.updatedAt()).isEqualTo(permission.updatedAt());
        }

        @Test
        @DisplayName("시스템 권한을 PermissionResponse로 변환한다")
        void shouldConvertSystemPermissionToResponse() {
            // given
            Permission systemPermission =
                    PermissionFixture.createReconstitutedSystem(
                            java.util.UUID.randomUUID(), "user:admin");

            // when
            PermissionResponse result = assembler.toResponse(systemPermission);

            // then
            assertThat(result).isNotNull();
            assertThat(result.type()).isEqualTo("SYSTEM");
        }

        @Test
        @DisplayName("커스텀 권한의 타입이 CUSTOM으로 설정된다")
        void shouldSetTypeToCustomForCustomPermission() {
            // given
            Permission customPermission = PermissionFixture.createReconstituted();

            // when
            PermissionResponse result = assembler.toResponse(customPermission);

            // then
            assertThat(result.type()).isEqualTo("CUSTOM");
        }
    }
}
