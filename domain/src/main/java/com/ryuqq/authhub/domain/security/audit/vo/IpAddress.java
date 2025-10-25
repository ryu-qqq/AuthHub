package com.ryuqq.authhub.domain.security.audit.vo;

import java.util.regex.Pattern;

/**
 * IP 주소를 나타내는 Value Object.
 * IPv4 및 IPv6 주소를 검증하고 안전하게 저장합니다.
 *
 * <p>이 Record는 불변(immutable) 객체이며, IP 주소 형식을 검증하여 도메인 무결성을 보장합니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>✅ 불변성 보장 - Record의 본질적 불변성</li>
 *   <li>✅ Law of Demeter 준수</li>
 *   <li>✅ 도메인 검증 로직 포함 - IPv4/IPv6 형식 검증</li>
 * </ul>
 *
 * @param value IP 주소 문자열 (null 불가, IPv4 또는 IPv6 형식)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record IpAddress(String value) {

    /**
     * IPv4 주소 검증을 위한 정규식 패턴.
     * 예: 192.168.0.1, 10.0.0.1
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );

    /**
     * IPv6 주소 검증을 위한 정규식 패턴.
     * 예: 2001:0db8:85a3:0000:0000:8a2e:0370:7334, ::1
     */
    private static final Pattern IPV6_PATTERN = Pattern.compile(
            "^(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|" +
            "([0-9a-fA-F]{1,4}:){1,7}:|" +
            "([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|" +
            "([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|" +
            "([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|" +
            "([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|" +
            "([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|" +
            "[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|" +
            ":((:[0-9a-fA-F]{1,4}){1,7}|:)|" +
            "fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|" +
            "::(ffff(:0{1,4}){0,1}:){0,1}" +
            "((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3}" +
            "(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$"
    );

    /**
     * 알 수 없는 IP 주소를 나타내는 상수.
     */
    public static final String UNKNOWN = "UNKNOWN";

    /**
     * Compact constructor - IP 주소 값의 검증을 수행합니다.
     *
     * @throws IllegalArgumentException value가 null이거나 유효하지 않은 IP 형식인 경우
     */
    public IpAddress {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }

        final String trimmedValue = value.trim();

        // UNKNOWN은 허용
        if (UNKNOWN.equals(trimmedValue)) {
            value = UNKNOWN;
            return;
        }

        // IPv4 또는 IPv6 형식 검증
        if (!isValidIpv4(trimmedValue) && !isValidIpv6(trimmedValue)) {
            throw new IllegalArgumentException("Invalid IP address format: " + trimmedValue);
        }

        value = trimmedValue;
    }

    /**
     * 새로운 IpAddress를 생성합니다.
     *
     * @param ipAddress IP 주소 문자열 (null 불가)
     * @return IpAddress 인스턴스
     * @throws IllegalArgumentException ipAddress가 null이거나 유효하지 않은 형식인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static IpAddress of(final String ipAddress) {
        return new IpAddress(ipAddress);
    }

    /**
     * 알 수 없는 IP 주소를 나타내는 IpAddress를 생성합니다.
     *
     * @return UNKNOWN 값을 가진 IpAddress 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static IpAddress unknown() {
        return new IpAddress(UNKNOWN);
    }

    /**
     * IPv4 형식인지 검증합니다.
     *
     * @param ip 검증할 IP 주소
     * @return IPv4 형식이면 true, 아니면 false
     */
    private static boolean isValidIpv4(final String ip) {
        return IPV4_PATTERN.matcher(ip).matches();
    }

    /**
     * IPv6 형식인지 검증합니다.
     *
     * @param ip 검증할 IP 주소
     * @return IPv6 형식이면 true, 아니면 false
     */
    private static boolean isValidIpv6(final String ip) {
        return IPV6_PATTERN.matcher(ip).matches();
    }

    /**
     * IPv4 주소인지 확인합니다.
     *
     * @return IPv4 형식이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isIpv4() {
        return isValidIpv4(this.value);
    }

    /**
     * IPv6 주소인지 확인합니다.
     *
     * @return IPv6 형식이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isIpv6() {
        return isValidIpv6(this.value);
    }

    /**
     * 알 수 없는 IP 주소인지 확인합니다.
     *
     * @return UNKNOWN이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isUnknown() {
        return UNKNOWN.equals(this.value);
    }

    /**
     * IP 주소 문자열을 반환합니다.
     *
     * @return IP 주소 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String asString() {
        return this.value;
    }
}
