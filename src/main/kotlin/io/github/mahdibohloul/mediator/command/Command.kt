package io.github.mahdibohloul.mediator.command

/**
 * Marker interface for a command that performs an action without returning a value.
 *
 * The Command interface represents a message that is sent through the mediator
 * to execute a specific action or operation. Unlike [Request], commands do not
 * return values and are typically used for operations that modify state or
 * trigger side effects.
 *
 * ## Key Characteristics:
 * - **Single Handler**: Only one handler can be registered per command type
 * - **No Return Value**: Commands do not return responses
 * - **Side Effects**: Typically used for operations that modify state
 * - **Fire and Forget**: Commands are executed asynchronously
 *
 * ## Usage Example:
 * ```kotlin
 * data class CreateUserCommand(
 *     val username: String,
 *     val email: String,
 *     val password: String
 * ) : Command
 *
 * @Component
 * class CreateUserCommandHandler(
 *     private val userRepository: UserRepository,
 *     private val passwordEncoder: PasswordEncoder
 * ) : CommandHandler<CreateUserCommand> {
 *
 *     override suspend fun handle(command: CreateUserCommand) {
 *         val user = User(
 *             username = command.username,
 *             email = command.email,
 *             password = passwordEncoder.encode(command.password)
 *         )
 *         userRepository.save(user)
 *     }
 * }
 *
 * // Usage in service
 * mediator.sendAsync(CreateUserCommand("john", "john@example.com", "password"))
 * ```
 *
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see CommandHandler for handling commands
 * @see Mediator.sendAsync for sending commands
 * @see Request for operations that return values
 */
interface Command
