package me.codeninja55.retail.controller.v1

import lombok.extern.slf4j.Slf4j
import me.codeninja55.retail.constants.ItemConstants.Companion.ITEMS_END_POINT_V1
import me.codeninja55.retail.constants.ItemConstants.Companion.ITEM_END_POINT_V1
import me.codeninja55.retail.document.Item
import me.codeninja55.retail.repository.ItemReactiveRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@Slf4j
class ItemController(@Autowired private val itemRepository: ItemReactiveRepository) {

    @PostMapping(ITEMS_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    fun postItem(@RequestBody item: Item) : Mono<Item> = itemRepository.save(item)

    @GetMapping(ITEMS_END_POINT_V1)
    fun getAllItems() : Flux<Item> = itemRepository.findAll()

    @GetMapping("$ITEM_END_POINT_V1/{id}")
    fun getItemById(@PathVariable id: String) : Mono<ResponseEntity<Item>> {
        return itemRepository.findById(id)
            .map { item: Item? -> ResponseEntity(item, HttpStatus.OK) }
            .defaultIfEmpty(ResponseEntity(HttpStatus.NOT_FOUND))
    }

    @PutMapping("$ITEM_END_POINT_V1/{id}")
    fun putItemById(@PathVariable id: String, @RequestBody newItem: Item) : Mono<ResponseEntity<Item>> {
        return itemRepository.findById(id)
            .flatMap { item: Item ->
                item.price = newItem.price
                item.description = newItem.description
                itemRepository.save(item)
            }
            .map { updatedItem: Item -> ResponseEntity(updatedItem, HttpStatus.OK) }
            .defaultIfEmpty(ResponseEntity(HttpStatus.NOT_FOUND))
    }

    @DeleteMapping("$ITEM_END_POINT_V1/{id}")
    fun deleteItemById(@PathVariable id: String) : Mono<Void> {
        return itemRepository.deleteById(id)
    }

}