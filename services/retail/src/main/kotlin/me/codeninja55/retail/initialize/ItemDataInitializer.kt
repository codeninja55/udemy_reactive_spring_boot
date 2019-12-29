package me.codeninja55.retail.initialize

import me.codeninja55.retail.document.Item
import me.codeninja55.retail.repository.ItemReactiveRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.lang.Exception

@Component
@Profile("!testing")
class ItemDataInitializer(@Autowired val itemReactiveRepository: ItemReactiveRepository) : CommandLineRunner {

    @Throws(Exception::class)
    override fun run(vararg args: String?) {
        initialDataSetup()
    }

    fun initialDataSetup() : Unit {
        itemReactiveRepository.deleteAll()
            .thenMany(Flux.fromIterable(
                listOf(
                    Item(null, "Pixel 4", 899.0),
                    Item(null, "Pixel 4 XL", 1279.0),
                    Item(null, "Nest Mini", 79.0),
                    Item(null, "Nest Hub Max", 349.0),
                    Item(null, "Nest Hub", 199.0),
                    Item(null, "Google Home", 149.0),
                    Item(null, "Google Home Max", 399.0),
                    Item(null, "Chromecast", 59.0),
                    Item(null, "Chromecast Ultra", 99.0),
                    Item(null, "Nest Wifi", 229.0)
                )
            ))
            .flatMap { itemReactiveRepository.save(it) }
            .thenMany(itemReactiveRepository.findAll())
            .subscribe { i: Item -> println("Item inserted: $i")}
    }

}