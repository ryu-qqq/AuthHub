package com.ryuqq.authhub.domain.service.aggregate;

/**
 * ServiceUpdateData - 서비스 수정 데이터 Value Object
 *
 * <p>Service Aggregate의 update() 메서드에서 사용하는 수정 데이터입니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>Domain 타입 의존 금지 (String 기반)
 *   <li>hasXxx() 메서드로 필드 변경 여부 확인
 *   <li>Lombok 금지
 * </ul>
 *
 * @param name 서비스 이름 (null이면 변경하지 않음)
 * @param description 서비스 설명 (null이면 변경하지 않음)
 * @param status 서비스 상태 (null이면 변경하지 않음)
 * @author development-team
 * @since 1.0.0
 */
public record ServiceUpdateData(String name, String description, String status) {

    /**
     * ServiceUpdateData 생성 팩토리 메서드
     *
     * @param name 서비스 이름
     * @param description 서비스 설명
     * @param status 서비스 상태
     * @return ServiceUpdateData
     */
    public static ServiceUpdateData of(String name, String description, String status) {
        return new ServiceUpdateData(name, description, status);
    }

    /**
     * 이름 변경 여부 확인
     *
     * @return name이 null이 아니면 true
     */
    public boolean hasName() {
        return name != null;
    }

    /**
     * 설명 변경 여부 확인
     *
     * @return description이 null이 아니면 true
     */
    public boolean hasDescription() {
        return description != null;
    }

    /**
     * 상태 변경 여부 확인
     *
     * @return status가 null이 아니면 true
     */
    public boolean hasStatus() {
        return status != null;
    }

    /**
     * 변경할 필드가 하나라도 있는지 확인
     *
     * @return 변경할 필드가 있으면 true
     */
    public boolean hasAnyUpdate() {
        return hasName() || hasDescription() || hasStatus();
    }
}
