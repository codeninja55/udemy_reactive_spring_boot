package me.codeninja55.reactiveretail.functional

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureWebTestClient
class SampleHandlerTest(@Autowired val client: WebTestClient) {

    @Test
    fun testFluxApproach1() {
        val testFlux: Flux<Int> = client.get()
            .uri("/functional/flux")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(Int::class.java)
            .responseBody

        StepVerifier.create(testFlux)
            .expectSubscription()
            .expectNext(1, 2, 3, 4)
            .verifyComplete()
    }

    @Test
    fun testFluxApproach2() {
        client.get()
            .uri("/functional/flux")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Int::class.java)
            .hasSize(4)
    }

    @Test
    fun testGetMono() {
        val bodySpec: EntityExchangeResult<Int> = client.get()
            .uri("/functional/mono")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(Int::class.java)
            .returnResult()

        assertEquals(1, bodySpec.responseBody)
    }

}