package me.codeninja55.retail.v1

import me.codeninja55.retail.constants.ItemConstants.Companion.ITEMS_END_POINT_V1
import me.codeninja55.retail.constants.ItemConstants.Companion.ITEM_END_POINT_V1
import me.codeninja55.retail.document.Item
import me.codeninja55.retail.repository.ItemReactiveRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@SpringBootTest
@ExtendWith(SpringExtension::class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("testing")
class ItemControllerTests {

    @Autowired
    private lateinit var client: WebTestClient

    @Autowired
    private lateinit var itemRepo: ItemReactiveRepository

    private val itemsList: List<Item> = listOf(
        Item(null, "Pixel 4", 899.0),
        Item(null, "Pixel 4 XL", 1279.0),
        Item(null, "Nest Mini", 79.0),
        Item(null, "Nest Hub Max", 349.0),
        Item(null, "Nest Hub", 199.0),
        Item(null, "Chromecast", 59.0),
        Item(null, "Chromecast Ultra", 99.0),
        Item(null, "Nest Wifi", 229.0)
    )

    @BeforeEach
    fun setUp() {
        itemRepo.deleteAll()
            .thenMany(Flux.fromIterable(itemsList))
            .flatMap { itemRepo.save(it) }
            .doOnNext { item: Item? ->
                val matchedItem = itemsList.find { testItem -> testItem.description == item!!.description }
                matchedItem!!.id = item!!.id
            }
            .blockLast()
    }

    @Test
    fun testCreateItemFail() {
        val badNewItem: String = "Item<id=INCORRECT_ID, description=Pixel Buds, price=199.0>"
        client.post()
            .uri(ITEMS_END_POINT_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(badNewItem), String::class.java)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .consumeWith { res: EntityExchangeResult<ByteArray> ->
                assertEquals(res.status, HttpStatus.BAD_REQUEST)
                assertEquals(res.rawStatusCode, 400)
            }
    }

    @Test
    fun testCreateItem() {
        val newItem = Item(null, "Pixel Buds", 199.0)
        client.post()
            .uri(ITEMS_END_POINT_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(newItem), Item::class.java)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .consumeWith { res: EntityExchangeResult<ByteArray> ->
                assertEquals(HttpStatus.CREATED, res.status)
                assertEquals(201, res.rawStatusCode)
            }
            .jsonPath("\$.id").isNotEmpty
            .jsonPath("\$.description").isEqualTo("Pixel Buds")
            .jsonPath("\$.price").isEqualTo(199.0)
    }

    @Test
    fun testRetrieveAllItems() {
        client.get()
            .uri(ITEMS_END_POINT_V1).exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Item::class.java)
            .hasSize(8)
    }

    @Test
    fun testRetrieveAllItemsApproach2() {
        client.get()
            .uri(ITEMS_END_POINT_V1).exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Item::class.java)
            .consumeWith<WebTestClient.ListBodySpec<Item>> { resp ->
                val items: List<Item> = resp.responseBody as List<Item>
                items.forEach { i: Item -> assertTrue(i.id != null) }
            }
    }

    @Test
    fun testRetrieveAllItemsApproach3() {
        val itemFlux: Flux<Item> = client.get()
            .uri(ITEMS_END_POINT_V1).exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(Item::class.java)
            .responseBody

        StepVerifier.create(itemFlux)
            .expectSubscription()
            .expectNextCount(8)
            .verifyComplete()
    }

    @Test
    fun testRetrieveSingleItemFail() {
        val id: String = "INCORRECT_ID"
        client.get()
            .uri("$ITEM_END_POINT_V1/$id")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testRetrieveSingleItem() {
        val id: String = itemsList[0].id!!
        client.get()
            .uri("$ITEM_END_POINT_V1/$id")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.price", 899.0)
    }

    @Test
    fun testRetrieveSingleItemByDescFail() {
        val desc: String = "Unknown product"
        client.get()
            .uri("/v1/item?desc=$desc")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .consumeWith { res: EntityExchangeResult<ByteArray> ->
                assertEquals(res.status, HttpStatus.NOT_FOUND)
                assertEquals(res.rawStatusCode, 404)
            }
    }

    @Test
    fun testRetrieveSingleItemByDesc() {
        val desc: String = itemsList[0].description
        client.get()
            .uri("/v1/item?desc=$desc")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.price").isEqualTo(899.0)
            .jsonPath("\$.description").isEqualTo("Pixel 4")
    }

    @Test
    fun testUpdateSingleItemFail() {
        val id = "INCORRECT_ID"

        client.put()
            .uri("$ITEM_END_POINT_V1/$id")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(Item(null, "Pixel 4", 799.0)), Item::class.java)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testUpdateSingleItemBadId() {
        val id = "BAD_ID"
        client.put()
            .uri("$ITEM_END_POINT_V1/$id")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .consumeWith { res: EntityExchangeResult<ByteArray> ->
                assertEquals(res.status, HttpStatus.NOT_FOUND)
                assertEquals(res.rawStatusCode, 404)
            }
    }

    @Test
    fun testUpdateSingleItem() {
        val id: String = itemsList[0].id!!
        val newPrice = 799.0
        val updatedItem = Item(null, "Pixel 4", newPrice)

        client.put()
            .uri("$ITEM_END_POINT_V1/$id")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(updatedItem), Item::class.java)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.price").isEqualTo(newPrice)
            .jsonPath("\$.description").isEqualTo("Pixel 4")
    }

    @Test
    fun testDeleteSingleItemBadId() {
        val id = "BAD_ID"
        client.delete()
            .uri("$ITEM_END_POINT_V1/$id")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .consumeWith { res: EntityExchangeResult<ByteArray> ->
                assertEquals(res.status, HttpStatus.NOT_FOUND)
                assertEquals(res.rawStatusCode, 404)
            }
    }

    @Test
    fun testDeleteSingleItem() {
        val id: String = itemsList[0].id!!
        client.delete()
            .uri("$ITEM_END_POINT_V1/$id")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent
            .expectBody(Void::class.java)
    }
}