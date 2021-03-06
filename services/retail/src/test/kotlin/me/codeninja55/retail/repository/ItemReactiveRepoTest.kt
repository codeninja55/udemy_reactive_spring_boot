package me.codeninja55.retail.repository

import me.codeninja55.retail.document.Item
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@DataMongoTest
@ExtendWith(SpringExtension::class)
@DirtiesContext
class ItemReactiveRepoTest {

    @Autowired
    private lateinit var itemRepo: ItemReactiveRepository

    private val itemsList = listOf(
        Item(null, "Aquila Dress Shoes", 299.0),
        Item(null, "Aquila Blair Embossed Der by Shoes", 99.0),
        Item(null, "Blaq Belt", 49.95),
        Item(null, "Van Heusen Dress Shirt", 80.0)
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

        StepVerifier.create(itemRepo.findAll())
            .expectSubscription()
            .expectNextCount(5)
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
        val item: Mono<Item> = itemRepo.findByDescription(itemsList[0].description)
        StepVerifier.create(item)
            .expectSubscription()
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun testUpdateItem() {
        val updatedItem: Mono<Item> = itemRepo.findByDescription("Van Heusen Dress Shirt")
            .map { item: Item ->
                item.price = 120.0
                item
            }
            .flatMap { item: Item -> itemRepo.save(item) }

        StepVerifier.create(updatedItem)
            .expectSubscription()
            .expectNextMatches { item -> item.price == 120.0 }
            .verifyComplete()
    }

    @Test
    fun testDeleteByFind() {
        val deletedItem = itemRepo.findByDescription("Blaq Belt")
            .map { item: Item -> item.id!! }
            .flatMap { id: String ->
                itemRepo.deleteById(id)
            }

        StepVerifier.create(deletedItem)
            .expectSubscription()
            .verifyComplete()

        StepVerifier.create(itemRepo.findAll())
            .expectSubscription()
            .expectNextCount(3)
            .verifyComplete()
    }

    @Test
    fun testDeleteItem() {
        val deletedItem: Mono<Void> = itemRepo.findByDescription("Aquila Blair Embossed Der by Shoes")
            .flatMap { item: Item -> itemRepo.delete(item) }

        StepVerifier.create(deletedItem)
            .expectSubscription()
            .verifyComplete()

        StepVerifier.create(itemRepo.findAll())
            .expectSubscription()
            .expectNextCount(3)
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