package io.mb.mediator.builder

import io.mb.mediator.infrastructure.DefaultFactory
import io.mb.mediator.infrastructure.DefaultMediator
import io.mb.mediator.interfaces.Mediator
import org.springframework.context.ApplicationContext

class MediatorBuilder(private val applicationContext: ApplicationContext) {
    fun build(): Mediator {
        return DefaultMediator(DefaultFactory(applicationContext))
    }
}