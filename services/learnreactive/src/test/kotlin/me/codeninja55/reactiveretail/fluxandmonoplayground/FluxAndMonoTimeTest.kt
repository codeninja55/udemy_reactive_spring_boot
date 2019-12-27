package me.codeninja55.reactiveretail.fluxandmonoplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration

class FluxAndMonoTimeTest {

    @Test
    @Throws(InterruptedException::class)
    fun testInfiniteSequence() {
        val testInfiniteFlux: Flux<Long> = Flux.interval(Duration.ofMillis(10))
            .take(5)

        StepVerifier.create(testInfiniteFlux)
            .expectNext(0L, 1L, 2L, 3L, 4L)
            .verifyComplete()
    }

    @Test
    @Throws(InterruptedException::class)
    fun testInfiniteSequenceMap() {
        val testInfiniteFlux: Flux<Int> = Flux.interval(Duration.ofMillis(10))
            .map { l: Long -> l.toInt() }
            .take(5)

        StepVerifier.create(testInfiniteFlux)
            .expectNext(0, 1, 2, 3, 4)
            .verifyComplete()
    }
}