package com.ryuqq.authhub.application.user.service.query;

import com.ryuqq.authhub.application.user.dto.query.GetUserQuery;
import com.ryuqq.authhub.application.user.dto.response.UserDetailResponse;
import com.ryuqq.authhub.application.user.port.in.query.GetUserDetailUseCase;
import com.ryuqq.authhub.application.user.port.out.query.UserAdminQueryPort;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import org.springframework.stereotype.Service;

/**
 * GetUserDetailService - 사용자 상세 조회 Service (Admin용)
 *
 * <p>GetUserDetailUseCase를 구현합니다.
 *
 * <p><strong>일반 QueryService와의 차이점:</strong>
 *
 * <ul>
 *   <li>AdminQueryPort 직접 호출 (Manager 불필요)
 *   <li>Assembler 불필요 (DTO Projection 직접 반환)
 *   <li>UserDetailResponse 반환 (역할 목록 포함)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (읽기 전용이므로 불필요)
 *   <li>Port 직접 호출 허용 (Admin 패턴)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see GetUserDetailUseCase Admin 상세 조회 UseCase
 * @see UserAdminQueryPort Admin 조회 Port
 */
@Service
public class GetUserDetailService implements GetUserDetailUseCase {

    private final UserAdminQueryPort adminQueryPort;

    public GetUserDetailService(UserAdminQueryPort adminQueryPort) {
        this.adminQueryPort = adminQueryPort;
    }

    @Override
    public UserDetailResponse execute(GetUserQuery query) {
        UserId userId = UserId.of(query.userId());
        return adminQueryPort
                .findUserDetail(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
