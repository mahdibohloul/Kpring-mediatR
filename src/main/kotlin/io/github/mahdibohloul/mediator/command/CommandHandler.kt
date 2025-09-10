package io.github.mahdibohloul.mediator.command

/**
 * Interface for handling commands that perform actions without returning values.
 *
 * CommandHandler is responsible for processing a specific type of [Command] and
 * executing the associated business logic. Each command type can have only one
 * handler registered, ensuring a clear single responsibility for each command.
 *
 * ## Key Characteristics:
 * - **Single Responsibility**: One handler per command type
 * - **Asynchronous**: Uses suspend functions for non-blocking operations
 * - **No Return Value**: Commands do not return responses
 * - **Spring Integration**: Should be annotated with @Component for auto-discovery
 *
 * ## Implementation Guidelines:
 * - Keep handlers focused on a single responsibility
 * - Use dependency injection for required services
 * - Handle exceptions appropriately (they will propagate to the caller)
 * - Consider using repositories or services for data access
 * - Commands should be idempotent when possible
 *
 * ## Usage Example:
 * ```kotlin
 * @Component
 * class UpdateUserCommandHandler(
 *     private val userRepository: UserRepository,
 *     private val eventPublisher: EventPublisher
 * ) : CommandHandler<UpdateUserCommand> {
 *
 *     override suspend fun handle(command: UpdateUserCommand) {
 *         val user = userRepository.findById(command.userId)
 *             ?: throw UserNotFoundException("User not found: ${command.userId}")
 *
 *         user.apply {
 *             username = command.username
 *             email = command.email
 *         }
 *
 *         userRepository.save(user)
 *         eventPublisher.publishEvent(UserUpdatedEvent(user))
 *     }
 * }
 * ```
 *
 * @param TCommand The type of command this handler processes
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see Command for the command interface
 * @see Mediator.sendAsync for sending commands
 */
interface CommandHandler<in TCommand : Command> {

  /**
   * Handles the specified command and executes the associated business logic.
   *
   * This method is called by the mediator when a command of type [TCommand] is sent.
   * The implementation should process the command and perform the necessary actions.
   *
   * @param command The command to handle
   * @throws Exception Any exception thrown will be propagated to the caller
   * @author Mahdi Bohloul
   */
  suspend fun handle(command: TCommand)
}
