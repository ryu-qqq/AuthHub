package com.ryuqq.authhub.adapter.in.rest.tenant.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.CreateTenantApiRequest;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;

/**
 * TenantApiMapper 단위 테스트
 *
 * <p>검증 범위:
 * <ul>
 *   <li>API Request → Application Command 변환</li>
 *   <li>모든 필드 매핑 검증</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("mapper")
@DisplayName("TenantApiMapper 테스트")
class TenantApiMapperTest {

    private final TenantApiMapper mapper = new TenantApiMapper();

    @Test
    @DisplayName("CreateTenantApiRequest → CreateTenantCommand 변환 성공")
    void givenApiRequest_whenToCommand_thenCorrectlyMapped() {
        // given
        String name = "Enterprise Tenant";
        CreateTenantApiRequest request = new CreateTenantApiRequest(name);

        // when
        CreateTenantCommand command = mapper.toCreateTenantCommand(request);

        // then
        assertThat(command.name()).isEqualTo(name);
    }
}
