package me.codeninja55.learnreactive.fluxandmonoplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration
import java.util.function.BiFunction

class FluxAndMonoCombinedTest {

    @Test
    fun testCombineFlux() {
        val testStr1: Flux<String> = Flux.just("A", "B", "C")
        val testStr2: Flux<String> = Flux.just("D", "E", "F")

        val mergedTestFlux: Flux<String> = Flux.merge(testStr1, testStr2)

        StepVerifier.create(mergedTestFlux)
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("D", "E", "F")
            .verifyComplete()
    }

    @Test
    fun testCombineFluxWithDelay() {
        val testStr1: Flux<String> = Flux.just("A", "B", "C")
            .delayElements(Duration.ofMillis(10))
        val testStr2: Flux<String> = Flux.just("D", "E", "F")
            .delayElements(Duration.ofMillis(10))

        val mergedTestFlux: Flux<String> = Flux.concat(testStr1, testStr2)

        StepVerifier.create(mergedTestFlux)
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("D", "E", "F")
            .verifyComplete()
    }

    @Test
    fun testZipFluxWithDelay() {
        val testStr1: Flux<String> = Flux.just("A", "B", "C")
        val testStr2: Flux<String> = Flux.just("D", "E", "F")

        val combine = BiFunction<String, String, String> { s1: String, s2: String -> "$s1$s2" }
        val mergedTestFlux: Flux<String> = Flux.zip<String, String, String>(testStr1, testStr2, combine)

        StepVerifier.create(mergedTestFlux)
            .expectSubscription()
            .expectNext("AD", "BE", "CF")
            .verifyComplete()
    }

}