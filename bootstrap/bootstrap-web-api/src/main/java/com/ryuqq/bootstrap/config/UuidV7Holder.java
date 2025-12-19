package com.ryuqq.bootstrap.config;

import com.github.f4b6a3.uuid.UuidCreator;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import java.util.UUID;

/**
 * UuidV7Holder - UuidHolder 구현체
 *
 * <p>UUIDv7 (RFC 9562)을 생성하는 UuidHolder 구현체입니다.
 *
 * <p><strong>UUIDv7 특징:</strong>
 *
 * <ul>
 *   <li>시간 기반 정렬 가능 (Unix Epoch 밀리초 타임스탬프)
 *   <li>시간순 정렬로 데이터베이스 인덱스 성능 향상
 *   <li>분산 환경에서 충돌 가능성 최소화
 *   <li>RFC 9562 표준 준수
 * </ul>
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>Bootstrap Layer에서 Bean으로 등록
 *   <li>Domain Layer의 UuidHolder 인터페이스 구현
 *   <li>uuid-creator 라이브러리 활용
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 * @see com.github.f4b6a3.uuid.UuidCreator#getTimeOrderedEpoch()
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9562.html">RFC 9562</a>
 */
public final class UuidV7Holder implements UuidHolder {

    /**
     * UUIDv7 생성
     *
     * <p>uuid-creator 라이브러리의 {@code getTimeOrderedEpoch()} 메서드를 사용하여 RFC 9562 규격의 UUIDv7을 생성합니다.
     *
     * <p><strong>UUID 구조 (128비트):</strong>
     *
     * <ul>
     *   <li>48비트: Unix Epoch 밀리초 타임스탬프
     *   <li>4비트: 버전 (7)
     *   <li>12비트: 랜덤 또는 카운터
     *   <li>2비트: 변형 (10)
     *   <li>62비트: 랜덤
     * </ul>
     *
     * @return UUIDv7 인스턴스
     */
    @Override
    public UUID random() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
