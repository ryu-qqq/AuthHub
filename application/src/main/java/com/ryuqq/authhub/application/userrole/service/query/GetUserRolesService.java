package com.ryuqq.authhub.application.userrole.service.query;

import com.ryuqq.authhub.application.userrole.assembler.UserRoleAssembler;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import com.ryuqq.authhub.application.userrole.dto.response.UserRolesResponse;
import com.ryuqq.authhub.application.userrole.facade.UserRoleReadFacade;
import com.ryuqq.authhub.application.userrole.port.in.query.GetUserRolesUseCase;
import com.ryuqq.authhub.domain.user.id.UserId;
import org.springframework.stereotype.Service;

/**
 * GetUserRolesService - 사용자 역할/권한 조회 Service
 *
 * <p>사용자에게 할당된 역할 및 권한 목록을 조회하는 비즈니스 로직을 처리합니다.
 *
 * <p><strong>사용처:</strong>
 *
 * <ul>
 *   <li>JWT 토큰 발급 시 호출
 *   <li>사용자 역할/권한 정보 조회 API
 * </ul>
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>UserRoleReadFacade로 역할 이름/권한 키 조회
 *   <li>Assembler로 응답 DTO 조립
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GetUserRolesService implements GetUserRolesUseCase {

    private final UserRoleReadFacade userRoleReadFacade;
    private final UserRoleAssembler assembler;

    public GetUserRolesService(UserRoleReadFacade userRoleReadFacade, UserRoleAssembler assembler) {
        this.userRoleReadFacade = userRoleReadFacade;
        this.assembler = assembler;
    }

    @Override
    public UserRolesResponse execute(String userId) {
        UserId userIdVo = UserId.of(userId);

        RolesAndPermissionsComposite result =
                userRoleReadFacade.findRolesAndPermissionsByUserId(userIdVo);

        return assembler.assemble(result.roleNames(), result.permissionKeys());
    }
}
