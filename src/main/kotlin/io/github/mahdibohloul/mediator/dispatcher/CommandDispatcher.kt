package io.github.mahdibohloul.mediator.dispatcher

import io.github.mahdibohloul.mediator.command.Command

/**
 * Interface for dispatching commands through the mediator pattern.
 *
 * CommandDispatcher provides the functionality to send commands that perform actions
 * without returning values. Commands are typically used for operations that modify
 * state or trigger side effects in the system.
 *
 * ## Key Characteristics:
 * - **No Return Value**: Commands do not return responses
 * - **Single Handler**: Only one handler can be registered per command type
 * - **Asynchronous**: Uses suspend functions for non-blocking execution
 * - **Fire and Forget**: Commands are executed asynchronously
 *
 * ## Usage Example:
 * ```kotlin
 * class UserService(private val commandDispatcher: CommandDispatcher) {
 *     suspend fun createUser(userData: UserData) {
 *         commandDispatcher.sendAsync(CreateUserCommand(userData))
 *     }
 *
 *     suspend fun updateUser(userId: String, updates: UserUpdates) {
 *         commandDispatcher.sendAsync(UpdateUserCommand(userId, updates))
 *     }
 * }
 * ```
 *
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see Command for the command interface
 * @see CommandHandler for handling commands
 * @see Mediator for the main mediator interface
 */
interface CommandDispatcher {

  /**
   * Sends a command asynchronously through the mediator.
   *
   * This method dispatches the specified command to its registered handler.
   * The command is processed asynchronously and does not return a value.
   *
   * @param command The command to send
   * @throws NoCommandHandlerException if no handler is registered for the command type
   * @throws Exception any exception thrown by the command handler will be propagated
   * @author Mahdi Bohloul
   */
  suspend fun sendAsync(command: Command)
}
