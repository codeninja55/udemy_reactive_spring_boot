package me.codeninja55.learnreactive.fluxandmonoplayground

import kotlinx.coroutines.reactor.flux
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers.parallel
import reactor.test.StepVerifier

class FluxAndMonoTransformTest {
    @Test
    fun testFluxMap() {
        val testList: List<String> = listOf("Iron Man", "Black Widow", "The Hulk", "Captain America")
        val testFluxUpper: Flux<String> = Flux.fromIterable(testList)
            .map { s -> s[0].toString() }

        StepVerifier.create(testFluxUpper)
            .expectNext("I", "B", "T", "C")
            .verifyComplete()
    }

    @Test
    fun testFluxMapLength() {
        val testList: List<String> = listOf("Iron Man", "Black Widow", "The Hulk", "Captain America")
        val testFluxUpper: Flux<Int> = Flux.fromIterable(testList)
            .map { it.length }

        StepVerifier.create(testFluxUpper)
            .expectNext(8, 11, 8, 15)
            .verifyComplete()
    }

    @Test
    fun testFluxMapLengthRepeat() {
        val testList: List<String> = listOf("Iron Man", "Black Widow", "The Hulk", "Captain America")
        val testFluxUpper: Flux<Int> = Flux.fromIterable(testList)
            .map { it.length }
            .repeat(1)

        StepVerifier.create(testFluxUpper)
            .expectNext(8, 11, 8, 15, 8, 11, 8, 15)
            .verifyComplete()
    }

    @Test
    fun testFluxMapLengthFilter() {
        val testList: List<String> = listOf("Iron Man", "Black Widow", "The Hulk", "Captain America")
        val testFluxUpper: Flux<String> = Flux.fromIterable(testList)
            .filter { it.length > 8 }
            .map { it.toUpperCase() }

        StepVerifier.create(testFluxUpper)
            .expectNext("BLACK WIDOW", "CAPTAIN AMERICA")
            .verifyComplete()
    }

    @Test
    fun testFluxMapBackPressure() {
        val testList: List<String> = listOf("Iron Man", "Black Widow", "The Hulk", "Captain America")
        val testFluxUpper: Flux<String> = Flux.fromIterable(testList)
            .take(2)
            .map { s -> s.toUpperCase() }

        StepVerifier.create(testFluxUpper)
            .expectNext("IRON MAN", "BLACK WIDOW")
            .verifyComplete()
    }

    private fun convertToList(s: String) : List<String> {
        Thread.sleep(1)
        return listOf(s, "new")
    }

    @Test
    fun testFluxFlatMapTransform() {
        val testList: List<String> = listOf("A","B","C","D","E","F")
        // When we need to make db/external service call that returns a flux
        val testFluxTest: Flux<String> = Flux.fromIterable(testList)
            .flatMap { c -> Flux.fromIterable(convertToList(c)) }

        StepVerifier.create(testFluxTest)
            .expectNextCount(12)
            .verifyComplete()
    }

    @Test
    fun testFluxFlatMapTransformUsingParallel() {
        val testList: List<String> = listOf("A","B","C","D","E","F")
        // When we need to make db/external service call that returns a flux
        val testFluxTest: Flux<String> = Flux.fromIterable(testList)
            .window(2) // Flux<Flux<String>> => (Flux[A], Flux[B])
            .flatMap { flux ->
                flux.map(this::convertToList)
                    .subscribeOn(parallel())
                    .flatMap { s -> Flux.fromIterable(s) }
            }

        StepVerifier.create(testFluxTest)
            .expectNextCount(12)
            .verifyComplete()
    }

    @Test
    fun testFluxFlatMapTransformUsingParallelOrdered() {
        val testList: List<String> = listOf("A","B","C","D","E","F")
        // When we need to make db/external service call that returns a flux
        val testFluxTest: Flux<String> = Flux.fromIterable(testList)
            .window(2) // Flux<Flux<String>> => (Flux[A], Flux[B])
            .flatMapSequential { flux ->  // Maintains sequential order
                flux.map(this::convertToList)
                    .subscribeOn(parallel())
                    .flatMap { s -> Flux.fromIterable(s) }
            }

        StepVerifier.create(testFluxTest)
            .expectNext("A", "new")
            .expectNext("B", "new")
            .expectNext("C", "new")
            .expectNext("D", "new")
            .expectNext("E", "new")
            .expectNext("F", "new")
            .verifyComplete()
    }
}