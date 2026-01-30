package com.ryuqq.authhub.application.token.internal;

import com.ryuqq.authhub.application.token.dto.composite.MyContextComposite;
import com.ryuqq.authhub.application.token.dto.composite.UserContextComposite;
import com.ryuqq.authhub.application.token.manager.query.UserContextCompositeReadManager;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import com.ryuqq.authhub.application.userrole.facade.UserRoleReadFacade;
import com.ryuqq.authhub.domain.user.id.UserId;
import org.springframework.stereotype.Component;

/**
 * MyContextReadFacade - 내 컨텍스트 조회 Facade
 *
 * <p>사용자의 전체 컨텍스트(기본정보, 테넌트, 조직, 역할, 권한)를 조회하기 위해 여러 ReadManager를 조율합니다.
 *
 * <p><strong>조회 흐름:</strong>
 *
 * <ol>
 *   <li>UserContextCompositeReadManager: User + Organization + Tenant 조인 조회
 *   <li>UserRoleReadFacade: 역할 이름 + 권한 키 조회
 *   <li>결과 조합하여 MyContextComposite 반환
 * </ol>
 *
 * <p><strong>예외 처리:</strong>
 *
 * <ul>
 *   <li>UserNotFoundException은 ReadManager에서 발생 (여기서 던지지 않음)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class MyContextReadFacade {

    private final UserContextCompositeReadManager userContextCompositeReadManager;
    private final UserRoleReadFacade userRoleReadFacade;

    public MyContextReadFacade(
            UserContextCompositeReadManager userContextCompositeReadManager,
            UserRoleReadFacade userRoleReadFacade) {
        this.userContextCompositeReadManager = userContextCompositeReadManager;
        this.userRoleReadFacade = userRoleReadFacade;
    }

    /**
     * 사용자 ID로 전체 컨텍스트 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 전체 컨텍스트 Composite
     */
    public MyContextComposite findMyContext(UserId userId) {
        UserContextComposite userContext =
                userContextCompositeReadManager.getUserContextByUserId(userId);

        RolesAndPermissionsComposite rolesAndPermissions =
                userRoleReadFacade.findRolesAndPermissionsByUserId(userId);

        return new MyContextComposite(userContext, rolesAndPermissions);
    }
}
