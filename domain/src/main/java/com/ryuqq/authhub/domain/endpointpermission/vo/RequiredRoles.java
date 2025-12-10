package com.ryuqq.authhub.domain.endpointpermission.vo;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * RequiredRoles Value Object - 필수 역할 목록
 *
 * <p><strong>도메인 규칙</strong>:
 *
 * <ul>
 *   <li>역할 이름: ADMIN, USER, MODERATOR 등
 *   <li>빈 목록 허용 (역할 체크 필요 없는 엔드포인트)
 *   <li>OR 조건으로 평가 (목록 중 하나만 만족하면 허용)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record RequiredRoles(Set<String> values) {

    /** Compact Constructor (검증 및 불변 처리) */
    public RequiredRoles {
        if (values == null) {
            values = Collections.emptySet();
        } else {
            values = Set.copyOf(values);
        }
    }

    /**
     * 값 기반 생성
     *
     * @param values 역할 이름 목록
     * @return RequiredRoles
     */
    public static RequiredRoles of(Set<String> values) {
        return new RequiredRoles(values);
    }

    /**
     * 빈 역할 목록 생성
     *
     * @return 빈 RequiredRoles
     */
    public static RequiredRoles empty() {
        return new RequiredRoles(Collections.emptySet());
    }

    /**
     * 단일 역할로 생성
     *
     * @param role 역할 이름
     * @return RequiredRoles
     */
    public static RequiredRoles single(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("역할은 null이거나 빈 문자열일 수 없습니다");
        }
        return new RequiredRoles(Set.of(role));
    }

    /**
     * 역할이 비어있는지 확인
     *
     * @return 비어있으면 true
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * 주어진 역할이 포함되어 있는지 확인
     *
     * @param role 확인할 역할
     * @return 포함되어 있으면 true
     */
    public boolean contains(String role) {
        return values.contains(role);
    }

    /**
     * 사용자 역할 중 하나라도 만족하는지 확인 (OR 조건)
     *
     * @param userRoles 사용자가 가진 역할 목록
     * @return 하나라도 만족하면 true
     */
    public boolean hasAnyOf(Set<String> userRoles) {
        if (values.isEmpty()) {
            return true;
        }
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }
        return values.stream().anyMatch(userRoles::contains);
    }

    /**
     * 역할 개수 반환
     *
     * @return 역할 개수
     */
    public int size() {
        return values.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequiredRoles that = (RequiredRoles) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return "RequiredRoles{values=" + values + "}";
    }
}
