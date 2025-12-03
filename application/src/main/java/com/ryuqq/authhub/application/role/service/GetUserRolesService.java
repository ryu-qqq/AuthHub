package com.ryuqq.authhub.application.role.service;

import com.ryuqq.authhub.application.role.assembler.RoleQueryAssembler;
import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import com.ryuqq.authhub.application.role.port.in.GetUserRolesUseCase;
import com.ryuqq.authhub.application.role.port.out.query.RoleQueryPort;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * GetUserRolesService - 사용자 권한 조회 서비스 구현체
 *
 * <p>사용자에게 할당된 Role과 Permission을 조회합니다.
 *
 * <p><strong>Query UseCase:</strong>
 *
 * <ul>
 *   <li>읽기 전용 트랜잭션 (@Transactional(readOnly = true))
 *   <li>데이터 변경 없음 - Manager 사용 안함
 *   <li>QueryAssembler로 Response DTO 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
@Transactional(readOnly = true)
public class GetUserRolesService implements GetUserRolesUseCase {

    private final RoleQueryPort roleQueryPort;
    private final RoleQueryAssembler roleQueryAssembler;

    public GetUserRolesService(RoleQueryPort roleQueryPort, RoleQueryAssembler roleQueryAssembler) {
        this.roleQueryPort = roleQueryPort;
        this.roleQueryAssembler = roleQueryAssembler;
    }

    @Override
    public UserRolesResponse execute(UUID userId) {
        UserId userIdVo = UserId.of(userId);

        // 1. 사용자의 Role 목록 조회 (Permission 포함)
        List<Role> roles = roleQueryPort.findByUserId(userIdVo);

        // 2. Response 변환 및 반환
        return roleQueryAssembler.toUserRolesResponse(userIdVo, roles);
    }
}
