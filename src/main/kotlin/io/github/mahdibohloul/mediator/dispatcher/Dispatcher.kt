package io.github.mahdibohloul.mediator.dispatcher

/**
 * Internal interface that combines command and request dispatching functionality.
 *
 * The Dispatcher interface provides a unified API for sending both commands and requests
 * through the mediator pattern. It combines the functionality of [CommandDispatcher] and
 * [RequestDispatcher] to handle different types of messages.
 *
 * **Note**: This interface is internal and should not be implemented directly. Use
 * [CommandDispatcher] or [RequestDispatcher] for custom implementations, or use the
 * [Mediator] interface for the complete functionality.
 *
 * ## Key Features:
 * - **Command Dispatching**: Send commands that perform actions without returning values
 * - **Request Dispatching**: Send requests that expect typed responses
 * - **Asynchronous**: All operations use suspend functions for non-blocking execution
 * - **Type Safety**: Strongly typed parameters for both commands and requests
 *
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see CommandDispatcher for command handling
 * @see RequestDispatcher for request handling
 * @see Mediator for the main mediator interface
 */
internal interface Dispatcher :
  CommandDispatcher,
  RequestDispatcher
