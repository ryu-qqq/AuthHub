package com.ryuqq.authhub.domain.endpointpermission.vo;

/**
 * HttpMethod Value Object - HTTP 메서드
 *
 * <p>RESTful API에서 사용되는 HTTP 메서드를 정의합니다.
 *
 * <p><strong>지원 메서드</strong>:
 *
 * <ul>
 *   <li>GET - 리소스 조회
 *   <li>POST - 리소스 생성
 *   <li>PUT - 리소스 전체 수정
 *   <li>PATCH - 리소스 부분 수정
 *   <li>DELETE - 리소스 삭제
 *   <li>OPTIONS - CORS preflight 요청
 *   <li>HEAD - 헤더 정보만 조회
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    HEAD("HEAD");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    /**
     * 문자열로부터 HttpMethod 조회
     *
     * @param value HTTP 메서드 문자열 (대소문자 무관)
     * @return HttpMethod
     * @throws IllegalArgumentException 유효하지 않은 HTTP 메서드인 경우
     */
    public static HttpMethod fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("HttpMethod는 null이거나 빈 문자열일 수 없습니다");
        }

        String upperValue = value.trim().toUpperCase();

        for (HttpMethod method : values()) {
            if (method.value.equals(upperValue)) {
                return method;
            }
        }

        throw new IllegalArgumentException("유효하지 않은 HTTP 메서드입니다: " + value);
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
     * 상태 변경 메서드인지 확인
     *
     * @return POST, PUT, PATCH, DELETE면 true
     */
    public boolean isMutating() {
        return this == POST || this == PUT || this == PATCH || this == DELETE;
    }
}
