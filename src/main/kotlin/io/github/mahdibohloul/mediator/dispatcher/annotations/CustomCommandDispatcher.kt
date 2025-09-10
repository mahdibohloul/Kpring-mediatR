package io.github.mahdibohloul.mediator.dispatcher.annotations

import io.github.mahdibohloul.mediator.command.Command
import kotlin.reflect.KClass
import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

/**
 * Annotation used to mark a class as a custom command dispatcher in a Spring-based application.
 *
 * A component annotated with this annotation will be registered as a Spring-managed bean
 * and will serve as a dispatcher for specific types of commands.
 * It is particularly useful within the mediator pattern where commands are routed to
 * their corresponding handlers.
 *
 * @property value An optional value to customize the Spring bean name for the component.
 * @property commandTypes Specifies the array of command types handled by the dispatcher.
 */
@Component
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class CustomCommandDispatcher(
  @get:AliasFor(annotation = Component::class)
  val value: String = "",
  val commandTypes: Array<KClass<out Command>>,
)
