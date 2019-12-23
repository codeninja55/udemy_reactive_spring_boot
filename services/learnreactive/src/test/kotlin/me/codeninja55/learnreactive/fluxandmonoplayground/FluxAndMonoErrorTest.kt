package me.codeninja55.learnreactive.fluxandmonoplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.lang.IllegalStateException
import java.lang.RuntimeException
import java.time.Duration

class FluxAndMonoErrorTest {

    @Test
    fun testFluxErrorHandling() {
        val testStrFlux: Flux<String> = Flux.just("A", "B", "C")
            .concatWith(Flux.error(RuntimeException("Exception Occurred")))
            .concatWith(Flux.just("D"))
            .onErrorResume { Flux.just("default") }

        StepVerifier.create(testStrFlux)
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("default")
            .verifyComplete()
    }

    @Test
    fun testFluxErrorHandlingReturn() {
        val testStrFlux: Flux<String> = Flux.just("A", "B", "C")
            .concatWith(Flux.error(RuntimeException("Exception Occurred")))
            .concatWith(Flux.just("D"))
            .onErrorReturn("default")

        StepVerifier.create(testStrFlux)
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("default")
            .verifyComplete()
    }

    @Test
    fun testFluxErrorHandlingMap() {
        val testStrFlux: Flux<String> = Flux.just("A", "B", "C")
            .concatWith(Flux.error(RuntimeException("Exception Occurred")))
            .concatWith(Flux.just("D"))
            .onErrorMap { e -> CustomException(e) }

        StepVerifier.create(testStrFlux)
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectError(CustomException::class.java)
            .verify()
    }

    @Test
    fun testFluxErrorHandlingMapWithRetry() {
        val testStrFlux: Flux<String> = Flux.just("A", "B", "C")
            .concatWith(Flux.error(RuntimeException("Exception Occurred")))
            .concatWith(Flux.just("D"))
            .onErrorMap { e -> CustomException(e) }
            .retry(2)

        StepVerifier.create(testStrFlux)
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectError(CustomException::class.java)
            .verify()
    }

    @Test
    fun testFluxErrorHandlingMapRetryBackoff() {
        val testStrFlux: Flux<String> = Flux.just("A", "B", "C")
            .concatWith(Flux.error(RuntimeException("Exception Occurred")))
            .concatWith(Flux.just("D"))
            .onErrorMap { e -> CustomException(e) }
            .retryBackoff(1, Duration.ofMillis(10))

        StepVerifier.create(testStrFlux)
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectError(IllegalStateException::class.java)
            .verify()
    }

}

class CustomException(e: Throwable?) : Throwable() {
    override val message: String = e?.message ?: ""
}
