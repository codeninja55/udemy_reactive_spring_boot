package me.codeninja55.learnreactive.handler

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

// Its a BEAN
@Component
class SampleHandler {

    fun getFlux(request: ServerRequest) : Mono<ServerResponse> {
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Flux.just(1, 2, 3, 4), Int::class.java)
    }

}