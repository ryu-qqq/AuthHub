package com.ryuqq.authhub.domain.common.util;

import java.util.UUID;

/**
 * UUID 생성자 인터페이스 (DIP - Dependency Inversion Principle)
 *
 * <p>Domain Layer는 구현체를 모르고, 이 인터페이스에만 의존합니다. 구현체는 Bootstrap Layer에서 제공됩니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>✅ Domain은 UuidHolder 인터페이스에만 의존
 *   <li>✅ Bootstrap에서 구현체 제공 (UuidV7Holder)
 *   <li>✅ Factory에서 UuidHolder를 주입받아 UUID 생성
 *   <li>✅ UUIDv7 (RFC 9562) 규격 사용 - 시간 순서 보장
 * </ul>
 *
 * <p><strong>UUIDv7 특징:</strong>
 *
 * <ul>
 *   <li>시간 기반 정렬 가능 (밀리초 단위 타임스탬프)
 *   <li>데이터베이스 인덱스 효율성 향상
 *   <li>분산 환경에서 충돌 가능성 최소화
 *   <li>RFC 9562 표준 준수
 * </ul>
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * // Factory (Application)
 * @Component
 * public class TenantCommandFactory {
 *     private final UuidHolder uuidHolder;
 *     private final Clock clock;
 *
 *     public Tenant create(CreateTenantCommand command) {
 *         TenantId tenantId = TenantId.forNew(uuidHolder.random());
 *         return Tenant.create(tenantId, TenantName.of(command.name()), clock);
 *     }
 * }
 * }</pre>
 *
 * <p><strong>테스트 시:</strong>
 *
 * <pre>{@code
 * // FixedUuidHolder (Test)
 * UUID fixedUuid = UUID.fromString("019234ab-cdef-7890-abcd-ef1234567890");
 * UuidHolder fixedUuidHolder = () -> fixedUuid;
 *
 * Tenant tenant = factory.create(command);
 * assertThat(tenant.tenantIdValue()).isEqualTo(fixedUuid);
 * }</pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9562.html">RFC 9562 - UUID Version 7</a>
 */
public interface UuidHolder {

    /**
     * 새로운 UUID 생성
     *
     * <p>Aggregate 식별자 생성 시 사용됩니다. 운영 환경에서는 UUIDv7을, 테스트 환경에서는 고정값을 반환합니다.
     *
     * @return 새로 생성된 UUID (UUIDv7 또는 테스트용 고정값)
     * @author AuthHub Team
     * @since 1.0.0
     */
    UUID random();
}
