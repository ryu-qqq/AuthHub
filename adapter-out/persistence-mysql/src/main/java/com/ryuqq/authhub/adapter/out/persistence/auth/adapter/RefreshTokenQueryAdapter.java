package com.ryuqq.authhub.adapter.out.persistence.auth.adapter;

import com.ryuqq.authhub.adapter.out.persistence.auth.repository.RefreshTokenQueryDslRepository;
import com.ryuqq.authhub.application.auth.port.out.query.RefreshTokenQueryPort;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * RefreshTokenQueryAdapter - RefreshToken Query Adapter
 *
 * <p>RefreshTokenQueryPort 구현체입니다. 조회 작업을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 사용 금지 (읽기 전용)
 *   <li>QueryDslRepository에 조회 로직 위임
 *   <li>저장/수정/삭제 메서드 금지 (CommandAdapter로 분리)
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class RefreshTokenQueryAdapter implements RefreshTokenQueryPort {

    private final RefreshTokenQueryDslRepository refreshTokenQueryDslRepository;

    public RefreshTokenQueryAdapter(RefreshTokenQueryDslRepository refreshTokenQueryDslRepository) {
        this.refreshTokenQueryDslRepository = refreshTokenQueryDslRepository;
    }

    /**
     * UserId로 RefreshToken 조회
     *
     * @param userId 사용자 ID (Value Object)
     * @return RefreshToken 문자열 (Optional)
     */
    @Override
    public Optional<String> findByUserId(UserId userId) {
        return refreshTokenQueryDslRepository
                .findByUserId(userId.value())
                .map(entity -> entity.getToken());
    }

    /**
     * UserId로 RefreshToken 존재 여부 확인
     *
     * @param userId 사용자 ID (Value Object)
     * @return 존재 여부
     */
    @Override
    public boolean existsByUserId(UserId userId) {
        return refreshTokenQueryDslRepository.existsByUserId(userId.value());
    }

    /**
     * RefreshToken으로 UserId 조회
     *
     * @param refreshToken RefreshToken 문자열
     * @return UserId (Optional)
     */
    @Override
    public Optional<UserId> findUserIdByToken(String refreshToken) {
        return refreshTokenQueryDslRepository
                .findByToken(refreshToken)
                .map(entity -> UserId.of(entity.getUserId()));
    }
}
