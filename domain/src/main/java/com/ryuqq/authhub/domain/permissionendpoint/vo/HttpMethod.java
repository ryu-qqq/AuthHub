package com.ryuqq.authhub.domain.permissionendpoint.vo;

/**
 * HttpMethod - HTTP 메서드 열거형
 *
 * <p>Gateway에서 URL 매핑 시 사용되는 HTTP 메서드를 정의합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum HttpMethod {

    /** HTTP GET 메서드 - 리소스 조회 */
    GET,

    /** HTTP POST 메서드 - 리소스 생성 */
    POST,

    /** HTTP PUT 메서드 - 리소스 전체 수정 */
    PUT,

    /** HTTP PATCH 메서드 - 리소스 부분 수정 */
    PATCH,

    /** HTTP DELETE 메서드 - 리소스 삭제 */
    DELETE,

    /** HTTP HEAD 메서드 - 헤더만 조회 */
    HEAD,

    /** HTTP OPTIONS 메서드 - 지원 메서드 조회 */
    OPTIONS;

    /**
     * 문자열로부터 HttpMethod 변환
     *
     * @param value HTTP 메서드 문자열
     * @return HttpMethod enum
     * @throws IllegalArgumentException 유효하지 않은 값인 경우
     */
    public static HttpMethod from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("HttpMethod는 null이거나 빈 값일 수 없습니다");
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 HttpMethod: " + value);
        }
    }

    /**
     * 읽기 전용 메서드인지 확인
     *
     * @return GET, HEAD, OPTIONS면 true
     */
    public boolean isReadOnly() {
        return this == GET || this == HEAD || this == OPTIONS;
    }

    /**
     * 쓰기 메서드인지 확인
     *
     * @return POST, PUT, PATCH, DELETE면 true
     */
    public boolean isWriteOperation() {
        return this == POST || this == PUT || this == PATCH || this == DELETE;
    }
}
