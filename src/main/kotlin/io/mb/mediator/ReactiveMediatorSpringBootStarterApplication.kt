package io.mb.mediator

import io.mb.mediator.interfaces.MediatorAnnotation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@MediatorAnnotation
class ReactiveMediatorSpringBootStarterApplication

fun main(args: Array<String>) {
    runApplication<ReactiveMediatorSpringBootStarterApplication>(*args)
}
