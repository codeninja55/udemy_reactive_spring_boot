package me.codeninja55.learnreactive.fluxandmonoplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.ConnectableFlux
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import java.time.Duration

class HotAndColdPublisherTest {

    @Test
    @Throws(InterruptedException::class)
    fun testColdPublisher() {
        val strFlux = listOf<String>("A", "B", "C", "D", "E", "F").toFlux()
            .delayElements(Duration.ofSeconds(1))

        // Multiple subscribers
        strFlux.subscribe { v -> println("Subscriber 1: $v") }
        Thread.sleep(2000)
        strFlux.subscribe { v2 -> println("Subscriber 2: $v2") }
        Thread.sleep(8000)
    }

    @Test
    @Throws(InterruptedException::class)
    fun testHotPublisher() {
        val strFlux = Flux.just("A", "B", "C", "D", "E", "F")
            .delayElements(Duration.ofSeconds(1))

        // Creates a hot publisher
        val connectableFlux: ConnectableFlux<String> = strFlux.publish()
        connectableFlux.connect()
        connectableFlux.subscribe { v -> println("Subscriber 1: $v") }
        Thread.sleep(3000)
        connectableFlux.subscribe { v2 -> println("Subscriber 2: $v2") }
        Thread.sleep(4000)
    }

}