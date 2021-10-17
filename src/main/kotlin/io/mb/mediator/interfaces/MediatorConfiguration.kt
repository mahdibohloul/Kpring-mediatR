package io.mb.mediator.interfaces

import io.mb.mediator.infrastructure.DefaultFactory
import io.mb.mediator.infrastructure.DefaultMediator
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Aspect
@Configuration
class MediatorConfiguration(
    @Autowired val applicationContext: ApplicationContext
) {
    @Bean
    @Before("@annotation(MediatorAnnotation)")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun registry(): Factory {
        return DefaultFactory(applicationContext)
    }

    @Bean
    @Before("@annotation(MediatorAnnotation)")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun mediator(factory: Factory): Mediator {
        return DefaultMediator(factory)
    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MediatorAnnotation {}

