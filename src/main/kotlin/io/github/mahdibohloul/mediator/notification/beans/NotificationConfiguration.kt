package io.github.mahdibohloul.mediator.notification.beans

import io.github.mahdibohloul.mediator.notification.NotificationProperties
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withTimeoutOrNull
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import kotlin.coroutines.CoroutineContext
import kotlin.time.toKotlinDuration

@Configuration
@EnableConfigurationProperties(NotificationProperties::class)
class NotificationConfiguration {
  @Bean("notificationCoroutineScope")
  fun notificationCoroutineScope(
    notificationProperties: NotificationProperties,
  ): CoroutineScope = ManagedCoroutineScope(
    name = "notification-handlers",
    dispatcher = Dispatchers.Default,
    timeoutPerChild = notificationProperties.gracefulShutdownPerChild,
    bestEffortAfterCancel = notificationProperties.gracefulBestEffortAfterCancel,
  )

  class ManagedCoroutineScope(
    name: String,
    dispatcher: CoroutineDispatcher,
    private val timeoutPerChild: Duration,
    private val bestEffortAfterCancel: Duration,
  ) : CoroutineScope,
    AutoCloseable {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val parent = SupervisorJob()
    private val handler = CoroutineExceptionHandler { _, e ->
      logger.error("Uncaught coroutine exception in $name scope", e)
    }

    override val coroutineContext: CoroutineContext =
      dispatcher + parent + handler + CoroutineName(name) + MDCContext()

    override fun close() {
      val children = parent.children.toList()
      logger.info("Shutting down {}: {} child(ren)", coroutineContext[CoroutineName]?.name, children.size)

      runBlocking {
        val waited = withTimeoutOrNull(timeoutPerChild.toKotlinDuration()) {
          children.joinAll()
          return@withTimeoutOrNull true
        }

        if (waited != true) {
          logger.warn(
            "Timeout waiting {}s for coroutines; best-effort extra wait {}s",
            timeoutPerChild.seconds,
            bestEffortAfterCancel.seconds,
          )
          val bestEffortAnswered = withTimeoutOrNull(bestEffortAfterCancel.toKotlinDuration()) {
            children.joinAll()
            return@withTimeoutOrNull true
          }
          if (bestEffortAnswered != true) {
            parent.cancel(CancellationException("Best-effort shutdown timeout"))
          }
        }
      }

      logger.info("Shutdown of ${coroutineContext[CoroutineName]?.name} complete")
    }
  }
}
