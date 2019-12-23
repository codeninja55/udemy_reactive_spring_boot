package me.codeninja55.learnreactive.fluxandmonoplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

class FluxAndMonoFilterTest {

    @Test
    fun testFluxAndMonoFilter() {
        val testList: List<String> = listOf("Iron Man", "Black Widow", "The Hulk", "Captain America")
        val testFluxFiltered: Flux<String> = Flux.fromIterable(testList)
            .filter { it.startsWith("Black") }

        StepVerifier.create(testFluxFiltered)
            .expectNext("Black Widow")
            .verifyComplete()
    }

    @Test
    fun testFluxAndMonoFilterLength() {
        val testList: List<String> = listOf("Iron Man", "Black Widow", "The Hulk", "Captain America")
        val testFluxFiltered: Flux<String> = Flux.fromIterable(testList)
            .filter { it.length > 8 }

        StepVerifier.create(testFluxFiltered)
            .expectNext("Black Widow", "Captain America")
            .verifyComplete()
    }
}