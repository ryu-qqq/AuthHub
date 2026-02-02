package com.ryuqq.authhub.application.userrole.service.query;

import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import com.ryuqq.authhub.application.userrole.dto.response.UserPermissionsResult;
import com.ryuqq.authhub.application.userrole.facade.UserRoleReadFacade;
import com.ryuqq.authhub.application.userrole.port.in.query.GetUserPermissionsUseCase;
import com.ryuqq.authhub.domain.user.id.UserId;
import org.springframework.stereotype.Service;

/**
 * GetUserPermissionsService - Gateway용 사용자 권한 조회 서비스
 *
 * <p>Gateway가 사용자 인가 검증을 위해 역할/권한 정보를 조회합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Query Service는 조회만 수행
 *   <li>@Transactional(readOnly=true) 생략 가능 (읽기 전용)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GetUserPermissionsService implements GetUserPermissionsUseCase {

    private final UserRoleReadFacade userRoleReadFacade;

    public GetUserPermissionsService(UserRoleReadFacade userRoleReadFacade) {
        this.userRoleReadFacade = userRoleReadFacade;
    }

    @Override
    public UserPermissionsResult getByUserId(String userId) {
        RolesAndPermissionsComposite composite =
                userRoleReadFacade.findRolesAndPermissionsByUserId(UserId.of(userId));

        return new UserPermissionsResult(userId, composite.roleNames(), composite.permissionKeys());
    }
}
