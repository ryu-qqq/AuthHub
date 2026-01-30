package com.ryuqq.authhub.sdk.sync;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("EndpointSyncException")
class EndpointSyncExceptionTest {

    @Nested
    @DisplayName("생성자")
    class Constructors {

        @Test
        @DisplayName("메시지로 예외 생성")
        void createsWithMessage() {
            EndpointSyncException ex = new EndpointSyncException("Sync failed");

            assertThat(ex.getMessage()).isEqualTo("Sync failed");
            assertThat(ex.getCause()).isNull();
        }

        @Test
        @DisplayName("메시지와 원인으로 예외 생성")
        void createsWithMessageAndCause() {
            RuntimeException cause = new RuntimeException("Connection refused");
            EndpointSyncException ex = new EndpointSyncException("Sync failed", cause);

            assertThat(ex.getMessage()).isEqualTo("Sync failed");
            assertThat(ex.getCause()).isSameAs(cause);
        }
    }
}
