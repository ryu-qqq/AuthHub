package com.ryuqq.bootstrap.config;

import com.ryuqq.authhub.domain.common.util.UuidHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * UUID 관련 Bean 설정
 *
 * <p>Bootstrap Layer에서 UUID 생성 관련 Bean을 등록합니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>✅ Infrastructure 관심사는 Bootstrap에서 관리
 *   <li>✅ UuidHolder는 DIP를 통해 Application에서 사용
 *   <li>✅ 테스트 환경에서는 FixedUuidHolder로 교체 가능
 *   <li>✅ UUIDv7 (RFC 9562) 규격 사용
 * </ul>
 *
 * <p><strong>Bean 등록 전략:</strong>
 *
 * <ul>
 *   <li>UuidHolder: UuidV7Holder 구현체 사용 (운영 환경)
 *   <li>테스트에서는 람다 또는 Fixture로 Mock 가능
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Configuration
public class UuidConfig {

    /**
     * UuidHolder Bean 등록
     *
     * <p>UuidV7Holder 구현체를 반환합니다.
     *
     * <p>Application Layer의 Factory 등에서 주입받아 사용할 수 있습니다.
     *
     * @return UuidV7Holder 구현체
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Bean
    public UuidHolder uuidHolder() {
        return new UuidV7Holder();
    }
}
