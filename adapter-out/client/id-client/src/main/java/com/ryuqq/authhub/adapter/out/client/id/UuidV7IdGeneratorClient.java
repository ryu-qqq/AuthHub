package com.ryuqq.authhub.adapter.out.client.id;

import com.github.f4b6a3.uuid.UuidCreator;
import com.ryuqq.authhub.application.common.port.out.IdGeneratorPort;
import org.springframework.stereotype.Component;

/**
 * UuidV7IdGeneratorClient - UUIDv7 기반 ID 생성 Client
 *
 * <p>uuid-creator 라이브러리를 사용하여 시간 순서가 보장되는 UUIDv7을 생성합니다.
 *
 * <p><strong>UUIDv7 특징:</strong>
 *
 * <ul>
 *   <li>시간 기반 정렬 가능 - 생성 순서대로 정렬됨
 *   <li>분산 환경에서 충돌 없는 고유 ID 생성
 *   <li>데이터베이스 인덱스 성능 최적화
 * </ul>
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * String id = idGenerator.generate();
 * // 결과: "01912c60-8e80-7d67-b48a-9a8b7c6d5e4f"
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UuidV7IdGeneratorClient implements IdGeneratorPort {

    /**
     * UUIDv7 형식의 고유 ID 생성
     *
     * <p>시간 순서가 보장되는 UUIDv7을 생성합니다. 생성된 UUID는 RFC 9562 표준을 따릅니다.
     *
     * @return 생성된 UUIDv7 문자열 (예: "01912c60-8e80-7d67-b48a-9a8b7c6d5e4f")
     */
    @Override
    public String generate() {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }
}
