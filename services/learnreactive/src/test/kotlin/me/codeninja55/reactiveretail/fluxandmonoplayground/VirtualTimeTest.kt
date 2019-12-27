package me.codeninja55.reactiveretail.fluxandmonoplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import reactor.test.scheduler.VirtualTimeScheduler
import java.time.Duration

class VirtualTimeTest {

    @Test
    fun testWithoutVirtualTime() {
        val testFlux: Flux<Long> = Flux.interval(Duration.ofSeconds(1))
            .take(3)

        StepVerifier.create(testFlux)
            .expectSubscription()
            .expectNext(0L, 1L, 2L)
            .verifyComplete()
    }

    @Test
    fun testWithVirtualTime() {
        // Virtualise the time to reduce time to test
        VirtualTimeScheduler.getOrSet()
        val testFlux: Flux<Long> = Flux.interval(Duration.ofSeconds(1))
            .take(3)

        StepVerifier.withVirtualTime { testFlux }
            .expectSubscription()
            .thenAwait(Duration.ofSeconds(3))
            .expectNext(0L, 1L, 2L)
            .verifyComplete()
    }

}