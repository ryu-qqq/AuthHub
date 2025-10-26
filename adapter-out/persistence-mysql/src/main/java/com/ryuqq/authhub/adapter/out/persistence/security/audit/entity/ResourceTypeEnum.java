package com.ryuqq.authhub.adapter.out.persistence.security.audit.entity;

/**
 * ResourceType JPA Enum.
 *
 * <p>Domain의 ResourceType과 1:1 매핑되는 JPA용 Enum입니다.
 * Domain Layer와 Persistence Layer를 명확히 분리하기 위해 별도로 정의합니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java Enum</li>
 *   <li>✅ Domain Enum과 분리 - Persistence Layer 독립성 보장</li>
 *   <li>✅ EnumType.STRING - DB에 문자열로 저장</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public enum ResourceTypeEnum {

    /**
     * 사용자 리소스.
     */
    USER,

    /**
     * 조직 리소스.
     */
    ORGANIZATION,

    /**
     * 회사 리소스.
     */
    COMPANY
}
