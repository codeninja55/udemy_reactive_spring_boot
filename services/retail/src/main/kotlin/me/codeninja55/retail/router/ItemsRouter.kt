package me.codeninja55.retail.router

import me.codeninja55.retail.handler.ItemsHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class ItemsRouter {

    @Bean
    fun itemsRoute(itemHandler: ItemsHandler) : RouterFunction<ServerResponse> {
        return router {
            accept(MediaType.APPLICATION_JSON).nest {
                "/v1".nest {
                    "/items".nest {
                        GET("/", itemHandler::getAllItems)
                        POST("/", itemHandler::postItem)
                    }
                    "/item".nest {
                        GET("/{id}", itemHandler::getItemById)
                        PUT("/{id}", itemHandler::putItemById)
                        DELETE("/{id}", itemHandler::deleteItemById)
                        (GET("") and queryParam("desc") { true }) { serverRequest: ServerRequest ->
                            itemHandler.getItemByDescription(serverRequest)
                        }
                    }

                }
            }
        }
    }

}