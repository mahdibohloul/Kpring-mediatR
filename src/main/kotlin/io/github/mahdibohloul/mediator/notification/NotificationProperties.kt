package io.github.mahdibohloul.mediator.notification

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * Configuration properties for Kpring MediatR notification handling.
 *
 * This class provides configuration options for notification processing, including
 * exception handling activation and graceful shutdown behavior. All properties
 * are prefixed with `mediator.notification` and can be configured via application
 * properties or YAML files.
 *
 * ## Configuration Properties:
 *
 * ### Exception Handling
 * - **activate-exception-handling**: Enable/disable notification exception handling
 *
 * ### Graceful Shutdown
 * - **graceful-shutdown-per-child**: Timeout for each notification handler during shutdown
 * - **graceful-best-effort-after-cancel**: Additional time after cancellation during shutdown
 *
 * ## Usage Examples:
 *
 * ### Properties File:
 * ```properties
 * # Enable exception handling
 * mediator.notification.activate-exception-handling=true
 *
 * # Configure graceful shutdown timeouts
 * mediator.notification.graceful-shutdown-per-child=30s
 * mediator.notification.graceful-best-effort-after-cancel=5s
 * ```
 *
 * ### YAML Configuration:
 * ```yaml
 * mediator:
 *   notification:
 *     activate-exception-handling: true
 *     graceful-shutdown-per-child: 30s
 *     graceful-best-effort-after-cancel: 5s
 * ```
 *
 * ## Default Values:
 * - Exception handling is **enabled** by default
 * - Graceful shutdown timeout is **30 seconds** per handler
 * - Best effort timeout is **5 seconds** after cancellation
 *
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see NotificationExceptionHandler for exception handling implementation
 * @see ComponentFactory for how these properties are used
 */
@ConfigurationProperties(prefix = "mediator.notification")
@Suppress("detekt.MagicNumber")
data class NotificationProperties(
  /**
   * Maximum time to wait for each notification handler to complete during graceful shutdown.
   *
   * This ensures that all in-flight notifications are processed before the application shuts down.
   * If a handler takes longer than this timeout, it will be cancelled and the shutdown will proceed.
   *
   * @default 30 seconds
   */
  val gracefulShutdownPerChild: Duration = Duration.ofSeconds(30),

  /**
   * Additional time to wait after cancellation during graceful shutdown.
   *
   * This provides a best-effort attempt to complete any remaining notification processing
   * after the initial timeout has been reached. This is useful for handlers that need
   * a bit more time to clean up resources or complete critical operations.
   *
   * @default 5 seconds
   */
  val gracefulBestEffortAfterCancel: Duration = Duration.ofSeconds(5),

  /**
   * Whether to activate exception handling for notifications.
   *
   * When enabled, exceptions thrown by notification handlers will be caught and processed
   * by registered [NotificationExceptionHandler] instances. This allows for centralized
   * error handling and recovery strategies for notification processing failures.
   *
   * @default true
   */
  val activateExceptionHandling: Boolean = true,
)
