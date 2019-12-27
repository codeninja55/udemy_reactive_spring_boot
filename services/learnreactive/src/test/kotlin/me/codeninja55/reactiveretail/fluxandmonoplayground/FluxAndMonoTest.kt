package me.codeninja55.reactiveretail.fluxandmonoplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.lang.RuntimeException

class FluxAndMonoTest {

    /*@Test
    fun fluxTest() {
        val strFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
            .concatWith(Flux.error(RuntimeException("Exception Occurred")))
            .concatWith(Flux.just("After Error"))  // data will not be emitted after Error
            .log()  // logging the steps

        strFlux.subscribe(
            { d -> println(d) },
            { e -> System.err.println("Exception is: $e") },
            { println("Completed") }
        )
    }*/

    @Test
    fun fluxTestElementsWithError() {
        val strFlux: Flux<String> = Flux.just("Spring", "Spring Boot", "Reactive Spring")
            .concatWith(Flux.error(RuntimeException("Exception Occurred")))
        StepVerifier.create(strFlux)
            .expectNext("Spring")
            .expectNext("Spring Boot")
            .expectNext("Reactive Spring")
            .expectError(RuntimeException::class.java)
            .verify()
    }

    @Test
    fun fluxTestElementsWithError1() {
        val strFlux: Flux<String> = Flux.just("Spring", "Spring Boot", "Reactive Spring")
            .concatWith(Flux.error(RuntimeException("Exception Occurred")))
        StepVerifier.create(strFlux)
            .expectNext("Spring", "Spring Boot", "Reactive Spring")
            .expectError(RuntimeException::class.java)
            .verify()
    }

    @Test
    fun fluxTestElementsWithErrorMsg() {
        val strFlux: Flux<String> = Flux.just("Spring", "Spring Boot", "Reactive Spring")
            .concatWith(Flux.error(RuntimeException("Exception Occurred")))
        StepVerifier.create(strFlux)
            .expectNext("Spring")
            .expectNext("Spring Boot")
            .expectNext("Reactive Spring")
            .expectErrorMessage("Exception Occurred")
            .verify()
    }

    @Test
    fun fluxTestElementsCount() {
        val strFlux: Flux<String> = Flux.just("Spring", "Spring Boot", "Reactive Spring")
        StepVerifier.create(strFlux)
            .expectNextCount(3)
            .verifyComplete()
    }

    @Test
    fun fluxTestElementsWithoutError() {
        val strFlux: Flux<String> = Flux.just("Spring", "Spring Boot", "Reactive Spring")
        StepVerifier.create(strFlux)
            .expectNext("Spring")
            .expectNext("Spring Boot")
            .expectNext("Reactive Spring")
            .verifyComplete()
    }

    @Test
    fun monoTestError() {
        StepVerifier.create(Mono.error<Throwable>(RuntimeException("Exception Occurred")))
            .expectError(RuntimeException::class.java)
            .verify()
    }

    @Test
    fun monoTest() {
        val strMono: Mono<String> = Mono.just("Spring")
        StepVerifier.create(strMono)
            .expectNext("Spring")
            .verifyComplete()
    }
}