package com.logistics.common;

import com.logistics.common.response.ApiResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void successResponse_isMarkedSuccessful() {
        ApiResponse<String> response = ApiResponse.success("hello");
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("hello");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    void errorResponse_isNotSuccessful() {
        ApiResponse<Void> response = ApiResponse.error("something went wrong");
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("something went wrong");
        assertThat(response.getData()).isNull();
    }
}
