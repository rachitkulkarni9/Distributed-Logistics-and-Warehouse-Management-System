package com.logistics.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.cloud.gateway.discovery.locator.enabled=false",
    "spring.data.redis.host=localhost",
    "spring.data.redis.password=",
    "app.jwt.secret=test-secret-key-that-is-long-enough-for-hmac-sha256-signing"
})
class ApiGatewayApplicationTest {

    @Test
    void contextLoads() {
        // Verifies the Spring context assembles without errors
    }
}
