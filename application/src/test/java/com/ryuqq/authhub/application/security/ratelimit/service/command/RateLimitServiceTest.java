package com.ryuqq.authhub.application.security.ratelimit.service.command;

import com.ryuqq.authhub.application.security.ratelimit.port.in.CheckRateLimitUseCase;
import com.ryuqq.authhub.application.security.ratelimit.port.in.IncrementRateLimitUseCase;
import com.ryuqq.authhub.application.security.ratelimit.port.in.ResetRateLimitUseCase;
import com.ryuqq.authhub.application.security.ratelimit.port.out.IncrementRateLimitPort;
import com.ryuqq.authhub.application.security.ratelimit.port.out.LoadRateLimitPort;
import com.ryuqq.authhub.application.security.ratelimit.port.out.ResetRateLimitPort;
import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

/**
 * RateLimitService 단위 테스트.
 *
 * <p>RateLimitService의 비즈니스 로직을 검증하는 단위 테스트입니다.
 * 모든 외부 의존성(Port)은 Mockito로 Mocking하여 순수한 Service 로직만 테스트합니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>✅ Spring Context 로딩 없음 (빠른 실행)</li>
 *   <li>✅ 모든 Port는 Mock 객체 사용</li>
 *   <li>✅ Given-When-Then 패턴</li>
 *   <li>✅ BDD 스타일 (given, when, then)</li>
 *   <li>✅ @Nested 클래스로 테스트 그룹화</li>
 * </ul>
 *
 * <p><strong>테스트 케이스:</strong></p>
 * <ul>
 *   <li>✅ Check Rate Limit: 제한 이내, 제한 초과</li>
 *   <li>✅ Increment Rate Limit: 카운터 증가</li>
 *   <li>✅ Reset Rate Limit: 카운터 삭제</li>
 *   <li>✅ 예외 케이스: null Command</li>
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
@DisplayName("RateLimitService 단위 테스트")
@Tag("unit")
class RateLimitServiceTest {

    @Mock
    private LoadRateLimitPort loadRateLimitPort;

    @Mock
    private IncrementRateLimitPort incrementRateLimitPort;

    @Mock
    private ResetRateLimitPort resetRateLimitPort;

    @InjectMocks
    private RateLimitService rateLimitService;

    @Nested
    @DisplayName("checkRateLimit 테스트")
    class CheckRateLimitTest {

        private CheckRateLimitUseCase.Command command;

        @BeforeEach
        void setUp() {
            command = new CheckRateLimitUseCase.Command(
                    "192.168.1.100",
                    "/api/v1/login",
                    RateLimitType.IP_BASED
            );
        }

        @Test
        @DisplayName("제한 이내 - 정상 케이스")
        void checkRateLimit_WithinLimit_Success() {
            // Given: 현재 카운트가 제한 이내 (50 < 100)
            given(loadRateLimitPort.loadCurrentCount(
                    eq("192.168.1.100"),
                    eq("/api/v1/login"),
                    eq(RateLimitType.IP_BASED)
            )).willReturn(50);

            // When: Rate Limit 확인
            CheckRateLimitUseCase.Result result = rateLimitService.checkRateLimit(command);

            // Then: 제한 이내 결과 반환
            assertThat(result).isNotNull();
            assertThat(result.isExceeded()).isFalse();
            assertThat(result.getCurrentCount()).isEqualTo(50);
            assertThat(result.getLimitCount()).isEqualTo(100);  // IP_BASED: 100회
            assertThat(result.getRemainingCount()).isEqualTo(50);  // 100 - 50
            assertThat(result.getTimeWindowSeconds()).isEqualTo(60);  // 60초

            // Verify: Port 호출 검증
            then(loadRateLimitPort).should(times(1))
                    .loadCurrentCount(anyString(), anyString(), any(RateLimitType.class));
        }

        @Test
        @DisplayName("제한 초과 - 예외 케이스")
        void checkRateLimit_Exceeded_ReturnsExceededResult() {
            // Given: 현재 카운트가 제한 초과 (105 >= 100)
            given(loadRateLimitPort.loadCurrentCount(
                    eq("192.168.1.100"),
                    eq("/api/v1/login"),
                    eq(RateLimitType.IP_BASED)
            )).willReturn(105);

            // When: Rate Limit 확인
            CheckRateLimitUseCase.Result result = rateLimitService.checkRateLimit(command);

            // Then: 제한 초과 결과 반환
            assertThat(result).isNotNull();
            assertThat(result.isExceeded()).isTrue();
            assertThat(result.getCurrentCount()).isEqualTo(105);
            assertThat(result.getLimitCount()).isEqualTo(100);
            assertThat(result.getRemainingCount()).isEqualTo(0);  // 초과 시 0
            assertThat(result.getTimeWindowSeconds()).isEqualTo(60);

            // Verify: Port 호출 검증
            then(loadRateLimitPort).should(times(1))
                    .loadCurrentCount(anyString(), anyString(), any(RateLimitType.class));
        }

        @Test
        @DisplayName("제한 정확히 도달 - 경계 케이스")
        void checkRateLimit_ExactlyAtLimit_ReturnsExceededResult() {
            // Given: 현재 카운트가 정확히 제한값 (100 == 100)
            given(loadRateLimitPort.loadCurrentCount(
                    eq("192.168.1.100"),
                    eq("/api/v1/login"),
                    eq(RateLimitType.IP_BASED)
            )).willReturn(100);

            // When: Rate Limit 확인
            CheckRateLimitUseCase.Result result = rateLimitService.checkRateLimit(command);

            // Then: 제한 초과로 판단 (100 >= 100)
            assertThat(result.isExceeded()).isTrue();
            assertThat(result.getCurrentCount()).isEqualTo(100);
            assertThat(result.getRemainingCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("카운트 0 - 첫 요청")
        void checkRateLimit_ZeroCount_FirstRequest() {
            // Given: 현재 카운트가 0 (첫 요청)
            given(loadRateLimitPort.loadCurrentCount(
                    eq("192.168.1.100"),
                    eq("/api/v1/login"),
                    eq(RateLimitType.IP_BASED)
            )).willReturn(0);

            // When: Rate Limit 확인
            CheckRateLimitUseCase.Result result = rateLimitService.checkRateLimit(command);

            // Then: 제한 이내
            assertThat(result.isExceeded()).isFalse();
            assertThat(result.getCurrentCount()).isEqualTo(0);
            assertThat(result.getRemainingCount()).isEqualTo(100);
        }

        @Test
        @DisplayName("Command가 null - 예외 발생")
        void checkRateLimit_NullCommand_ThrowsException() {
            // When & Then: NullPointerException 발생
            assertThatThrownBy(() -> rateLimitService.checkRateLimit(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Command cannot be null");

            // Verify: Port 호출 안 됨
            then(loadRateLimitPort).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("incrementRateLimit 테스트")
    class IncrementRateLimitTest {

        private IncrementRateLimitUseCase.Command command;

        @BeforeEach
        void setUp() {
            command = new IncrementRateLimitUseCase.Command(
                    "192.168.1.100",
                    "/api/v1/login",
                    RateLimitType.IP_BASED,
                    60L  // TTL: 60초
            );
        }

        @Test
        @DisplayName("카운터 증가 - 정상 케이스")
        void incrementRateLimit_Success() {
            // Given: Redis INCR 성공 (1 반환)
            given(incrementRateLimitPort.incrementAndGet(
                    eq("192.168.1.100"),
                    eq("/api/v1/login"),
                    eq(RateLimitType.IP_BASED),
                    eq(60L)
            )).willReturn(1L);

            // When: 카운터 증가
            rateLimitService.incrementRateLimit(command);

            // Then: Port 호출 검증
            then(incrementRateLimitPort).should(times(1))
                    .incrementAndGet(
                            eq("192.168.1.100"),
                            eq("/api/v1/login"),
                            eq(RateLimitType.IP_BASED),
                            eq(60L)
                    );
        }

        @Test
        @DisplayName("Command가 null - 예외 발생")
        void incrementRateLimit_NullCommand_ThrowsException() {
            // When & Then: NullPointerException 발생
            assertThatThrownBy(() -> rateLimitService.incrementRateLimit(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Command cannot be null");

            // Verify: Port 호출 안 됨
            then(incrementRateLimitPort).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("resetRateLimit 테스트")
    class ResetRateLimitTest {

        private ResetRateLimitUseCase.Command command;

        @BeforeEach
        void setUp() {
            command = new ResetRateLimitUseCase.Command(
                    "192.168.1.100",
                    "/api/v1/login",
                    RateLimitType.IP_BASED
            );
        }

        @Test
        @DisplayName("카운터 리셋 - 정상 케이스")
        void resetRateLimit_Success() {
            // Given: Redis DEL 성공 (true 반환)
            given(resetRateLimitPort.reset(
                    eq("192.168.1.100"),
                    eq("/api/v1/login"),
                    eq(RateLimitType.IP_BASED)
            )).willReturn(true);

            // When: 카운터 리셋
            rateLimitService.resetRateLimit(command);

            // Then: Port 호출 검증
            then(resetRateLimitPort).should(times(1))
                    .reset(
                            eq("192.168.1.100"),
                            eq("/api/v1/login"),
                            eq(RateLimitType.IP_BASED)
                    );
        }

        @Test
        @DisplayName("카운터 리셋 - 키가 없어도 성공 (멱등성)")
        void resetRateLimit_KeyNotExists_Success() {
            // Given: Redis DEL 실패 (false 반환 - 키가 없었음)
            given(resetRateLimitPort.reset(
                    eq("192.168.1.100"),
                    eq("/api/v1/login"),
                    eq(RateLimitType.IP_BASED)
            )).willReturn(false);

            // When: 카운터 리셋
            rateLimitService.resetRateLimit(command);

            // Then: 예외 없이 정상 완료 (멱등성 보장)
            then(resetRateLimitPort).should(times(1))
                    .reset(anyString(), anyString(), any(RateLimitType.class));
        }

        @Test
        @DisplayName("Command가 null - 예외 발생")
        void resetRateLimit_NullCommand_ThrowsException() {
            // When & Then: NullPointerException 발생
            assertThatThrownBy(() -> rateLimitService.resetRateLimit(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Command cannot be null");

            // Verify: Port 호출 안 됨
            then(resetRateLimitPort).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("타입별 Rate Limit 정책 테스트")
    class RateLimitPolicyByTypeTest {

        @Test
        @DisplayName("IP_BASED - 100회/60초")
        void ipBased_100RequestsPer60Seconds() {
            // Given: IP 기반 Command
            CheckRateLimitUseCase.Command command = new CheckRateLimitUseCase.Command(
                    "192.168.1.100",
                    "/api/v1/login",
                    RateLimitType.IP_BASED
            );

            given(loadRateLimitPort.loadCurrentCount(anyString(), anyString(), any(RateLimitType.class)))
                    .willReturn(99);

            // When: Rate Limit 확인
            CheckRateLimitUseCase.Result result = rateLimitService.checkRateLimit(command);

            // Then: IP_BASED 정책 (100회/60초)
            assertThat(result.getLimitCount()).isEqualTo(100);
            assertThat(result.getTimeWindowSeconds()).isEqualTo(60);
            assertThat(result.isExceeded()).isFalse();
        }

        @Test
        @DisplayName("USER_BASED - 1000회/60초")
        void userBased_1000RequestsPer60Seconds() {
            // Given: 사용자 기반 Command
            CheckRateLimitUseCase.Command command = new CheckRateLimitUseCase.Command(
                    "user-123",
                    "/api/v1/data",
                    RateLimitType.USER_BASED
            );

            given(loadRateLimitPort.loadCurrentCount(anyString(), anyString(), any(RateLimitType.class)))
                    .willReturn(999);

            // When: Rate Limit 확인
            CheckRateLimitUseCase.Result result = rateLimitService.checkRateLimit(command);

            // Then: USER_BASED 정책 (1000회/60초)
            assertThat(result.getLimitCount()).isEqualTo(1000);
            assertThat(result.getTimeWindowSeconds()).isEqualTo(60);
            assertThat(result.isExceeded()).isFalse();
        }

        @Test
        @DisplayName("ENDPOINT_BASED - 5000회/60초")
        void endpointBased_5000RequestsPer60Seconds() {
            // Given: 엔드포인트 기반 Command
            CheckRateLimitUseCase.Command command = new CheckRateLimitUseCase.Command(
                    "all",
                    "/api/v1/public",
                    RateLimitType.ENDPOINT_BASED
            );

            given(loadRateLimitPort.loadCurrentCount(anyString(), anyString(), any(RateLimitType.class)))
                    .willReturn(4999);

            // When: Rate Limit 확인
            CheckRateLimitUseCase.Result result = rateLimitService.checkRateLimit(command);

            // Then: ENDPOINT_BASED 정책 (5000회/60초)
            assertThat(result.getLimitCount()).isEqualTo(5000);
            assertThat(result.getTimeWindowSeconds()).isEqualTo(60);
            assertThat(result.isExceeded()).isFalse();
        }
    }
}
