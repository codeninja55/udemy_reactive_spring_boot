package me.codeninja55.reactiveretail.fluxandmonoplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import reactor.test.StepVerifier
import reactor.test.test
import kotlin.test.assertEquals

class FluxAndMonoBackPressureTest {

    @Test
    fun testFluxBackPressure() {
        val testFluxRange: Flux<Int> = Flux.range(0, 10)
        val testFluxRange2: Flux<Int> = (0..9).toFlux()

        StepVerifier.create(testFluxRange)
            .expectSubscription()
            .thenRequest(2)  // Back pressure control
            .expectNext(0)
            .expectNext(1)
            .thenRequest(2)
            .expectNext(2)
            .expectNext(3)
            .thenRequest(1)
            .expectNext(4)
            .thenCancel()
            .verify()

        testFluxRange2.test()
            .expectSubscription()
            .thenRequest(2)
            .expectNext(0)
            .expectNext(1)
            .thenCancel()
            .verify()
    }

    @Test
    fun testBackPressureSubscription() {
        val testFluxRange: Flux<Int> = Flux.range(0, 10)

        testFluxRange.subscribe(
            { elem ->
                println("Element is $elem")
                assertEquals(0, elem)
            },
            { err -> println("Exception $err") },
            { println("Not Printed when Cancelled by Back Pressure") },  // done
            { subscription -> subscription.request(1) }
        )
    }

    @Test
    fun testBackPressureSubscriptionAndCancel() {
        val testFluxRange: Flux<Int> = Flux.range(0, 10)

        testFluxRange.subscribe(
            { elem ->
                println("Element is $elem")
                assertEquals(0, elem)
            },
            { err -> println("Exception $err") },
            { println("Not Printed when Cancelled by Back Pressure") },  // done
            { subscription -> subscription.cancel() }
        )
    }

    @Test
    fun testCustomisedBackPressureSubscription() {
        val testFluxRange: Flux<Int> = Flux.range(0, 10)
            // .log()
        testFluxRange.subscribe(MyBaseSubscriber())
        /*testFluxRange.subscribe(BaseSubscriber<Int>() {
            override fun hookOnNext(value: Int) {
                request(1)
                println("Value received is: $value")
                if (value == 4) {
                    cancel()
                }
            }
        })*/
    }
}

class MyBaseSubscriber : BaseSubscriber<Int>() {
    override fun hookOnNext(value: Int) {
        request(1)
        println("Value received is: $value")
        if (value == 4) {
            cancel()
        }
    }

    override fun hookOnCancel() {
        println("Cancelled")
        super.hookOnCancel()
    }

    override fun hookOnComplete() {
        println("Completed")
        super.hookOnComplete()
    }
}
