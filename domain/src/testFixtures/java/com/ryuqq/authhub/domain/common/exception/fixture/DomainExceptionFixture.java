package com.ryuqq.authhub.domain.common.exception.fixture;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import java.util.Map;

/** DomainException Test Fixture Object Mother 패턴을 사용한 테스트 데이터 생성 */
public final class DomainExceptionFixture {

    /** 테스트용 ErrorCode 구현 */
    public enum TestErrorCode implements ErrorCode {
        TEST_ERROR("TEST-001", 400, "Test exception occurred"),
        NOT_FOUND("NOT_FOUND", 404, "Resource not found"),
        INVALID_INPUT("INVALID_INPUT", 400, "Invalid input provided"),
        USER_NOT_FOUND("USER-001", 404, "User not found");

        private final String code;
        private final int httpStatus;
        private final String message;

        TestErrorCode(String code, int httpStatus, String message) {
            this.code = code;
            this.httpStatus = httpStatus;
            this.message = message;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public int getHttpStatus() {
            return httpStatus;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    /** 테스트용 DomainException 구현 (protected 생성자 접근용) */
    public static class TestDomainException extends DomainException {
        public TestDomainException(ErrorCode errorCode) {
            super(errorCode);
        }

        public TestDomainException(ErrorCode errorCode, String message) {
            super(errorCode, message);
        }

        public TestDomainException(ErrorCode errorCode, Map<String, Object> args) {
            super(errorCode, args);
        }

        public TestDomainException(ErrorCode errorCode, String message, Map<String, Object> args) {
            super(errorCode, message, args);
        }
    }

    /**
     * 기본 DomainException 생성
     *
     * @return DomainException 인스턴스
     */
    public static DomainException aDomainException() {
        return new TestDomainException(TestErrorCode.TEST_ERROR);
    }

    /**
     * 특정 ErrorCode로 DomainException 생성
     *
     * @param errorCode 에러 코드
     * @return DomainException 인스턴스
     */
    public static DomainException aDomainException(ErrorCode errorCode) {
        return new TestDomainException(errorCode);
    }

    /**
     * 특정 ErrorCode와 메시지로 DomainException 생성
     *
     * @param errorCode 에러 코드
     * @param message 에러 메시지
     * @return DomainException 인스턴스
     */
    public static DomainException aDomainException(ErrorCode errorCode, String message) {
        return new TestDomainException(errorCode, message);
    }

    /**
     * 인자를 포함한 DomainException 생성
     *
     * @param errorCode 에러 코드
     * @param args 에러 인자
     * @return DomainException 인스턴스
     */
    public static DomainException aDomainExceptionWithArgs(
            ErrorCode errorCode, Map<String, Object> args) {
        return new TestDomainException(errorCode, args);
    }

    /**
     * NOT_FOUND 에러 DomainException 생성
     *
     * @return DomainException 인스턴스
     */
    public static DomainException aNotFoundException() {
        return new TestDomainException(TestErrorCode.NOT_FOUND);
    }

    /**
     * INVALID_INPUT 에러 DomainException 생성
     *
     * @return DomainException 인스턴스
     */
    public static DomainException anInvalidInputException() {
        return new TestDomainException(TestErrorCode.INVALID_INPUT);
    }

    /**
     * USER 도메인 에러 DomainException 생성
     *
     * @param userId 사용자 ID
     * @return DomainException 인스턴스
     */
    public static DomainException aUserNotFoundException(Long userId) {
        return new TestDomainException(TestErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
    }

    private DomainExceptionFixture() {
        // Utility class
    }
}
