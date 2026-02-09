package io.github.mahdibohloul.mediator.dispatcher.factories

import io.github.mahdibohloul.mediator.dispatcher.RequestDispatcher
import io.github.mahdibohloul.mediator.dispatcher.annotations.CustomRequestDispatcher
import io.github.mahdibohloul.mediator.request.Request
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.getBeansWithAnnotation
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class RequestDispatcherFactory(
  private val applicationContext: ApplicationContext,
) {
  private val logger = LoggerFactory.getLogger(this::class.java)

  private val dispatchers: Map<KClass<out Request<*>>, RequestDispatcher> by lazy {
    logger.debug("Discovering request dispatchers")
    applicationContext.getBeansWithAnnotation<CustomRequestDispatcher>()
      .map { it.value as RequestDispatcher }.flatMap { dispatcher ->
        getSupportedRequestTypes(dispatcher).map { requestClass ->
          (requestClass to dispatcher)
            .also {
              logger.debug("Registered ${dispatcher::class.simpleName} for request ${it.first.simpleName}")
            }
        }
      }.toMap()
      .also { logger.debug("Discovered ${it.size} request dispatchers") }
  }

  fun getRequestDispatcher(
    request: Request<*>,
  ): RequestDispatcher? = dispatchers
    .filterKeys { it.isInstance(request) }
    .values
    .singleOrNull()
    ?.also { logger.debug("Found dispatcher for request ${request::class.simpleName}") }

  private fun getSupportedRequestTypes(dispatcher: RequestDispatcher): Array<KClass<out Request<*>>> {
    val annotation = AnnotationUtils.findAnnotation(dispatcher::class.java, CustomRequestDispatcher::class.java)
    requireNotNull(annotation) {
      "Dispatcher ${dispatcher::class.simpleName} is not annotated with CustomRequestDispatcher"
    }
    return annotation.requestTypes
  }
}
