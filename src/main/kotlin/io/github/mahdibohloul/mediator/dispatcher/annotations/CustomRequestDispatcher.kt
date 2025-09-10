package io.github.mahdibohloul.mediator.dispatcher.annotations

import io.github.mahdibohloul.mediator.request.Request
import kotlin.reflect.KClass
import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

/**
 * Annotation used to mark a class as a custom request dispatcher in a Spring-based application.
 *
 * A component annotated with this annotation will be registered as a Spring-managed bean
 * and will serve as a dispatcher for specific types of requests.
 * It is particularly useful within the mediator pattern where requests are routed to
 * their corresponding handlers.
 *
 * @property value An optional value to customize the Spring bean name for the component.
 * @property requestTypes Specifies the array of request types handled by the dispatcher.
 */
@Component
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class CustomRequestDispatcher(
  @get:AliasFor(annotation = Component::class)
  val value: String = "",
  val requestTypes: Array<KClass<out Request<*>>>,
)
