package me.codeninja55.retail.repository

import me.codeninja55.retail.document.Item
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface ItemReactiveRepository : ReactiveMongoRepository<Item, String> {

    fun findByDescription(description: String) : Mono<Item>

}