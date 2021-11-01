package io.github.mahdibohloul.kpringmediator.builder

import io.github.mahdibohloul.kpringmediator.infrastructure.DefaultFactory
import io.github.mahdibohloul.kpringmediator.infrastructure.DefaultMediator
import io.github.mahdibohloul.kpringmediator.core.Mediator
import org.springframework.context.ApplicationContext

class MediatorBuilder(private val applicationContext: ApplicationContext) {
    fun build(): Mediator {
        return DefaultMediator(DefaultFactory(applicationContext))
    }
}