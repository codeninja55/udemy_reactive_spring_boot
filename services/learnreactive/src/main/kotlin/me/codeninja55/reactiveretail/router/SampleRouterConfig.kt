package me.codeninja55.reactiveretail.router

import me.codeninja55.reactiveretail.handler.SampleHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class SampleRouterConfig {

    @Bean
    fun route(handlerFunc: SampleHandler) : RouterFunction<ServerResponse> = router {
        accept(MediaType.APPLICATION_JSON).nest {
            "/functional".nest {
                "/flux".nest { GET("/", handlerFunc::getFlux) }
                "/mono".nest { GET("/") { _ ->
                    ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(1), Int::class.java)

                } }
            }
        }
    }
}