package com.ryuqq.authhub.application.security.blacklist.service.command;

import com.ryuqq.authhub.application.security.blacklist.port.in.CleanupBlacklistUseCase;
import com.ryuqq.authhub.application.security.blacklist.port.out.RemoveFromBlacklistPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/**
 * BlacklistCleanupService 단위 테스트.
 *
 * <p>BlacklistCleanupService의 비즈니스 로직을 검증하는 단위 테스트입니다.
 * 모든 외부 의존성(Port)은 Mockito로 Mocking하여 순수한 Service 로직만 테스트합니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>✅ Spring Context 로딩 없음 (빠른 실행)</li>
 *   <li>✅ 모든 Port는 Mock 객체 사용</li>
 *   <li>✅ Given-When-Then 패턴</li>
 *   <li>✅ BDD 스타일 (given, when, then)</li>
 * </ul>
 *
 * <p><strong>테스트 케이스:</strong></p>
 * <ul>
 *   <li>✅ 정상 케이스: 만료된 토큰 정리 성공 (10개)</li>
 *   <li>✅ 정상 케이스: 만료된 토큰이 없음 (0개)</li>
 *   <li>✅ 경계값 케이스: 배치 크기 정확히 1000개</li>
 *   <li>✅ 멱등성 테스트: 여러 번 실행</li>
 * </ul>
 *
 * <p><strong>테스트 태그:</strong></p>
 * <ul>
 *   <li>@Tag("unit") - 단위 테스트 (빠른 실행, Mock 사용)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BlacklistCleanupService 단위 테스트")
@Tag("unit")
class BlacklistCleanupServiceTest {

    @Mock
    private RemoveFromBlacklistPort removeFromBlacklistPort;

    @InjectMocks
    private BlacklistCleanupService blacklistCleanupService;

    @Test
    @DisplayName("정리 성공 - 만료된 토큰 10개 삭제")
    void cleanup_Success_With10ExpiredTokens() {
        // Given: 만료된 토큰 10개
        Set<String> expiredJtis = Set.of(
                "expired-jti-1", "expired-jti-2", "expired-jti-3", "expired-jti-4", "expired-jti-5",
                "expired-jti-6", "expired-jti-7", "expired-jti-8", "expired-jti-9", "expired-jti-10"
        );
        given(removeFromBlacklistPort.findExpiredJtis(anyLong(), eq(1000)))
                .willReturn(expiredJtis);
        given(removeFromBlacklistPort.removeAll(eq(expiredJtis)))
                .willReturn(10);

        // When
        CleanupBlacklistUseCase.Result result = blacklistCleanupService.cleanup();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDeletedCount()).isEqualTo(10);

        then(removeFromBlacklistPort).should(times(1))
                .findExpiredJtis(anyLong(), eq(1000));
        then(removeFromBlacklistPort).should(times(1))
                .removeAll(eq(expiredJtis));
    }

    @Test
    @DisplayName("정리 성공 - 만료된 토큰이 없음 (0개)")
    void cleanup_Success_WithNoExpiredTokens() {
        // Given: 만료된 토큰이 없음
        given(removeFromBlacklistPort.findExpiredJtis(anyLong(), eq(1000)))
                .willReturn(Collections.emptySet());

        // When
        CleanupBlacklistUseCase.Result result = blacklistCleanupService.cleanup();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDeletedCount()).isEqualTo(0);

        then(removeFromBlacklistPort).should(times(1))
                .findExpiredJtis(anyLong(), eq(1000));
        // 만료된 토큰이 없으면 removeAll 호출되지 않음
        then(removeFromBlacklistPort).should(never())
                .removeAll(eq(Collections.emptySet()));
    }

    @Test
    @DisplayName("정리 성공 - 배치 크기 정확히 1000개")
    void cleanup_Success_WithExactly1000Tokens() {
        // Given: 만료된 토큰 정확히 1000개
        Set<String> expiredJtis = new java.util.HashSet<>();
        for (int i = 1; i <= 1000; i++) {
            expiredJtis.add("expired-jti-" + i);
        }
        given(removeFromBlacklistPort.findExpiredJtis(anyLong(), eq(1000)))
                .willReturn(expiredJtis);
        given(removeFromBlacklistPort.removeAll(eq(expiredJtis)))
                .willReturn(1000);

        // When
        CleanupBlacklistUseCase.Result result = blacklistCleanupService.cleanup();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDeletedCount()).isEqualTo(1000);

        then(removeFromBlacklistPort).should(times(1))
                .findExpiredJtis(anyLong(), eq(1000));
        then(removeFromBlacklistPort).should(times(1))
                .removeAll(eq(expiredJtis));
    }

    @Test
    @DisplayName("멱등성 테스트 - 여러 번 실행해도 안전")
    void cleanup_Idempotency_MultipleCalls() {
        // Given: 첫 번째 호출에는 토큰 있음, 그 다음부터는 없음
        given(removeFromBlacklistPort.findExpiredJtis(anyLong(), anyInt()))
                .willReturn(Set.of("expired-jti-1"))
                .willReturn(Collections.emptySet())
                .willReturn(Collections.emptySet());
        given(removeFromBlacklistPort.removeAll(eq(Set.of("expired-jti-1"))))
                .willReturn(1);

        // When: 3번 실행
        CleanupBlacklistUseCase.Result result1 = blacklistCleanupService.cleanup();
        CleanupBlacklistUseCase.Result result2 = blacklistCleanupService.cleanup();
        CleanupBlacklistUseCase.Result result3 = blacklistCleanupService.cleanup();

        // Then
        assertThat(result1.getDeletedCount()).isEqualTo(1);
        assertThat(result2.getDeletedCount()).isEqualTo(0);
        assertThat(result3.getDeletedCount()).isEqualTo(0);

        then(removeFromBlacklistPort).should(times(3))
                .findExpiredJtis(anyLong(), eq(1000));
        then(removeFromBlacklistPort).should(times(1))
                .removeAll(eq(Set.of("expired-jti-1")));
    }

    @Test
    @DisplayName("Result Validation 실패 - deletedCount가 음수")
    void result_Validation_Failure_NegativeDeletedCount() {
        // When & Then
        assertThat(org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new CleanupBlacklistUseCase.Result(-1)
        )).hasMessageContaining("Deleted count cannot be negative");
    }

    @Test
    @DisplayName("Result Validation 성공 - deletedCount가 0")
    void result_Validation_Success_ZeroDeletedCount() {
        // When
        CleanupBlacklistUseCase.Result result = new CleanupBlacklistUseCase.Result(0);

        // Then
        assertThat(result.getDeletedCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Result Validation 성공 - deletedCount가 양수")
    void result_Validation_Success_PositiveDeletedCount() {
        // When
        CleanupBlacklistUseCase.Result result = new CleanupBlacklistUseCase.Result(100);

        // Then
        assertThat(result.getDeletedCount()).isEqualTo(100);
    }

    @Test
    @DisplayName("배치 처리 성능 테스트 - 대량 토큰 (10000개 시뮬레이션)")
    void cleanup_Performance_LargeNumberOfTokens() {
        // Given: 1000개씩 10번 호출 (총 10000개)
        // 실제로는 한 번에 1000개만 조회하므로, 첫 번째 호출만 테스트
        Set<String> expiredJtis = new java.util.HashSet<>();
        for (int i = 1; i <= 1000; i++) {
            expiredJtis.add("expired-jti-" + i);
        }
        given(removeFromBlacklistPort.findExpiredJtis(anyLong(), eq(1000)))
                .willReturn(expiredJtis);
        given(removeFromBlacklistPort.removeAll(eq(expiredJtis)))
                .willReturn(1000);

        // When
        CleanupBlacklistUseCase.Result result = blacklistCleanupService.cleanup();

        // Then: 한 번에 1000개만 처리됨 (배치 크기 제한)
        assertThat(result.getDeletedCount()).isEqualTo(1000);
    }
}
