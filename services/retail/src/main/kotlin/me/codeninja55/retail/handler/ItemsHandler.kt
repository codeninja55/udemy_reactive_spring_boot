package me.codeninja55.retail.handler

import me.codeninja55.retail.document.Item
import me.codeninja55.retail.repository.ItemReactiveRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters.fromPublisher
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.queryParamOrNull
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@Component
class ItemsHandler(@Autowired private val itemsRepository: ItemReactiveRepository) {

    companion object {
        private val notFound: Mono<ServerResponse> = ServerResponse.notFound().build()
        private val badRequest: Mono<ServerResponse> = ServerResponse.badRequest().build()
    }

    fun postItem(request: ServerRequest) : Mono<ServerResponse> {
        val newItemData: Mono<Item> = request.bodyToMono(Item::class.java)
            .map { item: Item? ->
                if (item != null) {
                    item.id = ObjectId().toHexString()
                }
                item
            }

        return newItemData.flatMap { item: Item ->
            ServerResponse.created(UriComponentsBuilder.fromPath("item/${item.id}").build().toUri())
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemsRepository.save(item), Item::class.java)
        }.switchIfEmpty(badRequest)
    }

    fun getAllItems(request: ServerRequest) : Mono<ServerResponse> {
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(itemsRepository.findAll(), Item::class.java)
    }

    fun getItemById(request: ServerRequest) : Mono<ServerResponse> {
        val id: String = request.pathVariable("id")
        val itemMono: Mono<Item> = itemsRepository.findById(id)

        return itemMono.flatMap {
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromPublisher(itemMono, Item::class.java))
        }.switchIfEmpty(notFound)
    }

    fun getItemByDescription(request: ServerRequest) : Mono<ServerResponse> = itemsRepository
        .findByDescription(request.queryParamOrNull("desc"))
            .flatMap {
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fromValue(it))
            }.switchIfEmpty(notFound)

    private fun updatedItem(reqData: Mono<Item>, id: String) : Mono<Item> = reqData.flatMap { newItem: Item ->
        itemsRepository.findById(id)
            .flatMap { oldItem: Item ->
                oldItem.description = newItem.description
                oldItem.price = newItem.price
                itemsRepository.save(oldItem)
            }
    }

    fun putItemById(request: ServerRequest) : Mono<ServerResponse> {
        val id: String = request.pathVariable("id")
        val newItemData: Mono<Item> = request.bodyToMono(Item::class.java)

        return updatedItem(newItemData, id).flatMap {
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(it))
        }.switchIfEmpty(notFound)
    }

    fun deleteItemById(request: ServerRequest) : Mono<ServerResponse> {
        val id: String = request.pathVariable("id")

        return itemsRepository.findById(id)
            .flatMap { item: Item ->
                ServerResponse.noContent()
                    .build(itemsRepository.delete(item))
            }.switchIfEmpty(notFound)
    }

}