package me.codeninja55.retail.repository

import me.codeninja55.retail.document.Item
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@DataMongoTest
@ExtendWith(SpringExtension::class)
class ItemReactiveRepoTest {

    @Autowired
    private lateinit var itemRepo: ItemReactiveRepository

    private val itemsList = listOf(
        Item(null, "Aquila Dress Shoes", 299.0),
        Item(null, "Aquila Blair Embossed Der by Shoes", 99.0),
        Item(null, "Blaq Belt", 49.95),
        Item(null, "Dress Shirt", 80.0)
    )

    @BeforeEach
    fun setUp() {
        itemRepo.deleteAll()
            .thenMany(Flux.fromIterable(itemsList))
            .flatMap { item: Item -> itemRepo.save(item) }
            .doOnNext { item: Item? ->
                val matchedItem = itemsList.find { testItem -> testItem.description == item!!.description }
                matchedItem!!.id = item!!.id
            }
            .blockLast()
    }

    @Test
    fun testCreateSingleItem() {
        val itemSaved: Mono<Item> = itemRepo
            .save(Item(null, "Bose QuietControl 30 Wireless Earphones", 449.0))
        StepVerifier.create(itemSaved)
            .expectSubscription()
            .expectNextMatches { i: Item -> i.id != null && i.description == "Bose QuietControl 30 Wireless Earphones" }
            .verifyComplete()
    }

    @Test
    fun testRetrieveAllItems() {
        val itemFlux: Flux<Item> = itemRepo.findAll()
        StepVerifier.create(itemFlux)
            .expectSubscription()
            .expectNextCount(4)
            .verifyComplete()
    }

    @Test
    fun testRetrieveItemById() {
        val item: Mono<Item> = itemsList[0].id?.let { itemRepo.findById(it) } as Mono<Item>
        StepVerifier.create(item)
            .expectSubscription()
            .expectNextMatches { i: Item ->
                i.description == itemsList[0].description
            }
            .verifyComplete()
    }

    @Test
    fun testRetrieveItemByDesc() {
        val item: Flux<Item> = itemRepo.findByDescription("Dress Shirt")
        StepVerifier.create(item)
            .expectSubscription()
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun testDeleteSingleItem() {
        itemRepo.deleteById(itemsList.last().id ?: "").block()
        StepVerifier.create(itemRepo.findAll())
            .expectSubscription()
            .expectNextCount(3)
            .verifyComplete()
    }
}