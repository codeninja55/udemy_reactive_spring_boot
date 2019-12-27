package me.codeninja55.learnreactive.controller

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
@WebFluxTest
class FluxAndMonoControllerTest(@Autowired val webTestClient: WebTestClient) {

    // private val webTestClient: WebTestClient = WebTestClient.bindToController(FluxAndMonoController()).build()

    @Test
    fun testFluxApproach1() {
        val testFlux: Flux<Int> = webTestClient.get()
            .uri("/flux")
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
        webTestClient.get()
            .uri("/flux")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Int::class.java)
            .hasSize(4)
    }

    @Test
    fun testFluxApproach3() {
        val expectedList: List<Int> = listOf(1, 2, 3, 4)

        val testFlux: EntityExchangeResult<List<Int>> = webTestClient.get()
            .uri("/flux")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(Int::class.java)
            .returnResult()

        assertEquals(expectedList, testFlux.responseBody)
    }

    @Test
    fun testFluxApproach4() {
        val expectedList: List<Int> = listOf(1, 2, 3, 4)
        webTestClient.get()
            .uri("/flux")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(Int::class.java)
            .consumeWith<WebTestClient.ListBodySpec<Int>> { resp: EntityExchangeResult<List<Int>> ->
                assertEquals(expectedList, resp.responseBody)
            }
    }

    @Test
    fun testStreamFluxApproach1() {
        val testFlux: Flux<Long> = webTestClient.get()
            .uri("/fluxstream")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(Long::class.java)
            .responseBody

        StepVerifier.create(testFlux)
            .expectSubscription()
            .expectNext(0L)
            .expectNext(1L)
            .expectNext(2L)
            .thenCancel()
            .verify()
    }

    @Test
    fun testGetMono() {
        val bodySpec: EntityExchangeResult<Int> = webTestClient.get()
            .uri("/mono")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(Int::class.java)
            .returnResult()

        assertEquals(1, bodySpec.responseBody)
    }
}