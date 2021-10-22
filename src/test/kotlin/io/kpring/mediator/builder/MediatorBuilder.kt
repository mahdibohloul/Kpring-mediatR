package io.kpring.mediator.builder

import io.kpring.mediator.infrastructure.DefaultFactory
import io.kpring.mediator.infrastructure.DefaultMediator
import io.kpring.mediator.core.Mediator
import org.springframework.context.ApplicationContext

class MediatorBuilder(private val applicationContext: ApplicationContext) {
    fun build(): Mediator {
        return DefaultMediator(DefaultFactory(applicationContext))
    }
}