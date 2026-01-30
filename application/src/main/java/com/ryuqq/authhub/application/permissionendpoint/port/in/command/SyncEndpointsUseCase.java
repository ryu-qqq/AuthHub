package com.ryuqq.authhub.application.permissionendpoint.port.in.command;

import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.SyncEndpointsResult;

/**
 * SyncEndpointsUseCase - 엔드포인트 동기화 UseCase
 *
 * <p>다른 서비스에서 @RequirePermission 어노테이션이 붙은 엔드포인트를 AuthHub에 동기화합니다.
 *
 * <p><strong>동작 방식:</strong>
 *
 * <ul>
 *   <li>Permission이 없으면 새로 생성 (CUSTOM 타입)
 *   <li>PermissionEndpoint가 없으면 새로 생성
 *   <li>PermissionEndpoint가 있으면 스킵 (이미 존재)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SyncEndpointsUseCase {

    /**
     * 엔드포인트 동기화 실행
     *
     * @param command 동기화 Command (서비스명, 엔드포인트 목록)
     * @return 동기화 결과
     */
    SyncEndpointsResult sync(SyncEndpointsCommand command);
}
