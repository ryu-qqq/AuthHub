package com.ryuqq.authhub.adapter.out.persistence.token.adapter;

import com.ryuqq.authhub.adapter.out.persistence.token.entity.RefreshTokenJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.token.repository.RefreshTokenJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.token.repository.RefreshTokenQueryDslRepository;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.token.port.out.command.RefreshTokenCommandPort;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RefreshTokenCommandAdapter - RefreshToken Command Adapter
 *
 * <p>RefreshTokenPersistencePort 구현체입니다. 저장/수정/삭제 작업을 담당합니다.
 *
 * <p><strong>시간 처리:</strong>
 *
 * <ul>
 *   <li>TimeProvider.now()로 현재 시각 획득 (Instant)
 *   <li>Entity 저장 시 Instant (UTC) 직접 전달
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 필수 (Command 작업)
 *   <li>JpaRepository에 저장/삭제 로직 위임
 *   <li>조회 메서드 금지 (QueryAdapter로 분리)
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class RefreshTokenCommandAdapter implements RefreshTokenCommandPort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final RefreshTokenQueryDslRepository refreshTokenQueryDslRepository;
    private final TimeProvider timeProvider;

    public RefreshTokenCommandAdapter(
            RefreshTokenJpaRepository refreshTokenJpaRepository,
            RefreshTokenQueryDslRepository refreshTokenQueryDslRepository,
            TimeProvider timeProvider) {
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
        this.refreshTokenQueryDslRepository = refreshTokenQueryDslRepository;
        this.timeProvider = timeProvider;
    }

    /**
     * RefreshToken 저장 또는 갱신
     *
     * <p>동일한 UserId에 대해 기존 토큰이 있으면 갱신, 없으면 신규 저장
     *
     * @param userId 사용자 ID (Value Object)
     * @param refreshToken RefreshToken 문자열
     */
    @Override
    @Transactional
    public void persist(UserId userId, String refreshToken) {
        Instant now = timeProvider.now();

        refreshTokenQueryDslRepository
                .findByUserId(UUID.fromString(userId.value()))
                .ifPresentOrElse(
                        existingEntity -> existingEntity.updateToken(refreshToken, now),
                        () -> {
                            RefreshTokenJpaEntity newEntity =
                                    RefreshTokenJpaEntity.forNew(
                                            UUID.randomUUID(),
                                            UUID.fromString(userId.value()),
                                            refreshToken,
                                            now);
                            refreshTokenJpaRepository.save(newEntity);
                        });
    }

    /**
     * UserId로 RefreshToken 삭제
     *
     * <p>로그아웃 또는 보안 목적으로 토큰 무효화 시 사용
     *
     * @param userId 사용자 ID (Value Object)
     */
    @Override
    @Transactional
    public void deleteByUserId(UserId userId) {
        refreshTokenJpaRepository.deleteByUserId(UUID.fromString(userId.value()));
    }
}
