package me.codeninja55.learnreactive.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@RestController
class FluxAndMonoController {

    @GetMapping("/flux")
    fun getFlux() : Flux<Int> {
        return Flux.just(1, 2, 3, 4)
    }

    @GetMapping("/fluxstream", produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    fun getFluxStream() : Flux<Long> {
        return Flux.interval(Duration.ofSeconds(1)).log()
    }

    @GetMapping("/mono", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMono() : Mono<Int> {
        return  Mono.just(1)
    }

}