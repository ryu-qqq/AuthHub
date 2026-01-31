package com.ryuqq.authhub.domain.user.vo;

/**
 * HashedPassword - 해시된 비밀번호 Value Object
 *
 * <p>보안을 위해 해시 처리된 비밀번호를 저장합니다. 원본 비밀번호는 저장하지 않습니다.
 *
 * <p><strong>특징:</strong>
 *
 * <ul>
 *   <li>이미 해시된 값만 저장 (평문 비밀번호 금지)
 *   <li>외부에서 해시 처리 후 전달
 *   <li>비밀번호 검증은 별도 서비스에서 수행
 * </ul>
 *
 * @param value 해시된 비밀번호 값
 * @author development-team
 * @since 1.0.0
 */
public record HashedPassword(String value) {

    /** Compact Constructor - null/blank 방어 */
    public HashedPassword {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("HashedPassword는 null이거나 빈 값일 수 없습니다");
        }
    }

    /**
     * HashedPassword 생성
     *
     * @param hashedValue 해시된 비밀번호 값
     * @return HashedPassword 인스턴스
     */
    public static HashedPassword of(String hashedValue) {
        return new HashedPassword(hashedValue);
    }
}
