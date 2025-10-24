package com.ryuqq.authhub.domain.identity.profile.vo;

/**
 * 사용자의 자기소개(Bio)를 나타내는 Value Object.
 * 자기소개는 선택 사항이며, 길이 제약을 가집니다.
 *
 * <p>이 Record는 불변(immutable) 객체이며, 동등성 비교는 내부 value 값으로 수행됩니다.</p>
 *
 * <p><strong>Validation 규칙:</strong></p>
 * <ul>
 *   <li>길이: 최대 500자</li>
 *   <li>null 허용 (자기소개 없음)</li>
 *   <li>빈 문자열 허용 (자기소개 없음)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>불변성 보장 - Record의 본질적 불변성</li>
 *   <li>Law of Demeter 준수</li>
 * </ul>
 *
 * @param value 자기소개 문자열 (null 허용, 최대 500자)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record Bio(String value) {

    private static final int MAX_LENGTH = 500;

    /**
     * Compact constructor - Bio 값의 길이 검증을 수행합니다.
     * null 또는 빈 문자열은 허용됩니다.
     *
     * @throws IllegalArgumentException value가 최대 길이를 초과한 경우
     */
    public Bio {
        if (value != null && value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Bio length must not exceed %d characters, but was %d",
                    MAX_LENGTH, value.length())
            );
        }
    }

    /**
     * 빈 Bio를 생성합니다 (자기소개 없음).
     *
     * @return 빈 Bio 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Bio empty() {
        return new Bio(null);
    }

    /**
     * 자기소개가 비어있는지 확인합니다.
     *
     * @return 자기소개가 null이거나 빈 문자열이면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isEmpty() {
        return value == null || value.trim().isEmpty();
    }

    /**
     * 자기소개가 설정되어 있는지 확인합니다.
     *
     * @return 자기소개가 null이 아니고 비어있지 않으면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean hasValue() {
        return !isEmpty();
    }

    /**
     * 자기소개 문자열을 반환합니다 (Law of Demeter 준수).
     * null인 경우 빈 문자열을 반환합니다.
     *
     * @return 자기소개 문자열 (null일 경우 빈 문자열)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getValue() {
        return value != null ? value : "";
    }

    /**
     * 자기소개의 실제 문자열 길이를 반환합니다.
     *
     * @return 자기소개 문자열 길이 (null일 경우 0)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public int getLength() {
        return value != null ? value.length() : 0;
    }

    /**
     * 주어진 문자열이 유효한 Bio 형식인지 검증합니다 (정적 헬퍼 메서드).
     *
     * @param value 검증 대상 문자열
     * @return 유효하면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static boolean isValid(final String value) {
        return value == null || value.length() <= MAX_LENGTH;
    }
}
