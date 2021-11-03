package io.mahdibohloul.kpringmediator.builder

import io.mahdibohloul.kpringmediator.infrastructure.DefaultFactory
import io.mahdibohloul.kpringmediator.infrastructure.DefaultMediator
import io.mahdibohloul.kpringmediator.core.Mediator
import org.springframework.context.ApplicationContext

class MediatorBuilder(private val applicationContext: ApplicationContext) {
    fun build(): Mediator {
        return DefaultMediator(DefaultFactory(applicationContext))
    }
}