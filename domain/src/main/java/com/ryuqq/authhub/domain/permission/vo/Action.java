package com.ryuqq.authhub.domain.permission.vo;

/**
 * Action - 권한 행위 Value Object
 *
 * <p>권한이 허용하는 행위를 나타내는 불변 값 객체입니다.
 *
 * <p><strong>검증 규칙:</strong>
 *
 * <ul>
 *   <li>null이거나 빈 값 불가
 *   <li>최대 50자
 *   <li>영문 소문자, 숫자, 하이픈만 허용 (예: read, create, update, delete, manage)
 * </ul>
 *
 * @param value 행위 문자열
 * @author development-team
 * @since 1.0.0
 */
public record Action(String value) {

    private static final int MAX_LENGTH = 50;
    private static final String PATTERN = "^[a-z][a-z0-9-]*$";

    public Action {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("action은 null이거나 빈 값일 수 없습니다");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("action은 " + MAX_LENGTH + "자를 초과할 수 없습니다");
        }
        if (!value.matches(PATTERN)) {
            throw new IllegalArgumentException(
                    "action은 영문 소문자로 시작하고, 영문 소문자, 숫자, 하이픈만 허용됩니다: " + value);
        }
    }

    /**
     * 팩토리 메서드
     *
     * @param value 행위 문자열
     * @return Action 인스턴스
     */
    public static Action of(String value) {
        return new Action(value);
    }
}
