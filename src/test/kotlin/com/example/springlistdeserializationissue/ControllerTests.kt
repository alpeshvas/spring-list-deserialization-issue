package com.example.springlistdeserializationissue

import com.example.springlistdeserializationissue.controllers.TestController
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(controllers = [TestController::class])
class ControllerTests {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `test json success`() {
        webTestClient.get().uri("/test/test-json")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("test")
    }

    @Test
    fun `test json fail`() {
        webTestClient.get().uri("/test/test-json-list-summary")
            .exchange()
            .expectStatus().isOk
            .expectBody()
    }
}