package io.github.mahdibohloul.mediator.dispatcher.factories

import io.github.mahdibohloul.mediator.command.Command
import io.github.mahdibohloul.mediator.dispatcher.CommandDispatcher
import io.github.mahdibohloul.mediator.dispatcher.annotations.CustomCommandDispatcher
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class CommandDispatcherFactory(
  private val applicationContext: ApplicationContext,
) {
  private val logger = LoggerFactory.getLogger(this::class.java)

  private val dispatchers: Map<KClass<out Command>, CommandDispatcher> by lazy {
    logger.info("Discovering command dispatchers")
    applicationContext.getBeansWithAnnotation(CustomCommandDispatcher::class.java)
      .map { it.value as CommandDispatcher }.flatMap { dispatcher ->
        findCommandTypes(dispatcher).map { commandClass ->
          (commandClass to dispatcher)
            .also {
              logger.info("Registered ${dispatcher::class.simpleName} for command ${it.first.simpleName}")
            }
        }
      }.toMap()
      .also { logger.info("Discovered ${it.size} command dispatchers") }
  }

  fun getCommandDispatcher(
    command: Command,
  ): CommandDispatcher? = dispatchers
    .filterKeys { it.isInstance(command) }
    .values
    .singleOrNull()
    ?.also { logger.info("Found dispatcher for command ${command::class.simpleName}") }

  private fun findCommandTypes(dispatcher: CommandDispatcher): Array<KClass<out Command>> {
    val annotation = AnnotationUtils.findAnnotation(dispatcher::class.java, CustomCommandDispatcher::class.java)
    requireNotNull(annotation) {
      "Dispatcher ${dispatcher::class.simpleName} is not annotated with CustomCommandDispatcher"
    }
    return annotation.commandTypes
  }
}
