package com.ryuqq.authhub.domain.permissionendpoint.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * DuplicatePermissionEndpointException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("DuplicatePermissionEndpointException 테스트")
class DuplicatePermissionEndpointExceptionTest {

    @Test
    @DisplayName("예외를 생성하고 ErrorCode를 반환한다")
    void shouldCreateExceptionWithErrorCode() {
        // when
        DuplicatePermissionEndpointException exception =
                new DuplicatePermissionEndpointException("/api/v1/users", "GET");

        // then
        assertThat(exception.getErrorCode()).isInstanceOf(ErrorCode.class);
        assertThat(exception.getErrorCode().getCode()).isEqualTo("PERM-EP-002");
        assertThat(exception.getErrorCode().getHttpStatus()).isEqualTo(409);
    }

    @Test
    @DisplayName("예외의 args에 URL 패턴과 HTTP 메서드가 포함된다")
    void shouldContainUrlPatternAndHttpMethodInArgs() {
        // when
        DuplicatePermissionEndpointException exception =
                new DuplicatePermissionEndpointException("/api/v1/users", "GET");

        // then - URL과 method는 args(컨텍스트 정보)에 저장, message는 ErrorCode에서 제공
        assertThat(exception.args()).containsEntry("urlPattern", "/api/v1/users");
        assertThat(exception.args()).containsEntry("httpMethod", "GET");
    }
}
