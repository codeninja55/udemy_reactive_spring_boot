package me.codeninja55.reactiveretail.fluxandmonoplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.function.Supplier

class FluxAndMonoFactoryTests {

    val testList: List<String> = listOf("Iron Man", "Black Widow", "The Hulk", "Captain America")
    val testArr: Array<String> = arrayOf("Thor", "Dr. Strange", "Spiderman")

    @Test
    fun testFluxUsingIterable() {
        val testFlux: Flux<String> = Flux.fromIterable(testList)
        StepVerifier.create(testFlux)
            .expectNext("Iron Man", "Black Widow", "The Hulk", "Captain America")
            .verifyComplete()
    }

    @Test
    fun testFluxUsingArray() {
        val testFlux: Flux<String> = Flux.fromArray(testArr)
        StepVerifier.create(testFlux)
            .expectNext("Thor", "Dr. Strange", "Spiderman")
            .verifyComplete()
    }

    @Test
    fun testFluxUsingStream() {
        val testStream: Flux<String> = Flux.fromStream(testList.stream())
        StepVerifier.create(testStream)
            .expectNext("Iron Man", "Black Widow", "The Hulk", "Captain America")
            .verifyComplete()
    }

    @Test
    fun testFluxWithRange() {
        val testFluxRange: Flux<Int> = Flux.range(0, 3)
        StepVerifier.create(testFluxRange)
            .expectNext(0, 1, 2)
            .verifyComplete()
    }

    @Test
    fun testMonoWithNull() {
        val testMono: Mono<String> = Mono.justOrEmpty(null)
        StepVerifier.create(testMono)
            .verifyComplete()
    }

    @Test
    fun testMonoUsingSupplier() {
        val testSupplier: Supplier<String> = Supplier { "Hello World!" }
        val testMono: Mono<String> = Mono.fromSupplier(testSupplier)
        StepVerifier.create(testMono)
            .expectNext("Hello World!")
            .verifyComplete()
    }
}