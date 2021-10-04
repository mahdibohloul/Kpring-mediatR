package io.mb.mediator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReactiveMediatorSpringBootStarterApplication

fun main(args: Array<String>) {
    runApplication<ReactiveMediatorSpringBootStarterApplication>(*args)
}
