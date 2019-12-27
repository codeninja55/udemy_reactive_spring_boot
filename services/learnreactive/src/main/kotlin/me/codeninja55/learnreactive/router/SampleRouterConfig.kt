package me.codeninja55.learnreactive.router

import me.codeninja55.learnreactive.handler.SampleHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.accept
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Configuration
class SampleRouterConfig {

    @Bean
    fun route(handlerFunc: SampleHandler) : RouterFunction<ServerResponse> = router {
        accept(MediaType.APPLICATION_JSON).nest {
            "/functional".nest {
                "/flux".nest { GET("/", handlerFunc::getFlux) }
                "/mono".nest { GET("/") { req ->
                    ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(1), Int::class.java)
                } }
            }
        }
    }
}