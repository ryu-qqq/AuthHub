package com.ryuqq.authhub.adapter.in.rest.organization.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.CreateOrganizationApiRequest;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;

/**
 * OrganizationApiMapper 단위 테스트
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
@DisplayName("OrganizationApiMapper 테스트")
class OrganizationApiMapperTest {

    private final OrganizationApiMapper mapper = new OrganizationApiMapper();

    @Test
    @DisplayName("CreateOrganizationApiRequest → CreateOrganizationCommand 변환 성공")
    void givenApiRequest_whenToCommand_thenCorrectlyMapped() {
        // given
        Long tenantId = 1L;
        String name = "Engineering Team";
        CreateOrganizationApiRequest request = new CreateOrganizationApiRequest(tenantId, name);

        // when
        CreateOrganizationCommand command = mapper.toCreateOrganizationCommand(request);

        // then
        assertThat(command.tenantId()).isEqualTo(tenantId);
        assertThat(command.name()).isEqualTo(name);
    }
}
