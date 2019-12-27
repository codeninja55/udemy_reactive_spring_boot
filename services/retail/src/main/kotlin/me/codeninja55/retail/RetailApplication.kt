package me.codeninja55.retail

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RetailApplication

fun main(args: Array<String>) {
	runApplication<RetailApplication>(*args)
}
